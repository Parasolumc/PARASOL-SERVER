package umc.parasol.domain.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchShopReq {

    @NotBlank(message = "검색어를 입력해야 합니다.")
    private String keyword;

    @NotNull(message = "사용자의 위도를 입력해야 합니다.")
    private BigDecimal userLatitude;

    @NotNull(message = "사용자의 경도를 입력해야 합니다.")
    private BigDecimal userLongitude;
}
