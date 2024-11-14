package unlimitedmarketplace.services;

import org.junit.jupiter.api.Test;
import unlimitedmarketplace.business.impl.UserConverter;
import unlimitedmarketplace.domain.User;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class UserConverterTest {

    @Test
     void testConvert() {
        // Arrange
        Long userId = 1L;
        String userName = "testUser";
        String email = "test@example.com";
        String passwordHash = "hashedPassword";
        UserRoles role = UserRoles.USER;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserName(userName);
        userEntity.setEmail(email);
        userEntity.setPasswordHash(passwordHash);
        userEntity.setUserRole(role);

        // Act
        User user = UserConverter.convert(userEntity);

        // Assert
        assertEquals(userId, user.getId());
        assertEquals(userName, user.getUserName());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(role, user.getRole());
    }
}
