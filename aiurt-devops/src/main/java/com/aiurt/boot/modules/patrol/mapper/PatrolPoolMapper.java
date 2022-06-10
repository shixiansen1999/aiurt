package com.aiurt.boot.modules.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.param.PoolPageParam;
import com.aiurt.boot.modules.patrol.param.StatisticsParam;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolVO;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskIgnoreVO;
import com.aiurt.boot.modules.patrol.vo.statistics.SimpIntegerSqlVO;
import com.aiurt.boot.modules.patrol.vo.statistics.SimpStringSqlVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @Description: 巡检计划池
 * @Author: Mr.zhao
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface PatrolPoolMapper extends BaseMapper<PatrolPool> {

	IPage<PatrolPoolVO> selectPageList(Page<PatrolPoolVO> page,@Param("param") PoolPageParam param);

	List<PatrolTaskIgnoreVO> selectIgnore(@Param("lastTime") LocalDateTime lastTime);

	List<SimpIntegerSqlVO> selectTitle(@Param("param") StatisticsParam param);

	List<SimpStringSqlVO> selectTeamCount(@Param("param")StatisticsParam param);

	List<SimpStringSqlVO> selectSystemCount(@Param("param") StatisticsParam param);

	List<SimpStringSqlVO> selectWarn(@Param("param") StatisticsParam param);

	List<SimpStringSqlVO> selectError(@Param("param") StatisticsParam param);

    int deleteByIds(@Param("poolIdList") List<String> poolIdList ,@Param("now") Date now);
}
