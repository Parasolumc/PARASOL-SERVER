package umc.parasol.domain.sell.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.parasol.domain.sell.application.SellService;
import umc.parasol.domain.sell.dto.SellResultRes;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

@RestController
@RequestMapping("/api/sell")
@RequiredArgsConstructor
public class SellController {

    private final SellService sellService;

    // 우산 판매
    @PostMapping("/{id}")
    public ResponseEntity<?> sellUmbrella(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable(value = "id") Long shopId) {

        SellResultRes sellResultRes = sellService.sellUmbrella(userPrincipal, shopId);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(sellResultRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    //판매한 우산 찾기
    public ResponseEntity<?> sellHistory(@CurrentUser UserPrincipal user){
        try {
            return ResponseEntity.ok(sellService.sellList(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
