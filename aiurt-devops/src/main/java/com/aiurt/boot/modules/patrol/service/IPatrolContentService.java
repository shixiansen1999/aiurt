package com.aiurt.boot.modules.patrol.service;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.patrol.entity.PatrolContent;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 巡检项内容
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IPatrolContentService extends IService<PatrolContent> {

	Result<?> queryTree(HttpServletRequest req, Long id);

	/**
	 * 添加巡检项
	 * @param patrolContent
	 * @return
	 */
	Result<?> add(PatrolContent patrolContent);

	/**
	 * 编辑巡检项
	 * @param patrolContent
	 * @return
	 */
	Result<?> edit(PatrolContent patrolContent);

	/**
	 * 列表项查询
	 * @param req
	 * @param id
	 * @return
	 */
	Result<?> queryList(HttpServletRequest req, Long id);
}
