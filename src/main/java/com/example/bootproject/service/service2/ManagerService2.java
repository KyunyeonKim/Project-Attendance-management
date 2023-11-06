package com.example.bootproject.service.service2;

import com.example.bootproject.vo.vo2.request.DefaultVacationRequestDto;
import com.example.bootproject.vo.vo2.request.PagingRequestDto;
import com.example.bootproject.vo.vo2.request.PagingRequestWithDateDto;
import com.example.bootproject.vo.vo2.request.PagingRequestWithIdStatusDto;
import com.example.bootproject.vo.vo2.response.*;

import java.util.List;

public interface ManagerService2 {
    public Page<List<VacationRequestDto>> getAllVacationHistory(PagingRequestWithDateDto pagingRequestWithDateDto);
    public Page<List<VacationRequestDto>> getHistoryVacationOfEmployee(PagingRequestWithIdStatusDto pagingRequestWithIdStatusDto);

    public Page<List<SettingWorkTimeDto>> getSettingWorkTime(PagingRequestDto pagingRequestDto);
    public Page<List<VacationQuantitySettingDto>> getVacationSettingHistory(PagingRequestDto pagingRequestDto);

    public DefaultVacationResponseDto makeDefaultVacationResponse(DefaultVacationRequestDto dto);

    public int getDefaultSettingValue(String employeeId);

    public int getEmployeeCheck(String id);

}
