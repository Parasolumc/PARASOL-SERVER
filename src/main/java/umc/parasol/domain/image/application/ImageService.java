package umc.parasol.domain.image.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.parasol.domain.common.Status;
import umc.parasol.domain.image.domain.Image;
import umc.parasol.domain.image.domain.repository.ImageRepository;
import umc.parasol.domain.image.dto.ImageRes;
import umc.parasol.domain.member.domain.Member;
import umc.parasol.domain.member.domain.repository.MemberRepository;
import umc.parasol.domain.shop.domain.Shop;
import umc.parasol.domain.shop.domain.repository.ShopRepository;
import umc.parasol.global.config.security.token.UserPrincipal;
import umc.parasol.global.infrastructure.S3Uploader;
import umc.parasol.global.payload.ApiResponse;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;

    public ApiResponse upload(MultipartFile file, UserPrincipal user) throws Exception {
        String storedFileUrl = s3Uploader.outerUpload(file, "shop", user);
        Member owner = memberRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("해당 member가 없습니다."));
        Shop targetShop = owner.getShop();
        Image storedImage = createImageEntity(storedFileUrl, targetShop.getId());

        return ApiResponse.builder()
                .check(true)
                .information(new ImageRes(storedImage.getId(), storedImage.getUrl()))
                .build();
    }

    private Image createImageEntity(String url, Long shopId) {
        deleteDuplicatedImage(url);
        Shop targetShop = getShop(shopId);
        return imageRepository.save(new Image(url, targetShop));
    }

    private void deleteDuplicatedImage(String url) {
        Image existedImage = imageRepository.findByUrl(url);
        if (existedImage != null) {
            imageRepository.delete(existedImage);
        }
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId).orElseThrow(
                () -> new IllegalStateException("해당 shop이 존재하지 않습니다.")
        );
    }

    @Transactional
    public String delete(Long id, UserPrincipal user) {
        Member owner = memberRepository.findById(
                user.getId()).orElseThrow(() -> new IllegalStateException("해당 member가 없습니다."));

        Shop ownerShop = owner.getShop();
        Image targetImage = imageRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("해당 사진이 없습니다.")
        );

        if (!targetImage.getShop().equals(ownerShop))
            throw new IllegalStateException("사진의 매장과 일치하지 않습니다.");

        System.out.println("targetImage.getUrl() = " + targetImage.getUrl());
        s3Uploader.deleteS3Object(targetImage.getUrl());
        targetImage.updateStatus(Status.DELETE);

        return "삭제 완료";
    }
}
