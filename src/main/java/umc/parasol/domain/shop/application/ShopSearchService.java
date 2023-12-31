package umc.parasol.domain.shop.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.shop.dto.SearchShopReq;
import umc.parasol.domain.shop.dto.SearchShopRes;
import umc.parasol.global.DefaultAssert;
import umc.parasol.global.config.security.token.UserPrincipal;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopSearchService {

    private final ShopRepository shopRepository;

    private final MemberRepository memberRepository;

    /**
     * 매장 검색 결과 조회
    */
    public List<SearchShopRes> getSearchShop(UserPrincipal userPrincipal,
                                             String keyword,
                                             BigDecimal lat,
                                             BigDecimal lon) {

        findValidMember(userPrincipal.getId());
        List<Shop> searchShopList = shopRepository.searchShopByKeyword(keyword);

        return searchShopList.stream()
                .map(shop -> createSearchShopRes(shop, lat, lon))
                .toList();
    }

    /**
     * 사용자의 위치와 매장 위치 사이의 거리를 계산해주는 메서드
     * @param userLat 사용자의 위도
     * @param userLon 사용자의 경도
     * @param shopLat 매장의 위도
     * @param shopLon 매장의 경도
     * @return 계산된 거리 반환(m, km 구분)
     */
    public String calculateDistance(BigDecimal userLat, BigDecimal userLon, BigDecimal shopLat, BigDecimal shopLon) {
        BigDecimal earthRadius = new BigDecimal("6371.0"); // 지구의 반지름 (단위: km)

        double latDistance = Math.toRadians(shopLat.subtract(userLat).doubleValue());
        double lonDistance = Math.toRadians(shopLon.subtract(userLon).doubleValue());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat.doubleValue())) * Math.cos(Math.toRadians(shopLat.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceInKm = earthRadius.multiply(BigDecimal.valueOf(c)).doubleValue();

        if (distanceInKm < 1.0) {
            BigDecimal distanceInMeters = BigDecimal.valueOf(distanceInKm * 1000);
            DecimalFormat df = new DecimalFormat("#");
            String formattedDistance = df.format(distanceInMeters);

            return formattedDistance+"m";
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedDistance = df.format(distanceInKm);

            return formattedDistance+"km";
        }
    }

    // getSearchShop의 응답을 생성해주는 메서드
    private SearchShopRes createSearchShopRes(Shop shop, BigDecimal userLat, BigDecimal userLon) {
        String distance = calculateDistance(userLat, userLon, shop.getLatitude(), shop.getLongitude());
        return SearchShopRes.builder()
                .id(shop.getId())
                .shopName(shop.getName())
                .roadNameAddress(shop.getRoadNameAddress())
                .distance(distance)
                .build();
    }

    // 유효한 사용자인지 체크하는 메서드
    private Member findValidMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 올바르지 않습니다."));
    }

}
