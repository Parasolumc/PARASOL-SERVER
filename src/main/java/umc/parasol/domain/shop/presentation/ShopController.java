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

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/api/shop", "/api/umbrella"})
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
            @RequestParam String keyword, @RequestParam BigDecimal lat, @RequestParam BigDecimal lon) {

        List<SearchShopRes> searchShopRes = shopSearchService.getSearchShop(userPrincipal, keyword, lat, lon);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(searchShopRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 매장 사진 업로드
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> imageUpload(@RequestParam(value = "image")
                                         MultipartFile file, @CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(imageService.upload(file, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 매장 사진 삭제
    @DeleteMapping("/image/{id}")
    public ResponseEntity<?> imageDelete(@PathVariable Long id, @CurrentUser UserPrincipal user) {
        try {
            return ResponseEntity.ok(imageService.delete(id, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 사장님 본인 매장 조회
    @GetMapping(value = "/owner")
    public ResponseEntity<?> getOwnerShop(@CurrentUser UserPrincipal userPrincipal){
        try {
            ShopRes shopRes = shopService.getShopById(userPrincipal);
            ApiResponse apiResponse = ApiResponse.builder()
                    .check(true)
                    .information(shopRes)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 매장 정보 수정
    @PutMapping(value = "/info")
    public ResponseEntity<?> changeShopInfo(@CurrentUser UserPrincipal userPrincipal, @RequestBody UpdateInfoReq updateInfoReq){
        try {
                ShopRes updatedShop = shopService.updateInfo(userPrincipal, updateInfoReq);
                ApiResponse apiResponse = ApiResponse.builder()
                        .check(true)
                        .information(updatedShop)
                        .build();
                return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 우산 대여
    @PostMapping("/rental/{id}")
    public ResponseEntity<?> rentalUmbrella(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(shopService.rentalUmbrella(user, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 우산 반납
    @PostMapping("/return/{id}")
    public ResponseEntity<?> returnUmbrella(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(shopService.returnUmbrella(user, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
