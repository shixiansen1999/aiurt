package com.aiurt.modules.schedule.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Column {
    private String title;
    private String align = "center";
    private String dataIndex;
}
