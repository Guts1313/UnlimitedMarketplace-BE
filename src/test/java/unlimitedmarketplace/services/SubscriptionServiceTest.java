package unlimitedmarketplace.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unlimitedmarketplace.business.impl.SubscriptionService;
import unlimitedmarketplace.persistence.repositories.SubscriptionRepository;
import unlimitedmarketplace.persistence.entity.SubscriptionEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
     void testGetUserSubscriptions() {
        Long userId = 1L;
        Long prodId = 2L;
        List<SubscriptionEntity> mockEntities = Arrays.asList(
                new SubscriptionEntity(prodId,userId, "/topic/product1"),
                new SubscriptionEntity(prodId, userId,"/queue/outbid1")
        );

        when(subscriptionRepository.findByUserId(userId)).thenReturn(mockEntities);

        List<String> subscriptions = subscriptionService.getUserSubscriptions(userId);

        assertEquals(2, subscriptions.size());
        assertEquals("/topic/product1", subscriptions.get(0));
        assertEquals("/queue/outbid1", subscriptions.get(1));
    }

    @Test
     void testGetUserSubscriptionsEmptySecond() {
        Long userId = 2L;

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<String> subscriptions = subscriptionService.getUserSubscriptions(userId);

        assertEquals(0, subscriptions.size());
    }



    @Test
     void testGetUserSubscriptionsEmpty() {
        Long userId = 2L;

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<String> subscriptions = subscriptionService.getUserSubscriptions(userId);

        assertEquals(0, subscriptions.size());
    }



    @Test
     void testGetUserSubscriptionsRepositoryException() {
        Long userId = 4L;

        when(subscriptionRepository.findByUserId(userId)).thenThrow(new RuntimeException("Repository error"));

        try {
            subscriptionService.getUserSubscriptions(userId);
        } catch (RuntimeException e) {
            assertEquals("Repository error", e.getMessage());
        }
    }

}


