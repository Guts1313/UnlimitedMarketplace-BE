package unlimitedmarketplace.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import unlimitedmarketplace.business.exceptions.DuplicateEmailException;
import unlimitedmarketplace.business.exceptions.DuplicateUsernameException;
import unlimitedmarketplace.business.interfaces.*;
import unlimitedmarketplace.domain.*;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;


@RestController
@RequestMapping("/unlimitedmarketplace")
@CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
@AllArgsConstructor
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @GetMapping("{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable(value = "id") final Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the authenticated user's username from the UserDetails
            String authenticatedUsername = userDetails.getUsername();
            log.info("Authenticated username: {}", authenticatedUsername);
            // Find the authenticated user entity using the username
            UserEntity authenticatedUser = userService.findByUsername(authenticatedUsername);
            List<SimpleGrantedAuthority> authorities = userService.getAuthoritiesByUsername(authenticatedUsername);
            log.info("Authenticated user: {}", authenticatedUser);
            log.info("Authorities of user: {}", authorities.get(0));

            // Check if the authenticated user's ID matches the requested user ID
            if (!authenticatedUser.getId().equals(id)) {
                // If IDs don't match, return a forbidden response
                log.info("authenticated user's id is: {}", authenticatedUser.getId());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            // Proceed with fetching the user details
            final GetUserResponse responseOptional = getUserUseCase.getUserById(id);
            return ResponseEntity.ok().body(responseOptional);
        } catch (Exception e) {
            log.info("Error fetching user details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @GetMapping
    public ResponseEntity<GetAllUsersResponse> getUsers(@RequestParam(value = "userName", required = false) String userName) {
        GetAllUsersRequest request = GetAllUsersRequest.builder().userName(userName).build();
        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authenticated user: {}", auth.getName() + " with roles: " + auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        try {
            CreateUserResponse response = createUserUseCase.saveUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DuplicateUsernameException | DuplicateEmailException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Void> updateUser(@PathVariable(value = "id") long id,
                                           @RequestBody @Valid UpdateUserPasswordRequest request) {

        request.setId(id);
        updateUserPasswordUseCase.updatePassword(request);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable("id") final long id) {
        deleteUserUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
