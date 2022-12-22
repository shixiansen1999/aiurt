package com.aiurt.modules.todo.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.todo.dto.TaskModuleDTO;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.service.ISysTodoListService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
@Api(tags = "待办池列表")
@RestController
@RequestMapping("/todo/sysTodoList")
@Slf4j
public class SysTodoListController extends BaseController<SysTodoList, ISysTodoListService> {
    @Autowired
    private ISysTodoListService sysTodoListService;

    /**
     * 分页列表查询
     *
     * @param sysTodoList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "待办池列表-分页列表查询", notes = "待办池列表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysTodoList>> queryPageList(SysTodoList sysTodoList,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<SysTodoList> page = new Page<SysTodoList>(pageNo, pageSize);
        IPage<SysTodoList> pageList = sysTodoListService.queryPageList(page, sysTodoList);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param sysTodoList
     * @return
     */
    @AutoLog(value = "待办池列表-添加")
    @ApiOperation(value = "待办池列表-添加", notes = "待办池列表-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysTodoList sysTodoList) {
        sysTodoListService.save(sysTodoList);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param sysTodoList
     * @return
     */
    @AutoLog(value = "待办池列表-编辑")
    @ApiOperation(value = "待办池列表-编辑", notes = "待办池列表-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody SysTodoList sysTodoList) {
        sysTodoListService.updateById(sysTodoList);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "待办池列表-通过id删除")
    @ApiOperation(value = "待办池列表-通过id删除", notes = "待办池列表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        sysTodoListService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "待办池列表-批量删除")
    @ApiOperation(value = "待办池列表-批量删除", notes = "待办池列表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysTodoListService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "待办池列表-通过id查询", notes = "待办池列表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysTodoList> queryById(@RequestParam(name = "id", required = true) String id) {
        SysTodoList sysTodoList = sysTodoListService.getById(id);
        if (sysTodoList == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(sysTodoList);
    }

    /**
     * 查询各个类型待办数量
     *
     * @param sysTodoList
     * @return
     */
    @ApiOperation(value = "查询各个类型待办数量", notes = "查询各个类型待办数量")
    @GetMapping(value = "/queryTaskModuleList")
    public Result<List<TaskModuleDTO>> queryTaskModuleList(SysTodoList sysTodoList) {
        List<TaskModuleDTO> sysTodoLists = sysTodoListService.queryTaskModuleList(sysTodoList);
        return Result.OK(sysTodoLists);
    }

}
