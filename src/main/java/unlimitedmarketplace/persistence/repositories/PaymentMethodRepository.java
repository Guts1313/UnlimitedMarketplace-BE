package unlimitedmarketplace.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import unlimitedmarketplace.persistence.entity.PaymentMethodEntity;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {
    List<PaymentMethodEntity> findByUserId(Long userId);
}