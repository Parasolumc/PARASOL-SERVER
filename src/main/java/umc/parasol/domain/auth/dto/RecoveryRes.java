package umc.parasol.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecoveryRes {

    private String email;
    private String nickname;
    private String phoneNumber;

    public static RecoveryRes from(String email, String nickname, String phoneNumber) {
        return new RecoveryRes(email, nickname, phoneNumber);
    }

    private RecoveryRes(String email, String nickname, String phoneNumber) {
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }
}
