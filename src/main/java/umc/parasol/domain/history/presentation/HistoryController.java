package umc.parasol.domain.history.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.parasol.domain.history.application.HistoryService;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    // 손님이 우산 대여
    @PostMapping("/rental/{id}")
    public ResponseEntity<?> rentalUmbrella(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(historyService.rentalUmbrella(user, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 손님의 우산 반납
    @PostMapping("/return/{id}")
    public ResponseEntity<?> returnUmbrella(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(historyService.returnUmbrella(user, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 손님의 대여 목록
    @GetMapping
    public ResponseEntity<?> histories(@CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(historyService.historyList(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
