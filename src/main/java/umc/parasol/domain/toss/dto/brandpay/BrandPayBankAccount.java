package umc.parasol.domain.toss.dto.brandpay;

import lombok.Data;

@Data
public class BrandPayBankAccount {

    private String id;

    private String methodKey;

    private String accountName;

    private String accountNumber;

    private String alias;

    private String bankCode;

    private String icon;

    private String iconUrl;

    private String registeredAt;

    private String status;

    private class color {
        private String background;
        private String text;
    }
}
