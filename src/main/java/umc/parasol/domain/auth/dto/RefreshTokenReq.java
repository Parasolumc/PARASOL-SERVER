package umc.parasol.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class RefreshTokenReq {

    @NotBlank(message = "Refersh Token을 입력해야 합니다.")
    private String refreshToken;

    public RefreshTokenReq(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
