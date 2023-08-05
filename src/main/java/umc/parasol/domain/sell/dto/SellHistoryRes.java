package umc.parasol.domain.sell.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
public class SellHistoryRes {
    private String sellShop; // 판매한 매장 이름
    private LocalDateTime createdAt; // 판매 시각
    private Long umbrellaCount;

    @Builder
    public SellHistoryRes(String sellShop, LocalDateTime createdAt,
                          Long umbrellaCount) {
        this.sellShop = sellShop;
        this.createdAt = createdAt;
        this.umbrellaCount = umbrellaCount;
    }
}
