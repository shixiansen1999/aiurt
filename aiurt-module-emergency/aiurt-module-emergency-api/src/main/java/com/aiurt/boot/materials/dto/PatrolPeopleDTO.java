package com.aiurt.boot.materials.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

/**
 * @author zwl
 */
@Data
public class PatrolPeopleDTO {

    /**当前登录巡视人Id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "当前登录巡视人Id")
    private java.lang.String patrolId;

    /**当前登录巡视人Id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "当前登录巡视人")
    private java.lang.String patrolName;


    /**当前登录人同部门巡视人集合*/
    @TableField(exist = false)
    @ApiModelProperty(value = "同部门巡视人列表")
    private List<LoginUser> loginUserList;
}
