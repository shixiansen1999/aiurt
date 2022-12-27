package com.aiurt.modules.sysfile.service;

import com.aiurt.modules.sysfile.param.SysFileTypeParam;
import com.aiurt.modules.sysfile.vo.SysFileTypeDetailVO;
import com.aiurt.modules.sysfile.vo.SysFileTypeTreeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.param.FileAppParam;
import com.aiurt.modules.sysfile.vo.FIlePlanVO;
import com.aiurt.modules.sysfile.vo.FileAppVO;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 文档表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
public interface ISysFileService extends IService<SysFile> {

	IPage<FileAppVO> selectAppList(HttpServletRequest req,FileAppParam param);

	List<FIlePlanVO> selectList();

	/**
	 * 添加权限
	 *
	 * @param req   要求的事情
	 * @param param 参数
	 * @return {@link Result}<{@link ?}>
	 */
	Result<?> add(HttpServletRequest req, SysFile param);


	/**
	 * 详情
	 *
	 * @param req 要求的事情
	 * @param id  id
	 * @return {@link Result}<{@link List}<{@link SysFileTypeTreeVO}>>
	 */
	Result<SysFileTypeDetailVO> detail(HttpServletRequest req, Long id);

}
