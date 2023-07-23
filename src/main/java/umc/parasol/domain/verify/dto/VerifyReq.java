package umc.parasol.domain.verify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyReq {

    @NotBlank(message = "이름을 입력해야 합니다.")
    private String name;

    @NotBlank(message = "관련 이메일이 설정되어 있어야 합니다.")
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호의 양식과 맞지 않습니다.")
    private String phoneNumber;

}
