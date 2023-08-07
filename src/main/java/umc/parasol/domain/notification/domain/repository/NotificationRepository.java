package umc.parasol.domain.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.notification.domain.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByRecipient(Member recipient);

}