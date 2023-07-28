package umc.parasol.domain.image.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @NotBlank(message = "관련 파일 url이 설정되어 있어야 합니다.")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @NotNull(message = "관련 상점이 설정되어 있어야 합니다.")
    private Shop shop;

    public Image(String url, Shop shop) {
        this.url = url;
        this.shop = shop;
    }
}
