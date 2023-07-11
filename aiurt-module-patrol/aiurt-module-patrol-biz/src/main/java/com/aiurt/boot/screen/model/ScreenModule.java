package com.aiurt.boot.screen.model;

import com.aiurt.boot.constant.PatrolConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author JB
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScreenModule {
    /**
     * 结束时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 工单任务完成状态
     */
    private final Integer checkStatus = PatrolConstant.BILL_COMPLETE;
    /**
     * 工单检查结果异常状态
     */
    private final Integer checkResult = PatrolConstant.RESULT_EXCEPTION;
    /**
     * 工单检查mac地址匹配异常状态
     */
    private final Integer macStatus = PatrolConstant.MAC_MATCH_EXCEPTION;
    /**
     * 漏检状态
     */
    private Integer omit;
    /**
     * 异常状态
     */
    private Integer abnormal;
    /**
     * 作废状态
     */
    private Integer discardStatus;
    /**
     * 当天日期
     */
    private Date today;
    /**
     * 组织机构编号
     */
    private List<String> orgCodes;
    /**
     * 线路编号
     */
    private String lineCode;
    /**
     * 站点编号
     */
    private String stationCode;
    /**
     * 条件筛选的任务状态
     */
    private Integer[] taskDeviceStatus;
    /**
     * 巡检结果：0异常、1正常
     */
    private Integer state;
    /**
     * 巡视用户名称
     */
    private String username;
//    /**
//     * 专业编号
//     */
//    private List<String> majors;
    private Date patrolDate;
}
