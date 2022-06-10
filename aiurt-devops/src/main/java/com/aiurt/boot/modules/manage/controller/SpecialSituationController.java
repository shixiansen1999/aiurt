package com.aiurt.boot.modules.manage.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.manage.entity.SituationUser;
import com.aiurt.boot.modules.manage.entity.SpecialSituation;
import com.aiurt.boot.modules.manage.model.SituationUserModel;
import com.aiurt.boot.modules.manage.service.ISituationUserService;
import com.aiurt.boot.modules.manage.service.ISpecialSituationService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserRoleService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: cs_special_situation
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "特情")
@RestController
@RequestMapping("/manage/specialSituation")
public class SpecialSituationController {
    @Autowired
    private ISpecialSituationService specialSituationService;
    @Autowired
    private ISituationUserService situationUserService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;

    /**
     * 分页列表查询
     *
     * @param specialSituation
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "cs_special_situation-分页列表查询")
    @ApiOperation(value = "cs_special_situation-分页列表查询", notes = "cs_special_situation-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SpecialSituation>> queryPageList(SpecialSituation specialSituation,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<SpecialSituation>> result = new Result<IPage<SpecialSituation>>();
        QueryWrapper<SpecialSituation> queryWrapper = QueryGenerator.initQueryWrapper(specialSituation, req.getParameterMap());
        Page<SpecialSituation> page = new Page<SpecialSituation>(pageNo, pageSize);
        IPage<SpecialSituation> pageList = specialSituationService.queryByCondition(page,queryWrapper,specialSituation);
        pageList.getRecords().forEach(temp -> {
            List<String> userNames=new ArrayList<>();
            List<SituationUser> situationUserList = situationUserService.list(new QueryWrapper<SituationUser>().eq("situation_id", temp.getId()));
            List<String> userids = situationUserList.stream().map(SituationUser::getUserId).collect(Collectors.toList());
            temp.setSelectedSubSysUsers(String.join(",", userids));
            userids.forEach(s -> {
                SysUser sysUser=sysUserService.getById(s);
                if (sysUser!=null){
                    userNames.add(sysUser.getRealname());
                }
            });
            temp.setSituationUsers(userNames.stream().map(String::valueOf).collect(Collectors.joining(",")));
            temp.setCreaterName(sysUserService.getUserByName(temp.getCreaterName()).getRealname());
        });
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param specialSituation
     * @return
     */
    @AutoLog(value = "cs_special_situation-添加")
    @ApiOperation(value = "cs_special_situation-添加", notes = "cs_special_situation-添加")
    @PostMapping(value = "/add")
    public Result<SpecialSituation> add(@RequestBody SpecialSituation specialSituation) {
        Result<SpecialSituation> result = new Result<SpecialSituation>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            specialSituation.setPublishTime(new Date());
            specialSituation.setCreateTime(new Date());
            specialSituation.setCreaterId(sysUser.getId());
            specialSituation.setCreaterName(sysUser.getUsername());
            specialSituationService.save(specialSituation);
            addSituationUser(specialSituation);//添加接收范围
            /**发布特情消息给指定人员消息*/
            Message message = new Message();
            message.setContent(ObjectUtil.isNotEmpty(specialSituation.getCotent()) ? "[" + specialSituation.getLevel() + "]" + specialSituation.getCotent().toString() : "您有一条新的特情消息");
            message.setTitle("特情通知");
            message.setCreateBy(sysUser.getId());
            message.setType(1);
            message.setCreateTime(new Date());
            message.setUpdateTime(new Date());
            String selectedSubSysUsers = specialSituation.getSelectedSubSysUsers();
            List<String> userIds = Arrays.stream(selectedSubSysUsers.split(",")).collect(Collectors.toList());
            List<String> userNames = sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds).eq(SysUser::getDelFlag, 0))
                    .stream().map(SysUser::getRealname).collect(Collectors.toList());
            MessageAddParam param = MessageAddParam.builder().message(message).userIds(userIds).userNames(userNames).build();
            messageService.addMessage(param);
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
     * @param specialSituation
     * @return
     */
    @AutoLog(value = "cs_special_situation-编辑")
    @ApiOperation(value = "cs_special_situation-编辑", notes = "cs_special_situation-编辑")
    @PutMapping(value = "/edit")
    public Result<SpecialSituation> edit(@RequestBody SpecialSituation specialSituation) {
        Result<SpecialSituation> result = new Result<SpecialSituation>();
        SpecialSituation specialSituationEntity = specialSituationService.getById(specialSituation.getId());
        if (specialSituationEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = specialSituationService.updateById(specialSituation);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 添加接受范围
     *
     * @param specialSituation
     */
    private void addSituationUser(SpecialSituation specialSituation) {
        if (oConvertUtils.isNotEmpty(specialSituation.getSelectedSubSysUsers())) {
            String[] users = specialSituation.getSelectedSubSysUsers().split(",");
            for (String user : users) {
                SituationUser situationUser = new SituationUser();
                situationUser.setUserId(user);
                situationUser.setSituationId(specialSituation.getId());
                situationUser.setCreateTime(new Date());
                situationUser.setViewStatus(0);//未查看
                situationUserService.save(situationUser);
            }
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "cs_special_situation-通过id删除")
    @ApiOperation(value = "cs_special_situation-通过id删除", notes = "cs_special_situation-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            specialSituationService.removeById(id);
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
    @AutoLog(value = "cs_special_situation-批量删除")
    @ApiOperation(value = "cs_special_situation-批量删除", notes = "cs_special_situation-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<SpecialSituation> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SpecialSituation> result = new Result<SpecialSituation>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.specialSituationService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "cs_special_situation-通过id查询")
    @ApiOperation(value = "cs_special_situation-通过id查询", notes = "cs_special_situation-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SpecialSituation> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SpecialSituation> result = new Result<SpecialSituation>();
        SpecialSituation specialSituation = specialSituationService.getById(id);
        if (specialSituation == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(specialSituation);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<SpecialSituation> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                SpecialSituation specialSituation = JSON.parseObject(deString, SpecialSituation.class);
                queryWrapper = QueryGenerator.initQueryWrapper(specialSituation, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SpecialSituation> pageList = specialSituationService.list(queryWrapper);
        pageList.forEach(temp -> {
            List<String> userNames=new ArrayList<>();
            List<SituationUser> situationUserList = situationUserService.list(new QueryWrapper<SituationUser>().eq("situation_id", temp.getId()));
            List<String> userids = situationUserList.stream().map(SituationUser::getUserId).collect(Collectors.toList());
            temp.setSelectedSubSysUsers(String.join(",", userids));
            userids.forEach(s -> {
                SysUser sysUser=sysUserService.getById(s);
                userNames.add(sysUser.getRealname());
            });
            temp.setSituationUsers(userNames.stream().map(String::valueOf).collect(Collectors.joining(",")));
        });
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "特情管理");
        mv.addObject(NormalExcelConstants.CLASS, SpecialSituation.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("特情管理列表数据", "", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
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
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SpecialSituation> listSpecialSituations = ExcelImportUtil.importExcel(file.getInputStream(), SpecialSituation.class, params);
                specialSituationService.saveBatch(listSpecialSituations);
                return Result.ok("文件导入成功！数据行数:" + listSpecialSituations.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    @ApiModelProperty(value = "查询班组下所有人员接口")
    @GetMapping("userSelectByBanzu")
    public Result<List<SituationUserModel>> userSelectByBanzu() {
        Result<List<SituationUserModel>> result = new Result<List<SituationUserModel>>();
        List<SituationUserModel> list = new ArrayList<>();
        result.setResult(list);
        return result;
    }

    @ApiModelProperty(value = "特情管理已读接口")
    @RequestMapping(value = "/specialSituationRead", method = RequestMethod.GET)
    public Result specialSituationRead(@RequestParam(name = "situationId", required = true) String situationId, @RequestParam(name = "userId", required = true) String userId) {
        Result result = new Result();
        SituationUser situationUser = situationUserService.getOne(new QueryWrapper<SituationUser>()
                .eq("situation_id", situationId)
                .eq("user_id", userId)
        );
        if (situationUser != null && situationUser.getViewStatus() == 0) {
            situationUser.setViewStatus(1);
            situationUser.setUpdateTime(new Date());
            situationUserService.updateById(situationUser);
        }
        result.setSuccess(true);
        result.success("已读成功！");
        return result;
    }

    @ApiModelProperty(value = "根据特清id查询接受情况")
    @RequestMapping(value = "/queryUserDataBySid", method = RequestMethod.GET)
    public Result<List<SituationUser>> queryUserDataBySid(@RequestParam(name = "situationId", required = true) String situationId,@RequestParam(name = "viewStatus", required = false) String viewStatus) {
        Result result = new Result();
        List<SituationUser> situationUserList = new ArrayList<>();
        if(oConvertUtils.isNotEmpty(viewStatus)) {
            situationUserList = situationUserService.list(new QueryWrapper<SituationUser>()
                    .eq("situation_id", situationId)
                    .eq("view_status",viewStatus)
            );
        }else {
            situationUserList = situationUserService.list(new QueryWrapper<SituationUser>()
                    .eq("situation_id", situationId)
            );
        }

        if (situationUserList != null && situationUserList.size() > 0) {
            situationUserList.forEach(temp -> {
                SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("id", temp.getUserId()));
                if (sysUser!=null){
                    temp.setUserName(sysUser.getRealname());
                }
            });
        }
        situationUserList = situationUserList.stream().filter(situationUser -> situationUser.getUserName() != null).collect(Collectors.toList());
        result.setResult(situationUserList);
        return result;
    }

    @ApiModelProperty(value = "首页查询最新特情管理")
    @RequestMapping(value = "/queryRecentSituation", method = RequestMethod.GET)
    public Result<SpecialSituation> queryRecentSituation(@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime) {
        List<SpecialSituation> specialSituations = new ArrayList<>();
        if(oConvertUtils.isNotEmpty(startTime)&&oConvertUtils.isNotEmpty(endTime)) {
            specialSituations = specialSituationService.list(new QueryWrapper<SpecialSituation>()
                    .gt("end_time", DateUtils.getDate())
                    .between("publish_time", startTime, endTime)
                    .orderByDesc("publish_time")
                    .last("limit 0,10")
            );
        }else {
            specialSituations = specialSituationService.list(new QueryWrapper<SpecialSituation>()
                    .orderByDesc("create_time").last("limit 0,10")
            );
        }
        //判空
        // TODO: 2021/12/20  改写返回为统一格式
        return Result.ok(CollectionUtils.isNotEmpty(specialSituations)?specialSituations.get(0):new SpecialSituation());
    }

    @RequestMapping(value = "/queryRecentSituation2", method = RequestMethod.GET)
    public Result<List<SpecialSituation>> queryRecentSituation2(@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime) {
        List<SpecialSituation> specialSituations = new ArrayList<>();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();
        List<String> userRole = sysUserRoleService.getUserRole(userId);
        //管理员可以看到全部班组特情
        if (userRole.contains("admin")){
            if(oConvertUtils.isNotEmpty(startTime)&&oConvertUtils.isNotEmpty(endTime)) {
                specialSituations = specialSituationService.list(new QueryWrapper<SpecialSituation>()
                        .gt("end_time", DateUtils.getDate())
                        .between("publish_time", startTime, endTime)
                        .orderByDesc("publish_time")
                        .last("limit 0,10")
                );
            }else {
                specialSituations = specialSituationService.list(new QueryWrapper<SpecialSituation>()
                        .orderByDesc("create_time").last("limit 0,10")
                );
            }
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("userId",userId);
            map.put("startTime",startTime);
            map.put("endTime",endTime);
            map.put("now",DateUtils.getNowDate());
            specialSituations = specialSituationService.getSpecialSituationsByUserId(map);
        }
        return Result.ok(specialSituations);
    }

    @GetMapping(value = "/getSpecialSituationNum")
    public Result getSpecialSituationNum(@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime) {
        long count = 0;
        if (oConvertUtils.isNotEmpty(startTime) && oConvertUtils.isNotEmpty(endTime)) {
            count = specialSituationService.count(new QueryWrapper<SpecialSituation>()
                    .between("publish_time", startTime, endTime)
            );
        } else {
            count = specialSituationService.count(new QueryWrapper<SpecialSituation>());
        }
        return Result.ok(count);
    }
}
