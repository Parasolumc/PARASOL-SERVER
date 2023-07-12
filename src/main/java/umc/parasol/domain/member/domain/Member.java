package umc.parasol.domain.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;

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

    //providerId로 대채
    //private Long uuid;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    @NotBlank(message = "관련 이메일이 설정되어 있어야 합니다.")
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "역할이 설정되어 있어야 합니다.")
    private Role role;

    @NotNull(message = "인증 여부가 설정되어 있어야 합니다.")
    private Boolean isVerified;

    @Enumerated(EnumType.STRING)
    private AuthRole authRole;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(unique = true)
    private String providerId;


    // @Setter 대신에 사용하는 업데이트 메소드 //
    public void updateNickname(String nickname){this.nickname = nickname;}

    public void updatePassword(String password){this.password = password;}

    public void updateIsVerified(Boolean isVerified) {this.isVerified = isVerified;}
}
