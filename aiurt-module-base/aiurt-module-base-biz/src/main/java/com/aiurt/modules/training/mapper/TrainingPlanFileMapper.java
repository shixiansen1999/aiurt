package com.aiurt.modules.training.mapper;

import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.training.entity.TrainingPlanFile;
import com.aiurt.modules.training.vo.TrainingPlanFileVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 培训文件
 * @Author: hlq
 * @Date: 2023-06-06
 * @Version: V1.0
 */


public interface TrainingPlanFileMapper extends BaseMapper<TrainingPlanFile> {
	/**
	 * 培训文件分页列表
	 * @param page 分页参数
	 * @param planId 培训计划id
	 * @return 培训文件列表
	 */
	IPage<TrainingPlanFileVO> listByPlanId(@Param("page")Page<TrainingPlanFileVO> page, @Param("planId") Long planId);

	/**
	 * 编辑-文件回显
	 * @param page             分页对象，用于返回分页结果
	 * @param condition  文件查询参数对象，包含查询条件
	 * @param currLoginUserId  当前登录用户ID
	 * @param currLoginOrgCode 当前登录用户所属组织机构代码
	 * @param userNames        用户账号集合
	 * @return  编辑-文件回显
	 */
	List<SysFileManageVO> getFilePageList(@Param("page") Page<SysFileManageVO> page, @Param("condition") SysFileWebParam condition, @Param("currLoginUserId") String currLoginUserId, @Param("currLoginOrgCode") String currLoginOrgCode, @Param("userNames") List<String> userNames);
}