package umc.parasol.domain.shop.dto;

import lombok.Builder;
import lombok.Data;
import umc.parasol.domain.timetable.dto.TimeTableRes;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopListRes {

    private Long id;

    private String shopName;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String roadNameAddress;

   private List<TimeTableRes> times;

    private int availableUmbrella;

    private int unavailableUmbrella;

    @Builder
    public ShopListRes(Long id, String shopName, BigDecimal latitude,
                       BigDecimal longitude, String roadNameAddress,
                       List<TimeTableRes> times,
                       int availableUmbrella, int unavailableUmbrella) {
        this.id = id;
        this.shopName = shopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.roadNameAddress = roadNameAddress;
        this.times = times;
        this.availableUmbrella = availableUmbrella;
        this.unavailableUmbrella = unavailableUmbrella;
    }

}
