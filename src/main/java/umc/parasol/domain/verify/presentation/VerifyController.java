package umc.parasol.domain.verify.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.parasol.domain.verify.application.VerifyService;
import umc.parasol.domain.verify.dto.CheckReq;
import umc.parasol.domain.verify.dto.VerifyReq;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verify")
public class VerifyController {

    private final VerifyService verifyService;

    // 문자 인증
    @PostMapping()
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyReq verifyReq) {
        try {
            return ResponseEntity.ok(verifyService.verify(verifyReq));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 문자 인증 응답
    @PostMapping("/check")
    public ResponseEntity<?> check(@Valid @RequestBody CheckReq checkReq) {
        try {
            return ResponseEntity.ok(verifyService.check(checkReq));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
