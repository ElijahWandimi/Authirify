package com.elijahwandimi.authoriseux.appuser.service;

import com.elijahwandimi.authoriseux.appuser.model.AppUser;
import com.elijahwandimi.authoriseux.appuser.repository.UserRepository;
import com.elijahwandimi.authoriseux.registration.service.RegistrationService;
import com.elijahwandimi.authoriseux.registration.token.ConfirmationToken;
import com.elijahwandimi.authoriseux.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private  final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final RegistrationService registrationService;
    private final static String USER_NOT_FOUND_MESSAGE = "User with this email %s does not exist";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, email)));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = userRepository.findByEmail(appUser.getEmail()).isPresent();
        if (userExists) {
            if (!appUser.isEnabled()) {
                registrationService.sendConfirmationEmail(appUser.getEmail());
            }
            throw new IllegalStateException("Email already exists");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        userRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }

    public AppUser getAppUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalStateException("User with this email does not exist"));
    }
}
