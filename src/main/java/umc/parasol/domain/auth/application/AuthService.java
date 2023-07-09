package umc.parasol.domain.auth.application;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import umc.parasol.domain.auth.domain.Token;
import umc.parasol.domain.auth.domain.repository.TokenRepository;
import umc.parasol.domain.auth.dto.*;
import umc.parasol.domain.member.domain.AuthRole;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.Role;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.payload.ApiResponse;
import umc.parasol.global.payload.Message;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final TokenRepository tokenRepository;

    private final CustomTokenProviderService customTokenProviderService;

    //회원가입 (일반 사용자-customer)
    @Transactional
    public ResponseEntity<?> signUp(SignUpReq signUpReq) {
        DefaultAssert.isTrue(!memberRepository.existsByEmail(signUpReq.getEmail()), "해당 이메일이 존재합니다.");

        Member member = Member.builder()
                .nickname(signUpReq.getNickname())
                .email(signUpReq.getEmail())
                .password(passwordEncoder.encode(signUpReq.getPassword()))
                .role(Role.CUSTOMER)
                .authRole(AuthRole.USER)
                .isVerified(Boolean.FALSE)
                .build();

        memberRepository.save(member);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users")
                .buildAndExpand(member.getId()).toUri();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("회원가입에 성공했습니다").build())
                .build();

        return ResponseEntity.created(location).body(apiResponse);
    }

    //로그인
    @Transactional
    public ResponseEntity<?> signIn(SignInReq signInReq){
        Optional<Member> member = memberRepository.findByEmail(signInReq.getEmail());
        DefaultAssert.isTrue(member.isPresent(), "유저가 올바르지 않습니다.");

        Member findMember = member.get();  // get() => Optional로 받은 User객체 꺼내기
        boolean passwordCheck = passwordEncoder.matches(signInReq.getPassword(), findMember.getPassword());
        DefaultAssert.isTrue(passwordCheck, "비밀번호가 일치하지 않습니다.");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInReq.getEmail(),
                        signInReq.getPassword()
                )
        );

        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .userEmail(tokenMapping.getUserEmail())
                .build();

        tokenRepository.save(token);
        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(tokenMapping.getRefreshToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //로그아웃
    @Transactional
    public ResponseEntity<?> signOut(RefreshTokenReq refreshTokenReq){
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshTokenReq.getRefreshToken());
        DefaultAssert.isTrue(token.isPresent(), "이미 로그아웃 되었습니다");

        tokenRepository.delete(token.get());
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("로그아웃 되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //토큰 리프레시
    @Transactional
    public ResponseEntity<?> refresh(RefreshTokenReq tokenRefreshRequest) {

        Optional<Token> token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken());
        DefaultAssert.isTrue(token.isPresent(), "다시 로그인 해주세요.");
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());

        TokenMapping tokenMapping;

        try {
            Long expirationTime = customTokenProviderService.getExpiration(tokenRefreshRequest.getRefreshToken());
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.get().getRefreshToken());
        } catch (ExpiredJwtException ex) {
            tokenMapping = customTokenProviderService.createToken(authentication);
            token.get().updateRefreshToken(tokenMapping.getRefreshToken());
        }

        Token updateToken = token.get().updateRefreshToken(tokenMapping.getRefreshToken());

        AuthRes authResponse = AuthRes.builder().accessToken(tokenMapping.getAccessToken()).refreshToken(updateToken.getRefreshToken()).build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
