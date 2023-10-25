package com.aiurt.modules.flow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class HistoricTaskReqDTO implements Serializable {
    private static final long serialVersionUID = -3916488710463959140L;

    private String processDefinitionName;

    private String userName;

    private Date beginDate;

    private Date endDate;

    private List<String> startTime;

    private Integer pageNo =1;
    private Integer pageSize =10;
}
