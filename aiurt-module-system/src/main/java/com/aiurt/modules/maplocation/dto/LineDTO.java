package com.aiurt.modules.maplocation.dto;

import com.aiurt.modules.position.entity.CsStation;
import lombok.Data;

import java.util.List;

@Data
public class LineDTO {
    String lineName;
    List<CsStation> station ;
}
