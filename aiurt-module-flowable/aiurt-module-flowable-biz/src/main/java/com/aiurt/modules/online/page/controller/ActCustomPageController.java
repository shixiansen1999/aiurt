package com.aiurt.modules.online.page.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.online.page.entity.ActCustomPage;
import com.aiurt.modules.online.page.service.IActCustomPageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 设计表单
 * @Author: aiurt
 * @Date:   2022-10-26
 * @Version: V1.0
 */
@Api(tags="设计表单")
@RestController
@RequestMapping("/page/actCustomPage")
@Slf4j
public class ActCustomPageController extends BaseController<ActCustomPage, IActCustomPageService> {

	@Autowired
	private IActCustomPageService actCustomPageService;

	@Autowired
	private ISysBaseAPI sysBaseApi;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomPage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="设计表单-分页列表查询", notes="设计表单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomPage>> queryPageList(ActCustomPage actCustomPage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {

		LambdaQueryWrapper<ActCustomPage> queryWrapper = new LambdaQueryWrapper<>();
		String sysOrgCode = actCustomPage.getSysOrgCode();
		if (StrUtil.isNotBlank(sysOrgCode)) {
			SysDepartModel sysDepartModel = sysBaseApi.selectAllById(sysOrgCode);
			if (Objects.nonNull(sysDepartModel)) {
				actCustomPage.setSysOrgCode(sysDepartModel.getOrgCode());
			}

			queryWrapper.eq(ActCustomPage::getSysOrgCode, actCustomPage.getSysOrgCode());
		}

		queryWrapper.like(StrUtil.isNotBlank(actCustomPage.getPageName()),ActCustomPage::getPageName, actCustomPage.getPageName());

		Page<ActCustomPage> page = new Page<>(pageNo, pageSize);
		IPage<ActCustomPage> pageList = actCustomPageService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomPage
	 * @return
	 */
	@AutoLog(value = "设计表单-添加")
	@ApiOperation(value="设计表单-添加", notes="设计表单-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomPage actCustomPage) {
		actCustomPage.setPageVersion(1);
		actCustomPageService.save(actCustomPage);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param actCustomPage
	 * @return
	 */
	@AutoLog(value = "设计表单-编辑")
	@ApiOperation(value="设计表单-编辑", notes="设计表单-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomPage actCustomPage) {

		actCustomPageService.edit(actCustomPage);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="设计表单-通过id删除", notes="设计表单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomPageService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@ApiOperation(value="设计表单-批量删除", notes="设计表单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomPageService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="设计表单-通过id查询", notes="设计表单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomPage> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomPage actCustomPage = actCustomPageService.getById(id);
		if(actCustomPage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomPage);
	}

	@GetMapping(value = "/queryList")
	@ApiOperation("查询表单list")
	public Result<List<ActCustomPage>> queryList(@RequestParam(value = "pageName",required = false) String pageName) {
		LambdaQueryWrapper<ActCustomPage> wrapper = new LambdaQueryWrapper<>();

		wrapper.eq(ActCustomPage::getDelFlag, CommonConstant.DEL_FLAG_0);

		List<ActCustomPage> actCustomPageList = actCustomPageService.getBaseMapper().selectList(wrapper);

		return Result.OK(actCustomPageList);
	}
}
