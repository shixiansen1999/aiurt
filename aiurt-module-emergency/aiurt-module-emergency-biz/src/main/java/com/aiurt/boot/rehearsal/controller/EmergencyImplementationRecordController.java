package com.aiurt.boot.rehearsal.controller;

import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalRegisterDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.service.IEmergencyImplementationRecordService;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.aiurt.boot.rehearsal.vo.EmergencyRecordReadOneVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDeptUserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public Result<String> add(@RequestBody @Validated(EmergencyImplementationRecord.Save.class) EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
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
        emergencyImplementationRecordService.submit(id, status);
        return Result.OK("提交成功！");
    }

    /**
     * 应急实施记录-编辑
     */
    @AutoLog(value = "应急实施记录-编辑")
    @ApiOperation(value = "应急实施记录-编辑", notes = "应急实施记录-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody @Validated(EmergencyImplementationRecord.Update.class) EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        emergencyImplementationRecordService.edit(emergencyRehearsalRegisterDTO);
        return Result.OK("编辑成功!");
    }


    /**
     * 应急实施记录-通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "应急实施记录-通过id删除")
    @ApiOperation(value = "应急实施记录-通过id删除", notes = "应急实施记录-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        emergencyImplementationRecordService.delete(id);
        return Result.OK("删除成功!");
    }

    /**
     * 应急实施记录-通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "应急实施记录-通过id查询")
    @ApiOperation(value = "应急实施记录-通过id查询", notes = "应急实施记录-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<EmergencyRecordReadOneVO> queryById(@RequestParam(name = "id", required = true) String id) {
        EmergencyRecordReadOneVO recordVO = emergencyImplementationRecordService.queryById(id);
        return Result.OK(recordVO);
    }

    /**
     * 应急模块-责任部门和用户联动信息
     */
    @AutoLog(value = "应急模块-责任部门和用户联动信息")
    @ApiOperation(value = "应急模块-责任部门和用户联动信息", notes = "应急模块-责任部门和用户联动信息")
    @GetMapping(value = "/getDeptUserGanged")
    public Result<List<SysDeptUserModel>> getDeptUserGanged() {
        List<SysDeptUserModel> deptUsers = emergencyImplementationRecordService.getDeptUserGanged();
        return Result.OK(deptUsers);
    }

    /**
     * 应急模块-责任人信息
     */
    @AutoLog(value = "应急模块-责任人信息")
    @ApiOperation(value = "应急模块-责任人信息", notes = "应急模块-责任人信息")
    @GetMapping(value = "/getDutyUser")
    public Result<List<LoginUser>> getDutyUser() {
        List<LoginUser> users = emergencyImplementationRecordService.getDutyUser();
        return Result.OK(users);
    }

}
