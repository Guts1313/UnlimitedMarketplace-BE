# Unlimited Marketplace — Backend

Unlimited Marketplace is an online auction house for sneakers. Sellers put a pair of shoes up for sale, buyers bid against each other in real time, and the moment a seller accepts a bid the shoe is sold and the winner gets pinged to pay. Think of it as a stripped-down, sneaker-focused take on the live-auction experience — you watch the price climb, you get told the instant someone outbids you, and you find out straight away when you've won.

This repository is the **backend**: a Spring Boot service that handles the accounts, the listings, the live bidding, and the payments. The React frontend lives in a separate repository and talks to this API.

> This started life as a Fontys ICT semester 3 project, so you'll see a few traces of that — a GitLab CI pipeline, SonarQube wiring, and design docs under `Documents/`.

## What it actually does

- **Accounts & auth.** Users register, log in, and get a JWT back. Tokens are short-lived and there's a refresh-token flow so people aren't kicked out mid-auction. Passwords are stored BCrypt-hashed, never in plain text. There are two roles — regular `USER` and `ADMIN`.
- **Listings.** A logged-in user can list a shoe (name, price, image URL) and browse everyone else's listings, optionally filtered by category. You can also pull up just your own listings. Each product carries a status (`ACTIVE` / `SOLD`) and a payment status (`AWAITING` / `PAID`).
- **Live bidding.** This is the heart of it. Bids go over a WebSocket connection rather than plain HTTP, so when you place a bid everyone watching that product sees the new price immediately. If someone outbids you, you get a notification on your personal channel. When a seller accepts a bid, the winner is notified directly.
- **Payments.** Winners can save payment methods and run a payment through to settle the auction. Each attempt is recorded as a transaction.
- **Admin panel.** Admins can list all users and remove accounts.

## How the real-time bidding works

The bidding isn't request/response — it's a STOMP-over-WebSocket setup (with SockJS as the fallback). The flow looks like this:

1. The client opens a socket to `/websocket-sockjs-stomp`, passing its JWT as an `access_token` query parameter. The handshake decodes the token and attaches the user's identity to the session, so the server always knows *who* is bidding.
2. A bid is sent to `/app/placeBid`. The server saves it, then broadcasts the new amount to `/topic/product{id}` — every spectator on that product gets the update.
3. Everyone who'd previously bid on that product (except the new top bidder) gets an "you've been outbid" message on `/queue/outbid{id}`.
4. When the seller accepts a bid (`/app/acceptBid`), the winning bidder is messaged on `/queue/winner{id}`.

Subscriptions are tracked server-side so the right people get the right notifications, and there's a small REST endpoint to read a user's active subscriptions back.

## Tech stack

| Area | Choice |
|------|--------|
| Language / runtime | Java 17 |
| Framework | Spring Boot 3.2.3 (Web, Data JPA, Security, Validation, WebSocket) |
| Real-time | STOMP over WebSocket + SockJS |
| Auth | JWT (Auth0 `java-jwt` + `jjwt`), stateless sessions, refresh tokens |
| Database | MySQL 8 in production, H2 in tests |
| Build | Gradle (Kotlin DSL) |
| Quality | JUnit, JaCoCo coverage, SonarQube |
| Packaging | Docker (multi-stage) + Docker Compose |
| Deployment | Google Cloud Run (image built and pushed via CI) |

## Project layout

The code follows a fairly classic layered structure, with use cases kept behind interfaces:

```
src/main/java/unlimitedmarketplace/
├── controllers/    REST + WebSocket entry points (products, bids, payments, users, auth, admin)
├── business/       Use cases — interfaces + impl, plus domain exceptions
├── domain/         Request/response DTOs and enums (ProductStatus, UserRoles, ...)
├── persistence/    JPA entities and Spring Data repositories
├── security/       JWT encoding/decoding, access & refresh token handling
├── configuration/  Security, CORS, and the STOMP/WebSocket config
└── util/           Helpers (JWT utilities)
```

## API at a glance

Most endpoints require a valid JWT (`Authorization: Bearer <token>`); creating an account and logging in don't.

**Auth** — `/unlimitedmarketplace/auth`
- `POST /login` — log in, returns access token + refresh token + user id
- `POST /refresh-token` — swap a refresh token for a fresh access token
- `POST /logout` — invalidate a refresh token

**Users** — `/unlimitedmarketplace`
- `POST /` — register a new user
- `GET /{id}` — fetch your own profile
- `PUT /{id}` — change password
- `GET /` — list all users *(admin only)*
- `DELETE /{id}` — delete a user *(admin only)*

**Products** — `/unlimitedmarketplace/products`
- `POST /` — list a shoe for auction
- `GET /` — browse all listings (optional `?productCat=`)
- `GET /mylistings?userId=` — your own listings
- `GET /{id}` — a single listing

**Bids** — `/bids`
- `GET /latest/{productId}` — current highest bid
- `GET /user-bids/{userId}` — products a user has bid on + their total
- WebSocket: `/app/placeBid`, `/app/acceptBid` (see the bidding section above)

**Payments** — `/payments`
- `POST /add` — save a payment method
- `GET /listpaymentoptions` — your saved payment methods
- `POST /process` — process a payment

**Admin** — `/adminpanel`
- `GET /users`, `DELETE /users/{userId}` *(admin only)*

## Running it locally

You'll need Java 17 and a MySQL 8 instance (or just use Docker Compose, which brings its own).

### Option A — Docker Compose (easiest)

This spins up the app and a MySQL container together.

```bash
cp .env.example .env      # then fill in real values
docker compose up --build
```

The API comes up on `http://localhost:8080`, and MySQL is exposed on host port `3390` (mapped to avoid clashing with a local MySQL on 3306).

### Option B — Run it directly with Gradle

Point it at a MySQL database, set your environment variables, and start it:

```bash
./gradlew bootRun
```

### Configuration

Everything sensitive is read from environment variables (with sane fallbacks in `application.properties`). Copy `.env.example` to `.env` and set at least:

| Variable | What it's for |
|----------|---------------|
| `SPRING_DATASOURCE_URL` | JDBC URL for MySQL |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | DB credentials |
| `JWT_SECRET` | Base64 HMAC secret for signing tokens (e.g. `openssl rand -base64 48`) |
| `JWT_EXPIRATION` | Access-token lifetime in minutes |
| `SERVER_PORT` | Port to run on (defaults to 8080) |

Don't commit your real `.env` — it's git-ignored on purpose.

## Tests & quality

```bash
./gradlew test            # run the test suite (uses H2)
./gradlew jacocoTestReport # coverage report -> build/reports/jacoco
./gradlew sonar            # push analysis to SonarQube (needs a running server)
```

Coverage runs automatically after the tests, and the report feeds straight into SonarQube.

## Deployment

The CI pipeline builds the app, runs tests and Sonar analysis, bakes a Docker image, pushes it to Google Container Registry, and deploys it to **Google Cloud Run** in `europe-north1`. The frontend is deployed separately on Cloud Run as well, which is why CORS in this service is locked to the deployed frontend's origin.

## A note on scope

This is a student project, so a few corners reflect that — CORS origins and a couple of URLs are hard-coded to the deployed environment, and the schema is managed by Hibernate's `ddl-auto=update` rather than migrations. It's built to demonstrate a full-stack auction system end to end, not to run a real sneaker exchange.

---

Built by **Angel Rusev**. Design documents, user stories, and the research report are in the `Documents/` folder.
