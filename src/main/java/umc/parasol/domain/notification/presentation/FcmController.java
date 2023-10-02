package umc.parasol.domain.notification.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.parasol.domain.notification.application.FirebaseCloudMessageService;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fcm")
public class FcmController {
    @Autowired
    private FirebaseCloudMessageService fcmService;

    /**
     *
     * @param userPrincipal
     * @param fcmToken
     * @return
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateFCMToken(@CurrentUser UserPrincipal userPrincipal, @RequestParam("token") String fcmToken) {
        try {
            boolean updated = fcmService.confirmFcmToken(userPrincipal, fcmToken);

            if (updated) {
                return ResponseEntity.ok("FCM 토큰이 업데이트되었습니다.");
            } else {
                return ResponseEntity.ok("기존 토큰과 동일합니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
