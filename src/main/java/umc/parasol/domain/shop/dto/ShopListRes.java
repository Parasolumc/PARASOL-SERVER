package umc.parasol.domain.shop.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopListRes {

    private Long id;

    private String shopName;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String roadNameAddress;

    private String openTime;

    private String closeTime;

    private int availableUmbrella;

    @Builder
    public ShopListRes(Long id, String shopName, BigDecimal latitude, BigDecimal longitude, String roadNameAddress, String openTime, String closeTime, int availableUmbrella) {
        this.id = id;
        this.shopName = shopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.roadNameAddress = roadNameAddress;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.availableUmbrella = availableUmbrella;
    }

}
