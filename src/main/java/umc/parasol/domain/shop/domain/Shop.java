package umc.parasol.domain.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @NotNull(message = "위도가 설정되어 있어야 합니다.")
    private BigDecimal latitude;

    @NotNull(message = "경도가 설정되어 있어야 합니다.")
    private BigDecimal longitude;

    @NotBlank(message = "도로명 주소를 입력해야 합니다.")
    private String roadNameAddress;

    @NotBlank(message = "지점 이름이 설정되어 있어야 합니다.")
    private String name;

    private String description;

    private String openTime;

    private String closeTime;

    // 매장의 대여 가능한 우산 개수
    private int availableUmbrella;

    // 매장의 대여 중인 우산 개수 (대여 불가)
    private int unavailableUmbrella;

    // update 메서드
    public void updateAvailableUmbrella(int count) {
        this.availableUmbrella = count;
    }

    public void updateUnavailableUmbrella(int count) {
        this.unavailableUmbrella = count;
    }
}
