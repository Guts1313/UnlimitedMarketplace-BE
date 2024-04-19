package semester3_angel_unlimitedmarketplace.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import semester3_angel_unlimitedmarketplace.business.*;
import semester3_angel_unlimitedmarketplace.domain.*;

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

    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @GetMapping("{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable(value = "id") final Long id){
        final GetUserResponse responseOptional = getUserUseCase.getUserById(id);
        return ResponseEntity.ok().body(responseOptional);
    }
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @GetMapping
    public ResponseEntity<GetAllUsersResponse> getUsers(@RequestParam(value = "userName", required = false) String userName) {
        GetAllUsersRequest request = GetAllUsersRequest.builder().userName(userName).build();
        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);
        return ResponseEntity.ok(response);
    }
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request){
        CreateUserResponse response = createUserUseCase.saveUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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

    public ResponseEntity<Void> deleteUser(@PathVariable("id") final long id){
        deleteUserUseCase.deleteUser(id);
       return ResponseEntity.noContent().build();
    }

}
