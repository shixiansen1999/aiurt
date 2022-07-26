package com.aiurt.modules.train.eaxm.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.eaxm.entity.BdExamRecordDetail;
import com.aiurt.modules.train.eaxm.service.IBdExamRecordDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 答题详情
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Api(tags="答题详情")
@RestController
@RequestMapping("/examrecorddetail/bdExamRecordDetail")
@Slf4j
public class BdExamRecordDetailController extends BaseController<BdExamRecordDetail, IBdExamRecordDetailService> {
	@Autowired
	private IBdExamRecordDetailService bdExamRecordDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param bdExamRecordDetail
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "答题详情-分页列表查询")
	@ApiOperation(value="答题详情-分页列表查询", notes="答题详情-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdExamRecordDetail bdExamRecordDetail,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdExamRecordDetail> queryWrapper = QueryGenerator.initQueryWrapper(bdExamRecordDetail, req.getParameterMap());
		Page<BdExamRecordDetail> page = new Page<BdExamRecordDetail>(pageNo, pageSize);
		IPage<BdExamRecordDetail> pageList = bdExamRecordDetailService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param bdExamRecordDetail
	 * @return
	 */
	@AutoLog(value = "答题详情-添加")
	@ApiOperation(value="答题详情-添加", notes="答题详情-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdExamRecordDetail bdExamRecordDetail) {
		bdExamRecordDetailService.save(bdExamRecordDetail);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bdExamRecordDetail
	 * @return
	 */
	@AutoLog(value = "答题详情-编辑")
	@ApiOperation(value="答题详情-编辑", notes="答题详情-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdExamRecordDetail bdExamRecordDetail) {
		bdExamRecordDetailService.updateById(bdExamRecordDetail);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "答题详情-通过id删除")
	@ApiOperation(value="答题详情-通过id删除", notes="答题详情-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdExamRecordDetailService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "答题详情-批量删除")
	@ApiOperation(value="答题详情-批量删除", notes="答题详情-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdExamRecordDetailService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "答题详情-通过id查询")
	@ApiOperation(value="答题详情-通过id查询", notes="答题详情-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdExamRecordDetail bdExamRecordDetail = bdExamRecordDetailService.getById(id);
		if(bdExamRecordDetail==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdExamRecordDetail);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bdExamRecordDetail
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdExamRecordDetail bdExamRecordDetail) {
        return super.exportXls(request, bdExamRecordDetail, BdExamRecordDetail.class, "答题详情");
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
        return super.importExcel(request, response, BdExamRecordDetail.class);
    }

}
