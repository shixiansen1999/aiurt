package com.aiurt.boot.record.controller;

import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.service.IFixedAssetsCheckRecordService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
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
 * @Description: fixed_assets_check_record
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Api(tags="固定资产盘点记录表")
@RestController
@RequestMapping("/record/fixedAssetsCheckRecord")
@Slf4j
public class FixedAssetsCheckRecordController extends BaseController<FixedAssetsCheckRecord, IFixedAssetsCheckRecordService> {
	@Autowired
	private IFixedAssetsCheckRecordService fixedAssetsCheckRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param fixedAssetsCheckRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "fixed_assets_check_record-分页列表查询")
	@ApiOperation(value="固定资产盘点记录表-分页列表查询", notes="固定资产盘点记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FixedAssetsCheckRecord>> queryPageList(FixedAssetsCheckRecord fixedAssetsCheckRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FixedAssetsCheckRecord> queryWrapper = QueryGenerator.initQueryWrapper(fixedAssetsCheckRecord, req.getParameterMap());
		Page<FixedAssetsCheckRecord> page = new Page<FixedAssetsCheckRecord>(pageNo, pageSize);
		IPage<FixedAssetsCheckRecord> pageList = fixedAssetsCheckRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param fixedAssetsCheckRecord
	 * @return
	 */
	@AutoLog(value = "固定资产盘点记录表-添加")
	@ApiOperation(value="固定资产盘点记录表-添加", notes="固定资产盘点记录表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FixedAssetsCheckRecord fixedAssetsCheckRecord) {
		fixedAssetsCheckRecordService.save(fixedAssetsCheckRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param fixedAssetsCheckRecord
	 * @return
	 */
	@AutoLog(value = "固定资产盘点记录表-编辑")
	@ApiOperation(value="固定资产盘点记录表-编辑", notes="固定资产盘点记录表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FixedAssetsCheckRecord fixedAssetsCheckRecord) {
		fixedAssetsCheckRecordService.updateById(fixedAssetsCheckRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "固定资产盘点记录表-通过id删除")
	@ApiOperation(value="固定资产盘点记录表-通过id删除", notes="固定资产盘点记录表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		fixedAssetsCheckRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "固定资产盘点记录表-批量删除")
	@ApiOperation(value="固定资产盘点记录表-批量删除", notes="固定资产盘点记录表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.fixedAssetsCheckRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "fixed_assets_check_record-通过id查询")
	@ApiOperation(value="固定资产盘点记录表-通过id查询", notes="固定资产盘点记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FixedAssetsCheckRecord> queryById(@RequestParam(name="id",required=true) String id) {
		FixedAssetsCheckRecord fixedAssetsCheckRecord = fixedAssetsCheckRecordService.getById(id);
		if(fixedAssetsCheckRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(fixedAssetsCheckRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param fixedAssetsCheckRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FixedAssetsCheckRecord fixedAssetsCheckRecord) {
        return super.exportXls(request, fixedAssetsCheckRecord, FixedAssetsCheckRecord.class, "fixed_assets_check_record");
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
        return super.importExcel(request, response, FixedAssetsCheckRecord.class);
    }

}
