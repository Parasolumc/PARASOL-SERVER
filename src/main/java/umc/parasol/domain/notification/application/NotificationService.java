package umc.parasol.domain.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.notification.domain.Notification;
import umc.parasol.domain.notification.domain.NotificationType;
import umc.parasol.domain.notification.domain.repository.NotificationRepository;
import umc.parasol.domain.notification.dto.NotificationRes;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.global.config.security.token.UserPrincipal;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
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

    /**
     * 알림 생성
     * @param targetShop 상점
     * @param member 유저
     * @param type 알림 종류
     */
    @Transactional
    public Notification makeNotification(Shop targetShop, Member member, NotificationType type) {
        String content = "";
        log.info(type.toString());
        if(type == NotificationType.RENT_COMPLETED){
            content = "대여를 완료했어요!";
        } else if (type == NotificationType.RETURN_COMPLETED) {
            content = "반납을 완료했어요!";
        } else if (type == NotificationType.SALE_COMPLETED) {
            content = "판매를 완료했어요!";
        } else if (type == NotificationType.FREE_RENTAL_END) {
            content = "3시간 후 무료대여가 종료됩니다.";
        } else if (type == NotificationType.OVERDUE_FINE) {
            content = "사용시간 초과로 연체료 1,000원이 누적되었습니다.";
        }
        return Notification.builder()
                .type(type)
                .content(content)
                .sentTime(LocalDateTime.now())
                .recipient(member)
                .shop(targetShop)
                .build();
    }
}