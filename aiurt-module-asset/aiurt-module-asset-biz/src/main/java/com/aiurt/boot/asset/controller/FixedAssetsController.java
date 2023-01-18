package com.aiurt.boot.asset.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.asset.dto.FixedAssetsDTO;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.check.service.IFixedAssetsCheckService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Api(tags="固定资产")
@RestController
@RequestMapping("/asset/fixedAssets")
@Slf4j
public class FixedAssetsController extends BaseController<FixedAssets, IFixedAssetsService> {
	@Autowired
	private IFixedAssetsService fixedAssetsService;
	 @Autowired
	 private IFixedAssetsCheckService assetsCheckService;

	/**
	 * 分页列表查询
	 *
	 * @param fixedAssetsDTO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "固定资产-分页列表查询")
	@ApiOperation(value="固定资产-分页列表查询", notes="固定资产-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FixedAssetsDTO>> queryPageList(FixedAssetsDTO fixedAssetsDTO,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<FixedAssetsDTO> pageList = new Page<>(pageNo, pageSize);
		Page<FixedAssetsDTO> list = fixedAssetsService.pageList(pageList, fixedAssetsDTO);
		return Result.OK(list);
	}
	 /**
	  *   添加
	  *
	  * @param assetCode
	  * @return
	  */
	 @AutoLog(value = "固定资产-详情")
	 @ApiOperation(value="固定资产-详情", notes="固定资产-详情")
	 @GetMapping(value = "/detail")
	 public Result<FixedAssetsDTO> detail(String assetCode) {
		 return  fixedAssetsService.detail(assetCode);
	 }
	/**
	 *   添加
	 *
	 * @param fixedAssets
	 * @return
	 */
	@AutoLog(value = "固定资产-添加")
	@ApiOperation(value="固定资产-添加", notes="固定资产-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FixedAssets fixedAssets) {
		fixedAssetsService.save(fixedAssets);
		return Result.OK("添加成功！");
	}
	 /**
	  *  校验
	  *
	  * @param assetCode
	  * @return
	  */
	 @AutoLog(value = "固定资产-校验")
	 @ApiOperation(value="固定资产-校验", notes="固定资产-校验")
	 @GetMapping(value = "/check")
	 public Result<String> check(String assetCode,@RequestParam(name="id",required=false) String id) {
		 LambdaQueryWrapper<FixedAssets> queryWrapper = new LambdaQueryWrapper<FixedAssets>();
		 queryWrapper.eq(FixedAssets::getAssetCode, assetCode);
		 FixedAssets assets = fixedAssetsService.getOne(queryWrapper);
		 if(ObjectUtil.isNotEmpty(id)){
		 	if(id!=assets.getId()){
				return Result.error("资产编号已存在！");
			}
		 }
		 else {
			 if(ObjectUtil.isNotEmpty(assets)){
				 return Result.error("资产编号已存在！");
			 }
		 }
		 return Result.OK();
	 }
	/**
	 *  编辑
	 *
	 * @param fixedAssets
	 * @return
	 */
	@AutoLog(value = "固定资产-编辑")
	@ApiOperation(value="固定资产-编辑", notes="固定资产-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FixedAssets fixedAssets) {
		fixedAssetsService.updateById(fixedAssets);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "固定资产-通过id删除")
	@ApiOperation(value="固定资产-通过id删除", notes="固定资产-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		fixedAssetsService.removeById(id);
		return Result.OK("删除成功!");
	}

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "fixed_assets-批量删除")
//	@ApiOperation(value="fixed_assets-批量删除", notes="fixed_assets-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.fixedAssetsService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "fixed_assets-通过id查询")
//	@ApiOperation(value="fixed_assets-通过id查询", notes="fixed_assets-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<FixedAssets> queryById(@RequestParam(name="id",required=true) String id) {
//		FixedAssets fixedAssets = fixedAssetsService.getById(id);
//		if(fixedAssets==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(fixedAssets);
//	}

    /**
    * 导出excel
    *
    * @param request
    * @param fixedAssets
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FixedAssets fixedAssets) {
        return super.exportXls(request, fixedAssets, FixedAssets.class, "fixed_assets");
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
        return super.importExcel(request, response, FixedAssets.class);
    }

}
