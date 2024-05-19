package unlimitedmarketplace.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import unlimitedmarketplace.business.CreateUserUseCase;
import unlimitedmarketplace.business.exceptions.DuplicateEmailException;
import unlimitedmarketplace.business.exceptions.DuplicateUsernameException;
import unlimitedmarketplace.controllers.UserController;

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
    void testCreateUserInternalServerError() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("username", "email@example.com", "password123", UserRoles.ADMIN);
        when(createUserUseCase.saveUser(any())).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());  // Assuming no body is returned on errors
    }
    @Test
    void testCreateUserDuplicateEmail() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("username", "duplicate@example.com", "password123", UserRoles.ADMIN);
        when(createUserUseCase.saveUser(any())).thenThrow(new DuplicateEmailException());

        // Act
        ResponseEntity<?> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
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


    @Test
    void testCreateUserWithExistingUsername() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("existingUser")
                .email("newemail@example.com")
                .passwordHash("password123")
                .role(UserRoles.ADMIN)
                .build();

        when(createUserUseCase.saveUser(any())).thenThrow(new DuplicateUsernameException(HttpStatus.BAD_REQUEST));

        ResponseEntity<CreateUserResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void testSuccessfulUserCreation() {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("newUser")
                .email("newemail@example.com")
                .passwordHash("password123")
                .role(UserRoles.ADMIN)
                .build();

        CreateUserResponse expectedResponse = new CreateUserResponse(1L, "newUser", "newemail@example.com", UserRoles.ADMIN);

        when(createUserUseCase.saveUser(any())).thenReturn(expectedResponse);

        ResponseEntity<CreateUserResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
    @Test
    void testDatabaseErrorDuringUserCreation() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("username")
                .email("email@example.com")
                .passwordHash("password123")
                .role(UserRoles.ADMIN)
                .build();

        when(createUserUseCase.saveUser(any())).thenThrow(new DataAccessException("Database error") {});

        ResponseEntity<CreateUserResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    @Test
    void testInvalidUserRole() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("username")
                .email("email@example.com")
                .passwordHash("password123")
                .role(null)
                .build();

        Errors errors = new BeanPropertyBindingResult(request, "createUserRequest");
        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("role"), "There should be an error for invalid role");
    }
    @Test
    void testAllFieldsNull() {
        CreateUserRequest request = new CreateUserRequest(null, null, null, null);

        Errors errors = new BeanPropertyBindingResult(request, "createUserRequest");
        validator.validate(request, errors);

        assertTrue(errors.hasErrors(), "There should be errors for all fields being null");
        assertTrue(errors.getFieldError("userName") != null);
        assertTrue(errors.getFieldError("email") != null);
        assertTrue(errors.getFieldError("passwordHash") != null);
        assertTrue(errors.getFieldError("role") != null);
    }


}