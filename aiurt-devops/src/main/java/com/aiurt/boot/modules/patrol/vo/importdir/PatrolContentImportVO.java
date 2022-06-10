package com.aiurt.boot.modules.patrol.vo.importdir;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @description: PatrolContentImportVO
 * @author: Mr.zhao
 * @date: 2021/11/10 13:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolContentImportVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Excel(name = "父级code", width = 15,replace = {"上一级编号,为空默认顶级_-1"})
	private Long parentName;

	@Excel(name = "本级code", width = 20 ,replace = "本条编号,可以为最多6位纯数字_-1")
	private Long code;

	@Excel(name = "检查项类型", width = 15 ,replace = {"否_0","是_1","填写'是'或'否'_99"})
	private Integer type;

	@Excel(name = "显示顺序", width = 15)
	private Integer sequence;

	@Excel(name = "填写选择状态项", width = 15,replace = {"--_-1","选择项_0", "文字填充项_1","填写 '选择项' 或 '文字填充项',无则不填或填写 '--'_99"})
	private Integer statusItem;

	@Excel(name = "检查内容", width = 20)
	private String content;

	@Excel(name = "更多说明", width = 15)
	private String note;
}
