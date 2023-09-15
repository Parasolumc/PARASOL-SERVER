package umc.parasol.domain.auth.application;

import devholic.library.oauth2.apple.AppleTokenAgent;
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
import umc.parasol.domain.member.dto.UpdateRoleReq;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.shop.dto.ShopReq;
import umc.parasol.domain.shop.dto.ShopRes;
import umc.parasol.domain.verify.application.VerifyService;
import umc.parasol.domain.verify.dto.CheckReq;
import umc.parasol.domain.verify.dto.VerifyResponse;
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
    private final VerifyService verifyService;

    private final ShopRepository shopRepository;


    //회원가입 (일반 사용자-customer)
    @Transactional
    public Long signUpCustomer(SignUpCustomerReq signUpReq) {
        DefaultAssert.isTrue(!memberRepository.existsByEmail(signUpReq.getEmail()), "해당 이메일이 존재합니다.");

        // 멤버 객체 생성
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

    @Transactional
    // 회원가입 (사장님-owner)
    public Long signUpOwner(SignUpOwnerReq signUpOwnerReq) {
        DefaultAssert.isTrue(!memberRepository.existsByEmail(signUpOwnerReq.getEmail()), "해당 이메일이 존재합니다.");

        // 매장 객체 생성
        Shop newShop = Shop.builder()
                .name(signUpOwnerReq.getShopName())
                .latitude(signUpOwnerReq.getLatitude())
                .longitude(signUpOwnerReq.getLongitude())
                .roadNameAddress(signUpOwnerReq.getRoadNameAddress())
                .build();

        shopRepository.save(newShop);

        // 멤버 객체 생성
        Member member = Member.builder()
                .nickname(signUpOwnerReq.getNickname())
                .email(signUpOwnerReq.getEmail())
                .password(passwordEncoder.encode(signUpOwnerReq.getPassword()))
                .role(Role.OWNER)
                .authRole(AuthRole.USER)
                .isVerified(Boolean.FALSE)
                .shop(newShop)
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

        Token token = createTokenFromTokenMapping(tokenMapping);

        tokenRepository.save(token);

        return createAuthRes(tokenMapping, findMember);
    }

    // 애플 소셜 로그인
    @Transactional
    public AuthRes appleLogin(AppleReq req) {
        String email = AppleTokenAgent.getUserResource(req.getToken());
        Member targetMember = memberRepository.findByEmail(email).orElseGet(
                () -> createSocialMember(email)
        );

        TokenMapping tokenMapping = customTokenProviderService.createAppleToken(targetMember.getId(), email);
        Token token = createTokenFromTokenMapping(tokenMapping);
        tokenRepository.save(token);
        return createAuthRes(tokenMapping, targetMember);
    }

    private static AuthRes createAuthRes(TokenMapping tokenMapping, Member targetMember) {
        return AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(tokenMapping.getRefreshToken())
                .role(targetMember.getRole())
                .memberId(targetMember.getId())
                .build();
    }

    private static Token createTokenFromTokenMapping(TokenMapping tokenMapping) {
        return Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .userEmail(tokenMapping.getUserEmail())
                .build();
    }

    //로그아웃
    @Transactional
    public void signOut(RefreshTokenReq refreshTokenReq){

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshTokenReq.getRefreshToken());
        DefaultAssert.isTrue(token.isPresent(), "이미 로그아웃 되었습니다");

        tokenRepository.delete(token.get());
    }

    @Transactional
    // 복구 요청
    public VerifyResponse recovery(RecoveryReq request) {
        Member targetMember = memberRepository.findByName(request.getName())
                .orElseThrow(() -> new IllegalStateException("등록되지 않은 이름입니다."));
        Optional<Member> numberOwner = memberRepository.findByPhoneNumber(request.getPhoneNumber());
        if (numberOwner.isPresent() && !numberOwner.get().getName().equals(request.getName()))
            throw new IllegalStateException("이미 등록된 전화번호지만, 이름이 다릅니다.");
        return verifyService.recoveryVerify(targetMember);
    }

    @Transactional
    // 복구 확인 요청
    public RecoveryRes recoveryCheck(CheckReq checkReq) {
        verifyService.check(checkReq);
        Member member = memberRepository.findByPhoneNumber(checkReq.getPhoneNumber())
                .orElseThrow(() -> new IllegalStateException("등록되지 않은 전화번호 입니다."));
        return RecoveryRes.from(member.getId(), member.getEmail());
    }

    @Transactional
    public Member createSocialMember(String email) {
        Member member = Member.builder()
                .nickname(email)
                .email(email)
                .password("")
                .role(null)
                .authRole(AuthRole.USER)
                .isVerified(Boolean.FALSE)
                .build();
        return memberRepository.save(member);
    }

    // 역할 설정
    @Transactional
    public String updateRole(String name, UpdateRoleReq req) {
        Member targetMember = memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("member not found"));
        targetMember.updateRole(req.getRole());
        if (req.getRole().equals("OWNER")) {
            Shop newShop = new Shop();
            Shop saveShop = shopRepository.save(newShop);
            targetMember.updateShop(saveShop);
        }
        return "변경 완료";
    }

    // 역할 설정 후 만약 점주로 바꿀 것이었다면
    @Transactional
    public ShopRes createNewShop(String name, ShopReq req) {
        Member targetMember = memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("member not found"));
        Shop targetShop = targetMember.getShop();

        targetShop.updateName(req.getShopName());
        targetShop.updateLatitude(req.getLatitude());
        targetShop.updateLongitude(req.getLongitude());
        targetShop.updateRoadNameAddress(req.getRoadNameAddress());
        targetShop.updateDescription(req.getDesc());

        ShopRes result = ShopRes.builder()
                .latitude(targetShop.getLatitude())
                .longitude(targetShop.getLongitude())
                .shopName(targetShop.getName())
                .roadNameAddress(targetShop.getRoadNameAddress())
                .id(targetShop.getId())
                .build();

        return result;
    }
}
