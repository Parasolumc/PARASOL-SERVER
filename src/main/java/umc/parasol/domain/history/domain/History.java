package umc.parasol.domain.history.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.umbrella.domain.Umbrella;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "관련 멤버가 설정되어 있어야 합니다.")
    @JoinColumn(name = "member_id")
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "관련 상점이 설정되어 있어야 합니다.")
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "관련 우산이 설정되어 있아야 합니다.")
    @JoinColumn(name = "umbrella_id")
    private Umbrella umbrella;

    @NotNull(message = "가격이 설정되어 있어야 합니다.")
    private Long cost;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "절차가 설정되어 있어야 합니다.")
    private Process process;
}
