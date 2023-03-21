package com.aiurt.modules.recycle.controller;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.recycle.constant.SysRecycleConstant;
import com.aiurt.modules.recycle.entity.SysRecycle;
import com.aiurt.modules.recycle.service.ISysRecycleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Api(tags="回收站")
@RestController
@RequestMapping("/recycle")
@Slf4j
public class SysRecycleController {
    @Autowired
    private ISysRecycleService sysRecycleService;

    /**
     * 分页列表查询
     *
     * @param sysRecycle
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "回收站分页列表查询")
    @ApiOperation(value="回收站分页列表查询", notes="回收站分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysRecycle>> queryPageList(SysRecycle sysRecycle,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SysRecycle> queryWrapper = QueryGenerator.initQueryWrapper(sysRecycle, req.getParameterMap());
        Page<SysRecycle> page = new Page<>(pageNo, pageSize);
//        queryWrapper.lambda().ne(SysRecycle::getState, SysRecycleConstant.STATE_DELETE);
        queryWrapper.lambda().eq(SysRecycle::getState, SysRecycleConstant.STATE_NORMAL);
        IPage<SysRecycle> pageList = sysRecycleService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询回收站记录")
    @ApiOperation(value="通过id查询回收站记录", notes="通过id查询回收站记录")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
        SysRecycle sysRecycle = sysRecycleService.getById(id);
        if(sysRecycle==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(sysRecycle);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "删除", operateType = 4, operateTypeAlias = "通过id删除回收站记录")
    @ApiOperation(value = "通过id删除回收站记录", notes = "通过id删除回收站记录")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        LambdaUpdateWrapper<SysRecycle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysRecycle::getState, SysRecycleConstant.STATE_DELETE);
        updateWrapper.eq(SysRecycle::getId, id);
        sysRecycleService.update(null, updateWrapper);
        return Result.OK("删除成功!");
    }

    /**
     * 通过ids批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "删除", operateType = 4, operateTypeAlias = "通过ids批量删除")
    @ApiOperation(value = "通过ids批量删除回收站记录", notes = "通过ids批量删除回收站记录")
    @DeleteMapping(value = "/deleteBatchByIds")
    public Result<?> deleteBatchByIds(@RequestParam(name = "ids", required = true) List<String> ids) {
        LambdaUpdateWrapper<SysRecycle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysRecycle::getState, SysRecycleConstant.STATE_DELETE);
        updateWrapper.in(SysRecycle::getId, ids);
        sysRecycleService.update(null, updateWrapper);
        return Result.OK("删除成功!");
    }

    /**
     * 通过id还原数据
     *
     * @param id
     * @return
     */
    @AutoLog(value = "还原数据",operateType = 1,operateTypeAlias = "通过id还原数据")
    @ApiOperation(value="通过id还原数据", notes="通过id还原数据")
    @PostMapping(value = "/restoreById")
    public Result<?> restoreById(@RequestParam(name="id",required=true) String id) throws SQLException {
        return sysRecycleService.restoreById(id);
    }

    /**
     * 通过ids批量还原数据
     *
     * @param
     * @return
     */
    @AutoLog(value = "还原数据",operateType = 1,operateTypeAlias = "通过ids批量还原数据")
    @ApiOperation(value="通过ids批量还原数据", notes="通过ids批量还原数据")
    @PostMapping(value = "/restoreBatchByIds")
    public Result<?> restoreBatchByIds(@RequestParam(name="ids",required=true) List<String> ids) throws SQLException {
//        List<String> ids = new ArrayList<>();
        return sysRecycleService.restoreBatchByIds(ids);
    }


}
