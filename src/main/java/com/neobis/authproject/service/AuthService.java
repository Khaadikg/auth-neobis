package com.neobis.authproject.service;

import com.neobis.authproject.entity.User;
import com.neobis.authproject.entity.dto.request.RegistrationRequest;
import com.neobis.authproject.entity.enums.Role;
import com.neobis.authproject.entity.enums.UserState;
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

    public String registration(RegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("User with username = " + request.getEmail() + " already exist");
        }
        String UUID = java.util.UUID.randomUUID().toString();
        String mailText = "Please click to link in below to finish registration!";
        sendSimpleMessage(request.getEmail(), request.getLink(), UUID, mailText);
        userRepository.save(mapUserRequestToUser(request, UUID));
        return "User successfully saved!";
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

    private User mapUserRequestToUser(RegistrationRequest request, String UUID) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .role(Role.USER)
                .UUID(UUID)
                .UUIDExpirationDate(LocalDateTime.now().plusMinutes(5))
                .state(UserState.DISABLED)
                .password(encoder.encode(request.getPassword()))
                .build();
    }

    public void sendSimpleMessage(String email, String link, String uuid, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mail);
        message.setSubject("Lorby registration!");
        message.setTo(email);
        message.setText(text + "\n" + link + "?token=" + uuid);
        javaMailSender.send(message);
    }
}