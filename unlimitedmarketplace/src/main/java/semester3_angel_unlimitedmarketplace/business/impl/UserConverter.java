package semester3_angel_unlimitedmarketplace.business.impl;

import semester3_angel_unlimitedmarketplace.domain.User;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;

final class UserConverter {
    private UserConverter() {

    }

    public static User convert(UserEntity entity) {
        return User.builder().id(entity.getId()).userName(entity.getUserName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash()).build();
    }
}
