package umc.parasol.domain.toss;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.toss.dto.AccessTokenReq;
import umc.parasol.domain.toss.dto.AccessTokenRes;
import umc.parasol.domain.toss.dto.brandpay.BrandPayMethod;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.TossConfig;
import umc.parasol.global.config.security.token.CurrentUser;
import umc.parasol.global.config.security.token.UserPrincipal;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class tossService {

    private final MemberRepository memberRepository;
    private RestTemplate restTemplate = new RestTemplate();

    // Access Token 발급 (Redirect Url 핸들링)
    // https://parasol-api.p-e.kr/toss/callback-auth?code={CODE}&customerKey={CUSTOMER_KEY}
    public AccessTokenRes getAccessToken(String code, String customerKey) {
        // AccessToken Request 객체 생성
        AccessTokenReq accessTokenReq = AccessTokenReq.builder()
                .grantType("AuthorizationCode")
                .code(code)
                .customerKey(customerKey)
                .build();
        // 생성된 AccessToken Request 객체로 Access Token 발급 API 호출
        return this.callGetAccessToken(accessTokenReq);
    }

    // Access Token 발급 API 호출 (Rest Template 호출)
    public AccessTokenRes callGetAccessToken(AccessTokenReq accessTokenReq) {
        AccessTokenRes response = this.callHttpGetAccessToken(
                this.buildHttpEntity(accessTokenReq));
        log.info(response.toString());
        return response;
    }

    // 자동 결제 api 호출
    public void callAutoPayment(@CurrentUser UserPrincipal owner, Long memberId) {

    }

    // SecretKey로 결제수단 조회 API
    public BrandPayMethod getPaymentMethod(Long memberId) {
        UUID customerKey = getCustomerKey(memberId);
        String apiUrl = TossConfig.PAYMENT_METHOD_API_URL + customerKey;
        BrandPayMethod brandPayMethod = this.callHttpGetPaymentMethod(apiUrl);
        log.info(brandPayMethod.toString());
        return brandPayMethod;
    }


    // api 호출에 필요한 Http Header를 만드는 메서드
    public HttpEntity<AccessTokenReq> buildHttpEntity(AccessTokenReq tossRequest) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(TossConfig.MEDIA_TYPE));
        // 시크릿키를 base64로 인코딩한 값. 추후 변수 처리
        httpHeaders.add(TossConfig.AUTHORIZATION, TossConfig.BASIC + TossConfig.ENCODING_KEY);
        return new HttpEntity<>(tossRequest, httpHeaders);
    }

    // [ Rest Template ] Toss에 Access Token 발급 API 요청
    public AccessTokenRes callHttpGetAccessToken(HttpEntity<AccessTokenReq> tossRequest) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 답변이 길어질 경우 TimeOut Error 발생하므로 time 설정
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);   //  30초

        restTemplate.setRequestFactory(requestFactory);

        ResponseEntity<AccessTokenRes> responseEntity = restTemplate.postForEntity(
                TossConfig.ACCESS_TOKEN_API_URL,
                tossRequest,
                AccessTokenRes.class);

        return responseEntity.getBody();
    }

    // [ Rest Template ] Toss에 결제수단 조회 API 요청
    public BrandPayMethod callHttpGetPaymentMethod(String apiUrl) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 답변이 길어질 경우 TimeOut Error 발생하므로 time 설정
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);   //  30초

        restTemplate.setRequestFactory(requestFactory);

        ResponseEntity<BrandPayMethod> responseEntity = restTemplate.getForEntity(
                apiUrl,
                BrandPayMethod.class);

        return responseEntity.getBody();
    }

    public UUID getCustomerKey(Long memberId) {
        Member findMember = isValidMember(memberId);
        return findMember.getTossCustomerKey();
    }

    public Member isValidMember(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        DefaultAssert.isTrue(findMember.isPresent(), "멤버가 올바르지 않습니다.");
        return findMember.get();
    }
}
