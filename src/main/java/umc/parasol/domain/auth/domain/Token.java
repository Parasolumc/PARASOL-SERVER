package umc.parasol.domain.auth.domain;

import umc.parasol.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(name="token")
@Entity
public class Token extends BaseEntity {

    @Id
    @Column(name = "user_email", length = 767 , nullable = false)
    private String userEmail;

    @Column(name = "refresh_token", length = 767 , nullable = false)
    private String refreshToken;

    public Token(){}

    public Token updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    @Builder
    public Token(String userEmail, String refreshToken) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }
}
