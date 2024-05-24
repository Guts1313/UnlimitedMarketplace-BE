package unlimitedmarketplace.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unlimitedmarketplace.persistence.SubscriptionRepository;
import unlimitedmarketplace.persistence.entity.SubscriptionEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<String> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(SubscriptionEntity::getChannel)
                .collect(Collectors.toList());
    }

    public void addUserSubscription(Long userId, String channel) {
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setUserId(userId);
        subscription.setChannel(channel);
        subscriptionRepository.save(subscription);
    }
}
