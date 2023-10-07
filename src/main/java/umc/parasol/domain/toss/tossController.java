package umc.parasol.domain.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.parasol.global.payload.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/toss")
public class tossController {

    private final tossService tossService;

    // Redirect URL 핸들링 -> Access Token 발급
    @GetMapping("/callback-auth")
    public ResponseEntity<?> handleRedirectUrl(
            @RequestParam String code,
            @RequestParam String customerKey
    ) {

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(tossService.getAccessToken(code, customerKey))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 자동결제 실행
//    @PostMapping("/payment")
//    public ResponseEntity<?> autoPayment(@CurrentUser UserPrincipal owner, @PathVariable Long memberId) {
//
//        ApiResponse apiResponse = ApiResponse.builder()
//                .check(true)
//                .information(tossService.callAutoPayment(owner, memberId))
//                .build();
//
//        return ResponseEntity.ok(apiResponse);
//    }

}
