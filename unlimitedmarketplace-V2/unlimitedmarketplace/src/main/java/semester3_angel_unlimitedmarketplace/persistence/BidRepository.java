package semester3_angel_unlimitedmarketplace.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import semester3_angel_unlimitedmarketplace.persistence.entity.BidEntity;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<BidEntity, Long> {
    @Query("SELECT b.user.id FROM BidEntity b WHERE b.product.id = :productId AND b.amount < :latestBidAmount")
    List<Long> findUserIdsOfOutbidUsers(@Param("productId") Long productId, @Param("latestBidAmount") BigDecimal latestBidAmount);
    @Query("SELECT b FROM BidEntity b WHERE b.product.id = :productId ORDER BY b.amount DESC")
    Page<BidEntity> findHighestBidByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT DISTINCT b.user.userName FROM BidEntity b WHERE b.product.id = :productId AND b.user.userName != :latestBidderUsername")
    List<String> findAllBiddersExceptLatest(@Param("productId") Long productId, @Param("latestBidderUsername") String latestBidderUsername);

}
