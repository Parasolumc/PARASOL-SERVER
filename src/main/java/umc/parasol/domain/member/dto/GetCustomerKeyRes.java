package umc.parasol.domain.member.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetCustomerKeyRes {

    private UUID customerKey;
}
