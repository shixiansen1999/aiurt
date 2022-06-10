package com.aiurt.boot.modules.statistical.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: niuzeyu
 * @date: 2022年01月21日 19:10
 */
@Data
public class FaultStatisticsVo implements Serializable {
    //故障总数
    private Integer faultNum;
    //未解决问题数
    private Integer unCompleteNum;
    //
    List<FaultStatisticsModal> modalList;
}
