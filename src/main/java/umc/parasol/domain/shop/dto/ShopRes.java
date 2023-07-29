package umc.parasol.domain.shop.dto;

import lombok.Builder;
import lombok.Data;
import umc.parasol.domain.image.dto.ImageRes;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ShopRes {

    private Long id;

    private String shopName;

    private String desc;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String roadNameAddress;

    private String openTime;

    private String closeTime;

    private int availableUmbrella;

    private List<ImageRes> image;


}
