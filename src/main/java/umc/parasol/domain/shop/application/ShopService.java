package umc.parasol.domain.shop.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.parasol.domain.image.domain.Image;
import umc.parasol.domain.image.domain.repository.ImageRepository;
import umc.parasol.domain.image.dto.ImageRes;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.domain.shop.dto.*;
import umc.parasol.global.config.security.token.UserPrincipal;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final MemberRepository memberRepository;

    private final ShopRepository shopRepository;

    private final ImageRepository imageRepository;

    /**
     * 매장 리스트 조회
     * @param userPrincipal api 호출하는 사용자 객체
     */
    public List<ShopListRes> getShopList(UserPrincipal userPrincipal) {

        findValidMember(userPrincipal.getId());
        List<Shop> shopList = shopRepository.findAll();

        return shopList.stream()
                .map(this::createShopListRes)
                .toList();
    }

    /**
     * 특정 매장 조회
     * @param userPrincipal api 호출하는 사용자 객체
     * @param shopId 조회할 매장의 ID
     */
    public ShopRes getShop(UserPrincipal userPrincipal, Long shopId) {

        findValidMember(userPrincipal.getId());
        Shop shop = findValidShop(shopId);

        List<Image> imageList = imageRepository.findAllByShop(shop);
        List<ImageRes> imageResList = imageList.stream()
                .map(this::createImageRes)
                .toList();

        return createShopRes(shop, imageResList);
    }

    // 유효한 사용자인지 체크하는 메서드
    private Member findValidMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 올바르지 않습니다."));
    }

    // 유효한 매장인지 체크하는 메서드
    private Shop findValidShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("매장이 올바르지 않습니다."));
    }

    // 사용자가 사장님인지 체크하는 메서드
    private Shop findValidShopForOwner(Member member) {
        return Optional.ofNullable(member.getShop())
                .orElseThrow(() -> new IllegalArgumentException("연결된 매장이 없습니다."));
    }

    // ShopList 응답을 생성해주는 메서드
    private ShopListRes createShopListRes(Shop shop) {
        return ShopListRes.builder()
                .id(shop.getId())
                .shopName(shop.getName())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .roadNameAddress(shop.getRoadNameAddress())
                .openTime(shop.getOpenTime())
                .closeTime(shop.getCloseTime())
                .build();
    }

    // Shop 응답을 생성해주는 메서드
    private ShopRes createShopRes(Shop shop, List<ImageRes> imageResList) {
        return ShopRes.builder()
                .id(shop.getId())
                .shopName(shop.getName())
                .desc(shop.getDescription())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .roadNameAddress(shop.getRoadNameAddress())
                .openTime(shop.getOpenTime())
                .closeTime(shop.getCloseTime())
                .image(imageResList)
                .build();
    }

    // 이미지 응답을 생성해주는 메서드
    private ImageRes createImageRes(Image image) {
        return ImageRes.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }
}
