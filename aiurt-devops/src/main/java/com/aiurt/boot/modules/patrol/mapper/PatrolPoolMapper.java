package com.aiurt.boot.modules.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.patrol.entity.PatrolPool;
import com.swsc.copsms.modules.patrol.param.PoolPageParam;
import com.swsc.copsms.modules.patrol.vo.PatrolPoolVO;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 巡检计划池
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface PatrolPoolMapper extends BaseMapper<PatrolPool> {

	IPage<PatrolPoolVO> selectPageList(Page<PatrolPoolVO> page, @Param("param") PoolPageParam param);
}
