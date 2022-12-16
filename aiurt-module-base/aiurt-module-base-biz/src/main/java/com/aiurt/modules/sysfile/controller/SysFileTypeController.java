package com.aiurt.modules.sysfile.controller;


import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFileRole;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.param.SysFileTypeParam;
import com.aiurt.modules.sysfile.service.ISysFileRoleService;
import com.aiurt.modules.sysfile.service.ISysFileService;
import com.aiurt.modules.sysfile.service.ISysFileTypeService;
import com.aiurt.modules.sysfile.vo.SysFileTypeDetailVO;
import com.aiurt.modules.sysfile.vo.SysFileTypeTreeVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 文档类型表
 * @Author: qian
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "文档类型表")
@RestController
@RequestMapping("/sysfile/sysFileType")
public class SysFileTypeController {

	@Autowired
	private ISysFileTypeService sysFileTypeService;
	@Autowired
	private ISysFileRoleService sysFileRoleService;
	@Autowired
	private ISysFileService sysFileService;



	/**
	 * 树状分类查询
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "文档类型表-树状分类查询")
	@ApiOperation(value = "文档类型表-树状分类查询", notes = "文档类型表-树状分类查询")
	@GetMapping(value = "/tree")
	public Result<List<SysFileTypeTreeVO>> queryTreeList(HttpServletRequest req) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();
		return sysFileTypeService.tree(userId);
	}


	/**
	 * 添加分类文档
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "文档类型表-添加分类文档")
	@ApiOperation(value = "文档类型表-添加分类文档", notes = "文档类型表-添加分类文档")
	@PostMapping(value = "/add")
	public Result<?> add(HttpServletRequest req, @RequestBody @Validated SysFileTypeParam param) {
//		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//		String userId = loginUser.getId();
//		List<String> editIds = param.getEditIds();
//		if (CollectionUtils.isNotEmpty(editIds)){
//			if (!editIds.contains(userId)) {
//				editIds.add(userId);
//			}
//		}
//		param.setEditIds(editIds);
		sysFileTypeService.add(req, param);
		return Result.ok("添加成功！");
	}

	/**
	 * 修改分类文档
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "文档类型表-修改分类文档")
	@ApiOperation(value = "文档类型表-修改分类文档", notes = "文档类型表-修改分类文档")
	@PostMapping(value = "/edit")
	public Result<?> edit(HttpServletRequest req, @RequestBody @Validated SysFileTypeParam param) {
//		if (param.getId() == null) {
//			return Result.error("修改失败");
//		}
//		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//		String userId = loginUser.getId();
//		List<String> editIds = param.getEditIds();
//		if (CollectionUtils.isNotEmpty(editIds)){
//			if (!editIds.contains(userId)) {
//				editIds.add(userId);
//			}
//		}
//		param.setEditIds(editIds);
		sysFileTypeService.edit(req, param);
		return Result.ok("修改成功！");
	}


	/**
	 * 树状分类查询
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "文档类型表-详情查询")
	@ApiOperation(value = "文档类型表-详情查询", notes = "文档类型表-详情查询")
	@GetMapping(value = "/detail")
	public Result<SysFileTypeDetailVO> detail(HttpServletRequest req,
	                                          @RequestParam("id") @NotNull(message = "id不能为空") Long id) {
		//LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		//if (loginUser == null) {
		//	return Result.ok();
		//}
		return sysFileTypeService.detail(req, id);
	}

	/**
	 * 树状分类查询
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "文档类型表-删除")
	@ApiOperation(value = "文档类型表-删除", notes = "文档类型表-删除")
	@PostMapping(value = "/delete")
	public Result<?> detail(HttpServletRequest req, @RequestBody @NotNull(message = "id不能为空") List<Long> ids) {
		SysFileType one = this.sysFileTypeService.lambdaQuery().in(SysFileType::getParentId, ids).last("limit 1").one();
		if (one!=null){
			throw new AiurtBootException("此目录下有文件夹,无法被直接删除");
		}

		if (!this.sysFileTypeService.removeByIds(ids)){
			return Result.error("删除失败!");
		}
		//为了清空文件和权限所作操作,并不一定有数据,不做判断
		this.sysFileService.lambdaUpdate().in(SysFile::getTypeId,ids).remove();
		this.sysFileRoleService.lambdaUpdate().in(SysFileRole::getTypeId, ids).remove();

		return Result.ok("删除成功!");
	}


}
