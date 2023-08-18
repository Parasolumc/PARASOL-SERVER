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


    // 손님의 대여 목록
    @GetMapping
    public ResponseEntity<?> histories(@CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(historyService.historyList(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 손님의 현재 대여 현황
    @GetMapping("/now")
    public ResponseEntity<?> currentRental(@CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(historyService.rentalStatus(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
