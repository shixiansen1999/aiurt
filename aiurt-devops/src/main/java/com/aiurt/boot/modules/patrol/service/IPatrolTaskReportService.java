package com.aiurt.boot.modules.patrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.patrol.entity.PatrolTaskReport;
import com.swsc.copsms.modules.patrol.param.ReportAllParam;
import com.swsc.copsms.modules.patrol.param.ReportOneParam;
import com.swsc.copsms.modules.patrol.vo.PatrolPoolContentTreeVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 巡检人员巡检项报告表
 * @Author: swsc
 * @Date:   2021-09-21
 * @Version: V1.0
 */
public interface IPatrolTaskReportService extends IService<PatrolTaskReport> {
	/**
	 * 树状查询所有巡检项
	 * @param req
	 * @param id taskId
	 * @return
	 */
	Result<List<PatrolPoolContentTreeVO>> tree(HttpServletRequest req, Long id);

	/**
	 * 单项异常提交
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> oneReport(HttpServletRequest req, ReportOneParam param);

	/**
	 * 所有项提交
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> reportAll(HttpServletRequest req, ReportAllParam param);

	/**
	 * 提交签字
	 * @param req
	 * @param id
	 * @param url
	 * @return
	 */
	Result<?> sign(HttpServletRequest req, Long id, String url);

	/**
	 * 回调
	 * @param taskId 任务表id
	 * @param poolContentId  巡检任务项id
	 * @param code 回调的内容
	 * @return
	 */
	boolean callback(Long taskId, Long poolContentId, String code);

	/**
	 * 附件url获取
	 * @param req
	 * @param id
	 * @return
	 */
	Result<?> getUrl(HttpServletRequest req, Long id);
}
