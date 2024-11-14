package unlimitedmarketplace.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import unlimitedmarketplace.persistence.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

}