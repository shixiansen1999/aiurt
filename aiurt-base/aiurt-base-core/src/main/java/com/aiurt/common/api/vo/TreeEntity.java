package com.aiurt.common.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wgp
 * @Title: wgp
 * @Description:
 * @date 2021/4/18:34
 */
@Data
public class TreeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 树id
     */
    @ApiModelProperty(value = "树id")
    @TableId
    private Integer id;
    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String name;
    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private Integer parentId;
    /**
     * 树形结构的key
     */
    @ApiModelProperty(value = "（前端使用）树形结构的key")
    private String key;
    /**
     * 树形结构的value
     */
    @ApiModelProperty(value = "（前端使用）树形结构的value")
    private String title;
    /**
     * 子节点
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "子节点")
    private List<TreeEntity> children = new ArrayList<>();
    /**
     * 是否有子节点
     */
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
    /**
     * 层级
     */
    private Integer catLevel;
    /**
     * 是否显示[0-不显示，1显示]
     */
    @TableLogic(value = "1", delval = "0")
    private Integer showStatus;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 图标地址
     */
    private Slots slots;

    /**
     * 专业id
     */
    @ApiModelProperty(value = "专业id")
    private Integer deptId;
    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private Integer typeId;
    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private String remark;

    @ApiModelProperty(value = "默认值")
    private Boolean defaultValue = false;

}
