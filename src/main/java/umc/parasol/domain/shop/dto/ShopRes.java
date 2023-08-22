package umc.parasol.domain.shop.dto;

import lombok.Builder;
import lombok.Data;
import umc.parasol.domain.image.dto.ImageRes;
import umc.parasol.domain.timetable.dto.TimeTableRes;

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

    private int availableUmbrella;

    private List<ImageRes> image;

    private List<TimeTableRes> times;
}
