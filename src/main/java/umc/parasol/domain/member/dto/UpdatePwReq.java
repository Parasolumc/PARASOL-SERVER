package umc.parasol.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePwReq {

    @NotBlank(message = "현재 비밀번호를 입력해야 합니다.")
    private String oldPw;

    @NotBlank(message = "새 비밀번호를 입력해야 합니다.")
    private String newPw;

    @NotBlank(message = "refresh token을 입력해야 합니다.")
    private String refreshToken;
}
