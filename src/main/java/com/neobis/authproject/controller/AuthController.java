package com.neobis.authproject.controller;

import com.neobis.authproject.entity.dto.request.LoginRequest;
import com.neobis.authproject.entity.dto.request.RegistrationRequest;
import com.neobis.authproject.entity.dto.response.LoginResponse;
import com.neobis.authproject.exception.reponse.ExceptionResponse;
import com.neobis.authproject.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://neobis-front-authorization.vercel.app", "http://localhost:5173"})
@Tag(name = "Auth controller", description = "Uses for logic upon registration and authentication(login)")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    @Operation(summary = "Registration", description = "Saves user but NOT activate account",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "string"),
                            responseCode = "200", description = "Good"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json"),
                            responseCode = "302", description = "User already exist exception"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "406", description = "Validation exception")
            }
    )
    public String registration(@RequestBody @Valid RegistrationRequest request) {
        return authService.registration(request);
    }

    @PutMapping("/ensure-registration")
    @Operation(summary = "Registration ensure", description = "Activates user account by uniq token, if token EXPIRED deletes user account to re-register",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "string"),
                            responseCode = "200", description = "Good"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "400", description = "Token expired exception"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "404", description = "Finding user by TOKEN not found exception")
            }
    )
    public String ensureRegistration(@Parameter(description = "The token that comes from backend", required = true) @RequestParam(name = "token") @NotBlank String token) {
        return authService.ensureRegistration(token);
    }

    @PutMapping("/send-message")
    @Operation(summary = "Registration send message", description = "Sends new token, uses REGISTRATION json, user must be already saved via sign-up",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "string"),
                            responseCode = "200", description = "Good"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "406", description = "Validation exception"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "404", description = "User not found with such email exception")
            }
    )
    public String resendMessage(@RequestBody @Valid RegistrationRequest request,
                                @Parameter(description = "The link to frontend service", required = true)@RequestParam(name = "link") String link) {
        return authService.sendMessage(request, link);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Login", description = "Implements login logic for ACTIVATED only users",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)),
                            responseCode = "200", description = "Good"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "406", description = "Validation exception"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "400", description = "Incorrect login exception"),
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)),
                            responseCode = "404", description = "User not found exception")
            }
    )
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}
