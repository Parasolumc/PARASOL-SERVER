package umc.parasol.domain.shop.dto;

import lombok.Data;
import umc.parasol.domain.timetable.dto.TimeTableReq;

import java.util.List;

@Data
public class UpdateInfoReq {
    private String desc;

    private List<TimeTableReq> times;
}
