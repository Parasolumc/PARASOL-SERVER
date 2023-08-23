package umc.parasol.domain.notification.dto;

import lombok.Builder;
import lombok.Data;
import umc.parasol.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationRes {
    private Long id; // 알림 ID

    private String shopName;  // 매장 이름

    private String content; // 알림 내용

    private LocalDateTime sentTime; // 알림 발송 시간

    private Long recipientId; // 알림 수신 대상

    private NotificationType type; // 알림 타입
}