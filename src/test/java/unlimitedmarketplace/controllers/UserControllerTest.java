package unlimitedmarketplace.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import unlimitedmarketplace.business.interfaces.CreateUserUseCase;
import unlimitedmarketplace.business.interfaces.DeleteUserUseCase;
import unlimitedmarketplace.business.interfaces.GetUsersUseCase;
import unlimitedmarketplace.business.interfaces.UpdateUserPasswordUseCase;
import unlimitedmarketplace.domain.*;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

 class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUsersUseCase getUsersUseCase;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    @MockBean
    private DeleteUserUseCase deleteUserUseCase;


    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
     void getUsers_ShouldReturnAllUsers() throws Exception {
        List<User> userList = Arrays.asList(new User(1L, "userTest1","userTest1pw", "user1@test.com",UserRoles.ADMIN)); // Include role in constructor
        GetAllUsersResponse response = new GetAllUsersResponse(userList);
        given(getUsersUseCase.getAllUsers(any(GetAllUsersRequest.class))).willReturn(response);

        mockMvc.perform(get("/unlimitedmarketplace").param("userName", "userTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allUsers").isNotEmpty())
                .andExpect(jsonPath("$.allUsers[0].role").value("ADMIN")); // Check if the role is correctly returned
    }



    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})

     void createUser_ShouldReturnCreated() throws Exception {
        CreateUserRequest request = new CreateUserRequest("newUser", "newUser@test.com", "password", UserRoles.USER);
        CreateUserResponse response = new CreateUserResponse(1L, "newUser", "newUser@test.com", UserRoles.USER);

        given(createUserUseCase.saveUser(any(CreateUserRequest.class))).willReturn(response);

        String jsonContent = """
        {
            "userName": "newUser",
            "email": "newUser@test.com",
            "passwordHash": "password",
            "role": "USER"
        }""";

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

     void updateUser_ShouldReturnNoContent() throws Exception {
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest(1L, "newPassword");

        mockMvc.perform(put("/unlimitedmarketplace/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isNoContent());

        verify(updateUserPasswordUseCase, times(1)).updatePassword(any(UpdateUserPasswordRequest.class));
    }

    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})

     void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/unlimitedmarketplace/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteUserUseCase, times(1)).deleteUser(1L);
    }
}
