package unlimitedmarketplace.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.annotation.Import;
import unlimitedmarketplace.business.impl.PaymentServiceImpl;
import unlimitedmarketplace.configuration.TestsSecurityConfig;
import unlimitedmarketplace.domain.PaymentRequest;
import unlimitedmarketplace.persistence.repositories.PaymentMethodRepository;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.repositories.TransactionRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.PaymentMethodEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.TransactionEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import(TestsSecurityConfig.class)

  class PaymentServiceImplTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private Random random; // Mocked Random object

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentMethodRepository, userRepository, transactionRepository, productRepository, random);
    }

    @Test
     void testProcessPaymentSuccess() {
        PaymentRequest request = new PaymentRequest();
        request.setCardType("VISA");
        request.setCardNumber("4111111111111111");
        request.setCardName("John Doe");
        request.setExpirationDate("12/25");
        request.setCvv("123");
        request.setUserId(1L);
        request.setProductId(2L);
        request.setAmount(100.0);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(2L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(productRepository.findProductEntityById(anyLong())).thenReturn(productEntity);
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doReturn(true).when(random).nextBoolean(); // Simulate payment success

        String transactionId = paymentService.processPayment(request);

        assertNotNull(transactionId);
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        verify(productRepository, times(1)).saveAndFlush(productEntity);
        assertEquals("PAID", productEntity.getPaymentStatus());
    }

    @Test
     void testProcessPaymentFailure() {
        PaymentRequest request = new PaymentRequest();
        request.setCardType("VISA");
        request.setCardNumber("4111111111111111");
        request.setCardName("John Doe");
        request.setExpirationDate("12/25");
        request.setCvv("123");
        request.setUserId(1L);
        request.setProductId(2L);
        request.setAmount(100.0);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(2L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(productRepository.findProductEntityById(anyLong())).thenReturn(productEntity);
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doReturn(false).when(random).nextBoolean(); // Simulate payment failure

        Exception exception = assertThrows(RuntimeException.class, () -> paymentService.processPayment(request));
        assertEquals("Insufficient funds", exception.getMessage());

        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        verify(productRepository, never()).saveAndFlush(any(ProductEntity.class));
    }


    @Test
     void testAddPaymentMethod() {
        PaymentRequest request = new PaymentRequest();
        request.setCardType("VISA");
        request.setCardNumber("4111111111111111");
        request.setCardName("John Doe");
        request.setExpirationDate("12/25");
        request.setCvv("123");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        PaymentMethodEntity paymentMethodEntity = new PaymentMethodEntity();
        paymentMethodEntity.setCardType("VISA");
        paymentMethodEntity.setCardNumber("**** **** **** 1111");
        paymentMethodEntity.setCardName("John Doe");
        paymentMethodEntity.setExpirationDate("12/25");

        when(paymentMethodRepository.save(any(PaymentMethodEntity.class))).thenReturn(paymentMethodEntity);

        PaymentMethodEntity result = paymentService.addPaymentMethod(1L, request);

        assertNotNull(result);
        assertEquals("VISA", result.getCardType());
        assertEquals("**** **** **** 1111", result.getCardNumber());
        assertEquals("John Doe", result.getCardName());
        assertEquals("12/25", result.getExpirationDate());
        verify(paymentMethodRepository, times(1)).save(any(PaymentMethodEntity.class));
    }

    @Test
     void testGetUserPaymentMethods() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        List<PaymentMethodEntity> paymentMethods = List.of(
                new PaymentMethodEntity(1L,userEntity,"VISA", "**** **** **** 1111", "John Doe", "12/25","fasd123"),
                new PaymentMethodEntity(2L,userEntity,"MASTERCARD", "**** **** **** 2222", "Jane Doe", "11/24","fasd1234")
        );

        when(paymentMethodRepository.findByUserId(anyLong())).thenReturn(paymentMethods);

        List<PaymentMethodEntity> result = paymentService.getUserPaymentMethods(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("VISA", result.get(0).getCardType());
        assertEquals("**** **** **** 1111", result.get(0).getCardNumber());
        assertEquals("John Doe", result.get(0).getCardName());
        assertEquals("12/25", result.get(0).getExpirationDate());
        assertEquals("MASTERCARD", result.get(1).getCardType());
        assertEquals("**** **** **** 2222", result.get(1).getCardNumber());
        assertEquals("Jane Doe", result.get(1).getCardName());
        assertEquals("11/24", result.get(1).getExpirationDate());
        verify(paymentMethodRepository, times(1)).findByUserId(anyLong());
    }
}
