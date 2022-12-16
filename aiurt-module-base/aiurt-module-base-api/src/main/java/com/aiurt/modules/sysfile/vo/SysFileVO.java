package com.aiurt.modules.sysfile.vo;

import com.aiurt.modules.sysfile.entity.SysFile;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.api.vo.Result;

import java.io.Serializable;

/**
 * @description: SysFileVO
 * @author: Mr.zhao
 * @date: 2021/10/29 13:54
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileVO extends SysFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String typeName;
	/**
	 * 是否为创建人,有全体控制按钮
	 */
	private Integer allFlag;

	@ApiModelProperty(value = "上传人名称")
	private String createByName;

	@ApiModelProperty(value = "权限列表")
	@TableField(exist = false)
	private SysFileTypeDetailVO detail;
}
