package semester3_angel_unlimitedmarketplace;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import semester3_angel_unlimitedmarketplace.business.*;
import semester3_angel_unlimitedmarketplace.business.customexceptions.DuplicateUsernameException;
import semester3_angel_unlimitedmarketplace.configuration.TestsSecurityConfig;
import semester3_angel_unlimitedmarketplace.controllers.UserController;
import semester3_angel_unlimitedmarketplace.domain.CreateUserRequest;
import semester3_angel_unlimitedmarketplace.domain.CreateUserResponse;
import semester3_angel_unlimitedmarketplace.domain.GetUserResponse;
import semester3_angel_unlimitedmarketplace.domain.UpdateUserPasswordRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(TestsSecurityConfig.class) // Add this line to force-load  TestSecurityConfig

@WebMvcTest(UserController.class)
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
    public void getUser_ShouldReturnUser() throws Exception {
        GetUserResponse response = new GetUserResponse(1L, "userTest", "user@test.com");
        given(getUserUseCase.getUserById(1L)).willReturn(response);

        mockMvc.perform(get("/unlimitedmarketplace/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("userTest"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    public void createUser_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        // Prepare the request and response objects
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("newUser")
                .email("newUser@test.com")
                .passwordHash("password")
                .build();

        CreateUserResponse response = new CreateUserResponse(1L, "newUser", "newUser@test.com");

        // Mock the behavior of the use case to return the expected response
        given(createUserUseCase.saveUser(any(CreateUserRequest.class))).willReturn(response);

        // Perform the POST request and expect the correct status and response body
        mockMvc.perform(post("/unlimitedmarketplace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"newUser\",\"email\":\"newUser@test.com\",\"passwordHash\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("newUser"))
                .andExpect(jsonPath("$.email").value("newUser@test.com"));
    }
    @Test
    public void createUser_ShouldFail400_WhenUsernameAlreadyExists() throws Exception {
        // Simulate the scenario where the username is already taken
        given(createUserUseCase.saveUser(any(CreateUserRequest.class)))
                .willThrow(new DuplicateUsernameException(HttpStatus.BAD_REQUEST));

        // Perform the POST request with a request body that would trigger the duplicate username situation
        mockMvc.perform(post("/unlimitedmarketplace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"newUser\",\"email\":\"newUser@test.com\",\"passwordHash\":\"password\"}"))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request status

    }




    @Test
    public void updateUser_ShouldReturnNoContent() throws Exception {
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest(1L, "newPassword");

        mockMvc.perform(put("/unlimitedmarketplace/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isNoContent());

        // Here, you might also want to verify updateUserPasswordUseCase.updatePassword was called
    }

    @Test
    public void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/unlimitedmarketplace/1"))
                .andExpect(status().isNoContent());

        // Similarly, you might want to verify deleteUserUseCase.deleteUser was called with the correct ID
    }
}
