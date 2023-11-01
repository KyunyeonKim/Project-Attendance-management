package com.example.bootproject.service.service2;

import com.example.bootproject.vo.vo2.response.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ManagerService2 {
    public Page<List<VacationRequestDto>> getAllVacationHistory(PagingRequestWithDateDto pagingRequestWithDateDto);
    public Page<List<VacationRequestDto>> getEmpReqVacationHistory(PagingRequestWithIdDto pagingRequestWithIdDto);
    public Page<List<SettingWorkTimeDto>> getSettingWorkTime(PagingRequestDto pagingRequestDto);
    public List<VacationQuantitySettingDto> getVacationSettingHistory();
    public List<VacationRequestDto> getHistoryOfRejectedVacationOfEmployee(String employeeId);
}
