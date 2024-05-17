package unlimitedmarketplace.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
    @Query("SELECT p FROM ProductEntity p WHERE p.productName LIKE :productName")
    List<ProductEntity> findProductEntitiesByProductNameLike(@Param("productName") String productName);

    @Query("SELECT p FROM ProductEntity p WHERE p.user.id = :userId")
    List<ProductEntity> findListedByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM ProductEntity p WHERE p.productStatus = :productStatus")
    List<ProductEntity> findAllByProductStatus(@Param("productStatus") String productStatus);

    @Query("SELECT p FROM ProductEntity p WHERE p.paymentStatus = :paymentStatus")
    List<ProductEntity> findAllByPaymentStatus(@Param("paymentStatus") String paymentStatus);

    @Query("SELECT p FROM ProductEntity p WHERE p.id = :productId")
    ProductEntity findProductEntityById(@Param("productId") Long productId);


}
