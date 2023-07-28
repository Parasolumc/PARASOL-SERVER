package umc.parasol.domain.shop.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.parasol.domain.shop.application.ShopService;
import umc.parasol.domain.shop.dto.ShopListRes;
import umc.parasol.domain.shop.dto.UpdateUmbrellaReq;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;

    // 매장 조회
    @GetMapping
    public ResponseEntity<?> getShopList(
            @CurrentUser UserPrincipal userPrincipal) {

        List<ShopListRes> shopListRes = shopService.getShopList(userPrincipal);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(shopListRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 대여 가능 우산 개수 업데이트
    @PutMapping("/umbrella")
    public ResponseEntity<?> updateUmbrella(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody UpdateUmbrellaReq updateUmbrellaReq) {

        ShopListRes shopListRes = shopService.updateUmbrellaCount(userPrincipal, updateUmbrellaReq);


        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(shopListRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
