package com.aiurt.boot.rehearsal.controller;

import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalRegisterDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.service.IEmergencyImplementationRecordService;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
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

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: emergency_implementation_record
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Api(tags = "应急实施记录")
@RestController
@RequestMapping("/emergency/emergencyImplementationRecord")
@Slf4j
public class EmergencyImplementationRecordController extends BaseController<EmergencyImplementationRecord, IEmergencyImplementationRecordService> {
    @Autowired
    private IEmergencyImplementationRecordService emergencyImplementationRecordService;

    /**
     * 应急实施记录-分页列表查询
     *
     * @param
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation(value = "应急实施记录-分页列表查询", notes = "应急实施记录-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<EmergencyImplementationRecordVO>> queryPageList(EmergencyRecordDTO emergencyRecordDTO,
                                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                        HttpServletRequest req) {
        Page<EmergencyImplementationRecordVO> page = new Page<>(pageNo, pageSize);
        IPage<EmergencyImplementationRecordVO> pageList = emergencyImplementationRecordService.queryPageList(page, emergencyRecordDTO);
        return Result.OK(pageList);
    }

    /**
     * 应急实施记录-演练登记
     *
     * @param emergencyRehearsalRegisterDTO
     * @return
     */
    @AutoLog(value = "应急实施记录-演练登记")
    @ApiOperation(value = "应急实施记录-演练登记", notes = "应急实施记录-演练登记")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        String id = emergencyImplementationRecordService.rehearsalRegister(emergencyRehearsalRegisterDTO);
        return Result.OK("添加成功！记录ID为：【" + id + "】");
    }

    /**
     * 应急实施记录-提交(将记录更新为已提交)
     *
     * @return
     */
    @AutoLog(value = "应急实施记录-提交(将记录更新为已提交)")
    @ApiOperation(value = "应急实施记录-提交(将记录更新为已提交)", notes = "应急实施记录-提交(将记录更新为已提交)")
    @PostMapping(value = "/submit")
    public Result<String> submit(@RequestParam @ApiParam(value = "记录ID", name = "id", required = true) String id,
                                 @RequestParam @ApiParam(value = "记录状态(1待提交、2已提交)", name = "status", required = true) Integer status) {
        boolean submit = emergencyImplementationRecordService.submit(id, status);
        return Result.OK("提交成功！");
    }
//
//    /**
//     * 编辑
//     *
//     * @param emergencyImplementationRecord
//     * @return
//     */
//    @AutoLog(value = "emergency_implementation_record-编辑")
//    @ApiOperation(value = "emergency_implementation_record-编辑", notes = "emergency_implementation_record-编辑")
//    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
//    public Result<String> edit(@RequestBody EmergencyImplementationRecord emergencyImplementationRecord) {
//        emergencyImplementationRecordService.updateById(emergencyImplementationRecord);
//        return Result.OK("编辑成功!");
//    }
//
//    /**
//     * 通过id删除
//     *
//     * @param id
//     * @return
//     */
//    @AutoLog(value = "emergency_implementation_record-通过id删除")
//    @ApiOperation(value = "emergency_implementation_record-通过id删除", notes = "emergency_implementation_record-通过id删除")
//    @DeleteMapping(value = "/delete")
//    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
//        emergencyImplementationRecordService.removeById(id);
//        return Result.OK("删除成功!");
//    }
//
//    /**
//     * 批量删除
//     *
//     * @param ids
//     * @return
//     */
//    @AutoLog(value = "emergency_implementation_record-批量删除")
//    @ApiOperation(value = "emergency_implementation_record-批量删除", notes = "emergency_implementation_record-批量删除")
//    @DeleteMapping(value = "/deleteBatch")
//    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
//        this.emergencyImplementationRecordService.removeByIds(Arrays.asList(ids.split(",")));
//        return Result.OK("批量删除成功!");
//    }
//
//    /**
//     * 通过id查询
//     *
//     * @param id
//     * @return
//     */
//    //@AutoLog(value = "emergency_implementation_record-通过id查询")
//    @ApiOperation(value = "emergency_implementation_record-通过id查询", notes = "emergency_implementation_record-通过id查询")
//    @GetMapping(value = "/queryById")
//    public Result<EmergencyImplementationRecord> queryById(@RequestParam(name = "id", required = true) String id) {
//        EmergencyImplementationRecord emergencyImplementationRecord = emergencyImplementationRecordService.getById(id);
//        if (emergencyImplementationRecord == null) {
//            return Result.error("未找到对应数据");
//        }
//        return Result.OK(emergencyImplementationRecord);
//    }
//
//    /**
//     * 导出excel
//     *
//     * @param request
//     * @param emergencyImplementationRecord
//     */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, EmergencyImplementationRecord emergencyImplementationRecord) {
//        return super.exportXls(request, emergencyImplementationRecord, EmergencyImplementationRecord.class, "emergency_implementation_record");
//    }
//
//    /**
//     * 通过excel导入数据
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, EmergencyImplementationRecord.class);
//    }

}
