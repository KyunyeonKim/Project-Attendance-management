package com.example.bootproject.service.service3.impl;

import com.example.bootproject.repository.mapper3.appeal.AppealMapper;
import com.example.bootproject.repository.mapper3.attendanceinfo.AttendanceInfoMapper;
import com.example.bootproject.service.service3.api.AppealService;
import com.example.bootproject.vo.vo3.request.appeal.AppealRequestDto;
import com.example.bootproject.vo.vo3.response.appeal.AppealRequestResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppealServiceImpl implements AppealService {
    private final AttendanceInfoMapper attendanceInfoMapper;
    private final AppealMapper appealMapper;
    private final HttpSession session;
    /*
     *
     * 1. 로그인 정보 확인 - 로그인 확인 API 혹은 로직 사용
     * 2. post - form을 통해 전달 받은 데이터를 유효성 검증 수행
     * 3. 서비스로 입력 데이터 전달
     * 4. 서비스에서 attendance_info_id 에 대해서 조정 요청 튜플이 있는지 확인
     *     1. 분기점 1 : 기존 조정 요청이 있는 경우
     *         1. 중복된 요청임을 알리는 응답을 담은 응답을 전달
     *     2. 분기점 1 : 기존 조정 요청이 없는 경우
     *         1. 분기점 1 - A : attendance_info_id가 잘못된 경우
     *             - dto에 이미 요청이 있음을 담아서 전달
     *         2. 분기점 1 - A : 정상적인 attendance_info_id 인 경우
     *             - 신청 내역 테이블에 insert  수행
     *   - 최종 응답 : generated key를 이용하여 다시 신청 내역을 조회하여 ResponseDto에 담아서 전달
     */

    @Override
    public AppealRequestResponseDto makeAppealRequest(AppealRequestDto dto) {
        Integer attendanceInfoIdExist = attendanceInfoMapper.countById(dto.getAttendanceInfoId());
        if (attendanceInfoIdExist < 1) {
            //TODO : 근태기록 자체를 찾지 못한 경우 응답 설정
            return null;
        }
        if (appealMapper.checkAppealRequestExist(dto.getAttendanceInfoId()) >= 1) {
            //TODO : 해당 근태기록에 대해 이미 조정 요청이 있는 경우 처리
            return null;
        }
        String employeeId = (String) session.getAttribute("loginId");
        appealMapper.makeRequest(dto, employeeId);
        AppealRequestResponseDto result = appealMapper.findById(dto.getAttendanceInfoId());
        return result;
    }
}