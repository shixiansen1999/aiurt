package com.aiurt.boot.modules.patrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.vo.PatrolContentTreeVO;
import com.aiurt.boot.modules.patrol.vo.importdir.PatrolContentImportVO;

import java.util.List;

/**
 * @Description: 巡检项内容
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IPatrolContentService extends IService<PatrolContent> {

	Result<List<PatrolContentTreeVO>> queryTree(Long id);

	/**
	 * 列表项查询
	 */
	Result<?> queryList(Long code);

	List<PatrolContentImportVO> selectExportList(Long id);

}
