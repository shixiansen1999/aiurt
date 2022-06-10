package com.aiurt.boot.modules.system.model;

import com.aiurt.boot.modules.schedule.model.SysUserScheduleModel;
import lombok.Data;

import java.util.List;

/**
 * @author: niuzeyu
 * @date: 2022年01月18日 17:11
 */
@Data
public class DepartScheduleModel {
    /**
     * 班组
     */
    private String departName;
    /**
     * 班组内人员数量
     */
    private Long num;
    /**
     * 班长
     */
    private String monitor;
    /**
     * 今日当班人员
     */
    List<SysUserScheduleModel> dutyUsers;
}
