package com.aiurt.boot.check.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.check.dto.AssetsResultDTO;
import com.aiurt.boot.check.dto.FixedAssetsCheckDTO;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.service.IFixedAssetsCheckService;
import com.aiurt.boot.check.vo.CheckUserVO;
import com.aiurt.boot.check.vo.FixedAssetsCheckVO;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Api(tags = "固定资产盘点任务信息表")
@RestController
@RequestMapping("/check/fixedAssetsCheck")
@Slf4j
public class FixedAssetsCheckController extends BaseController<FixedAssetsCheck, IFixedAssetsCheckService> {
    @Autowired
    private IFixedAssetsCheckService fixedAssetsCheckService;

    /**
     * 固定资产盘点任务信息表-分页列表查询
     *
     * @param fixedAssetsCheckDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "固定资产盘点任务信息表-分页列表查询", notes = "固定资产盘点任务信息表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FixedAssetsCheckVO>> pageList(FixedAssetsCheckDTO fixedAssetsCheckDTO,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<FixedAssetsCheckVO> page = new Page<>(pageNo, pageSize);
        IPage<FixedAssetsCheckVO> pageList = fixedAssetsCheckService.pageList(page, fixedAssetsCheckDTO);
        return Result.OK(pageList);
    }

    /**
     * 固定资产盘点管理-审核分页列表查询
     *
     * @return
     */
    @ApiOperation(value = "固定资产盘点管理-审核分页列表查询", notes = "固定资产盘点管理-审核分页列表查询")
    @GetMapping(value = "/auditList")
    public Result<IPage<FixedAssetsCheckVO>> auditList(FixedAssetsCheckDTO fixedAssetsCheckDTO,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<FixedAssetsCheckVO> page = new Page<>(pageNo, pageSize);
        IPage<FixedAssetsCheckVO> pageList = fixedAssetsCheckService.pageList(page, fixedAssetsCheckDTO);
        return Result.OK(pageList);
    }
//    /**
//     * 分页列表查询
//     *
//     * @param fixedAssetsCheck
//     * @param pageNo
//     * @param pageSize
//     * @param req
//     * @return
//     */
//    //@AutoLog(value = "fixed_assets_check-分页列表查询")
//    @ApiOperation(value = "固定资产盘点任务信息表-分页列表查询", notes = "固定资产盘点任务信息表-分页列表查询")
//    @GetMapping(value = "/list")
//    public Result<IPage<FixedAssetsCheck>> queryPageList(FixedAssetsCheck fixedAssetsCheck,
//                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
//                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
//                                                         HttpServletRequest req) {
//        Page<FixedAssetsCheck> page = new Page<FixedAssetsCheck>(pageNo, pageSize);
//        IPage<FixedAssetsCheck> pageList = fixedAssetsCheckService.queryPageList(page, fixedAssetsCheck);
//        return Result.OK(pageList);

//    }

    /**
     * 添加
     *
     * @param fixedAssetsCheck
     * @return
     */
    @AutoLog(value = "固定资产盘点任务信息表-添加")
    @ApiOperation(value = "固定资产盘点任务信息表-添加", notes = "固定资产盘点任务信息表-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody FixedAssetsCheck fixedAssetsCheck) {
        fixedAssetsCheck.setInventoryList(this.generateCode());
//		fixedAssetsCheckService.save(fixedAssetsCheck);
        String id = fixedAssetsCheckService.saveCheckInfo(fixedAssetsCheck);
        return Result.OK("添加成功！", id);
    }

//	/**
//	 *  填写盘点记录保存接口
//	 *
//	 * @param fixedAssetsCheck
//	 * @return
//	 */
//	@AutoLog(value = "固定资产盘点任务信息表-填写盘点记录保存接口")
//	@ApiOperation(value="固定资产盘点任务信息表-填写盘点记录保存接口", notes="固定资产盘点任务信息表-填写盘点记录保存接口")
//	@RequestMapping(value = "/addInventoryResults", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> addInventoryResults(@RequestBody FixedAssetsCheck fixedAssetsCheck) {
//		fixedAssetsCheckService.addInventoryResults(fixedAssetsCheck);
//		return Result.OK("编辑成功!");
//	}
//	/**
//	 *  填写盘点记录提交接口
//	 *
//	 * @param fixedAssetsCheck
//	 * @return
//	 */
//	@AutoLog(value = "固定资产盘点任务信息表-填写盘点记录提交接口")
//	@ApiOperation(value="固定资产盘点任务信息表-填写盘点记录提交接口", notes="固定资产盘点任务信息表-填写盘点记录提交接口")
//	@RequestMapping(value = "/addInventoryResultsBySubmit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> addInventoryResultsBySubmit(@RequestBody FixedAssetsCheck fixedAssetsCheck) {
//		fixedAssetsCheckService.addInventoryResultsBySubmit(fixedAssetsCheck);
//		return Result.OK("编辑成功!");
//	}

