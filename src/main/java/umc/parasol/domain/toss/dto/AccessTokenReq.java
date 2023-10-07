package umc.parasol.domain.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 토스 페이먼츠 Access Token 발급 API request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenReq {

    // 요청 타입. AuthorizationCode(신규 고객), RefreshToken(기존 고객) 중 하나 입니다.
    private String grantType;

    // 고객 ID
    private String customerKey;

    // 리다이렉트 URL의 쿼리 파라미터로 돌아온 code
    private String code;

}
