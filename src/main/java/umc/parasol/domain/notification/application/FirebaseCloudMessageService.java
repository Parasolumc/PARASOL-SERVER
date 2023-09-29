package umc.parasol.domain.notification.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.global.config.security.token.UserPrincipal;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/parasol-bcbf5/messages:send";
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    /**
     * 동일 user가 기존 token과 입력된 token이 달라질 경우에만 업데이트하여 저장
     * @param userPrincipal
     * @param fcmToken
     */
    public boolean confirmFcmToken(UserPrincipal userPrincipal, String fcmToken) {
        Member member = findMemberById(userPrincipal.getId());

        // 현재 토큰과 달라질 경우에만 업데이트
        if (!fcmToken.equals(member.getFcmToken())) {
            member.updateFcmToken(fcmToken);
            memberRepository.save(member);
            return true;
        }
        return false;
    }
    private Member findMemberById(Long memerId) {
        return memberRepository.findById(memerId).orElseThrow(
                () -> new IllegalStateException("해당 member가 없습니다.")
        );
    }

}
