package com.aiurt.boot.screen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author JB
 * @Description: 业务层封装的传输实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScreenTran {
    /**
     * 起始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 作废状态
     */
    private Integer discardStatus;
    /**
     * 组织机构编号
     */
    private List<String> orgCodes;
//    /**
//     * 线路编号
//     */
//    private List<String> lines;
//    /**
//     * 专业编号
//     */
//    private List<String> majors;
}
