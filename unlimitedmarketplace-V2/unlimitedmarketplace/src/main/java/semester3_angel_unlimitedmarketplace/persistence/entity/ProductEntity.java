package semester3_angel_unlimitedmarketplace.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="app_products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,name = "product_name")
    private String productName;

    @Column(unique = false,name = "product_price")
    private double productPrice;

    @Column(unique = false,name = "product_url")
    private String productUrl;

    @Column(unique = false,name = "date_added")
    private String productDateAdded;
}
