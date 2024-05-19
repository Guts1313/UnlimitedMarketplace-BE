package unlimitedmarketplace.business.impl;

import unlimitedmarketplace.domain.User;
import unlimitedmarketplace.persistence.entity.UserEntity;

final class UserConverter {
    private UserConverter() {

    }

    public static User convert(UserEntity entity) {
        return User.builder().id(entity.getId()).userName(entity.getUserName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash()).role(entity.getUserRole(entity.getId())).build();
    }
}
