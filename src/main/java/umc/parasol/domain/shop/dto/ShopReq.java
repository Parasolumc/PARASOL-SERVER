package umc.parasol.domain.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ShopReq {

    private String email; // 점주 email
    private String shopName;
    private String desc;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String roadNameAddress;
    private int availableUmbrella;
}
