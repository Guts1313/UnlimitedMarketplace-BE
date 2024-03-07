package semester3_angel_unlimitedmarketplace.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semester3_angel_unlimitedmarketplace.business.CreateUserUseCase;
import semester3_angel_unlimitedmarketplace.business.GetUserUseCase;
import semester3_angel_unlimitedmarketplace.business.GetUsersUseCase;
import semester3_angel_unlimitedmarketplace.business.UpdateUserPasswordUseCase;
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


    @GetMapping("{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable(value = "id") final Long id){
        final Optional<GetUserResponse> responseOptional = getUserUseCase.getUserById(id);
        return ResponseEntity.ok().body(responseOptional.get());
    }
    @GetMapping
    public ResponseEntity<GetAllUsersResponse> getUsers(@RequestParam(value = "userName", required = false) String userName) {
        GetAllUsersRequest request = GetAllUsersRequest.builder().userName(userName).build();
        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request){
        CreateUserResponse response = createUserUseCase.saveUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("{id}")
    public ResponseEntity<Void> updateUser(@PathVariable("id") long id,
                                           @RequestBody @Valid UpdateUserPasswordRequest request){

        request.setId(id);
        updateUserPasswordUseCase.updatePassword(request);
        return ResponseEntity.noContent().build();
    }

}
