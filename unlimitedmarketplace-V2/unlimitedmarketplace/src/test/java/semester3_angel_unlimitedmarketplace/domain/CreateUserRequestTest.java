package semester3_angel_unlimitedmarketplace.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import semester3_angel_unlimitedmarketplace.business.CreateUserUseCase;
import semester3_angel_unlimitedmarketplace.business.customexceptions.DuplicateEmailException;
import semester3_angel_unlimitedmarketplace.business.customexceptions.DuplicateUsernameException;
import semester3_angel_unlimitedmarketplace.controllers.UserController;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class CreateUserRequestTest {
    @Mock
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private UserController userController;

    private Validator validator;

    @BeforeEach
    void setup() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        validator = localValidatorFactoryBean;
    }

    @Test
    void testValidCreateUserRequest() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("username")
                .email("email@example.com")
                .passwordHash("password123")
                .role(UserRoles.ADMIN)
                .build();

        Errors errors = new BeanPropertyBindingResult(request, "createUserRequest");
        validator.validate(request, errors);

        assertFalse(errors.hasErrors(), "There should be no validation errors for a valid request");
    }





    @Test
    void testValidationFailsForEmptyUsername() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("")
                .email("email@example.com")
                .passwordHash("password123")
                .role(UserRoles.ADMIN)
                .build();

        Errors errors = new BeanPropertyBindingResult(request, "createUserRequest");
        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("userName"), "There should be an error for empty username");
        assertEquals("Username cannot be empty", errors.getFieldError("userName").getDefaultMessage());
    }

    @Test
    void testValidationFailsForInvalidEmail() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("username")
                .email("invalid-email")
                .passwordHash("password123")
                .role(UserRoles.ADMIN)
                .build();

        Errors errors = new BeanPropertyBindingResult(request, "createUserRequest");
        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("email"), "There should be an error for invalid email");
        assertEquals("Email should be valid", errors.getFieldError("email").getDefaultMessage());
    }

    @Test
    void testValidationFailsForBlankPassword() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("username")
                .email("email@example.com")
                .passwordHash("")
                .role(UserRoles.ADMIN)
                .build();

        Errors errors = new BeanPropertyBindingResult(request, "createUserRequest");
        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("passwordHash"), "There should be an error for blank password");
        assertEquals("Password cannot be blank", errors.getFieldError("passwordHash").getDefaultMessage());
    }

    @Test
    void testValidationFailsForNullRole() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("username")
                .email("email@example.com")
                .passwordHash("password123")
                .role(null)  // Explicitly setting role to null
                .build();

        Errors errors = new BeanPropertyBindingResult(request, "createUserRequest");
        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("role"), "There should be an error for null role");
        assertNotNull(errors.getFieldError("role").getDefaultMessage(), "The error message should not be null");
    }




}