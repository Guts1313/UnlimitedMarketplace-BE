package unlimitedmarketplace.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unlimitedmarketplace.persistence.entity.BidEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

public interface BidRepository extends JpaRepository<BidEntity, Long> {
    @Query("SELECT b FROM BidEntity b WHERE b.product.id = :productId ORDER BY b.amount DESC")
    Page<BidEntity> findHighestBidByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT DISTINCT b.user.userName FROM BidEntity b WHERE b.product.id = :productId AND b.user.userName != :latestBidderUsername")
    List<String> findAllBiddersExceptLatest(@Param("productId") Long productId, @Param("latestBidderUsername") String latestBidderUsername);
    @Query("SELECT DISTINCT p FROM BidEntity b JOIN b.product p WHERE b.user.id = :userId")
    List<ProductEntity> findProductsByUserId(@Param("userId") Long userId);
    @Query("SELECT b FROM BidEntity b WHERE b.user.id = :userId ORDER BY b.bidTime DESC")
    List<BidEntity> findBiddedProductsByUserId(@Param("userId") Long userId);

}
