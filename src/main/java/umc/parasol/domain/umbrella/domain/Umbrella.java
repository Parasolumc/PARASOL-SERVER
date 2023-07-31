package umc.parasol.domain.umbrella.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;
import umc.parasol.domain.shop.domain.Shop;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Umbrella extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "umbrella_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private boolean available;

    public static final int MAX = 10;

    public static Umbrella createUmbrella(Shop shop) {
        Umbrella newUmbrella = new Umbrella();
        newUmbrella.updateShop(shop);
        newUmbrella.updateAvailable(true);
        return newUmbrella;
    }

    public void updateShop(Shop shop) {
        this.shop = shop;
    }

    public void updateAvailable(boolean available) {
        this.available = available;
    }
}
