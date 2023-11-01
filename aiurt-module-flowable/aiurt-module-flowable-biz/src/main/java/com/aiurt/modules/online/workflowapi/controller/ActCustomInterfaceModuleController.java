package com.aiurt.modules.online.workflowapi.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterfaceModule;
import com.aiurt.modules.online.workflowapi.service.IActCustomInterfaceModuleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static com.aiurt.modules.online.workflowapi.service.IActCustomInterfaceModuleService.ROOT_PID_VALUE;

/**
 * @Description: act_custom_interface_module
 * @Author: wgp
 * @Date: 2023-08-14
 * @Version: V1.0
 */
@Api(tags = "自定义接口所属模块")
@RestController
@RequestMapping("/workflowapi/actCustomInterfaceModule")
@Slf4j
public class ActCustomInterfaceModuleController extends BaseController<ActCustomInterfaceModule, IActCustomInterfaceModuleService> {
    private static final String IS_TRUE = "true";
    @Autowired
    private IActCustomInterfaceModuleService actCustomInterfaceModuleService;

    /**
     * 分页列表查询
     *
     * @param actCustomInterfaceModule
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation(value = "act_custom_interface_module-分页列表查询", notes = "act_custom_interface_module-分页列表查询")
    @GetMapping(value = "/rootList")
    public Result<IPage<ActCustomInterfaceModule>> queryPageList(ActCustomInterfaceModule actCustomInterfaceModule,
                                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                 HttpServletRequest req) {
        String hasQuery = req.getParameter("hasQuery");
        if (hasQuery != null && IS_TRUE.equals(hasQuery)) {
            QueryWrapper<ActCustomInterfaceModule> queryWrapper = QueryGenerator.initQueryWrapper(actCustomInterfaceModule, req.getParameterMap());
            List<ActCustomInterfaceModule> list = actCustomInterfaceModuleService.queryTreeListNoPage(queryWrapper);
            IPage<ActCustomInterfaceModule> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } else {
            String parentId = actCustomInterfaceModule.getPid();
            if (oConvertUtils.isEmpty(parentId)) {
                parentId = "0";
            }
            actCustomInterfaceModule.setPid(null);
            QueryWrapper<ActCustomInterfaceModule> queryWrapper = QueryGenerator.initQueryWrapper(actCustomInterfaceModule, req.getParameterMap());
            // 使用 eq 防止模糊查询
            queryWrapper.eq("pid", parentId);
            Page<ActCustomInterfaceModule> page = new Page<ActCustomInterfaceModule>(pageNo, pageSize);
            IPage<ActCustomInterfaceModule> pageList = actCustomInterfaceModuleService.page(page, queryWrapper);
            return Result.OK(pageList);
        }
    }

    /**
     * 加载全部节点的数据
     *
     * @return
     */
    @ApiOperation(value = "加载全部节点的数据", notes = "加载全部节点的数据")
    @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
    public Result<List<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "name", required = false) String name) {
        Result<List<SelectTreeModel>> result = new Result<>();
        try {
            List<SelectTreeModel> ls = actCustomInterfaceModuleService.getModuleTree(name);
            result.setResult(ls);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 分层加载节点的数据
     *
     * @param pid 父节点iD
     * @return
     */
    @ApiOperation(value = "分层加载节点的数据", notes = "分层加载节点的数据")
    @GetMapping(value = "/childList")
    public Result<List<ActCustomInterfaceModule>> queryChildList(@RequestParam(name = "pid", required = false) String pid,
                                                                 @RequestParam(name = "name", required = false) String name) {

        LambdaQueryWrapper<ActCustomInterfaceModule> lam = new LambdaQueryWrapper<>();

        lam.eq(ActCustomInterfaceModule::getPid, StrUtil.isNotEmpty(pid) ? pid : ROOT_PID_VALUE);
        if (StrUtil.isNotEmpty(name)) {
            lam.like(ActCustomInterfaceModule::getModuleName, name);
        }

        List<ActCustomInterfaceModule> list = actCustomInterfaceModuleService.list(lam);

        return Result.OK(list);
    }

    /**
     * 批量查询子节点
     *
     * @param parentIds 父ID（多个采用半角逗号分割）
     * @param parentIds
     * @return 返回 IPage
     * @return
     */
    @ApiOperation(value = "act_custom_interface_module-批量获取子数据", notes = "act_custom_interface_module-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<ActCustomInterfaceModule> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<ActCustomInterfaceModule> list = actCustomInterfaceModuleService.list(queryWrapper);
            IPage<ActCustomInterfaceModule> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }

    /**
     * 添加自定义接口模块
     *
     * @param actCustomInterfaceModule
     * @return
     */
    @AutoLog(value = "添加自定义接口模块")
    @ApiOperation(value = "添加自定义接口模块", notes = "添加自定义接口模块")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ActCustomInterfaceModule actCustomInterfaceModule) {
        actCustomInterfaceModuleService.addActCustomInterfaceModule(actCustomInterfaceModule);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑自定义接口模块
     *
     * @param actCustomInterfaceModule
     * @return
     */
    @AutoLog(value = "编辑自定义接口模块")
    @ApiOperation(value = "编辑自定义接口模块", notes = "编辑自定义接口模块")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody ActCustomInterfaceModule actCustomInterfaceModule) {
        actCustomInterfaceModuleService.updateActCustomInterfaceModule(actCustomInterfaceModule);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除自定义接口模块
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id删除自定义接口模块")
    @ApiOperation(value = "通过id删除自定义接口模块", notes = "通过id删除自定义接口模块")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        actCustomInterfaceModuleService.deleteActCustomInterfaceModule(id);
        return Result.OK("删除成功!");
    }


    /**
     * 通过id查询自定义接口模块
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "通过id查询自定义接口模块", notes = "通过id查询自定义接口模块")
    @GetMapping(value = "/queryById")
    public Result<ActCustomInterfaceModule> queryById(@RequestParam(name = "id", required = true) String id) {
        ActCustomInterfaceModule actCustomInterfaceModule = actCustomInterfaceModuleService.getById(id);
        if (actCustomInterfaceModule == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(actCustomInterfaceModule);
    }

}
