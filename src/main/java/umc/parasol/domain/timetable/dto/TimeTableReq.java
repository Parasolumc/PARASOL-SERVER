package umc.parasol.domain.timetable.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import umc.parasol.domain.timetable.domain.Day;

@Data
@NoArgsConstructor
public class TimeTableReq {

    private Day day;
    private String openTime;
    private String endTime;
}
