package com.aiurt.modules.weeklyplan.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 工作场所表，存储用户工作场所信息
 * @Author: jeecg-boot
 * @Date: 2021-03-29
 * @Version: V1.0
 */
@Data
@TableName("bd_station")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_station对象", description = "工作场所表，存储用户工作场所信息")
public class BdStation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**
     * 站点名称
     */
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    @Length(max = 16, message = "站点名称长度不能超过16")
    private String name;
    /**
     * 线路
     */
    @Excel(name = "线路", width = 15, dictTable = "bd_line", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bd_line", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "线路")
    private Integer lineId;

    /**
     * 站点编码
     */
    @Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    @Length(max = 16, message = "站点编码长度不能超过16")
    private String code;
    /**
     * 同站点首次出现的id，对应ht_station表id，标识站点换乘关系
     */
    @Excel(name = "同站点首次出现的id，对应ht_station表id，标识站点换乘关系", width = 15)
    @ApiModelProperty(value = "同站点首次出现的id，对应ht_station表id，标识站点换乘关系")
    private String firstId;
    /**
     * 线路站点序号
     */
    @Excel(name = "线路站点序号", width = 15)
    @ApiModelProperty(value = "线路站点序号")
    private Integer indexId;
    /**
     * 巡检所属内容
     */
    @Excel(name = "巡检所属内容", width = 15)
    @ApiModelProperty(value = "巡检所属内容")
    private String patrolType;
    /**
     * 箱变
     */
    @Excel(name = "箱变", width = 15)
    @ApiModelProperty(value = "箱变")
    @Length(max = 60, message = "变电所长度不能超过60")
    private String transformerSubstation;
    /**
     * 巡逻时间
     */
    @Excel(name = "巡逻时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "巡逻时间")
    private Date patrolFormSetTime;
    /**
     * 站点位置x坐标
     */
    @Excel(name = "站点位置x坐标", width = 15)
    @ApiModelProperty(value = "站点位置x坐标")
    private Double positionX;
    /**
     * 站点位置y坐标
     */
    @Excel(name = "站点位置y坐标", width = 15)
    @ApiModelProperty(value = "站点位置y坐标")
    private Double positionY;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;

    @ApiModelProperty(value = "设备数据导入编码映射")
    private String deviceImportMappingCode;

    @ApiModelProperty(value = "设备数据导入班组映射")
    private Integer deviceImportMappingTeamId;

    @ApiModelProperty(value = "关联的变电所")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String associatedSubstation;

    /**
     * 普通巡视巡检周期
     */
    @ApiModelProperty(value = "普通巡视巡检周期")
    private Integer patrolCycle;
    /**
     * 专项巡视巡检周期
     */
    @ApiModelProperty(value = "专项巡视巡检周期")
    private Integer SpecialPatrolCycle;
    /**
     * 排序用
     */
    @ApiModelProperty(value = "排序用")
    @TableField(exist = false)
    private Integer sort;


    @ApiModelProperty(value = "父级id")
    private String pid;

    @ApiModelProperty(value = "层级")
    private Integer lvl;

    /**是否有子节点*/
    @Excel(name = "是否有子节点", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
    /**
     * 子集-变电所
     */
    @ApiModelProperty(value = "子集-变电所 ")
    @TableField(exist = false)
    private List<BdStation> children ;



    public void addChildren(BdStation child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

}
