package com.aiurt.boot.modules.patrol.service;

import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.entity.PatrolPoolContent;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 巡检人员任务项
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IPatrolPoolContentService extends IService<PatrolPoolContent> {

	Result<?> queryList(Long id, HttpServletRequest req);

	/**
	 * 复制巡逻池内容
	 *
	 * @param list 列表
	 * @param id   id
	 * @return {@link List}<{@link PatrolPoolContent}>
	 */
	boolean copyContent(List<PatrolContent> list, Long id);
}
