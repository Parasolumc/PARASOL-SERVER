package umc.parasol.domain.umbrella.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.parasol.domain.umbrella.application.UmbrellaService;
import umc.parasol.domain.umbrella.dto.UmbrellaAddReq;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;

@RestController
@RequestMapping("/api/umbrella")
@RequiredArgsConstructor
public class UmbrellaController {

    private final UmbrellaService umbrellaService;

    @PostMapping("/init")
    // 매장에 우산 초기 등록
    public ResponseEntity<?> umbrellaSetting(@CurrentUser UserPrincipal user, @RequestBody UmbrellaAddReq req) {
        try {
            return ResponseEntity.ok(umbrellaService.init(user, req.getCount()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/buy")
    // 손님으로부터 우산 구입
    public ResponseEntity<?> buyUmbrella(@CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(umbrellaService.buyUmbrella(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
