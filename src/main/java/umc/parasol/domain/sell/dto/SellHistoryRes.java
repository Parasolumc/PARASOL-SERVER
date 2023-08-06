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
    private String createdAt; // 판매 날짜
    private Long umbrellaCount; //우산 개수

    @Builder
    public SellHistoryRes(String sellShop, String createdAt,
                          Long umbrellaCount) {
        this.sellShop = sellShop;
        this.createdAt = createdAt;
        this.umbrellaCount = umbrellaCount;
    }
}
