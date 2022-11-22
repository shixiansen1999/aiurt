package com.aiurt.boot.weeklyplan.controller;

import com.aiurt.boot.weeklyplan.entity.ConstructionTemplate;
import com.aiurt.boot.weeklyplan.service.IConstructionTemplateService;
import com.aiurt.boot.weeklyplan.vo.ConstructionTemplateVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: construction_template
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Api(tags = "施工供电模板")
@RestController
@RequestMapping("/weeklyplan/constructionTemplate")
@Slf4j
public class ConstructionTemplateController extends BaseController<ConstructionTemplate, IConstructionTemplateService> {
    @Autowired
    private IConstructionTemplateService constructionTemplateService;

    /**
     * 施工供电模板分页查询
     *
     * @param constructionTemplate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "施工供电模板分页查询")
    @ApiOperation(value = "施工供电模板分页查询", notes = "施工供电模板分页查询")
    @GetMapping(value = "/list")
    public Result<IPage<ConstructionTemplateVO>> queryPageList(ConstructionTemplate constructionTemplate,
                                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               HttpServletRequest req) {
        Page<ConstructionTemplateVO> page = new Page<>(pageNo, pageSize);
        IPage<ConstructionTemplateVO> pageList = constructionTemplateService.queryPageList(page, constructionTemplate);
        return Result.OK(pageList);
    }

    /**
     * 施工供电模板-查询所有
     *
     * @param constructionTemplate
     * @param req
     * @return
     */
    @AutoLog(value = "施工供电模板-查询所有")
    @ApiOperation(value = "施工供电模板-查询所有", notes = "施工供电模板-查询所有")
    @GetMapping(value = "/selectAll")
    public Result<List<ConstructionTemplateVO>> selectAll(ConstructionTemplate constructionTemplate, HttpServletRequest req) {
        List<ConstructionTemplateVO> pageList = constructionTemplateService.selectAll(constructionTemplate);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param constructionTemplate
     * @return
     */
    @AutoLog(value = "施工供电模板-添加")
    @ApiOperation(value = "施工供电模板-添加", notes = "施工供电模板-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ConstructionTemplate constructionTemplate) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        constructionTemplate.setUserId(loginUser.getId());
        constructionTemplateService.save(constructionTemplate);
        return Result.OK("添加成功！记录ID为：【" + constructionTemplate.getId() + "】");
    }

    /**
     * 编辑
     *
     * @param constructionTemplate
     * @return
     */
    @AutoLog(value = "施工供电模板-编辑")
    @ApiOperation(value = "施工供电模板-编辑", notes = "施工供电模板-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody ConstructionTemplate constructionTemplate) {
        constructionTemplateService.updateById(constructionTemplate);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工供电模板-通过id删除")
    @ApiOperation(value = "施工供电模板-通过id删除", notes = "施工供电模板-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        constructionTemplateService.removeById(id);
        return Result.OK("删除成功!");
    }


    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工供电模板-通过id查询")
    @ApiOperation(value = "施工供电模板-通过id查询", notes = "施工供电模板-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ConstructionTemplateVO> queryById(@RequestParam(name = "id", required = true) String id) {
        ConstructionTemplateVO templateVO = constructionTemplateService.queryById(id);
        return Result.OK(templateVO);
    }

}
