package semester3_angel_unlimitedmarketplace.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import semester3_angel_unlimitedmarketplace.business.*;
import semester3_angel_unlimitedmarketplace.controllers.UserController;
import semester3_angel_unlimitedmarketplace.domain.*;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Ensure this profile configures the necessary beans and settings for tests

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUserUseCase getUserUseCase;

    @MockBean
    private GetUsersUseCase getUsersUseCase;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    @MockBean
    private DeleteUserUseCase deleteUserUseCase;

    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})

    public void getUser_ShouldReturnUser() throws Exception {
        // Arrange
        GetUserResponse response = new GetUserResponse(1L, "userTest", "user@test.com");
        given(getUserUseCase.getUserById(1L)).willReturn(response);

        // Act & Assert
        mockMvc.perform(get("/unlimitedmarketplace/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("userTest"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void getUsers_ShouldReturnAllUsers() throws Exception {
        // Arrange
        List<User> userList = Arrays.asList(new User(1L, "userTest1","userTest1pw", "user1@test.com",UserRoles.ADMIN)); // Include role in constructor
        GetAllUsersResponse response = new GetAllUsersResponse(userList);
        given(getUsersUseCase.getAllUsers(any(GetAllUsersRequest.class))).willReturn(response);

        // Act & Assert
        mockMvc.perform(get("/unlimitedmarketplace").param("userName", "userTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allUsers").isNotEmpty())
                .andExpect(jsonPath("$.allUsers[0].role").value("ADMIN")); // Check if the role is correctly returned
    }



    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})

    public void createUser_ShouldReturnCreated() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("newUser", "newUser@test.com", "password", UserRoles.USER);
        CreateUserResponse response = new CreateUserResponse(1L, "newUser", "newUser@test.com", UserRoles.USER);

        // Correctly setup Mockito to expect any valid CreateUserRequest and return the prepared response
        given(createUserUseCase.saveUser(any(CreateUserRequest.class))).willReturn(response);

        // Prepare JSON content matching the structure of CreateUserRequest
        String jsonContent = """
        {
            "userName": "newUser",
            "email": "newUser@test.com",
            "passwordHash": "password",
            "role": "USER"
        }""";

        // Act & Assert
        mockMvc.perform(post("/unlimitedmarketplace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("newUser"))
                .andExpect(jsonPath("$.email").value("newUser@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));  // Ensure the role is also verified if it is part of the response

    }


    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})

    public void updateUser_ShouldReturnNoContent() throws Exception {
        // Arrange
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest(1L, "newPassword");

        // Act & Assert
        mockMvc.perform(put("/unlimitedmarketplace/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isNoContent());

        verify(updateUserPasswordUseCase, times(1)).updatePassword(any(UpdateUserPasswordRequest.class));
    }

    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})

    public void deleteUser_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/unlimitedmarketplace/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteUserUseCase, times(1)).deleteUser(1L);
    }
}
