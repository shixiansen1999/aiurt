package com.aiurt.modules.search.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wgp
 * @Title: 搜索结果dto
 * @Description:
 * @date 2023/2/148:40
 */
@Data
@ApiModel(value = "搜索结果dto", description = "搜索结果dto")
public class SearchResponseDTO extends CommonResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "故障知识库id")
    private String id;

    @ApiModelProperty(value = "故障现象分类编码")
    @Dict(dictTable = "fault_knowledge_base_type", dicText = "name", dicCode = "code")
    private String knowledgeBaseTypeCode;

    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    @ApiModelProperty(value = "故障原因")
    private String faultReason;

    @ApiModelProperty(value = "解决方案")
    private String solution;

    @ApiModelProperty(value = "故障编号_字符串")
    private String faultCodes;

    @ApiModelProperty(value = "排查方法")
    private String method;

    @ApiModelProperty(value = "携带工具")
    private String tools;

    @ApiModelProperty(value = "设备组件")
    @Dict(dictTable = "device_assembly", dicText = "material_name", dicCode = "material_code")
    private String materialCode;

    @ApiModelProperty(value = "设备分类")
    @Dict(dictTable = "device_Type", dicText = "name", dicCode = "code")
    private String deviceTypeCode;

    @ApiModelProperty(value = "状态(0:待审批,1:已审批,2:已驳回)")
    private Integer status;

    @ApiModelProperty(value = "附件")
    private java.lang.String filePath;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "故障编号_集合")
    @TableField(exist = false)
    private List<String> faultCodeList;

    public void setFaultCodes(String faultCodes) {
        this.faultCodes = faultCodes;
        if (StrUtil.isNotEmpty(faultCodes)) {
            if (CollUtil.isEmpty(faultCodeList)) {
                faultCodeList = CollUtil.newArrayList();
            }
            faultCodeList.addAll(StrUtil.split(faultCodes, ','));
        }
    }

    public void setFaultPhenomenon(String faultPhenomenon) {
        this.faultPhenomenon = faultPhenomenon;
        if(StrUtil.isNotEmpty(faultPhenomenon)){
            setTitle(faultPhenomenon);
        }
    }
}
