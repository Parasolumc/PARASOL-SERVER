package umc.parasol.domain.member.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.parasol.domain.member.application.MemberService;
import umc.parasol.domain.member.dto.UpdatePwReq;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePwReq updatePwReq,
                                            @CurrentUser UserPrincipal user) {
        return ResponseEntity.ok(memberService.updatePassword(updatePwReq, user));
    }
}
