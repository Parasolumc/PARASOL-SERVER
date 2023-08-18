package umc.parasol.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecoveryRes {

    private Long id;
    private String email;

    public static RecoveryRes from(Long id, String email) {
        return new RecoveryRes(id, email);
    }

    private RecoveryRes(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
