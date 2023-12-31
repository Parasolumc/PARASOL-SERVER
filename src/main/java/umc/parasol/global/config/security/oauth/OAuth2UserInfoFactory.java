package umc.parasol.global.config.security.oauth;

import umc.parasol.domain.member.domain.Provider;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.security.oauth.company.Google;
import umc.parasol.global.config.security.oauth.company.Naver;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(Provider.GOOGLE.toString())) {
            return new Google(attributes);
        }
        if (registrationId.equalsIgnoreCase(Provider.NAVER.toString())) {
            return new Naver(attributes);
        }
        DefaultAssert.isAuthentication("해당 oauth2 기능은 지원하지 않습니다.");
        return null;
    }
}