package com.neobis.authproject.service;

import com.neobis.authproject.entity.User;
import com.neobis.authproject.entity.dto.request.LoginRequest;
import com.neobis.authproject.entity.dto.request.RegistrationRequest;
import com.neobis.authproject.entity.enums.Role;
import com.neobis.authproject.entity.enums.UserState;
import com.neobis.authproject.exception.IncorrectLoginException;
import com.neobis.authproject.exception.NotFoundException;
import com.neobis.authproject.exception.RegistrationTokenExpiredException;
import com.neobis.authproject.exception.UserAlreadyExistException;
import com.neobis.authproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${gmail}")
    private String mail;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    String mailText = "Please click to link in below to finish registration!";

    public String registration(RegistrationRequest request) {
        if (userRepository.findByUniqConstraint(request.getUsername(), request.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("User with username = " + request.getEmail() + " already exist");
        }
        userRepository.save(mapUserRequestToUser(request));
        return "User successfully saved!";
    }

    public String sendMessage(RegistrationRequest request, String link) {
        User user = userRepository.findByUniqConstraint(request.getUsername(), request.getEmail()).orElseThrow(
                () -> new NotFoundException("User with email = " + request.getEmail() + " not exist")
        );
        String UUID = java.util.UUID.randomUUID().toString();
        sendSimpleMessage(request.getEmail(), link, UUID);
        user.setUUIDExpirationDate(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        return "Письмо отправлено на почту " + request.getEmail();
    }

    public String sendMessage_dev(RegistrationRequest request, String link) {
        User user = userRepository.findByUniqConstraint(request.getUsername(), request.getEmail()).orElseThrow(
                () -> new NotFoundException("User with email = " + request.getEmail() + " not exist")
        );
        String UUID = java.util.UUID.randomUUID().toString();
        user.setUUIDExpirationDate(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        return link + "?token=" + UUID; // disable for prod
    }
    public String ensureRegistration(String UUID) {
        User user = userRepository.findByUUID(UUID).orElseThrow(
                () -> new NotFoundException("User is not found by UUID = " + UUID)
        );
        if (user.getUUIDExpirationDate().isBefore(LocalDateTime.now())) {
            userRepository.delete(user);
            throw new RegistrationTokenExpiredException("Your registration token got expired!");
        }
        user.setState(UserState.ACTIVATED);
        user.setUUID(null);
        user.setUUIDExpirationDate(null);
        userRepository.save(user);
        return "User account successfully activated";
    }

    public String login(LoginRequest loginRequest) {
        User existUser = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found by username = " + loginRequest.getUsername()));
        if (encoder.matches(loginRequest.getPassword(), existUser.getPassword()) && existUser.getState() == UserState.ACTIVATED) {
            return "Welcome back!";
        } else {
            throw new IncorrectLoginException("Password is not correct or Access denied! You are not registered");
        }
    }

    private User mapUserRequestToUser(RegistrationRequest request) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .role(Role.USER)
                .UUIDExpirationDate(LocalDateTime.now().plusMinutes(5))
                .state(UserState.DISABLED)
                .password(encoder.encode(request.getPassword()))
                .build();
    }

    public void sendSimpleMessage(String email, String link, String uuid) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mail);
        message.setSubject("Lorby registration!");
        message.setTo(email);
        message.setText(mailText + "\n" + link + "?token=" + uuid);
        javaMailSender.send(message);
    }
}