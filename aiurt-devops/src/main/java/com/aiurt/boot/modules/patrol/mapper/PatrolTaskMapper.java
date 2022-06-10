package com.aiurt.boot.modules.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.patrol.entity.PatrolTask;
import com.swsc.copsms.modules.patrol.param.PatrolPoolParam;
import com.swsc.copsms.modules.patrol.vo.PatrolTaskVO;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 巡检人员任务
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {

	/**
	 * 巡检人员查询
	 * @param page
	 * @param param
	 * @return
	 */
	IPage<PatrolTaskVO> selectPageList(Page<PatrolTaskVO> page, @Param("param") PatrolPoolParam param);

}
