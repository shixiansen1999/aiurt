package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sparepart.entity.SparePartLendEnclosure;
import com.aiurt.modules.sparepart.service.ISparePartLendEnclosureService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: spare_part_lend_enclosure
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Api(tags="spare_part_lend_enclosure")
@RestController
@RequestMapping("/sparepart/sparePartLendEnclosure")
@Slf4j
public class SparePartLendEnclosureController extends BaseController<SparePartLendEnclosure, ISparePartLendEnclosureService> {
	@Autowired
	private ISparePartLendEnclosureService sparePartLendEnclosureService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartLendEnclosure
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_lend_enclosure-分页列表查询")
	@ApiOperation(value="spare_part_lend_enclosure-分页列表查询", notes="spare_part_lend_enclosure-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartLendEnclosure>> queryPageList(SparePartLendEnclosure sparePartLendEnclosure,
                                                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                               HttpServletRequest req) {
		QueryWrapper<SparePartLendEnclosure> queryWrapper = QueryGenerator.initQueryWrapper(sparePartLendEnclosure, req.getParameterMap());
		Page<SparePartLendEnclosure> page = new Page<SparePartLendEnclosure>(pageNo, pageSize);
		IPage<SparePartLendEnclosure> pageList = sparePartLendEnclosureService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sparePartLendEnclosure
	 * @return
	 */
	@AutoLog(value = "spare_part_lend_enclosure-添加")
	@ApiOperation(value="spare_part_lend_enclosure-添加", notes="spare_part_lend_enclosure-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartLendEnclosure sparePartLendEnclosure) {
		sparePartLendEnclosureService.save(sparePartLendEnclosure);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartLendEnclosure
	 * @return
	 */
	@AutoLog(value = "spare_part_lend_enclosure-编辑")
	@ApiOperation(value="spare_part_lend_enclosure-编辑", notes="spare_part_lend_enclosure-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartLendEnclosure sparePartLendEnclosure) {
		sparePartLendEnclosureService.updateById(sparePartLendEnclosure);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_lend_enclosure-通过id删除")
	@ApiOperation(value="spare_part_lend_enclosure-通过id删除", notes="spare_part_lend_enclosure-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartLendEnclosureService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "spare_part_lend_enclosure-批量删除")
	@ApiOperation(value="spare_part_lend_enclosure-批量删除", notes="spare_part_lend_enclosure-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sparePartLendEnclosureService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_lend_enclosure-通过id查询")
	@ApiOperation(value="spare_part_lend_enclosure-通过id查询", notes="spare_part_lend_enclosure-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartLendEnclosure> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartLendEnclosure sparePartLendEnclosure = sparePartLendEnclosureService.getById(id);
		if(sparePartLendEnclosure==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartLendEnclosure);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sparePartLendEnclosure
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SparePartLendEnclosure sparePartLendEnclosure) {
        return super.exportXls(request, sparePartLendEnclosure, SparePartLendEnclosure.class, "spare_part_lend_enclosure");
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
        return super.importExcel(request, response, SparePartLendEnclosure.class);
    }

}
