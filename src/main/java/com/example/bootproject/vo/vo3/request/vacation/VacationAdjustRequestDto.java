package com.example.bootproject.vo.vo3.request.vacation;

import lombok.Data;

@Data
public class VacationAdjustRequestDto {
    private String adjustQuantity;
    private String reason;
    private String adjustType;
    private Long generatedKey;
}
