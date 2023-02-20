package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * 用户组织机构model
 * @author: lkj
 */
@Data
public class CsUserDepartModel {

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
    /**部门id*/
    @Excel(name = "部门id", width = 15)
    @ApiModelProperty(value = "部门id")
    private String departId;
    /**部门id*/
    @Excel(name = "父部门id", width = 15)
    @ApiModelProperty(value = "父部门id")
    private String parentId;
    /**机构/部门名称*/
    @ApiModelProperty(value = "机构/部门名称")
    @Excel(name="机构/部门名称",width=15)
    private String departName;
    /**机构编码*/
    @ApiModelProperty(value = "机构编码")
    @Excel(name="机构编码",width=15)
    private String orgCode;
    @ApiModelProperty(value = "机构编码层级结构")
    private String orgCodeCc;
    @ApiModelProperty(value = "是否可选")
    @Excel(name="是否可选",width=15)
    private Boolean select;
    @ApiModelProperty(value = "子级")
    private List<CsUserDepartModel> childrenList;
    private String color;
}
