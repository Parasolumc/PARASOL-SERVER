package umc.parasol.domain.umbrella.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.parasol.domain.umbrella.application.UmbrellaService;
import umc.parasol.domain.umbrella.dto.UmbrellaAddReq;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;

@RestController
@RequestMapping("/api/umbrella")
@RequiredArgsConstructor
public class UmbrellaController {

    private final UmbrellaService umbrellaService;

    @PostMapping("/add")
    // 매장에 우산 초기 등록
    public ResponseEntity<?> add(@CurrentUser UserPrincipal user, @RequestBody UmbrellaAddReq req) {
        try {
            return ResponseEntity.ok(umbrellaService.addUmbrella(user, req.getCount()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/sell/{id}")
    // 손님이 우산 판매
    public ResponseEntity<?> buyUmbrella(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(umbrellaService.buyUmbrella(user, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
