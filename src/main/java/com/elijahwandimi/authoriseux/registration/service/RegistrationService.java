package com.elijahwandimi.authoriseux.registration.service;


import com.elijahwandimi.authoriseux.appuser.model.AppUser;
import com.elijahwandimi.authoriseux.appuser.model.AppUserRole;
import com.elijahwandimi.authoriseux.appuser.service.AppUserService;
import com.elijahwandimi.authoriseux.email.model.EmailSender;
import com.elijahwandimi.authoriseux.registration.dto.RegistrationRequest;
import com.elijahwandimi.authoriseux.registration.token.ConfirmationToken;
import com.elijahwandimi.authoriseux.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    private final String link = "http://localhost:8080/api/v1/registration/confirm?token=";
    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.email());
        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }
        String userToken = appUserService.signUpUser(
                new AppUser(
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.password(),
                        AppUserRole.USER
                )
        );
        emailSender.send(
                request.email(),
                buildEmail(request.firstName(), userToken)
        );
        return userToken;
    }

    private String buildEmail(String firstName, String userToken) {
        return """
<div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
        <span style="display:none;font-size:1px;color:#fff;max-height:0"></span>
            <table role="presentation" width="100%" style="border-collapse:collapse;min-width:100%;width:100%!important" cellpadding="0" cellspacing="0" border="0">
               <tbody><tr>
              <td width="100%" height="53" bgcolor="#0b0c0c">
                <table role="presentation" width="100%" style="border-collapse:collapse;max-width:580px" cellpadding="0" cellspacing="0" border="0" align="center">
                  <tbody><tr>
                    <td width="70" bgcolor="#0b0c0c" valign="middle">
                        <table role="presentation" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse">
                          <tbody><tr>
                            <td style="padding-left:10px">
                            </td>
                            <td style="font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px">
                              <span style="font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block">Confirm your email</span>
                            </td>
                         </tr>
                        </tbody></table>
                      </a>
                    </td>
                  </tr>
                </tbody></table>
              </td>
            </tr>
          </tbody></table>
          <table role="presentation" class="m_-6186904992287805515content" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse;max-width:580px;width:100%!important" width="100%">
              <td width="10" height="10" valign="middle"></td>
              <td>
                        <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse">
                          <tbody><tr>
                            <td bgcolor="#1D70B8" width="100%" height="10"></td>
                          </tr>
                        </tbody></table>
              </td>
              <td width="10" valign="middle" height="10"></td>
            </tr>
          </tbody></table>
          <table role="presentation" class="m_-6186904992287805515content" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse;max-width:580px;width:100%!important" width="100%">
            <tbody><tr>
              <td height="30"><br></td>
            </tr>
            <tr>
              <td width="10" valign="middle"><br></td>
              <td style="font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px">
                    <p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">Hi %s, </p><p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style="Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px"><p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c"> <a href="%s%s">Activate Now</a> </p></blockquote>Link will expire in 15 minutes. <p>See you soon</p>
              </td>
              <td width="10" valign="middle"><br></td>
            </tr>
            <tr>
              <td height="30"><br></td>
            </tr>
          </tbody></table><div class="yj6qo"></div><div class="adL">
        </div></div>
    """.formatted(firstName, link, userToken);
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken  confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail()
        );
        return "Email confirmed";

    }

    public void sendConfirmationEmail(String email) {
        AppUser appUser = appUserService.getAppUser(email);
        String token = appUserService.signUpUser(appUser);
        emailSender.send(
                email,
                buildEmail(appUser.getFirstName(), token)
        );
    }
}

