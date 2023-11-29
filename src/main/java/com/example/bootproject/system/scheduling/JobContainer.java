package com.example.bootproject.system.scheduling;

import com.example.bootproject.repository.mapper1.EmployeeMapper1;
import com.example.bootproject.repository.mapper3.attendanceinfo.AttendanceInfoMapper;
import com.example.bootproject.service.service3.api.CalendarService;
import com.example.bootproject.vo.vo1.request.calendar.holiday.CalendarSearchRequestDtoForHoliday;
import com.example.bootproject.vo.vo1.response.attendance.AttendanceCheckResponse;
import com.example.bootproject.vo.vo1.response.calendar.holiday.ApiItemToEventDtoForHoliday;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.bootproject.system.StaticString.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobContainer {

    private final AttendanceInfoMapper attendanceInfoMapper;
    private final EmployeeMapper1 employeeMapper;
    private final CalendarService calendarService;
    //공휴일에는 동작을 안하게 막아야한다
    public boolean checkIsTodayHoliday() throws JSONException, IOException, URISyntaxException, ParseException {
        //TODO:Test 코드이므로 반드시 삭제 - now()로 변경 필요
        LocalDate today = LocalDate.of(2023,12,25);
        CalendarSearchRequestDtoForHoliday dto = new CalendarSearchRequestDtoForHoliday();
        //TODO:Test 코드이므로 반드시 삭제
        dto.setMonth(12);
        List<ApiItemToEventDtoForHoliday> holidays = calendarService.getHolidayEvents(dto);

        log.info("checkIsTodayHoliday");
        log.info("holidays {}",holidays);
        return holidays.stream().anyMatch((holiday) -> {
            if (holiday.getDate().equals(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                return true;
            } return false;
        });
    }


    @Scheduled(cron = "0 58 13 * * *")
    public void autoInsertAttedanceInfo() throws JSONException, IOException, URISyntaxException, ParseException {
        if(checkIsTodayHoliday()){
            return;
        }
        int affected = attendanceInfoMapper.insertAttendanceInfo();
        log.info("auto insert attendance information affected result {} ", affected);
    }

    @Scheduled(cron = "0 30 19 * * *")
    public void autoCheckAttendanceStatus() throws JSONException, IOException, URISyntaxException, ParseException {
        if(checkIsTodayHoliday()){
            return;
        }
        log.info("auto check attendance information affected start");
        List<AttendanceCheckResponse> infos = attendanceInfoMapper.getListAttendanceInfoOfTodayAfterAutoInsert();
        AtomicInteger[] affected = new AtomicInteger[1];
        affected[0] = new AtomicInteger(0);
        //Todo : 정규 출근 시간, 정규 퇴근 시간 불러오기
        LocalDateTime regularEndTime = LocalDateTime.now().minusHours(1L);
        LocalDateTime regularStartTime = LocalDateTime.now().minusHours(9L);
        infos.stream().forEach(info -> {
            Long attendanceInfoId = info.getAttendanceInfoId();
            LocalDateTime startTime = info.getStartTime();
            LocalDateTime endTime = info.getEndTime();
            if (startTime == null && endTime == null) {
                //결근
                attendanceInfoMapper.updateAttendanceInfoStatus(ATTENDANCE_INFO_STATUS_ABSENT, info.getAttendanceInfoId());
                log.info("attendanceInfoId = {}, startTime = {} endTime = {} result = {}", attendanceInfoId, startTime, endTime, "결근");
            } else if (endTime.isBefore(regularEndTime) && startTime.isBefore(regularStartTime)) {
                //정상 출근 +조기 퇴근
                attendanceInfoMapper.updateAttendanceInfoStatus(ATTENDANCE_INFO_STATUS_NORMAL_START_EARLY_END, info.getAttendanceInfoId());
                log.info("attendanceInfoId = {}, startTime = {} endTime = {} result = {}", attendanceInfoId, startTime, endTime, "정상 출근 +조기 퇴근");
            } else if (endTime.isBefore(regularEndTime) && startTime.isAfter(regularStartTime)) {
                //지각 출근 +조기 퇴근
                attendanceInfoMapper.updateAttendanceInfoStatus(ATTENDANCE_INFO_STATUS_LATE_START_EARLY_END, info.getAttendanceInfoId());
                log.info("attendanceInfoId = {}, startTime = {} endTime = {} result = {}", attendanceInfoId, startTime, endTime, "지각 출근 +조기 퇴근");
            } else if (endTime.isAfter(regularEndTime) && startTime.isAfter(regularStartTime)) {
                //지각 출근 + 정규 퇴근
                attendanceInfoMapper.updateAttendanceInfoStatus(ATTENDANCE_INFO_STATUS_LATE_START_NORMAL_END, info.getAttendanceInfoId());
                log.info("attendanceInfoId = {}, startTime = {} endTime = {} result = {}", attendanceInfoId, startTime, endTime, "지각 출근 + 정규 퇴근");
            } else if (endTime.isAfter(regularEndTime) && startTime.isBefore(regularStartTime)) {
                //정규 출근 + 정규 퇴근
                attendanceInfoMapper.updateAttendanceInfoStatus(ATTENDANCE_INFO_STATUS_NORMAL, info.getAttendanceInfoId());
                log.info("attendanceInfoId = {}, startTime = {} endTime = {} result = {}", attendanceInfoId, startTime, endTime, "정규 출근 + 정규 퇴근");
            } else if (startTime.isBefore(regularStartTime) && (endTime == null || endTime.isAfter(regularEndTime))) {
                attendanceInfoMapper.updateAttendanceInfoStatus(ATTENDANCE_INFO_STATUS_NORMAL_START_NULL_END, info.getAttendanceInfoId());
                log.info("attendanceInfoId = {}, startTime = {} endTime = {} result = {}", attendanceInfoId, startTime, endTime, "정상 출근 + 야근 상태");
                //정상 출근 + 야근 상태
            } else if (startTime.isAfter(regularStartTime) && (endTime == null || endTime.isAfter(regularEndTime))) {
                //지각 출근 + 야근 상태
                attendanceInfoMapper.updateAttendanceInfoStatus(ATTENDANCE_INFO_STATUS_LATE_START_NULL_END, info.getAttendanceInfoId());
                log.info("attendanceInfoId = {}, startTime = {} endTime = {} result = {}", attendanceInfoId, startTime, endTime, "지각 출근 + 야근 상태");
            }
            affected[0].incrementAndGet();
        });
        /*
         * 근태 이상 종류
         *
         * 결근
         * 지각 - 출근과 합치기
         * 지각 + 조기퇴근
         * 정상 출근 + 조기 퇴근
         * 야근 상태
         * */
        log.info("auto check attendance information affected result {} ", affected[0].get());
    }

}
