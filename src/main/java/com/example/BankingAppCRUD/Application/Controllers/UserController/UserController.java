package com.example.BankingAppCRUD.Application.Controllers.UserController;

import com.example.BankingAppCRUD.Application.DTOs.Requests.Account.AccountDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.StatusChangeRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.User.*;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Application.Exceptions.UserAccountNotFoundException;
import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import com.example.BankingAppCRUD.Infrastructure.Service.User.UserServiceImpl;
import com.example.BankingAppCRUD.Infrastructure.Repository.User.UserJPARepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "User account management and profile operations")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userService;
    private final UserJPARepository userRepository;

    public UserController(UserServiceImpl userService, UserJPARepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details", description = "Retrieve detailed information about a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getUserDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting user details for userId: {} [correlationId: {}]", userId, correlationId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserAccountNotFoundException("User not found with ID: " + userId));

            UserDTO userResponse = UserDTO.builder()
                    .firstName(user.getUser_name().getFirst())
                    .lastName(user.getUser_name().getLast())
                    .userName(user.getUsername())
                    .email(user.getUser_email())
                    .address(user.getUser_address())
                    .id(user.getUser_id())
                    .roles(user.getUser_roles())
                    .build();

            logger.info("Successfully retrieved user details for userId: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("User details retrieved successfully")
                    .build());

        } catch (UserAccountNotFoundException e) {
            logger.warn("User not found: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("User not found").build());
        } catch (Exception e) {
            logger.error("Error getting user details for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }


    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> registerUser(
            @RequestBody UserRegistrationRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Processing user registration for username: {} [correlationId: {}]",
                request.getUserName(), correlationId);

        try {
            UserDTO userDTO = UserDTO.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .userName(request.getUserName())
                    .email(request.getEmail())
                    .address(request.getAddress())
                    .password(request.getPassword())
                    .build();

            Response response = userService.register(userDTO);
            logger.info("Successfully registered user: {} [correlationId: {}]", request.getUserName(), correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error registering user: {} [correlationId: {}]", request.getUserName(), correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }


    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete a specific user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Deleting user with userId: {} [correlationId: {}]", userId, correlationId);

        try {
            Response response = userService.deleteUser(userId);
            logger.info("Successfully deleted user: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting user: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{userId}/create-account")
    @Operation(summary = "Create account for user", description = "Create a new bank account for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> createAccount(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestBody AccountCreationRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Creating {} account for userId: {} [correlationId: {}]",
                request.getAccountType(), userId, correlationId);

        try {
            AccountDTO accountDTO = AccountDTO.builder()
                    .accountType(request.getAccountType())
                    .build();

            Response response = userService.createAccount(accountDTO);
            logger.info("Successfully created account for userId: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating account for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @DeleteMapping("/{userId}/accounts/{accountId}")
    @Operation(summary = "Delete user account", description = "Delete a specific account for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteAccount(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Deleting account {} for userId: {} [correlationId: {}]", accountId, userId, correlationId);

        try {
            Response response = userService.deleteAccount(accountId);
            logger.info("Successfully deleted account {} for userId: {} [correlationId: {}]", accountId, userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting account {} for userId: {} [correlationId: {}]", accountId, userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{userId}/accounts")
    @Operation(summary = "Get user accounts", description = "Retrieve all accounts for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<AccountDTO>> getUserAccounts(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting accounts for userId: {} [correlationId: {}]", userId, correlationId);

        try {
            List<AccountDTO> accounts = userService.getAccounts(userId);

            logger.info("Successfully retrieved {} accounts for userId: {} [correlationId: {}]",
                    accounts.size(), userId, correlationId);
            return ResponseEntity.ok(accounts);

        } catch (Exception e) {
            logger.error("Error getting accounts for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{userId}/email")
    @Operation(summary = "Change user email", description = "Change the email address for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> changeEmail(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestBody EmailChangeRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Changing email for userId: {} [correlationId: {}]", userId, correlationId);

        try {
            Response response = userService.changeEmail(request.getNewEmail(), userId);
            logger.info("Successfully changed email for userId: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error changing email for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PutMapping("/{userId}/address")
    @Operation(summary = "Change user address", description = "Change the address for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> changeAddress(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestBody AddressChangeRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Changing address for userId: {} [correlationId: {}]", userId, correlationId);

        try {
            Response response = userService.changeAddress(request.getNewAddress(), userId);
            logger.info("Successfully changed address for userId: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error changing address for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PutMapping("/{userId}/name")
    @Operation(summary = "Change user name", description = "Change the name for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Name changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> changeName(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestBody NameChangeRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Changing name for userId: {} [correlationId: {}]", userId, correlationId);

        try {
            Response response = userService.changeName(userId, request.getFirstName(), request.getLastName());
            logger.info("Successfully changed name for userId: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error changing name for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PutMapping("/{userId}/role")
    @Operation(summary = "Change user role", description = "Change the role for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> changeRole(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestBody RoleChangeRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Changing role for userId: {} to {} [correlationId: {}]",
                userId, request.getNewRole(), correlationId);

        try {
            Response response = userService.setRole(request.getNewRole(), userId);
            logger.info("Successfully changed role for userId: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error changing role for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "Change user status", description = "Change the status for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> changeStatus(
            @Parameter(description = "User ID", required = true)
            @PathVariable UUID userId,
            @RequestBody StatusChangeRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Changing status for userId: {} to {} [correlationId: {}]",
                userId, request.getNewStatus(), correlationId);

        try {
            Response response = userService.changeStatus(userId, request.getNewStatus());
            logger.info("Successfully changed status for userId: {} [correlationId: {}]", userId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error changing status for userId: {} [correlationId: {}]", userId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve user information by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getUserByUsername(
            @Parameter(description = "Username", required = true)
            @PathVariable String username,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting user by username: {} [correlationId: {}]", username, correlationId);

        try {
            userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserAccountNotFoundException("User not found with username: " + username));

            logger.info("Successfully found user by username: {} [correlationId: {}]", username, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("User found successfully")
                    .build());

        } catch (UserAccountNotFoundException e) {
            logger.warn("User not found with username: {} [correlationId: {}]", username, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("User not found").build());
        } catch (Exception e) {
            logger.error("Error finding user by username: {} [correlationId: {}]", username, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }


}
