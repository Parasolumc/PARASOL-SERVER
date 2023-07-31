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
    @PostMapping("/add/{id}")
    public ResponseEntity<?> addHistory(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(historyService.createHistory(user, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 손님의 우산 반납
    @PostMapping("/back/{id}")
    public ResponseEntity<?> backUmbrella(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(historyService.giveBack(user, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 손님의 대여 목록
    @GetMapping("/mine")
    public ResponseEntity<?> histories(@CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(historyService.historyList(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
