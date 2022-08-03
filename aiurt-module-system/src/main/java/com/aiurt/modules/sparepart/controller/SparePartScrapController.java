package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Api(tags="备件管理-备件报废管理")
@RestController
@RequestMapping("/sparepart/sparePartScrap")
@Slf4j
public class SparePartScrapController extends BaseController<SparePartScrap, ISparePartScrapService> {
	@Autowired
	private ISparePartScrapService sparePartScrapService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartScrap
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_scrap-分页列表查询")
	@ApiOperation(value="spare_part_scrap-分页列表查询", notes="spare_part_scrap-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartScrap>> queryPageList(SparePartScrap sparePartScrap,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//QueryWrapper<SparePartScrap> queryWrapper = QueryGenerator.initQueryWrapper(sparePartScrap, req.getParameterMap());
		Page<SparePartScrap> page = new Page<SparePartScrap>(pageNo, pageSize);
		List<SparePartScrap> list = sparePartScrapService.selectList(page, sparePartScrap);
		page.setRecords(list);
		return Result.OK(page);
	}

	/**
	 *   添加
	 *
	 * @param sparePartScrap
	 * @return
	 */
	@AutoLog(value = "spare_part_scrap-添加")
	@ApiOperation(value="spare_part_scrap-添加", notes="spare_part_scrap-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartScrap sparePartScrap) {
		sparePartScrapService.save(sparePartScrap);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartScrap
	 * @return
	 */
	@AutoLog(value = "spare_part_scrap-编辑")
	@ApiOperation(value="spare_part_scrap-编辑", notes="spare_part_scrap-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartScrap sparePartScrap) {
		sparePartScrapService.updateById(sparePartScrap);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_scrap-通过id删除")
	@ApiOperation(value="spare_part_scrap-通过id删除", notes="spare_part_scrap-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartScrapService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "spare_part_scrap-批量删除")
	@ApiOperation(value="spare_part_scrap-批量删除", notes="spare_part_scrap-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sparePartScrapService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_scrap-通过id查询")
	@ApiOperation(value="spare_part_scrap-通过id查询", notes="spare_part_scrap-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartScrap> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartScrap sparePartScrap = sparePartScrapService.getById(id);
		if(sparePartScrap==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartScrap);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sparePartScrap
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SparePartScrap sparePartScrap) {
        return super.exportXls(request, sparePartScrap, SparePartScrap.class, "spare_part_scrap");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SparePartScrap.class);
    }

}
