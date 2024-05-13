package semester3_angel_unlimitedmarketplace.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
    List<ProductEntity> findProductEntitiesByProductNameLike(String productName);
}
