package umc.parasol.domain.image.dto;

import lombok.Data;

@Data
public class ImageRes {

    private Long id;
    private String url;

    public ImageRes(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}
