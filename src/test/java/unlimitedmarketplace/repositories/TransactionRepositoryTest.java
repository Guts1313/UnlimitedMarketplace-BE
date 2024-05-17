package unlimitedmarketplace.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unlimitedmarketplace.persistence.repositories.TransactionRepository;
import unlimitedmarketplace.persistence.entity.TransactionEntity;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
 class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private TransactionEntity transaction;

    @BeforeEach
    public void setUp() {
        transaction = TransactionEntity.builder()
                .amount(100.0)
                .status("SUCCESS")
                .build();

        transactionRepository.save(transaction);
    }

    @Test
     void testFindAll() {
        List<TransactionEntity> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getStatus()).isEqualTo("SUCCESS");
    }

    @Test
     void testFindById() {
        TransactionEntity foundTransaction = transactionRepository.findById(transaction.getId()).orElse(null);
        assertThat(foundTransaction).isNotNull();
        assertThat(foundTransaction.getAmount()).isEqualTo(100.0);
    }
}
