package com.aiurt.boot.modules.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.vo.importdir.PatrolContentImportVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 巡检项内容
 * @Author: Mr.zhao
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface PatrolContentMapper extends BaseMapper<PatrolContent> {

	List<PatrolContentImportVO> selectExportList(@Param("id")Long id);
}
