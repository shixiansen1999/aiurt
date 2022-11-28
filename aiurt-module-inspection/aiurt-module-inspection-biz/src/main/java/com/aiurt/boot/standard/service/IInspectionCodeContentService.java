package com.aiurt.boot.standard.service;

import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IInspectionCodeContentService extends IService<InspectionCodeContent> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param inspectionCodeContent
	 */
	void addInspectionCodeContent(InspectionCodeContent inspectionCodeContent);

	/**
   * 修改节点
   *
   * @param inspectionCodeContent
   * @throws AiurtBootException
   */
	void updateInspectionCodeContent(InspectionCodeContent inspectionCodeContent) throws AiurtBootException;

	/**
	 * 删除节点
	 *
	 * @param id
   * @throws AiurtBootException
	 */
	void deleteInspectionCodeContent(String id) throws AiurtBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<InspectionCodeContent>
	   */
    List<InspectionCodeContent> queryTreeListNoPage(QueryWrapper<InspectionCodeContent> queryWrapper);

	/**
	 * 【vue3专用】根据父级编码加载分类字典的数据
	 *
	 * @param parentCode
	 * @return
	 */
	List<SelectTreeModel> queryListByCode(String parentCode);

	/**
	 * 【vue3专用】根据pid查询子节点集合
	 *
	 * @param pid
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(String pid);

	/**
	 * 检修检查项表-树型分页列表查询
	 * @param page
	 * @param inspectionCodeContent
	 * @return
	 */
	IPage<InspectionCodeContent> pageList(Page<InspectionCodeContent> page, InspectionCodeContent inspectionCodeContent);
	/**
	 * 通过检修标准id查看检修项
	 *
	 * @param id  检修标准id
	 * @return
	 */
    List<InspectionCodeContent> selectCodeContentList(String id);

	/**
	 * 校验code
     * @param code
     * @param inspectionCodeId
     * @param id
     */
    void checkCode(String code, String inspectionCodeId, String id);

	/**
	 * 配置检查项导出
	 * @param request
	 * @param response
	 * @param inspectionCodeContent
	 * @return
	 */
	ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response, InspectionCodeContent inspectionCodeContent);

	/**
	 * 配置检查项导入
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
