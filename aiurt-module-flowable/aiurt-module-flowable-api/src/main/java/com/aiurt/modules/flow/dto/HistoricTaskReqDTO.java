package com.aiurt.modules.flow.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class HistoricTaskReqDTO implements Serializable {
    private static final long serialVersionUID = -3916488710463959140L;

    private String processDefinitionName;
    private String beginDate;
    private String endDate;
    private Integer pageNo =1;
    private Integer pageSize =10;
}