    /**
     * 编辑
     *
     * @param fixedAssetsCheck
     * @return
     */
    @AutoLog(value = "固定资产盘点任务信息表-编辑")
    @ApiOperation(value = "固定资产盘点任务信息表-编辑", notes = "固定资产盘点任务信息表-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody FixedAssetsCheck fixedAssetsCheck) {
        String id = fixedAssetsCheckService.editCheckInfo(fixedAssetsCheck);
//		fixedAssetsCheckService.updateById(fixedAssetsCheck);
        return Result.OK("编辑成功!", id);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "固定资产盘点任务信息表-通过id删除")
    @ApiOperation(value = "固定资产盘点任务信息表-通过id删除", notes = "固定资产盘点任务信息表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        fixedAssetsCheckService.deleteCheckInfo(id);
//		FixedAssetsCheck fixedAssetsCheck =new FixedAssetsCheck();
//		fixedAssetsCheck.setId(id);
//		fixedAssetsCheck.setDelFlag(1);
//		fixedAssetsCheckService.updateById(fixedAssetsCheck);
        return Result.OK("删除成功!");
    }

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "固定资产盘点任务信息表-批量删除")
//	@ApiOperation(value="固定资产盘点任务信息表-批量删除", notes="固定资产盘点任务信息表-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
//		List<String> id = Arrays.asList(ids.split(","));
//		fixedAssetsCheckService.removeBatchByIds(id);
//		return Result.OK("批量删除成功!");
//	}
//	/**
//	 * 点击修状态接口
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "fixed_assets_check-详情")
//	@ApiOperation(value="固定资产盘点任务信息表-点击修状态接口", notes="固定资产盘点任务信息表-点击修状态接口")
//	@GetMapping(value = "/updateStatus")
//	public Result<String> updateStatus(@RequestParam(name="id",required=true) String id,
//									   @RequestParam(name = "status")Integer status,
//									   @RequestParam(name = "num",required = false) Integer num) {
//		 fixedAssetsCheckService.updateStatus(id,status,num);
//		return Result.OK("成功");
//	}

    @ApiOperation(value = "盘点管理-下发接口", notes = "盘点管理-下发接口")
    @GetMapping(value = "/issued")
    public Result<String> issued(@RequestParam @ApiParam(name = "id", value = "记录ID") String id) {
        fixedAssetsCheckService.issued(id);
        return Result.OK("下发成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "fixed_assets_check-详情")
    @ApiOperation(value = "固定资产盘点任务信息表-详情", notes = "固定资产盘点任务信息表-通过id查询-详情")
    @GetMapping(value = "/queryById")
    public Result<FixedAssetsCheckVO> queryById(@RequestParam(name = "id", required = true) String id) {
//        FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.getById(id);
        FixedAssetsCheckVO checkVO = fixedAssetsCheckService.getCheckInfo(id);
        return Result.OK(checkVO);
    }

    /**
     * 固定资产盘点任务记录-记录的盘点人下拉接口
     */
    @ApiOperation(value = "固定资产盘点任务记录-记录的盘点人下拉接口", notes = "固定资产盘点任务记录-记录的盘点人下拉接口")
    @GetMapping(value = "/checkUserInfo")
    public Result<List<CheckUserVO>> checkUserInfo() {
        List<CheckUserVO> checkUserInfo = fixedAssetsCheckService.checkUserInfo();
        return Result.OK(checkUserInfo);
    }

    /**
     * 查询资产分类下拉框
     *
     * @param orgCodes
     * @return
     */
    //@AutoLog(value = "查询资产分类下拉框")
    @ApiOperation(value = "查询资产分类下拉框", notes = "查询资产分类下拉框")
    @GetMapping(value = "/queryBySpinner")
    public Result<List<FixedAssetsCategory>> queryBySpinner(@RequestParam(name = "orgCodes", required = true) String orgCodes) {
        List<FixedAssetsCategory> fixedAssetsCategories = fixedAssetsCheckService.queryBySpinner(orgCodes);
        return Result.OK(fixedAssetsCategories);
    }

    //@AutoLog(value = "fixed_assets_check-详情")
    @ApiOperation(value = "固定资产盘点任务信息表-填写盘点结果查询", notes = "固定资产盘点任务信息表-通过id查询-填写盘点结果查询")
    @GetMapping(value = "/queryInventoryResults")
    public Result<List<FixedAssets>> queryInventoryResults(@RequestParam(name = "orgCodes", required = true) String orgCodes,
                                                           @RequestParam(name = "categoryCodes", required = true) String categoryCodes,
                                                           @RequestParam(name = "id") String id) {
        List<FixedAssets> fixedAssets = fixedAssetsCheckService.queryInventoryResults(orgCodes, categoryCodes, id);
        return Result.OK(fixedAssets);
    }

    /**
     * 生成盘点任务编号
     *
     * @return
     */
    public String generateCode() {
        Calendar calendar = Calendar.getInstance();
        String time = Integer.toString(calendar.get(Calendar.YEAR)) + Integer.toString((calendar.get(Calendar.MONTH) + 1)) + Integer.toString(calendar.get(Calendar.DATE));
        FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.lambdaQuery()
                .like(FixedAssetsCheck::getInventoryList, time)
                .orderByDesc(FixedAssetsCheck::getInventoryList)
                .last("limit 1").one();
        String fixedAssetsCheckCode = "";
        if (ObjectUtil.isNotEmpty(fixedAssetsCheck)) {
            String newStr = StrUtil.sub(fixedAssetsCheck.getInventoryList(), fixedAssetsCheck.getInventoryList().length() - 4, fixedAssetsCheck.getInventoryList().length());
            Integer i = Integer.valueOf(newStr) + 1;
            fixedAssetsCheckCode = "ZP" + time + StrUtil.sub(i.toString(),i.toString().length()-3, i.toString().length());
        } else {
            fixedAssetsCheckCode = "ZP" + time + "001";
        }
        return fixedAssetsCheckCode;
    }

    /**
     * 固定资产盘点管理-更新盘点结果数据记录(保存/提交测试)
     * ---***接入流程后删掉***----
     */
    @ApiOperation(value = "固定资产盘点管理-更新盘点结果数据记录(保存/提交)", notes = "固定资产盘点管理-更新盘点结果数据记录(保存/提交)")
    @PostMapping(value = "/startProcess")
    public Result<IPage<FixedAssetsCheckRecord>> startProcess(@RequestBody AssetsResultDTO assetsResultDTO) {
        String a = fixedAssetsCheckService.startProcess(assetsResultDTO);
        return Result.OK();
    }

    /**
     * 导出excel
     *
     * @param request
     * @param fixedAssetsCheck
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FixedAssetsCheck fixedAssetsCheck) {
        return super.exportXls(request, fixedAssetsCheck, FixedAssetsCheck.class, "fixed_assets_check");
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
        return super.importExcel(request, response, FixedAssetsCheck.class);
    }

}
