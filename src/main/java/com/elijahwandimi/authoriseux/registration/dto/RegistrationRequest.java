package com.elijahwandimi.authoriseux.registration.dto;

public record RegistrationRequest(
        String firstName, String lastName, String password, String email
) {
}
