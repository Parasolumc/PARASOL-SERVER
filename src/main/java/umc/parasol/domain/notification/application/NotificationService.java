package umc.parasol.domain.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.notification.domain.Notification;
import umc.parasol.domain.notification.domain.repository.NotificationRepository;
import umc.parasol.domain.notification.dto.NotificationRes;
import umc.parasol.global.config.security.token.UserPrincipal;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    // 유효한 사용자인지 체크하는 메서드
    private Member findValidMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 올바르지 않습니다."));
    }


    // NotificationRes 응답을 생성해주는 메서드
    private NotificationRes createNotificationRes(Notification notification) {
        return NotificationRes.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .sentTime(notification.getSentTime())
                .recipientId(notification.getRecipient().getId())
                .type(notification.getType())
                .build();
    }

    @Transactional
    public List<NotificationRes> getAllNotifications(UserPrincipal user) {
        Member recipient = findValidMember(user.getId());

        List<Notification> notificationList = notificationRepository.findAllByRecipient(recipient);

        return notificationList.stream()
                .map(this::createNotificationRes)
                .toList();
    }

}