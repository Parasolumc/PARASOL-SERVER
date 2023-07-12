package umc.parasol.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenReq {

    @NotBlank(message = "Refersh Token을 입력해야 합니다.")
    private String refreshToken;

}
