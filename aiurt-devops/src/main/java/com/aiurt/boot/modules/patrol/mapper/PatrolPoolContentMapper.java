package com.aiurt.boot.modules.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.patrol.entity.PatrolPoolContent;
import com.swsc.copsms.modules.patrol.vo.PatrolPoolContentTreeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 巡检人员任务项
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface PatrolPoolContentMapper extends BaseMapper<PatrolPoolContent> {

	List<PatrolPoolContentTreeVO> selectTreeDetails(@Param("id") Long id, @Param("taskId") Long taskId, @Param("parentId") Long parentId);

}
