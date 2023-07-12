package umc.parasol.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class TokenMapping {

    private String userEmail;

    private String accessToken;

    private String refreshToken;

    @Builder
    public TokenMapping(String userEmail, String accessToken, String refreshToken) {
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
