package com.aiurt.modules.common.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("下列列表")
public class SelectTable {

    private String value;

    private String label;

    private List<SelectTable> children;
}
