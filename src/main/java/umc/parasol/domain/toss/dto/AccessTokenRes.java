package umc.parasol.domain.toss.dto;

import lombok.Data;


/**
 * 토스 페이먼츠 Access Token 발급 API response. Access Token 객체.
 */
@Data
public class AccessTokenRes {

    // 사용자에 할당된 Access Token 입니다. customerKey와 연결되어 있는 값으로 고객이 탈퇴하거나 Refresh Token으로 새로 발급받지 않는 한 변하지 않습니다.
    private String accessToken;

    // Access Token을 새로 발급 받을 때 사용할 토큰입니다.
    private String refreshToken;

    // 브랜드페이 API를 요청할 때 쓰는 인증 방식인 bearer가 고정값으로 돌아옵니다.
    private String tokenType;

    // Access Token의 유효기간을 초 단위로 나타낸 값입니다. Access Token이 만료되면 Access Token 발급 API로 유효기간을 연장하거나 새로 발급받으세요.
    private int expiresIn;
}
