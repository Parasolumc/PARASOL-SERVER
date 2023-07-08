package umc.parasol.domain.umbrella.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;
import umc.parasol.domain.shop.domain.Shop;

@Entity
@Getter
@NoArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Umbrella extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "umbrella_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @NotNull(message = "관련 상점이 설정되어 있어야 합니다.")
    private Shop shop;

    @NotNull(message = "이용 가능 여부가 설정되어 있어야 합니다.")
    private Boolean available;
}
