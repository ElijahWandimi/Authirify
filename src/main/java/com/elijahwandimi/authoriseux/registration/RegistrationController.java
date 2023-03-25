package com.elijahwandimi.authoriseux.registration;

import com.elijahwandimi.authoriseux.registration.dto.RegistrationRequest;
import com.elijahwandimi.authoriseux.registration.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return  registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
