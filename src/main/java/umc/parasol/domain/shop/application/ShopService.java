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
import umc.parasol.domain.umbrella.domain.Umbrella;
import umc.parasol.domain.umbrella.domain.repository.UmbrellaRepository;
import umc.parasol.global.config.security.token.UserPrincipal;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final MemberRepository memberRepository;

    private final ShopRepository shopRepository;

    private final ImageRepository imageRepository;

    private final UmbrellaRepository umbrellaRepository;

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

    // 매장에 있는 모든 우산 리스트를 가져오는 메서드
    public List<Umbrella> getUmbrella(Shop shop) {
        return Optional.ofNullable(umbrellaRepository.findAllByShop(shop))
                .orElseThrow(() -> new IllegalArgumentException("우산이 없습니다."));
    }

    // 매장에 있는 대여 가능한 우산 리스트를 가져오는 메서드
    public List<Umbrella> getAvailableUmbrella(Shop shop) {
        return Optional.ofNullable(umbrellaRepository.findAllByShopAndAvailableIsTrue(shop))
                .orElseThrow(() -> new IllegalArgumentException("대여 가능한 우산이 없습니다."));
    }

    // ShopList 응답을 생성해주는 메서드
    private ShopListRes createShopListRes(Shop shop) {
        String openTime = shop.getOpenTime() != null ? shop.getOpenTime() : "";
        String closeTime = shop.getCloseTime() != null ? shop.getCloseTime() : "";
        return ShopListRes.builder()
                .id(shop.getId())
                .shopName(shop.getName())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .roadNameAddress(shop.getRoadNameAddress())
                .openTime(openTime)
                .closeTime(closeTime)
                .availableUmbrella(getAvailableUmbrella(shop).size())
                .unavailableUmbrella(getUmbrella(shop).size() - getAvailableUmbrella(shop).size())
                .build();
    }

    // Shop 응답을 생성해주는 메서드
    private ShopRes createShopRes(Shop shop, List<ImageRes> imageResList) {
        String desc = shop.getDescription() != null ? shop.getDescription() : "";
        String openTime = shop.getOpenTime() != null ? shop.getOpenTime() : "";
        String closeTime = shop.getCloseTime() != null ? shop.getCloseTime() : "";
        return ShopRes.builder()
                .id(shop.getId())
                .shopName(shop.getName())
                .desc(desc)
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .roadNameAddress(shop.getRoadNameAddress())
                .openTime(openTime)
                .closeTime(closeTime)
                .availableUmbrella(getAvailableUmbrella(shop).size())
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

    /**
     * 본인 매장 조회
     * @param userPrincipal api 호출하는 사용자 객체
     */
    public ShopRes getShopById(UserPrincipal userPrincipal) {

        Member member = findValidMember(userPrincipal.getId());

        Shop shop = findValidShopForOwner(member);

        List<Image> imageList = imageRepository.findAllByShop(shop);
        List<ImageRes> imageResList = imageList.stream()
                .map(this::createImageRes)
                .toList();

        return createShopRes(shop, imageResList);
    }

    /**
     * 매장 정보 수정
     * @param userPrincipal api 호출하는 사용자 객체
     * @param updateInfoReq update할 정보 객체
     */
    @Transactional
    public ShopRes updateInfo(UserPrincipal userPrincipal, UpdateInfoReq updateInfoReq) {

        Member member = findValidMember(userPrincipal.getId());

        Shop shop = findValidShopForOwner(member);

        shop.updateDescription(updateInfoReq.getDesc());
        shop.updateOpenTime(updateInfoReq.getOpenTime());
        shop.updateCloseTime(updateInfoReq.getCloseTime());

        Shop updatedShop = shopRepository.save(shop);


        List<Image> imageList = imageRepository.findAllByShop(updatedShop);
        List<ImageRes> imageResList = imageList.stream()
                .map(this::createImageRes)
                .toList();

        return createShopRes(updatedShop, imageResList);


    }

}
