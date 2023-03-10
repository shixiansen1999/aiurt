package com.aiurt.boot.drools.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.drools.util.DroolsUtil;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.logging.log4j.util.Strings;
import org.jeecg.common.api.vo.Result;
import com.aiurt.boot.drools.entity.DroolsRule;
import com.aiurt.boot.drools.service.IDroolsRuleService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

/**
 * @Description: drools_rule
 * @Author: aiurt
 * @Date: 2023-03-09
 * @Version: V1.0
 */
@Api(tags = "drools_rule")
@RestController
@RequestMapping("/drools/droolsRule")
@Slf4j
public class DroolsRuleController extends BaseController<DroolsRule, IDroolsRuleService> {
    @Autowired
    private IDroolsRuleService droolsRuleService;

    @PostMapping("verifyRule")
    public Result<String> verifyRule(@RequestBody DroolsRule droolsRule) throws Exception {
        try {
            KieSession kieSession = DroolsUtil.reload(droolsRule.getRule());
            kieSession.dispose();
//			log.info("规则验证成功");
            return Result.ok("规则验证成功");
        } catch (Exception e) {
//			e.printStackTrace();
//			log.info("规则验证失败");
            return Result.error("规则验证失败");
        }
    }

    /**
     * 分页列表查询
     *
     * @param droolsRule
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "drools_rule-分页列表查询")
    @ApiOperation(value = "drools_rule-分页列表查询", notes = "drools_rule-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<DroolsRule>> queryPageList(DroolsRule droolsRule,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
//        QueryWrapper<DroolsRule> queryWrapper = QueryGenerator.initQueryWrapper(droolsRule, req.getParameterMap());
        LambdaQueryWrapper<DroolsRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DroolsRule::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.like(Strings.isNotBlank(droolsRule.getName()), DroolsRule::getName, droolsRule.getName());
        Page<DroolsRule> page = new Page<DroolsRule>(pageNo, pageSize);
        IPage<DroolsRule> pageList = droolsRuleService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param droolsRule
     * @return
     */
    @AutoLog(value = "drools_rule-添加")
    @ApiOperation(value = "drools_rule-添加", notes = "drools_rule-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody DroolsRule droolsRule) {
        if (droolsRule.getName() == null) {
            return Result.error("请输入规则名称");
        }
        if (droolsRuleService.queryByName(droolsRule.getName()) != null) {
            return Result.error("规则名称已存在，请换个名称");
        }
        // 验证规则
        try {
            KieSession kieSession = DroolsUtil.reload(droolsRule.getRule());
            kieSession.dispose();
        } catch (Exception e) {
            return Result.error("规则验证失败");
        }
        droolsRuleService.save(droolsRule);
        return Result.OK("添加成功！");
    }

    /**
     * 根据Id编辑
     *
     * @param droolsRule
     * @return
     */
    @AutoLog(value = "drools_rule-根据Id编辑")
    @ApiOperation(value = "drools_rule-根据Id编辑", notes = "drools_rule-根据Id编辑")
    @RequestMapping(value = "/editById", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> editById(@RequestBody DroolsRule droolsRule) {
        // 验证规则
        try {
            KieSession kieSession = DroolsUtil.reload(droolsRule.getRule());
            kieSession.dispose();
        } catch (Exception e) {
            return Result.error("规则验证失败");
        }
        droolsRuleService.updateById(droolsRule);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "drools_rule-通过id删除")
    @ApiOperation(value = "drools_rule-通过id删除", notes = "drools_rule-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        droolsRuleService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "drools_rule-批量删除")
    @ApiOperation(value = "drools_rule-批量删除", notes = "drools_rule-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.droolsRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "drools_rule-通过id查询")
    @ApiOperation(value = "drools_rule-通过id查询", notes = "drools_rule-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<DroolsRule> queryById(@RequestParam(name = "id", required = true) String id) {
        DroolsRule droolsRule = droolsRuleService.getById(id);
        if (droolsRule == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(droolsRule);
    }

    /**
     * 通过规则名称查询
     *
     * @param name
     * @return
     */
    //@AutoLog(value = "drools_rule-通过name查询")
    @ApiOperation(value = "drools_rule-通过name查询", notes = "drools_rule-通过name查询")
    @GetMapping(value = "/queryByName")
    public Result<DroolsRule> queryByName(@RequestParam(name = "name", required = true) String name) {
        DroolsRule droolsRule = droolsRuleService.queryByName(name);
        if (droolsRule == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(droolsRule);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param droolsRule
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DroolsRule droolsRule) {
        return super.exportXls(request, droolsRule, DroolsRule.class, "drools_rule");
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
        return super.importExcel(request, response, DroolsRule.class);
    }

}
