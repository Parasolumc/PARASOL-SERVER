package umc.parasol.domain.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.auth.domain.Token;
import umc.parasol.domain.auth.domain.repository.TokenRepository;
import umc.parasol.domain.auth.dto.*;
import umc.parasol.domain.member.domain.AuthRole;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.Role;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.global.DefaultAssert;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthSignService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final TokenRepository tokenRepository;

    private final CustomTokenProviderService customTokenProviderService;

    //회원가입 (일반 사용자-customer)
    @Transactional
    public Long signUp(SignUpReq signUpReq) {
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

        return member.getId();
    }

    //로그인
    @Transactional
    public AuthRes signIn(SignInReq signInReq){
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

        return AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(tokenMapping.getRefreshToken())
                .build();
    }

    //로그아웃
    @Transactional
    public void signOut(RefreshTokenReq refreshTokenReq){

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshTokenReq.getRefreshToken());
        DefaultAssert.isTrue(token.isPresent(), "이미 로그아웃 되었습니다");

        tokenRepository.delete(token.get());
    }


}
