package org.jeecg.common.system.vo;


import lombok.Data;

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
    private Integer num;
    /**
     * 班长
     */
    private String monitor;
    /**
     * 今日当班人员
     */
    // todo 后期修改
//    List<SysUserScheduleModel> dutyUsers;
}
