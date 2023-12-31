package umc.parasol.domain.notification.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.parasol.domain.notification.application.NotificationService;
import umc.parasol.domain.notification.dto.NotificationRes;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 모두 조회
    @GetMapping
    public ResponseEntity<?> getAllNotifications(@CurrentUser UserPrincipal user) {

        List<NotificationRes> notificationRes = notificationService.getAllNotifications(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(notificationRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 알림 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.deleteNotification(user, id));
    }
}