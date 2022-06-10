package com.aiurt.boot.modules.sysFile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.sysFile.entity.SysFileType;
import com.aiurt.boot.modules.sysFile.param.SysFileTypeParam;
import com.aiurt.boot.modules.sysFile.vo.SysFileTypeDetailVO;
import com.aiurt.boot.modules.sysFile.vo.SysFileTypeTreeVO;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 文档类型表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
public interface ISysFileTypeService extends IService<SysFileType> {

	/**
	 * 获取树型结构
	 *
	 * @param userId 用户id
	 * @return {@link Result}<{@link List}<{@link SysFileTypeTreeVO}>>
	 */
	Result<List<SysFileTypeTreeVO>> tree(String userId);

	/**
	 * 添加
	 *
	 * @param req   要求的事情
	 * @param param 参数
	 * @return {@link Result}<{@link ?}>
	 */
	Result<?> add(HttpServletRequest req, SysFileTypeParam param);

	/**
	 * 编辑
	 *
	 * @param req   要求的事情
	 * @param param 参数
	 * @return {@link Result}<{@link ?}>
	 */
	Result<?> edit(HttpServletRequest req, SysFileTypeParam param);

	/**
	 * 详情
	 *
	 * @param req 要求的事情
	 * @param id  id
	 * @return {@link Result}<{@link List}<{@link SysFileTypeTreeVO}>>
	 */
	Result<SysFileTypeDetailVO> detail(HttpServletRequest req, Long id);
}
