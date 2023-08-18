package umc.parasol.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecoveryReq {

    private String phoneNumber;

    public RecoveryReq(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
