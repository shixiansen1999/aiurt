package com.aiurt.boot.modules.standardManage.inspectionSpecification.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.util.StrUtil;

import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.standardManage.inspectionSpecification.entity.InspectionCode;
import com.aiurt.boot.modules.standardManage.inspectionSpecification.service.IInspectionCodeService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.mapper.SysDepartMapper;
import lombok.extern.slf4j.Slf4j;

import org.apache.shiro.SecurityUtils;

import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 标准管理-检修规范
 * @Author: qian
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "标准管理-检修规范")
@RestController
@RequestMapping("/inspectionSpecification/inspectionCode")
public class InspectionCodeController {
    @Autowired
    private IInspectionCodeService inspectionCodeService;

    @Autowired
    private SysDepartMapper departMapper;

    /**
     * 分页列表查询
     *
     * @param inspectionCode
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "检修标准管理-分页列表查询")
    @ApiOperation(value = "检修标准管理-分页列表查询", notes = "检修标准管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<InspectionCode>> queryPageList(InspectionCode inspectionCode,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        Result<IPage<InspectionCode>> result = new Result<IPage<InspectionCode>>();
        QueryWrapper<InspectionCode> queryWrapper = QueryGenerator.initQueryWrapper(inspectionCode, req.getParameterMap());
        Page<InspectionCode> page = new Page<InspectionCode>(pageNo, pageSize);
        IPage<InspectionCode> pageList = inspectionCodeService.page(page, queryWrapper);
        final List<InspectionCode> list = pageList.getRecords();
        list.forEach(x -> {
            final String teamIds = x.getOrganizationIds();
            final String[] split = teamIds.split(",");
            String teamNames = "";
            for (String teamId : split) {
                final SysDepart sysDepart = departMapper.selectById(teamId);
                if (sysDepart == null){
                    return;
                }
                teamNames = teamNames.concat(sysDepart.getDepartName()).concat(",");
            }
            if (StrUtil.isNotBlank(teamNames)) {
                int indexb = teamNames.lastIndexOf(",");
                teamNames = teamNames.substring(0, indexb) + teamNames.substring(indexb + 1, teamNames.length());
            }
            x.setTeamNames(teamNames);
        });
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param inspectionCode
     * @return
     */
    @AutoLog(value = "检修标准管理-添加")
    @ApiOperation(value = "检修标准管理-添加", notes = "检修标准管理-添加")
    @PostMapping(value = "/add")
    public Result<InspectionCode> add(@RequestBody @Validated InspectionCode inspectionCode) {
        Result<InspectionCode> result = new Result<InspectionCode>();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        try {
            inspectionCode.setCreateBy(user.getRealname());

            //一个班组一年一条检修规范
            final String organizationIds = inspectionCode.getOrganizationIds();
            final String[] split = organizationIds.split(",");
            for (String teamId : split) {
                final QueryWrapper<InspectionCode> wrapper = new QueryWrapper<>();
                wrapper.apply(teamId != null, "find_in_set('" + teamId + "',organization_ids)")
                        .eq("years", inspectionCode.getYears()).eq("del_flag", 0);
                final Long count = inspectionCodeService.count(wrapper);
                if (count >= 1L) {
                    return result.error500("一个班组一年只存在一条检修规范!");
                }
            }
            inspectionCodeService.save(inspectionCode);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param inspectionCode
     * @return
     */
    @AutoLog(value = "检修标准管理-编辑")
    @ApiOperation(value = "检修标准管理-编辑", notes = "检修标准管理-编辑")
    @PutMapping(value = "/edit")
    public Result<InspectionCode> edit(@RequestBody @Validated InspectionCode inspectionCode) {
        Result<InspectionCode> result = new Result<InspectionCode>();
        InspectionCode inspectionCodeEntity = inspectionCodeService.getById(inspectionCode.getId());
        if (inspectionCodeEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = inspectionCodeService.updateById(inspectionCode);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修标准管理-通过id删除")
    @ApiOperation(value = "检修标准管理-通过id删除", notes = "检修标准管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            inspectionCodeService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "检修标准管理-批量删除")
    @ApiOperation(value = "检修标准管理-批量删除", notes = "检修标准管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<InspectionCode> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<InspectionCode> result = new Result<InspectionCode>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.inspectionCodeService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修标准管理-通过id查询")
    @ApiOperation(value = "检修标准管理-通过id查询", notes = "检修标准管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<InspectionCode> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<InspectionCode> result = new Result<InspectionCode>();
        InspectionCode inspectionCode = inspectionCodeService.getById(id);
        if (inspectionCode == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(inspectionCode);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 通过id复制
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修标准管理-通过id复制")
    @ApiOperation(value = "检修标准管理-通过id复制", notes = "检修标准管理-通过id复制")
    @GetMapping(value = "/copyById")
    public Result copyById(@RequestParam(name = "id", required = true) String id) {
        return inspectionCodeService.copy(id);
    }

    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    @AutoLog(value = "生成年检计划")
    @ApiOperation(value = "生成年检计划-通过id", notes = "生成年检计划-通过id")
    @PostMapping("/addAnnualPlan")
    public Result addAnnualPlan(@RequestParam(name = "id", required = true) String id) throws Exception {
        return inspectionCodeService.addAnnualPlan(id);
    }

    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    @AutoLog(value = "重新生成年检计划")
    @ApiOperation(value = "重新生成年检计划-通过id", notes = "重新生成年检计划-通过id")
    @PostMapping("/addAnnualNewPlan")
    public Result addAnnualNewPlan(@RequestParam(name = "id", required = true) String id) throws Exception {
        return inspectionCodeService.addAnnualNewPlan(id);
    }
}
