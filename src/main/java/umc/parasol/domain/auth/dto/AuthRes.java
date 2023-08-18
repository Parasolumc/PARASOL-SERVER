package umc.parasol.domain.auth.dto;

import lombok.Builder;
import lombok.Data;
import umc.parasol.domain.member.domain.Role;

@Data
public class AuthRes {

    private String accessToken;

    private String refreshToken;

    private Role role;

    private Long memberId;


    @Builder
    public AuthRes(String accessToken, String refreshToken, Role role, Long memberId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.memberId = memberId;
    }

}
