package com.example.bootproject.service.service1;


import com.example.bootproject.repository.mapper.EmployeeMapper1;

import com.example.bootproject.vo.vo1.page.Page;
import com.example.bootproject.vo.vo1.request.*;
import com.example.bootproject.vo.vo1.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EmployeeService1Impl implements EmployeeService1 {

        private final EmployeeMapper1 employeeMapper1;




        //TODO 예외처리 컨트롤러로 보내기
        //출근요청
        @Override
        public AttendanceInfoResponseDto makeStartResponse(AttendanceInfoStartRequestDto dto, String employeeId) {
                // Check if the employee exists
                if (!employeeExists(employeeId)) {
                        log.info("이 아이디가 존재하지 않는다: {}", employeeId);
                        return null;
                }

                LocalDate attendanceDate = LocalDate.now();
                LocalDateTime findStartTime = employeeMapper1.getStartTimeByEmployeeIdAndDate(employeeId, attendanceDate);

                // Check if the start time already exists
                if (findStartTime != null) {
                        // 출근 시간이 이미 기록되어 있는 경우
                        log.info("출근기록이있습니다: {}", employeeId);
                        return null;
                } else {
                        // 출근 시간이 아직 기록되지 않은 경우, 출근 시간 기록 로직
                        LocalDateTime startTime = LocalDateTime.now();
                        dto.setEmployeeId(employeeId);
                        dto.setAttendanceDate(attendanceDate);
                        dto.setStartTime(startTime);

                        // DB에 출근 시간 기록
                        employeeMapper1.startTimeRequest(dto);

                        // 기록된 출근 정보를 바탕으로 AttendanceInfoResponseDto 가져오기
                        AttendanceInfoResponseDto responseDto = employeeMapper1.findattendanceInfo(employeeId, attendanceDate);
                        if (responseDto != null) {
                                // 조회 성공, 출근 정보 반환
                                return responseDto;
                        } else {
                                // 조회 실패, 로그 기록 후 null 반환
                                log.info("출근 정보를 조회하는데 실패하였습니다: {}", employeeId);
                                return null;
                        }
                }
        }


 /*
- 사원 ID로 사원의 존재 여부를 확인한다.
- 사원 ID가 없을 경우, 로그를 남기고 null을 반환하여 204 No Content 응답을 나타낸다.
- 사원 ID가 존재하지 않는 경우, 로그를 남기고 null을 반환하여 400 Bad Request 응답을 나타낸다.
- 해당 날짜에 대해 사원의 출근 시간을 조회한다.
- 출근 시간이 이미 기록되어 있으면 로그를 남기고 null을 반환한다.
- 출근 시간이 없으면 현재 시간으로 출근 시간을 기록하고 데이터베이스에 저장한다.
- 기록 후, 출근 정보를 조회하여 반환한다.
- 조회에 성공하면 출근 정보를 담아 200 OK 응답과 함께 반환한다.
- 조회에 실패하면 로그를 남기고 null을 반환하여 400 Bad Request 응답을 나타낸다.
*/

        //퇴근요청
        @Override
        public AttendanceInfoResponseDto makeEndResponse(AttendanceInfoEndRequestDto dto, String employeeId) {

                if (!employeeExists(employeeId)) {
                        log.info("이 아이디가 존재하지 않는다: {}", employeeId);
                        return null;
                }
                LocalDate attendanceDate=LocalDate.now();
                LocalDateTime findStartTime = employeeMapper1.getStartTimeByEmployeeIdAndDate(employeeId,attendanceDate);
                LocalDateTime findEndTime = employeeMapper1.getEndTimeByEmployeeIdAndDate(employeeId,attendanceDate);

                if(findStartTime == null){
                        log.info("출근기록이있습니다");
                        return null;
                } else if(findEndTime!= null){

                        log.info("퇴근기록이있습니다");
                        return null;
                }else{
                        LocalDateTime endTime = LocalDateTime.now();
                        dto.setEmployeeId(employeeId);
                        dto.setAttendanceDate(attendanceDate);
                        dto.setEndTime(endTime);

                        int endKey = employeeMapper1.endTimeRequest(dto);
                        if(endKey >0){
                                AttendanceInfoResponseDto responseDto = employeeMapper1.findattendanceInfo(employeeId,attendanceDate);
                                if(responseDto !=null){
                                        return responseDto;
                                }else{
                                        log.info("출근정보를 조회하는데 실패하였습니다");
                                        return null;
                                }
                        }
                        else {
                                log.info("기록에 실패하셨습니다");
                                return null;
                        }


                }

        }

  /*
- 사원 ID로 사원의 존재 여부를 확인한다.
- 사원 ID가 없을 경우, 로그를 남기고 null을 반환
- 사원 ID에 대한 유효성 검사에 실패하면 로그를 남기고 null을 반환
- 해당 날짜에 대해 사원의 출근 시간과 퇴근 시간을 조회한다.
- 출근 시간이 없으면 로그를 남기고 null을 반환한다.
- 퇴근 시간이 이미 기록되어 있으면 로그를 남기고 null을 반환한다.
- 퇴근 시간이 없으면 현재 시간으로 퇴근 시간을 기록하고 데이터베이스에 저장한다.
- 기록 후, 퇴근 정보를 조회하여 반환한다
- 조회에 실패하면 로그를 남기고 null을 반환
*/


//        사원 년,월,일 사원근태정보검색
        @Override
        public List<AttendanceInfoResponseDto> getAttendanceByDateAndEmployee(LocalDate attendanceDate, String employeeId) {
                if (!employeeExists(employeeId)) {
                        log.info("이 아이디가 존재하지 않는다: {}", employeeId);
                        return null;
                }
                return employeeMapper1.selectAttendanceByDate(attendanceDate,employeeId);
        }

        /*
- 사원 ID로 사원의 존재 여부를 확인한다.
- 사원 ID가 없을 경우, 로그를 남기고 null을 반환하여 204 No Content 응답을 나타낸다.
- 유효한 사원 ID가 있을 경우, 해당 날짜의 사원 근태 정보를 조회한다.
- 조회된 정보가 없으면 로그를 남기고 null을 반환하여 204 No Content 응답을 나타낸다.
- 조회에 성공하면 근태 정보 목록을 담아 200 OK 응답과 함께 반환한다.
*/


        //사원 년,월 사원근태정보검색
        @Override
        public List<AttendanceInfoResponseDto> getAttendanceByMonthAndEmployee(int year, int month, String employeeId) {
                if (!employeeExists(employeeId)) {
                        log.info("이 아이디가 존재하지 않는다: {}", employeeId);
                        return null;
                }
                return employeeMapper1.selectAttendanceByMonthAndEmployee(year , month ,employeeId);
        }

 /*
- 사원 ID로 사원의 존재 여부를 확인한다.
- 사원 ID가 없을 경우, 로그를 남기고 null을 반환하여 204 No Content 응답을 나타낸다.
- 유효한 사원 ID가 있을 경우, 해당 연월의 사원 근태 정보를 조회한다.
- 조회된 정보가 없으면 로그를 남기고 null을 반환하여 204 No Content 응답을 나타낸다.
- 조회에 성공
*/

        //자신의 근태승인요청

        public AttendanceApprovalResponseDto approveAttendance(
                                                               String employeeId, Long attendanceInfoId) {
                if (!employeeExists(employeeId)) {
                        log.info("이 아이디가 존재하지 않는다: {}", employeeId);
                        return null;
                }

                AttendanceStatusCategoryRequestDto lateStatus = employeeMapper1.findLateStatus();
                log.info("지각 상태 정보를 가져왔습니다: {}", lateStatus);

                // 2. 근태 상태 업데이트
                AttendanceApprovalUpdateRequestDto updateRequestDto = new AttendanceApprovalUpdateRequestDto(
                        lateStatus.getKey(),
                        attendanceInfoId
                );
                int updatedRows = employeeMapper1.updateAttendanceStatus(updateRequestDto);
                log.info("근태 상태를 업데이트 했습니다. 업데이트된 행의 수: {}", updatedRows);

                // 3. 근태 승인 정보 삽입
                AttendanceApprovalInsertRequestDto insertRequestDto = new AttendanceApprovalInsertRequestDto(
                        attendanceInfoId,
                        employeeId
                );
                int insertedRows = employeeMapper1.insertAttendanceApproval(insertRequestDto);
                log.info("근태 승인 정보를 삽입했습니다. 삽입된 행의 수: {}", insertedRows);

                // 4. 근태 승인 정보를 검색하여 반환
                return employeeMapper1.findAttendanceApproval(employeeId, attendanceInfoId);
        }

        /*
- 사원 ID의 존재 유무를 확인한다.
- 존재하지 않는 경우, 로그를 남기고 null을 반환한다.
- 지각 상태 정보를 조회하고, 근태 상태를 업데이트한다.
- 업데이트된 행 수를 로그로 남긴다.
- 근태 승인 정보를 데이터베이스에 삽입한다.
- 삽입된 행 수를 로그로 남긴다.
- 근태 승인 정보를 조회하여 반환한다.
*/

        //todo  2번조건이 성립되면 3번으로 넘어가게

        //자신의 근태 승인내역
        @Override
        public Page<List<AttendanceApprovalUpdateResponseDto>> getApprovalInfoByEmployeeId(String employeeId, int page, String sort, String desc) {
                int size = Page.PAGE_SIZE;
                int startRow = (page - 1) * size;

                if (!employeeExists(employeeId)) {
                        log.info("이 아이디가 존재하지 않는다: {}", employeeId);
                        return null;
                }

                // 데이터베이스에서 데이터를 조회합니다.
                List<AttendanceApprovalUpdateResponseDto> data = employeeMapper1.getAllEmployeeByEmployeeId(
                        employeeId, sort, desc, size, startRow);
                int totalElement = employeeMapper1.countApprovalInfoByEmployeeId(employeeId);

                // 다음 페이지 존재 여부 계산
                boolean hasNext = (page * size) < totalElement;

                // Page 객체 생성. 여기서 data는 List<AttendanceApprovalUpdateResponseDto> 입니다.
                Page<List<AttendanceApprovalUpdateResponseDto>> result = new Page<>(
                        data,
                        !hasNext,
                        sort,
                        desc,
                        page,
                        totalElement
                );

                return result;
        }
/*
        getApprovalInfoByEmployeeId 메소드:
                - 사원 ID의 존재 유무를 확인한다.
- 존재하지 않는 경우, 로그를 남기고 null을 반환한다.
                - 정렬과 페이징 정보를 기반으로 근태 승인 내역을 데이터베이스에서 조회한다.
- 조회 결과를 Page 객체에 담아 반환한다.
*/

        //본인의 조정요청이력조회
        @Override
        public Page<List<AttendanceAppealMediateResponseDto>> findAttendanceInfoByMine(String employeeId,int page, String sort, String desc) {


                if (!employeeExists(employeeId)) {
                        log.info("이 아이디가 존재하지 않는다: {}", employeeId);
                        return null;
                }

                int size = Page.PAGE_SIZE;
                int startRow = (page - 1) * size;
                List<AttendanceAppealMediateResponseDto> data = employeeMapper1.findAttendanceAppealByEmployeeId(employeeId, sort, desc, size, startRow);
                int totalElements = employeeMapper1.countAttendanceAppealByEmployeeId(employeeId);
                boolean hasNext = (page * size) < totalElements;

                // 로그를 남깁니다.
                log.info("사원 ID {}에 대한 조정요청 목록: {}", employeeId, totalElements);

                Page<List<AttendanceAppealMediateResponseDto>> result = new Page<>(
                        data, // 바로 data를 넘깁니다. List<T> 타입을 만족합니다.
                        !hasNext, // 다음 페이지가 없으면 isLastPage는 true입니다.
                        sort,
                        desc,
                        page,
                        totalElements
                );

                return result;
        }

        /*findAttendanceInfoByMine 메소드:
- 정렬과 페이징 정보를 기반으로 사원의 조정 요청 이력을 데이터베이스에서 조회한다.
- 조회된 목록과 페이징 정보를 Page 객체에 담아 반환한다.
- 사원 ID와 조회된 이력의 총 수를 로그로 남긴다.
*/

        //사원검색
        @Override
        public List<EmployeeSearchResponseDto> searchByEmployeeIdOrName(String searchParameter) {
                // 숫자만 있는지 검사합니다.
                if (searchParameter.matches("\\d+")) {
                        // 숫자일 경우, employeeId로 검색
                        return employeeMapper1.searchEmployeeEmployeeId(searchParameter);
                } else {
                        // 문자열일 경우, name으로 검색
                        return employeeMapper1.searchEmployeeName(searchParameter);
                }
        }

          /*
        searchByEmployeeIdOrName 메소드:
                - 검색 파라미터가 숫자인지 문자인지에 따라 사원 ID 또는 이름으로 검색을 수행한다.
- 검색 결과를 리스트로 반환한다.
                */

        @Override
        public boolean employeeExists(String employeeId) {
                return employeeMapper1.existsById(employeeId);
        }











}