package umc.parasol.domain.verify.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckReq {
    private int code;
    private String phoneNumber;
}
