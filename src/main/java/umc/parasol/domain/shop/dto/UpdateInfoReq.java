package umc.parasol.domain.shop.dto;

import lombok.Data;

@Data
public class UpdateInfoReq {
    private String desc;

    private String openTime;

    private String closeTime;

}
