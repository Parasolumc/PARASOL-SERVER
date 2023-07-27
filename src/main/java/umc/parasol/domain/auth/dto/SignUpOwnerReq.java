package umc.parasol.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpOwnerReq {

    @NotBlank(message = "이름을 입력해야 합니다.")
    private String nickname;

    @NotBlank
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    private String password;

    @NotBlank(message = "매장명을 입력해야 합니다.")
    private String shopName;

    @NotNull(message = "위도를 입력해야 합니다.")
    private BigDecimal latitude;

    @NotNull(message = "경도를 입력해야 합니다.")
    private BigDecimal longitude;

    @NotBlank(message = "도로명 주소를 입력해야 합니다.")
    private String roadNameAddress;
}
