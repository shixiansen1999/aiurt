package com.aiurt.modules.maplocation.dto;

import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import lombok.Data;

import java.util.List;

@Data
public class LineDTO {
    String id;
    String lineCode;
    String lineName;
    List<CsStation> children ;
}
