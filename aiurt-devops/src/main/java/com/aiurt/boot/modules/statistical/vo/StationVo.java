package com.aiurt.boot.modules.statistical.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.system.entity.SysUser;
import lombok.Data;

import java.util.List;

@Data
public class StationVo extends Station {
    @TableField(exist = false)
    private List<SysUser> userList;

    @TableField(exist = false)
    private String teamPhone;

}
