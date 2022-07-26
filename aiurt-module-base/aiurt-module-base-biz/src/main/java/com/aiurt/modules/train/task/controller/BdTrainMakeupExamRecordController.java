package com.aiurt.modules.train.task.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.aiurt.modules.train.task.service.IBdTrainMakeupExamRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 培训补考记录
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
@Api(tags = "培训补考记录")
@RestController
@RequestMapping("/task/bdTrainMakeupExamRecord")
@Slf4j
public class BdTrainMakeupExamRecordController extends BaseController<BdTrainMakeupExamRecord, IBdTrainMakeupExamRecordService> {
    @Autowired
    private IBdTrainMakeupExamRecordService bdTrainMakeupExamRecordService;

    /**
     * 分页列表查询
     *
     * @param bdTrainMakeupExamRecord
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "培训补考记录-分页列表查询")
    @ApiOperation(value = "培训补考记录-分页列表查询", notes = "培训补考记录-分页列表查询")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = BdTrainMakeupExamRecord.class)
    })
    @GetMapping(value = "/list")
    public Result<?> queryPageList(BdTrainMakeupExamRecord bdTrainMakeupExamRecord,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<BdTrainMakeupExamRecord> page = new Page<BdTrainMakeupExamRecord>(pageNo, pageSize);
        IPage<BdTrainMakeupExamRecord> pageList = bdTrainMakeupExamRecordService.getList(page, bdTrainMakeupExamRecord);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param bdTrainMakeupExamRecord
     * @return
     */
    @AutoLog(value = "培训补考记录-添加")
    @ApiOperation(value = "培训补考记录-添加", notes = "培训补考记录-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody BdTrainMakeupExamRecord bdTrainMakeupExamRecord) {
        bdTrainMakeupExamRecordService.save(bdTrainMakeupExamRecord);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param bdTrainMakeupExamRecord
     * @return
     */
    @AutoLog(value = "培训补考记录-编辑")
    @ApiOperation(value = "培训补考记录-编辑", notes = "培训补考记录-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody BdTrainMakeupExamRecord bdTrainMakeupExamRecord) {
        bdTrainMakeupExamRecordService.updateById(bdTrainMakeupExamRecord);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "培训补考记录-通过id删除")
    @ApiOperation(value = "培训补考记录-通过id删除", notes = "培训补考记录-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        bdTrainMakeupExamRecordService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "培训补考记录-批量删除")
    @ApiOperation(value = "培训补考记录-批量删除", notes = "培训补考记录-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bdTrainMakeupExamRecordService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "培训补考记录-通过id查询")
    @ApiOperation(value = "培训补考记录-通过id查询", notes = "培训补考记录-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        BdTrainMakeupExamRecord bdTrainMakeupExamRecord = bdTrainMakeupExamRecordService.getById(id);
        if (bdTrainMakeupExamRecord == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(bdTrainMakeupExamRecord);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param bdTrainMakeupExamRecord
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainMakeupExamRecord bdTrainMakeupExamRecord) {
        return super.exportXls(request, bdTrainMakeupExamRecord, BdTrainMakeupExamRecord.class, "培训补考记录");
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
        return super.importExcel(request, response, BdTrainMakeupExamRecord.class);
    }

    /**
     * 批准补考
     *
     * @param makeUpTime
     * @return
     */
    @AutoLog(value = "批准补考")
    @ApiOperation(value = "批准补考", notes = "批准补考")
    @RequestMapping(value = "/makeUp", method = RequestMethod.POST)
    public Result<?> makeUp(@RequestParam(name = "makeUpTime", required = true) String makeUpTime,
                            @RequestParam(name = "id", required = true) String id) throws ParseException {
        bdTrainMakeupExamRecordService.makeUp(makeUpTime, id);
        return Result.OK("补考成功!");
    }


    /**
     * 批量批准
      * @param makeUpTime
     * @param idList
     * @return
     */
    @AutoLog(value = "批量批准")
    @ApiOperation(value = "批量批准", notes = "批量批准")
    @RequestMapping(value = "/batchMakeUp", method = RequestMethod.POST)
    public Result<?> batchMakeUp(@RequestParam(name = "makeUpTimeList", required = true) String makeUpTime,
                                 @RequestParam(name = "idList", required = true) List<String> idList) {
        bdTrainMakeupExamRecordService.batchMakeUp(makeUpTime, idList);
        return Result.OK("批量补考成功!");
    }
}
