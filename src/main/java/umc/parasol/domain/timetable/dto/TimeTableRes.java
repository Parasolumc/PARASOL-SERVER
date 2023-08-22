package umc.parasol.domain.timetable.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.parasol.domain.timetable.domain.Day;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTableRes {

    private Day day;
    private String openTime;
    private String endTime;

    private TimeTableRes(Day day, String openTime, String endTime) {
        this.day = day;
        this.openTime = openTime;
        this.endTime = endTime;
    }

    public static TimeTableRes dayAndTime(Day day, String openTime, String endTime) {
        return new TimeTableRes(day, openTime, endTime);
    }
}
