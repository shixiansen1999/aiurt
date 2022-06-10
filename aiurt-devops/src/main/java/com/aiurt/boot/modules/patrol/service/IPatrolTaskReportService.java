package com.aiurt.boot.modules.patrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.patrol.entity.PatrolTaskReport;
import com.aiurt.boot.modules.patrol.param.*;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentOneTreeVO;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentTreeVO;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 巡检人员巡检项报告服务接口
 *
 * @author Mr. zhao
 * @version V1.0
 * @date 2021-09-21
 */
public interface IPatrolTaskReportService extends IService<PatrolTaskReport> {
	/**
	 * 树状查询所有巡检项
	 *
	 * @param req
	 * @param id  taskId
	 * @return
	 */
	Result<List<PatrolPoolContentTreeVO>> tree(HttpServletRequest req, Long id, TreeParam param);

	/**
	 * 树状反向查询巡检项
	 *
	 * @param param taskId
	 * @return
	 */
	PatrolPoolContentOneTreeVO getOneTree(OneTreeParam param);

	/**
	 * 单项异常提交
	 *
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> oneReport(HttpServletRequest req, ReportOneParam param);

	/**
	 * 所有项提交
	 *
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> reportAll(HttpServletRequest req, ReportAllParam param);

	/**
	 * 提交签字
	 *
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> sign(HttpServletRequest req, ReportSignParam param);

	/**
	 * 回调
	 *
	 * @param taskId        任务表id
	 * @param poolContentId 巡检任务项id
	 * @param code          回调的内容
	 * @return
	 */
	boolean callback(Long taskId, Long poolContentId, String code);

	/**
	 * 附件url获取
	 *
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> getUrl(HttpServletRequest req, UrlParam param);


}
