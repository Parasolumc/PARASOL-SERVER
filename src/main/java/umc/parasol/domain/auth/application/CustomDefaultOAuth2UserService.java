package umc.parasol.domain.auth.application;

import umc.parasol.domain.member.domain.AuthRole;
import umc.parasol.domain.member.domain.Provider;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.security.oauth.OAuth2UserInfo;
import umc.parasol.global.config.security.oauth.OAuth2UserInfoFactory;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.error.DefaultAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomDefaultOAuth2UserService extends DefaultOAuth2UserService{

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception e) {
            try {
                DefaultAssert.isAuthentication(e.getMessage());
            } catch (DefaultAuthenticationException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) throws DefaultAuthenticationException {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        DefaultAssert.isAuthentication(!oAuth2UserInfo.getEmail().isEmpty());

        Optional<Member> userOptional = memberRepository.findByEmail(oAuth2UserInfo.getEmail());
        Member member;
        if(userOptional.isPresent()) {
            member = userOptional.get();
            DefaultAssert.isAuthentication(member.getProvider().equals(Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId())));
            member = updateExistingUser(member, oAuth2UserInfo);
        } else {
            member = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(member, oAuth2User.getAttributes());
    }

    private Member registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {

        Member member = Member.builder()
                .provider(Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
                .providerId(oAuth2UserInfo.getId())
                .nickname(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .authRole(AuthRole.USER)
                .build();

        return memberRepository.save(member);
    }

    private Member updateExistingUser(Member member, OAuth2UserInfo oAuth2UserInfo) {

        member.updateNickname(oAuth2UserInfo.getName());

        return memberRepository.save(member);
    }
}
