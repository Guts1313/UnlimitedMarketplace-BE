package unlimitedmarketplace.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import unlimitedmarketplace.business.interfaces.DeleteUserUseCase;
import unlimitedmarketplace.business.interfaces.GetUsersUseCase;
import unlimitedmarketplace.domain.GetAllUsersRequest;
import unlimitedmarketplace.domain.GetAllUsersResponse;

@RestController
@RequestMapping("/adminpanel")
@CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
public class AdminController {
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetUsersUseCase getUsersUseCase;

    public AdminController(DeleteUserUseCase deleteUserUseCase, GetUsersUseCase getUsersUseCase) {
        this.deleteUserUseCase = deleteUserUseCase;
        this.getUsersUseCase = getUsersUseCase;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    public ResponseEntity<GetAllUsersResponse> getAllUsers(GetAllUsersRequest request) {
        GetAllUsersResponse response = getUsersUseCase.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        deleteUserUseCase.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}