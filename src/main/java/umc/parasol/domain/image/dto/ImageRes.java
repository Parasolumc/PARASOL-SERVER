package umc.parasol.domain.image.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ImageRes {

    private Long id;
    private String url;

    @Builder
    public ImageRes(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}
