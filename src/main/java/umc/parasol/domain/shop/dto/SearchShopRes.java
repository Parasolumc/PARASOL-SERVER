package umc.parasol.domain.shop.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SearchShopRes {

    private Long id;

    private String shopName;

    private String roadNameAddress;

    private String distance;

}
