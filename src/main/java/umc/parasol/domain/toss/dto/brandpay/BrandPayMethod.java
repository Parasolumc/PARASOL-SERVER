package umc.parasol.domain.toss.dto.brandpay;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BrandPayMethod {

    private boolean isIdentified;

    private String selectedMethodId;

    private ArrayList<BrandPayCard> cards;

    private ArrayList<BrandPayBankAccount> accounts;
}
