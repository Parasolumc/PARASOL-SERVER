package umc.parasol.domain.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.parasol.domain.auth.application.AuthService;
import umc.parasol.domain.auth.dto.RefreshTokenReq;
import umc.parasol.domain.auth.dto.SignInReq;
import umc.parasol.domain.auth.dto.SignUpReq;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @Valid @RequestBody SignUpReq signUpReq) {
        System.out.println("gg");
        return authService.signUp(signUpReq);
    }

    //로그인
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @Valid @RequestBody SignInReq signInReq) {

        return authService.signIn(signInReq);
    }

    //로그아웃
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody RefreshTokenReq refreshTokenReq){

        return authService.signOut(refreshTokenReq);
    }

    //토큰 리프레시
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(
            @Valid @RequestBody RefreshTokenReq tokenRefreshRequest){

        return authService.refresh(tokenRefreshRequest);
    }
}