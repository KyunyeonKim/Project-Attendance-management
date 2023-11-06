package com.example.bootproject.repository.mapper1;

import com.example.bootproject.vo.vo1.request.*;
import com.example.bootproject.vo.vo1.response.*;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmployeeMapper1 {


    //출근기록
    @Insert("INSERT INTO attendance_info (employee_id, attendance_date, start_time) " +
            "VALUES (#{dto.employeeId}, #{dto.attendanceDate}, #{dto.startTime}) " +
            "ON DUPLICATE KEY UPDATE start_time = VALUES (start_time)")
    int startTimeRequest(@Param("dto") AttendanceInfoStartRequestDto dto);


    //응답 근태정보테이블
    @Select("SELECT * " +
            "FROM attendance_info " +
            "WHERE employee_id = #{employeeId} AND attendance_date = #{AttendanceDate}")
    AttendanceInfoResponseDto findattendanceInfo(String employeeId, LocalDate AttendanceDate);


    //출근내역찾기
    @Select("SELECT start_time FROM attendance_info " +
            "WHERE employee_id = #{employeeId} AND attendance_date = #{date}")
    LocalDateTime getStartTimeByEmployeeIdAndDate(String employeeId, LocalDate date);


    //퇴근기록
    @Insert("UPDATE attendance_info SET end_time = #{dto.endTime} " +
            "WHERE employee_id = #{dto.employeeId} AND attendance_date = #{dto.attendanceDate}")
    int endTimeRequest(@Param("dto") AttendanceInfoEndRequestDto dto);

    //퇴근내역찾기
    @Select("SELECT end_time FROM attendance_info " +
            "WHERE employee_id = #{employeeId} AND attendance_date = #{date}")
    LocalDateTime getEndTimeByEmployeeIdAndDate(String employeeId, LocalDate date);


    //사원년월일 사원근태정보검색
    @Select("SELECT " +
            "attendance_info_id, " +
            "attendance_status_category, " +
            "employee_id, " +
            "start_time, " +
            "end_time, " +
            "attendance_date " +
            "FROM attendance_info " +
            "WHERE attendance_date = #{attendanceDate} AND employee_id = #{employeeId} " +
            "ORDER BY ${sort} ${desc} " + // sort 파라미터가 'attendance_date'가 되도록 확실히 하세요.
            "LIMIT #{size} OFFSET #{startRow}")
    List<AttendanceInfoResponseDto> selectAttendanceByDate(@Param("attendanceDate") LocalDate attendanceDate,
                                                           @Param("employeeId") String employeeId,
                                                           @Param("sort") String sort,
                                                           @Param("desc") String desc,
                                                           @Param("size") int size,
                                                           @Param("startRow") int startRow);


    //사원년월 사원근태정보검색
    @Select("SELECT " +
            "attendance_info_id, " +
            "attendance_status_category, " +
            "employee_id, " +
            "start_time, " +
            "end_time, " +
            "attendance_date " +
            "FROM attendance_info " +
            "WHERE employee_id = #{employeeId} " +
            "AND YEAR(attendance_date) = #{year} " +
            "AND MONTH(attendance_date) = #{month} " +
            "ORDER BY ${sort} ${desc} " + // 여기에 공백 추가
            "LIMIT #{size} OFFSET #{startRow}"
    )
    List<AttendanceInfoResponseDto> selectAttendanceByMonthAndEmployee(int year, int month, String employeeId, String sort, String desc,
                                                                       int size, int startRow);

    @Select({
            "SELECT COUNT(*) FROM attendance_info WHERE employee_id = #{employeeId} AND YEAR(attendance_date) = #{year} AND MONTH(attendance_date) = #{month} "
    })
    int countAttendanceInfoByEmployeeId(@Param("employeeId") String employeeId, int year, int month);


    @Select({
            "SELECT COUNT(*) FROM attendance_info WHERE employee_id = #{employeeId} AND attendance_date = #{attendanceDate} "
    })
    int countAttendanceInfoByMonthEmployeeId(@Param("employeeId") String employeeId, LocalDate attendanceDate);

    //세션에서 attendance_info정보를 찾아오는걸로 변경
    //'지각' key값을 찾는 쿼리문이다
    @Select("SELECT * FROM attendance_status_category WHERE `key` = 'abnormal'")
    AttendanceStatusCategoryRequestDto findLateStatus();

    //todo 그럼 자바에서 저장해서 public static final

    //attendance_info테이블에 대리키를 조회해 현재상태를 만약 근태이상이라고 있으면 이거를 인정한 지각이라는 데이터로 변경한다
    @Update("UPDATE attendance_info SET attendance_status_category = #{dto.attendanceStatusCategory} WHERE attendance_info_id = #{dto.attendanceInfoId}")
    int updateAttendanceStatus(@Param("dto") AttendanceApprovalUpdateRequestDto attendanceApprovalUpdateRequestDto);
//    dto보단 key만 적는다

    //근태정보--승인 테이블에 승인을 한 내역을 남긴다
    @Insert("INSERT IGNORE INTO attendance_approval (attendance_info_id, attendance_approval_date, employee_id) VALUES (#{dto.attendanceInfoId}, NOW(), #{dto.employeeId}) ")
    int insertAttendanceApproval(@Param("dto") AttendanceApprovalInsertRequestDto attendanceApprovalInsertRequestDto);

    //근태이상확인
    //todo 근태상태가 근태이상인지 확인하고 지각승인을 할 즉 update를 할 내역조회할 쿼리가 필요하다

    @Select("SELECT attendance_approval_id , attendance_info_id , attendance_approval_date , employee_id " +
            "FROM attendance_approval " +
            "WHERE employee_id = #{employeeId} AND attendance_info_id = #{attendanceInfoId}")
    AttendanceApprovalResponseDto findAttendanceApproval(String employeeId, Long attendanceInfoId);


    //자신의 근태이상승인내역

    @Select("SELECT * FROM attendance_approval WHERE employee_id = #{employeeId} ORDER BY ${sort} ${desc} LIMIT #{size} OFFSET #{startRow}")
    List<AttendanceApprovalUpdateResponseDto> getAllEmployeeByEmployeeId(
            @Param("employeeId") String employeeId,
            @Param("sort") String sort,
            @Param("desc") String desc,
            @Param("size") int size,
            @Param("startRow") int startRow
    );

    //근태이상승이내역전체갯수조회
    @Select({
            "SELECT COUNT(*) FROM attendance_approval WHERE employee_id = #{employeeId}"
    })
    int countApprovalInfoByEmployeeId(@Param("employeeId") String employeeId);


    //본인의 조정 요청 이력 조회
    @Select(
            "SELECT attendance_appeal_request_id, status, reason, attendance_info_id, appealed_start_time, appealed_end_time, employee_id, attendance_appeal_request_time, reason_for_rejection " +
                    "FROM attendance_appeal_request " +
                    "WHERE employee_id = #{employeeId} " +
                    "ORDER BY ${sort} ${desc} " +
                    "LIMIT #{size} " +
                    "OFFSET #{startRow} "
    )
    List<AttendanceAppealMediateResponseDto> findAttendanceAppealByEmployeeId(
            @Param("employeeId") String employeeId,
            @Param("sort") String sort,
            @Param("desc") String desc,
            @Param("size") int size,
            @Param("startRow") int startRow
    );

    //본인의 조정 요청 이력 갯수확인
    @Select({
            "SELECT COUNT(*) FROM attendance_appeal_request WHERE employee_id = #{employeeId}"
    })
    int countAttendanceAppealByEmployeeId(@Param("employeeId") String employeeId);


    //이름검색
    @Select("SELECT * FROM employee WHERE name LIKE CONCAT('%', #{searchParameter}, '%')")
    List<EmployeeSearchResponseDto> searchEmployeeName(String searchParameter);

    //사원id검색
    @Select("SELECT * FROM employee WHERE employee_id LIKE CONCAT('%', #{searchParameter}, '%')")
    List<EmployeeSearchResponseDto> searchEmployeeEmployeeId(String searchParameter);


    @Select("SELECT EXISTS(SELECT 1 FROM employee WHERE employee_id = #{employeeId})")
    boolean existsById(String employeeId);


}