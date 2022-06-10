package com.aiurt.boot.modules.schedule.model;

import lombok.Data;

/**
 * @Description:
 * @author: niuzeyu
 * @date: 2022年01月19日 11:05
 */
@Data
public class SysUserScheduleModel  {
    private String userId;
    private String realName;
    private String itemName;
    private Integer status;
}
