package umc.parasol.domain.shop.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.parasol.domain.image.application.ImageService;
import umc.parasol.domain.shop.application.ShopSearchService;
import umc.parasol.domain.shop.application.ShopService;
import umc.parasol.domain.shop.dto.*;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;

    private final ShopSearchService shopSearchService;

    private final ImageService imageService;

    // 매장 리스트 조회
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

    // 특정 매장 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getShop(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable(value = "id") Long shopId) {

        ShopRes shopRes = shopService.getShop(userPrincipal, shopId);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(shopRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 매장 검색 결과 조회
    @GetMapping("/search")
    public ResponseEntity<?> searchShop(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody SearchShopReq searchShopReq) {

        List<SearchShopRes> searchShopRes = shopSearchService.getSearchShop(userPrincipal, searchShopReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(searchShopRes)
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

    // 매장 사진 업로드
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> imageUpload(@RequestParam(value = "image")
                                         MultipartFile file, @PathVariable Long id, @CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(imageService.upload(id, file, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
