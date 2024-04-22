package semester3_angel_unlimitedmarketplace.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import semester3_angel_unlimitedmarketplace.business.*;
import semester3_angel_unlimitedmarketplace.business.customexceptions.DuplicateEmailException;
import semester3_angel_unlimitedmarketplace.business.customexceptions.DuplicateUsernameException;
import semester3_angel_unlimitedmarketplace.domain.*;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;

import java.util.Optional;

@RestController
@RequestMapping("/unlimitedmarketplace")
@AllArgsConstructor
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private static final Logger log = LoggerFactory.getLogger(AccessTokenEncoderDecoderImpl.class);

    @PreAuthorize("hasRole('USER')")

    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @GetMapping("{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable(value = "id") final Long id){
        final GetUserResponse responseOptional = getUserUseCase.getUserById(id);
        return ResponseEntity.ok().body(responseOptional);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @GetMapping
    public ResponseEntity<GetAllUsersResponse> getUsers(@RequestParam(value = "userName", required = false) String userName) {
        GetAllUsersRequest request = GetAllUsersRequest.builder().userName(userName).build();
        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authenticated user: " + auth.getName() + " with roles: " + auth.getAuthorities());
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('USER')")
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest request) {
        try {
            CreateUserResponse response = createUserUseCase.saveUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DuplicateUsernameException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        } catch (DuplicateEmailException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }
    @PreAuthorize("hasRole('USER')")
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @PutMapping("{id}")
    public ResponseEntity<Void> updateUser(@PathVariable("id") long id,
                                           @RequestBody @Valid UpdateUserPasswordRequest request){

        request.setId(id);
        updateUserPasswordUseCase.updatePassword(request);
        return ResponseEntity.noContent().build();
    }
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @DeleteMapping("{id}")
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") final long id){
        deleteUserUseCase.deleteUser(id);
       return ResponseEntity.noContent().build();
    }

}
