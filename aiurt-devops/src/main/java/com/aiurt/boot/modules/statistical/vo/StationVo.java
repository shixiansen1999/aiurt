package com.aiurt.boot.modules.statistical.vo;

import com.aiurt.boot.modules.manage.entity.Station;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

@Data
public class StationVo extends Station {
    @TableField(exist = false)
    private List<LoginUser> userList;

    @TableField(exist = false)
    private String teamPhone;

}
