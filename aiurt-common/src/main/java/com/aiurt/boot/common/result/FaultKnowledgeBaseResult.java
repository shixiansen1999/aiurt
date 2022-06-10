package com.aiurt.boot.common.result;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description: 故障知识库
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("fault_knowledge_base")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fault_knowledge_base对象", description="故障知识库")
public class FaultKnowledgeBaseResult {

	/**主键id*/
	private Long id;
	/**类型id*/
	private Integer typeId;
	/**故障类型*/
	private Integer faultType;
	/**故障类型描述*/
	private String faultTypeDesc;
	/**故障现象*/
	private String faultPhenomenon;
	/**故障原因*/
	private String faultReason;
	/**故障措施/解决方案*/
	private String solution;
	/**关联故障集合,例:G101.2109.001，G101.2109.002*/
	private String faultCodes;
	/**删除状态:0.未删除 1已删除*/
	private Integer delFlag;
	/**系统编号*/
	private String systemCode;
	/**创建人*/
	private String createBy;
	/**修改人*/
	private String updateBy;
	/**创建时间,CURRENT_TIMESTAMP*/
	private Date createTime;
	/**修改时间,根据当前时间戳更新*/
	private Date updateTime;
}
