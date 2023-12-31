package umc.parasol.domain.history.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.parasol.domain.history.domain.Process;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Getter
@NoArgsConstructor
public class HistoryRes {
    private String member; // member 이름
    private String fromShop; // 빌린 매장 이름
    private String endShop; // 반환한 매장 이름

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt; // 대여 시각
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime clearedAt; // 종료 시각
    private Process process; // 내역 단계

    @Builder
    public HistoryRes(String member, String fromShop, String endShop, LocalDateTime createdAt,
                      LocalDateTime clearedAt, Process process) {
        this.member = member;
        this.fromShop = fromShop;
        this.endShop = endShop;

        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        this.createdAt = ZonedDateTime.of(createdAt, koreaZoneId).toLocalDateTime();
        this.clearedAt = ZonedDateTime.of(clearedAt, koreaZoneId).toLocalDateTime();

        this.process = process;
    }
}
