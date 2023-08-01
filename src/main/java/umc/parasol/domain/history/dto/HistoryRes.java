package umc.parasol.domain.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.parasol.domain.history.domain.Process;

import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
public class HistoryRes {
    private String member; // member 이름
    private String fromShop; // 빌린 매장 이름
    private String endShop; // 반환한 매장 이름

    private LocalDateTime createdAt; // 대여 시각
    private LocalDateTime clearedAt; // 종료 시각
    private Process process; // 내역 단계

    @Builder
    public HistoryRes(String member, String fromShop, String endShop, LocalDateTime createdAt,
                      LocalDateTime clearedAt, Process process) {
        this.member = member;
        this.fromShop = fromShop;
        this.endShop = endShop;
        this.createdAt = createdAt;
        this.clearedAt = clearedAt;
        this.process = process;
    }
}
