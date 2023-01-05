package com.aiurt.modules.param.service;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.param.entity.SysParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SelectTreeModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: sys_param
 * @Author: aiurt
 * @Date:   2022-12-30
 * @Version: V1.0
 */
public interface ISysParamService extends IService<SysParam> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param sysParam
	 */
	Result<String> addSysParam(SysParam sysParam);

	/**
   * 修改节点
   *
   * @param sysParam
   * @throws AiurtBootException
   */
	Result<String> updateSysParam(SysParam sysParam) throws AiurtBootException;

	/**
	 * 删除节点
	 *
	 * @param id
   * @throws AiurtBootException
	 */
	Result<String>  deleteSysParam(String id) throws AiurtBootException;


	/**
	 * 【vue3专用】根据pid查询子节点集合
	 *
	 * @param pid
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(String pid);

	/**
	 * 获取类别名称
	 * @param sysParam
	 */
	void getCategoryName(SysParam sysParam);

	/**
	 * 分页列表查询
	 *
	 * @param sysParam
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
    Result<IPage<SysParam>> queryPageList(SysParam sysParam, Integer pageNo,Integer pageSize);
}
