package com.aiurt.boot.modules.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.patrol.entity.PatrolPoolContent;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentOneTreeVO;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentTreeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 巡检人员任务项
 * @Author: Mr.zhao
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface PatrolPoolContentMapper extends BaseMapper<PatrolPoolContent> {

	List<PatrolPoolContentTreeVO> selectTreeDetails(@Param("id")Long id,
	                                                @Param("taskId")Long taskId,
	                                                @Param("parentId") Long parentId,
	                                                @Param("title")String title);

	List<PatrolPoolContentOneTreeVO> selectOneTreeDetails(@Param("id")Long id,
	                                                      @Param("taskId")Long taskId,
	                                                      @Param("typeIds") List<Long> typeIds);

}
