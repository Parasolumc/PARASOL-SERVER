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
                .shopId(notification.getShop().getId())
                .type(notification.getType())
                .build();
    }

    /**
     * 알림 조회
     * @param user api 호출하는 사용자 객체
     */
    @Transactional
    public List<NotificationRes> getAllNotifications(UserPrincipal user) {
        Member recipient = findValidMember(user.getId());

        List<Notification> notificationList = notificationRepository.findAllByRecipient(recipient);

        return notificationList.stream()
                .map(this::createNotificationRes)
                .toList();
    }

    /**
     * 알림 삭제
     * @param user api 호출하는 사용자 객체
     * @param id 삭제할 알림 ID
     */
    @Transactional
    public Object deleteNotification(UserPrincipal user, Long id) {
        findValidMember(user.getId());
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림이 올바르지 않습니다."));

        notificationRepository.delete(notification);

        return true;
    }

}