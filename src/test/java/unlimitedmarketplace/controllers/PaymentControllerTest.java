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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unlimitedmarketplace.business.interfaces.PaymentService;
import unlimitedmarketplace.domain.PaymentRequest;
import unlimitedmarketplace.domain.UserService;
import unlimitedmarketplace.persistence.entity.PaymentMethodEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
 class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
     void testAddPaymentMethod() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCardType("VISA");
        paymentRequest.setCardNumber("4111111111111111");
        paymentRequest.setCardName("John Doe");
        paymentRequest.setExpirationDate("12/25");
        paymentRequest.setCvv("123");

        PaymentMethodEntity paymentMethodEntity = new PaymentMethodEntity();
        paymentMethodEntity.setCardType("VISA");
        paymentMethodEntity.setCardNumber("**** **** **** 1111");
        paymentMethodEntity.setCardName("John Doe");
        paymentMethodEntity.setExpirationDate("12/25");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Mockito.when(userService.findByUsername(any())).thenReturn(userEntity);
        Mockito.when(paymentService.addPaymentMethod(anyLong(), any(PaymentRequest.class))).thenReturn(paymentMethodEntity);

        mockMvc.perform(post("/payments/add")
                        .contentType("application/json")
                        .content("{\"cardType\":\"VISA\",\"cardNumber\":\"4111111111111111\",\"cardName\":\"John Doe\",\"expirationDate\":\"12/25\",\"cvv\":\"123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardType").value("VISA"))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 1111"))
                .andExpect(jsonPath("$.cardName").value("John Doe"))
                .andExpect(jsonPath("$.expirationDate").value("12/25"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
     void testGetPaymentMethods() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        List<PaymentMethodEntity> paymentMethods = List.of(
                new PaymentMethodEntity(1L,userEntity,"VISA", "**** **** **** 1111", "John Doe", "12/25","fassada31s41"),
                new PaymentMethodEntity(2L,userEntity,"MASTERCARD", "**** **** **** 2222", "Jane Doe", "11/24","fasdas21a")
        );

        Mockito.when(userService.findByUsername(any())).thenReturn(userEntity);
        Mockito.when(paymentService.getUserPaymentMethods(anyLong())).thenReturn(paymentMethods);

        mockMvc.perform(get("/payments/listpaymentoptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardType").value("VISA"))
                .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 1111"))
                .andExpect(jsonPath("$[0].cardName").value("John Doe"))
                .andExpect(jsonPath("$[0].expirationDate").value("12/25"))
                .andExpect(jsonPath("$[1].cardType").value("MASTERCARD"))
                .andExpect(jsonPath("$[1].cardNumber").value("**** **** **** 2222"))
                .andExpect(jsonPath("$[1].cardName").value("Jane Doe"))
                .andExpect(jsonPath("$[1].expirationDate").value("11/24"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
     void testProcessPayment() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCardType("VISA");
        paymentRequest.setCardNumber("4111111111111111");
        paymentRequest.setCardName("John Doe");
        paymentRequest.setExpirationDate("12/25");
        paymentRequest.setCvv("123");
        paymentRequest.setUserId(1L);
        paymentRequest.setProductId(2L);
        paymentRequest.setAmount(100.0);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Mockito.when(userService.findByUsername(any())).thenReturn(userEntity);
        Mockito.when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn("GG");

        mockMvc.perform(post("/payments/process")
                        .contentType("application/json")
                        .content("{\"cardType\":\"VISA\",\"cardNumber\":\"4111111111111111\",\"cardName\":\"John Doe\",\"expirationDate\":\"12/25\",\"cvv\":\"123\",\"userId\":1,\"productId\":2,\"amount\":100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionStatus").value("GG"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
     void testProcessPaymentFailure() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCardType("VISA");
        paymentRequest.setCardNumber("4111111111111111");
        paymentRequest.setCardName("John Doe");
        paymentRequest.setExpirationDate("12/25");
        paymentRequest.setCvv("123");
        paymentRequest.setUserId(1L);
        paymentRequest.setProductId(2L);
        paymentRequest.setAmount(100.0);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Mockito.when(userService.findByUsername(any())).thenReturn(userEntity);
        Mockito.when(paymentService.processPayment(any(PaymentRequest.class))).thenThrow(new RuntimeException("Insufficient funds"));

        mockMvc.perform(post("/payments/process")
                        .contentType("application/json")
                        .content("{\"cardType\":\"VISA\",\"cardNumber\":\"4111111111111111\",\"cardName\":\"John Doe\",\"expirationDate\":\"12/25\",\"cvv\":\"123\",\"userId\":1,\"productId\":2,\"amount\":100.0}"))
                .andExpect(status().isPaymentRequired());
    }
}
