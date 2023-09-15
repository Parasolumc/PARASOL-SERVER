package umc.parasol.domain.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;
import umc.parasol.domain.shop.domain.Shop;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    @NotBlank(message = "관련 이메일이 설정되어 있어야 합니다.")
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthRole authRole;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(unique = true)
    private String providerId;

    // 인증 관련 필드
    @NotNull(message = "인증 여부가 설정되어 있어야 합니다.")
    private Boolean isVerified;

    private String name;

    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    // update 메서드
    public void updateNickname(String nickname){this.nickname = nickname;}

    public void updatePassword(String password){this.password = password;}

    public void updateIsVerified(Boolean isVerified) {this.isVerified = isVerified;}

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateRole(String role) {
        this.role = Role.valueOf(role);
    }

    public void updateShop(Shop shop) {
        this.shop = shop;
    }
}
