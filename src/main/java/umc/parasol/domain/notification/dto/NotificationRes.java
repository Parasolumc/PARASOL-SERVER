package umc.parasol.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import umc.parasol.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationRes {
    private Long id;
    private String content; // 알림 내용
  
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime sentTime;
    private Long recipientId;
    private Long shopId;
    private NotificationType type;
    private String shopName;  // 매장 이름
}
