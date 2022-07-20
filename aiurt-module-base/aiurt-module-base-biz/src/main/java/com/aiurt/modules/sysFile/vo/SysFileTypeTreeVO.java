package com.aiurt.modules.sysFile.vo;

import com.aiurt.modules.sysFile.entity.SysFileType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 树状返回
 *
 * @description: SysFileTypeTreeVO
 * @author: Mr.zhao
 * @date: 2021/10/26 14:58
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileTypeTreeVO extends SysFileType implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SysFileTypeTreeVO> children;


}
