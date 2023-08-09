package com.aiurt.modules.common.entity;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecg.common.system.vo.SysUserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zwl
 */
@Data
@ApiModel("下列列表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectTable {

    @ApiModelProperty("id")
    private String id;

    private String key;

    private String value;

    private String label;

    private List<SelectTable> children ;

    private Integer level;

    /**
     * 针对位置管理: 线路
     */
    private String lineCode;

    /**
     * 针对位置管理: 站所
     */
    private String stationCode;

    /**
     * 位置
     */
    private String positionCode;

    /**
     *
     */
    @ApiModelProperty(value = "人员数量")
    private Long userNum;

    @ApiModelProperty(value = "当前部门及子级的人员数量")
    private Long subUserNum;
    /**
     *
     */
    private String parentValue;


    private String color;

    /**
     *
     */
    @ApiModelProperty("是否为结构, true为是, false为否")
    private Boolean isOrg = false;

    @ApiModelProperty("部门编码")
    private String orgCode;

    @ApiModelProperty("部门名称")
    private String orgName;

    private Boolean isLeaf;

    public void addChildren(SelectTable child) {
        if (children == null) {
            children = new ArrayList<SelectTable>();
        }
        children.add(child);
    }

    private String title;


    @JsonProperty("pId")
    private String pid;

    private String systemCode;

    private String majorCode;

    /**是否是知识库类别*/
    private Boolean isBaseType;

    @ApiModelProperty(value = "设备类型")
    private String deviceTypeCode;
    @ApiModelProperty(value = "岗位")
    private String postName;
    @ApiModelProperty(value = "角色")
    private String roleName;
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 递归计算 subUserNum
     * @return
     */
    public Long calculateSubUserNum() {
        if (children == null || children.isEmpty()) {
            // 如果没有子部门，subUserNum 等于 userNum
            subUserNum = userNum;
            return subUserNum;
        }

        // 遍历所有子部门，递归计算 subUserNum，并累加到当前部门的 subUserNum 上
        subUserNum = userNum;
        for (SelectTable child : children) {
            if (child.userNum != null) {
                subUserNum += child.calculateSubUserNum();
            }
        }
        return subUserNum;
    }

    private List<SysUserModel> convertToSysUserModelList(List<SelectTable> resultList) {
        List<SysUserModel> sysUserModels = new ArrayList<>();

        for (SelectTable selectTable : resultList) {
            SysUserModel sysUserModel = new SysUserModel();
            sysUserModel.setId(selectTable.getId());
            sysUserModel.setKey(selectTable.getKey());
            sysUserModel.setValue(selectTable.getValue());
            sysUserModel.setLabel(selectTable.getLabel());
            sysUserModel.setTitle(selectTable.getLabel());
            sysUserModel.setIsOrg(selectTable.getIsOrg());
            sysUserModel.setRoleName(selectTable.getRoleName());
            sysUserModel.setPostName(selectTable.getPostName());
            sysUserModel.setOrgCode(selectTable.getOrgCode());
            sysUserModel.setOrgName(selectTable.getOrgName());
            sysUserModel.setAvatar(selectTable.getAvatar());
            sysUserModel.setUserNum(selectTable.getUserNum());

            // 递归转换子部门信息
            if (CollUtil.isNotEmpty(selectTable.getChildren())) {
                sysUserModel.setChildren(convertToSysUserModelList(selectTable.getChildren()));
            }

            sysUserModels.add(sysUserModel);
        }

        return sysUserModels;
    }

}
