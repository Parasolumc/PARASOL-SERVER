package umc.parasol.domain.toss.dto.brandpay;

import lombok.Data;

@Data
public class BrandPayCard {

    private String id;

    private String methodKey;

    private String alias;

    private String cardName;

    private String cardNumber;

    private String issuerCode;

    private String acquirerCode;

    private String ownerType;

    private String cardType;

    private Integer installmentMinimumAmount;

    private String registeredAt;

    private String status;

    private String icon;

    private String iconUrl;

    private String cardImgUrl;

    private class color {
        private String background;
        private String text;
    }
}
