package umc.parasol.domain.notification.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import umc.parasol.domain.common.BaseEntity;
import umc.parasol.domain.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @NotNull(message = "알림 내용이 설정되어 있어야 합니다.")
    private String content; // 알림 내용

    private LocalDateTime sentTime; // 알림 발송 시간

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Member recipient;


    @NotNull(message = "알림 타입이 설정되어 있어야 합니다.")
    private String type; // 알림 타입

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public void updateRecipient(Member recipient) {
        this.recipient = recipient;
    }

    public void updateType(String type) {
        this.type = type;
    }
}