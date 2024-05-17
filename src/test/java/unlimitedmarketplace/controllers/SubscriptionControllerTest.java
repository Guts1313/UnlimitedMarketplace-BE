package unlimitedmarketplace.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import unlimitedmarketplace.business.impl.SubscriptionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
     void testGetUserSubscriptions() {
        Long userId = 1L;
        List<String> mockSubscriptions = Arrays.asList("/topic/product1", "/queue/outbid1");

        when(subscriptionService.getUserSubscriptions(userId)).thenReturn(mockSubscriptions);

        ResponseEntity<List<String>> response = subscriptionController.getUserSubscriptions(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockSubscriptions, response.getBody());
    }
   @Test
   void testGetUserSubscriptionsSecond() {
      Long userId = 1L;
      List<String> mockSubscriptions = Arrays.asList("/topic/product1", "/queue/outbid1");

      when(subscriptionService.getUserSubscriptions(userId)).thenReturn(mockSubscriptions);

      ResponseEntity<List<String>> response = subscriptionController.getUserSubscriptions(userId);

      assertEquals(200, response.getStatusCodeValue());
      assertEquals(mockSubscriptions, response.getBody());
   }

   @Test
   void testGetUserSubscriptionsEmpty() {
      Long userId = 2L;

      when(subscriptionService.getUserSubscriptions(userId)).thenReturn(Collections.emptyList());

      ResponseEntity<List<String>> response = subscriptionController.getUserSubscriptions(userId);

      assertEquals(200, response.getStatusCodeValue());
      assertEquals(Collections.emptyList(), response.getBody());
   }


}
