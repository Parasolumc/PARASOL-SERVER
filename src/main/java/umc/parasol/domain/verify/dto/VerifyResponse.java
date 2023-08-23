package umc.parasol.domain.verify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifyResponse {

    private final int code;
    private final String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime expirationTime;

    public static VerifyResponse of(int code, String name, LocalDateTime createdAt, LocalDateTime expirationTime) {
        return new VerifyResponse(code, name, createdAt, expirationTime);
    }
}
