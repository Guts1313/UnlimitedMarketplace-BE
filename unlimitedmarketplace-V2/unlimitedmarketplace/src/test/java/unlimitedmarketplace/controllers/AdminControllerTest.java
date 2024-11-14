package unlimitedmarketplace.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unlimitedmarketplace.business.interfaces.DeleteUserUseCase;
import unlimitedmarketplace.business.interfaces.GetUsersUseCase;
import unlimitedmarketplace.domain.User;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;
import unlimitedmarketplace.domain.GetAllUsersResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
 class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeleteUserUseCase deleteUserUseCase;

    @MockBean
    private GetUsersUseCase getUsersUseCase;

    @MockBean
    private AccessTokenEncoderDecoderImpl accessTokenEncoderDecoder;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
     void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
     void testGetAllUsers() throws Exception {
        GetAllUsersResponse response = new GetAllUsersResponse();
        response.setAllUsers(List.of(
                new User(1L, "user1", "asd", "user1@example.com", UserRoles.ADMIN),
                new User(2L, "user2", "asd", "user2@example.com", UserRoles.ADMIN)
        ));

        Mockito.when(getUsersUseCase.getAll()).thenReturn(response);

        MvcResult result = mockMvc.perform(get("/adminpanel/users"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response: " + responseBody);

        mockMvc.perform(get("/adminpanel/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allUsers", hasSize(2)))
                .andExpect(jsonPath("$.allUsers[0].id", is(1)))
                .andExpect(jsonPath("$.allUsers[0].userName", is("user1")))
                .andExpect(jsonPath("$.allUsers[0].email", is("user1@example.com")))
                .andExpect(jsonPath("$.allUsers[1].id", is(2)))
                .andExpect(jsonPath("$.allUsers[1].userName", is("user2")))
                .andExpect(jsonPath("$.allUsers[1].email", is("user2@example.com")));
    }




    @Test
    @WithMockUser(roles = "ADMIN")
     void testDeleteUser() throws Exception {
        Mockito.doNothing().when(deleteUserUseCase).deleteUser(anyLong());

        mockMvc.perform(delete("/adminpanel/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
     void testGetAllUsersUnauthorized() throws Exception {
        mockMvc.perform(get("/adminpanel/users"))
                .andExpect(status().isForbidden());
    }

    @Test
     void testDeleteUserUnauthorized() throws Exception {
        mockMvc.perform(delete("/adminpanel/users/1"))
                .andExpect(status().isForbidden());
    }
}
