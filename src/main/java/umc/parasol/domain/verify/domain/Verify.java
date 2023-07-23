package umc.parasol.domain.verify.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;
import umc.parasol.domain.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Verify extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verify_id")
    private Long id;

    private Integer code;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    private LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(3);

    public boolean checkExpiration(LocalDateTime now) {
        return now.isAfter(expirationTime);
    }
}
