package umc.parasol.domain.notification.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Component;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.notification.domain.FcmMessage;
import umc.parasol.global.config.security.token.UserPrincipal;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {
    @Value("${fcm.key.path}")
    private String SERVICE_ACCOUNT_JSON;

    @Value("${fcm.api.url}")
    private String FCM_API_URL;

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;


    /**
     * makeMessage 메서드를 호출하여 메시지를 생성한 후, OkHttpClient를 이용하여 FCM 서버로 전송
     * @param targetToken
     * @param title
     * @param body
     * @throws IOException
     */
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(FCM_API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request)
                .execute();

        log.info("알림 전송 성공 ! successCount: 1 messages were sent successfully");
        log.info("알림 전송: {}", response.body().string());

    }

    /**
     * targetToken, title, body 값을 매개변수로 받아서 FcmMessage 객체를 빌더 패턴을 이용하여 생성
     * FcmMessage 객체를 생성하고, 이를 JSON 문자열로 변환하여 반환하는 역할
     * @param targetToken
     * @param title
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();

        log.info("메세지 생성 성공 ! successCount: 1 messages were made successfully");
        log.info("메세지 생성: title-{}, body-{}, token-{}", fcmMessage.getMessage().getNotification().getTitle(), fcmMessage.getMessage().getNotification().getBody(), fcmMessage.getMessage().getToken());
        return objectMapper.writeValueAsString(fcmMessage);
    }

    /**
     * Firebase Admin SDK를 사용하기 위해 Google Cloud의 인증서버에서 Access Token을 얻어오는 기능 수행
     * @return
     * @throws IOException
     */
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = SERVICE_ACCOUNT_JSON;

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new URL(firebaseConfigPath).openStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        log.info("getAccessToken() - googleCredentials: {} ", googleCredentials.getAccessToken().getTokenValue());

        return googleCredentials.getAccessToken().getTokenValue();
    }



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

