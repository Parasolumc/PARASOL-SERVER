package umc.parasol.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import retrofit2.http.Url;
import umc.parasol.domain.notification.domain.FcmMessage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

@Slf4j
@Configuration
public class FcmConfig {
    @Value("${fcm.key.path}")
    private String SERVICE_ACCOUNT_JSON;

    @PostConstruct
    public void init() {
        try {
            URL url = new URL(SERVICE_ACCOUNT_JSON);
            InputStream serviceAccount = url.openStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            log.error("파이어베이스 서버와의 연결에 실패했습니다.");
        }
    }


}
