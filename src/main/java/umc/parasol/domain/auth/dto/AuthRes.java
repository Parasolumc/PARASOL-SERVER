package umc.parasol.domain.auth.dto;

import lombok.Builder;
import lombok.Data;
import umc.parasol.domain.member.domain.Role;

@Data
public class AuthRes {

    private String accessToken;

    private String refreshToken;

    private Role role;


    @Builder
    public AuthRes(String accessToken, String refreshToken, Role role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }

}
