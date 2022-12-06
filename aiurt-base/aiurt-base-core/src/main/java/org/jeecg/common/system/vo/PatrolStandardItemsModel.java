package org.jeecg.common.system.vo;


import lombok.Data;

import java.util.List;

@Data
public class PatrolStandardItemsModel {

    /**主键id*/
    private java.lang.String id;
    /**标准ID*/
    private java.lang.String standardId;
    /**层级类型：0一级、1子级*/
    private java.lang.Integer hierarchyType;
    /**层级类型：0一级、1子级*/
    private java.lang.String hierarchyTypeName;
    /**父级*/
    private java.lang.String parent;
    /**巡检项内容*/
    private java.lang.String content;
    /**巡检项编号*/
    private java.lang.String code;
    /**内容排序*/
    private java.lang.Integer order;
    /**内容排序*/
    private java.lang.String detailOrder;
    /**是否为巡检项目：0否 1是*/
    private java.lang.Integer check;
    /**是否为巡检项目：0否 1是*/
    private java.lang.String checkName;
    /**质量标准*/
    private java.lang.String qualityStandard;
    /**父级ID，其中顶级为0*/
    private java.lang.String parentId;
    /**数据填写类型：1开关项(即二选一)、2选择项、3输入项*/
    private java.lang.Integer inputType;
    /**选择项关联的数据字典code*/
    private java.lang.String dictCode;
    /**数据校验表达式*/
    private java.lang.String regular;
    /**检查值是否必填：0否、1是*/
    private java.lang.Integer required;
    /**检查值是否必填字典名：0否、1是*/
    private java.lang.String requiredDictName;
    /**删除状态： 0未删除 1已删除*/
    private java.lang.Integer delFlag;
    /**创建人*/
    private java.lang.String createBy;
    /**创建时间*/
    private java.util.Date createTime;
    /**更新人*/
    private java.lang.String updateBy;
    /**更新时间*/
    private java.util.Date updateTime;

    /**存放子集*/
    private List<PatrolStandardItemsModel> children;

    /**错误原因*/
    private  String  itemParentMistake;
}
