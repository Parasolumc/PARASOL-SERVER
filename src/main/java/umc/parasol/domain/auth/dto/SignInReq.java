package umc.parasol.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SignInReq {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

}
