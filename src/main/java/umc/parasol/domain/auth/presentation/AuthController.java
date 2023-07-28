package umc.parasol.domain.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import umc.parasol.domain.auth.application.AuthSignService;
import umc.parasol.domain.auth.application.AuthTokenService;
import umc.parasol.domain.auth.dto.*;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.payload.ApiResponse;
import umc.parasol.global.payload.Message;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthSignService authService;

    private final AuthTokenService authTokenService;

    //회원가입 (일반 사용자-customer)
    @PostMapping("/sign-up/customer")
    public ResponseEntity<?> signUpCustomer(
            @Valid @RequestBody SignUpCustomerReq signUpReq) {

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users")
                .buildAndExpand(authService.signUpCustomer(signUpReq)).toUri();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("회원가입에 성공했습니다").build())
                .build();

        return ResponseEntity.created(location).body(apiResponse);
    }

    //회원가입 (사장님-owner)
    @PostMapping("/sign-up/owner")
    public ResponseEntity<?> signUpOwner(
            @Valid @RequestBody SignUpOwnerReq signUpOwnerReq) {

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users")
                .buildAndExpand(authService.signUpOwner(signUpOwnerReq)).toUri();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("회원가입에 성공했습니다").build())
                .build();

        return ResponseEntity.created(location).body(apiResponse);
    }

    //로그인
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @Valid @RequestBody SignInReq signInReq) {

        AuthRes authRes = authService.signIn(signInReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //로그아웃
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody RefreshTokenReq refreshTokenReq){

        authService.signOut(refreshTokenReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("로그아웃 되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //토큰 리프레시
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(
            @Valid @RequestBody RefreshTokenReq tokenRefreshRequest){

        AuthRes authRes = authTokenService.refresh(tokenRefreshRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}