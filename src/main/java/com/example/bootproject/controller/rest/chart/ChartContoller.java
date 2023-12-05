package com.example.bootproject.controller.rest.chart;

import com.example.bootproject.service.chartservice.ChartService;
import com.example.bootproject.service.service3.api.AppealService;
import com.example.bootproject.service.service3.api.LoginService;
import com.example.bootproject.service.service3.api.LogoutService;
import com.example.bootproject.service.service3.api.VacationService;
import com.example.bootproject.vo.vo1.response.AttendanceInfoResponseDto;
import com.example.bootproject.vo.vo1.response.Chart.ResponseVacationEmployeeChart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.example.bootproject.system.util.ValidationChecker.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChartContoller {

    private final ChartService chartService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final VacationService vacationService;
    private final AppealService appealService;


    @GetMapping("/chart/vacationmine")
    public ResponseEntity<ResponseVacationEmployeeChart> getEmployeeVacationChart(HttpServletRequest req
    ) {
        String employeeId = getLoginIdOrNull(req);
        if (employeeId == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }



        ResponseVacationEmployeeChart responseVacationEmployeeChart = chartService.getEmployeeVacationChart(employeeId);

        // 응답 데이터를 ResponseEntity에 담아 반환
        return ResponseEntity.ok(responseVacationEmployeeChart);
    }

    //메인에 사용할 년월 달력데이터를 받아 현재 월의 연차 승인사용갯수현황에 필요한 api
    @GetMapping("/chart/approvalmonthvacation")
    public ResponseEntity<Integer> getApprovalMonthUseVacation(@RequestParam int year, @RequestParam int month, HttpServletRequest req
    )
    {

        String employeeId=getLoginIdOrNull(req);

        if(employeeId == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        int approvalVacationDays = chartService.getApprovalMonthUseVacation(year, month, employeeId);
        log.info("연과 월에대한 연차 승인 사용갯수",approvalVacationDays);
        return ResponseEntity.ok(approvalVacationDays);
    }

    @GetMapping("chart/requestedmonthvacation")
    public ResponseEntity<Integer>getRequetedMonthUseVacation(@RequestParam int year, @RequestParam int month, HttpServletRequest req
    ){
        String employeeId = getLoginIdOrNull(req);
        if(employeeId == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        int requestedVacationDays = chartService.getRequestedMonthUseVacation(year,month,employeeId);
        log.info("연과 월에대한 연차 승인 사용갯수",requestedVacationDays);
        return ResponseEntity.ok(requestedVacationDays);
    }

    @GetMapping("chart/rejectedmonthvacation")
    public ResponseEntity<Integer>getRejectedMonthUseVacation(@RequestParam int year, @RequestParam int month , HttpServletRequest req
    ){
        String employeeId = getLoginIdOrNull(req);
        if(employeeId == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        int rejectedVacationDays = chartService.getRejectedMonthUseVacation(year,month,employeeId);
        log.info("연과 월에대한 연차 거부 사용갯수",rejectedVacationDays);
        return ResponseEntity.ok(rejectedVacationDays);
    }

    @GetMapping("chart/abnormal")
    public ResponseEntity<Integer>getAbnormalAttendance(@RequestParam int year , @RequestParam int month , HttpServletRequest req
    ){
        String employeeId = getLoginIdOrNull(req);
        if(employeeId == null){
            return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        int abnormalAttendance = chartService.getApprovalMonthUseAttendance(year,month,employeeId);
        return ResponseEntity.ok(abnormalAttendance);
    }

    @GetMapping("chart/normal")
    public ResponseEntity<Integer>getNormalAttendance(@RequestParam int year , @RequestParam int month , HttpServletRequest req
    ){
        String employeeId = getLoginIdOrNull(req);
        if(employeeId == null){
            return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        int normalAttendance = chartService.getNoApprovalMonthUseAttendance(year,month,employeeId);
        return ResponseEntity.ok(normalAttendance);
    }

    @GetMapping("chart/requested")
    public ResponseEntity<Integer>getRequestedAttendance(@RequestParam int year , @RequestParam int month , HttpServletRequest req)
    {
        String employeeId = getLoginIdOrNull(req);
        if(employeeId == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        int requstedAttendance = chartService.getApprovalRequestedAttendance(year,month,employeeId);
        return ResponseEntity.ok(requstedAttendance);
    }


}