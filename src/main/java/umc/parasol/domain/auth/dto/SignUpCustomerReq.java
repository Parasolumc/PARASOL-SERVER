package umc.parasol.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpCustomerReq {

    @NotBlank(message = "이름을 입력해야 합니다.")
    private String nickname;

    @NotBlank
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    private String password;

    //추후 결제수단..?

}
