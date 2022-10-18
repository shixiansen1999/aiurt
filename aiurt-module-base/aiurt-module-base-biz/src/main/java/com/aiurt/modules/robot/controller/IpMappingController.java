package com.aiurt.modules.robot.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.entity.IpMapping;
import com.aiurt.modules.robot.service.IIpMappingService;
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

/**
 * @Description: ip_mapping
 * @Author: jeecg-boot
 * @Date: 2022-10-10
 * @Version: V1.0
 */
@Api(tags = "ip地址映射管理")
@RestController
@RequestMapping("/robot/ipMapping")
@Slf4j
public class IpMappingController extends BaseController<IpMapping, IIpMappingService> {
    @Autowired
    private IIpMappingService ipMappingService;

    /**
     * 分页列表查询
     *
     * @param ipMapping
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "ip_mapping-分页列表查询")
    @ApiOperation(value = "ip_mapping-分页列表查询", notes = "ip_mapping-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(IpMapping ipMapping,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<IpMapping> queryWrapper = QueryGenerator.initQueryWrapper(ipMapping, req.getParameterMap());
        Page<IpMapping> page = new Page<IpMapping>(pageNo, pageSize);
        IPage<IpMapping> pageList = ipMappingService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param ipMapping
     * @return
     */
    @AutoLog(value = "ip_mapping-添加")
    @ApiOperation(value = "ip_mapping-添加", notes = "ip_mapping-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody IpMapping ipMapping) {
        ipMappingService.saveIpMapping(ipMapping);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param ipMapping
     * @return
     */
    @AutoLog(value = "ip_mapping-编辑")
    @ApiOperation(value = "ip_mapping-编辑", notes = "ip_mapping-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody IpMapping ipMapping) {
        ipMappingService.updateIpMappingById(ipMapping);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "ip_mapping-通过id删除")
    @ApiOperation(value = "ip_mapping-通过id删除", notes = "ip_mapping-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        ipMappingService.removeById(id);
        return Result.OK("删除成功!");
    }


    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "ip_mapping-通过id查询")
    @ApiOperation(value = "ip_mapping-通过id查询", notes = "ip_mapping-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        IpMapping ipMapping = ipMappingService.getById(id);
        return Result.OK(ipMapping);
    }

    /**
     * 重复校验接口
     *
     * @param ipMapping
     * @return
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ApiOperation("重复校验接口")
    public Result<Object> doDuplicateCheck(IpMapping ipMapping) {
        return ipMappingService.doDuplicateCheck(ipMapping);
    }
}
