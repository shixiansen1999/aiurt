package com.aiurt.boot.category.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: fixed_assets_category
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
public class FixedAssetsCategoryImport {

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**父级ID，第一级默认0*/
    @ApiModelProperty(value = "父级ID，第一级默认0")
    private java.lang.String pid;
    /**上级节点*/
    @Excel(name = "上级节点", width = 15)
    @ApiModelProperty(value = "上级节点")
    private java.lang.String pidName;
    /**分类名称*/
    @Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private java.lang.String categoryName;
    /**导入后拼接的分类编码*/
    @ApiModelProperty(value = "分类编码")
    private java.lang.String categoryCode;
    /**导入的分类编码*/
    @Excel(name = "分类编码", width = 15)
    @ApiModelProperty(value = "分类编码")
    private java.lang.String splicingCode;
    /**分类编码*/
    @ApiModelProperty(value = "层级")
    private java.lang.String level;
    /**是否已经导入上级节点*/
    @ApiModelProperty(value = "是否已经导入上级节点")
    private java.lang.Boolean isNotImportParentNode;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**错误原因*/
    @Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    private java.lang.String wrongReason;

}
