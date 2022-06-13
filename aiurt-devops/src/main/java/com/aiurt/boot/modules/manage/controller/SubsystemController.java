package com.aiurt.boot.modules.manage.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.RoleAdditionalUtils;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.service.IDeviceTypeService;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.entity.SubsystemUser;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.manage.service.ISubsystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: cs_subsystem
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "子系统信息")
@RestController
@RequestMapping("/manage/subsystem")
public class SubsystemController {
    @Autowired
    private ISubsystemService subsystemService;
    @Autowired
    private ISubsystemUserService subsystemUserService;

    @Autowired
    private IDeviceTypeService deviceTypeService;

    @Resource
    private RoleAdditionalUtils roleAdditionalUtils;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param subsystem
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "子系统信息-分页列表查询")
    @ApiOperation(value = "子系统信息-分页列表查询", notes = "子系统信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Subsystem>> queryPageList(Subsystem subsystem,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        Result<IPage<Subsystem>> result = new Result<IPage<Subsystem>>();
        QueryWrapper<Subsystem> queryWrapper = QueryGenerator.initQueryWrapper(subsystem, req.getParameterMap());
        Page<Subsystem> page = new Page<Subsystem>(pageNo, pageSize);
        IPage<Subsystem> pageList = subsystemService.page(page, queryWrapper);
        pageList.getRecords().forEach(temp -> {
            List<SubsystemUser> subsystemUsers=subsystemUserService.list(new QueryWrapper<SubsystemUser>().eq("sub_id",temp.getId()));
            List<String> userids=subsystemUsers.stream().map(SubsystemUser::getUserId).collect(Collectors.toList());
            temp.setSelectedSubSysUsers(String.join(",",userids));
        });
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param subsystem
     * @return
     */
    @AutoLog(value = "子系统信息-添加")
    @ApiOperation(value = "子系统信息-添加", notes = "子系统信息-添加")
    @PostMapping(value = "/add")
    public Result<Subsystem> add(@RequestBody Subsystem subsystem) {
        Result<Subsystem> result = new Result<Subsystem>();
        try {
            subsystemService.save(subsystem);
            addSubSysUser(subsystem);
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
     * @param subsystem
     * @return
     */
    @AutoLog(value = "子系统信息-编辑")
    @ApiOperation(value = "子系统信息-编辑", notes = "子系统信息-编辑")
    @PutMapping(value = "/edit")
    public Result<Subsystem> edit(@RequestBody Subsystem subsystem) {
        Result<Subsystem> result = new Result<Subsystem>();
        Subsystem subsystemEntity = subsystemService.getById(subsystem.getId());
        if (subsystemEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = subsystemService.updateById(subsystem);
            //先删除 再添加
            subsystemUserService.remove(new QueryWrapper<SubsystemUser>().eq("sub_id", subsystem.getId()));
            addSubSysUser(subsystem);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 添加技术员
     *
     * @param subsystem
     */
    private void addSubSysUser(Subsystem subsystem) {
        if (oConvertUtils.isNotEmpty(subsystem.getSelectedSubSysUsers())) {
            String[] users = subsystem.getSelectedSubSysUsers().split(",");
            for (String user : users) {
                SubsystemUser subsystemUser = new SubsystemUser();
                subsystemUser.setUserId(user);
                subsystemUser.setSubId(subsystem.getId());
                subsystemUser.setCreateTime(new Date());
                subsystemUserService.save(subsystemUser);
            }
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "子系统信息-通过id删除")
    @ApiOperation(value = "子系统信息-通过id删除", notes = "子系统信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            subsystemService.removeById(id);
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
    @AutoLog(value = "子系统信息-批量删除")
    @ApiOperation(value = "子系统信息-批量删除", notes = "子系统信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Subsystem> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Subsystem> result = new Result<Subsystem>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.subsystemService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "子系统信息-通过id查询")
    @ApiOperation(value = "子系统信息-通过id查询", notes = "子系统信息-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Subsystem> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Subsystem> result = new Result<Subsystem>();
        Subsystem subsystem = subsystemService.getById(id);
        if (subsystem == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(subsystem);
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
        QueryWrapper<Subsystem> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                Subsystem subsystem = JSON.parseObject(deString, Subsystem.class);
                queryWrapper = QueryGenerator.initQueryWrapper(subsystem, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<Subsystem> pageList = subsystemService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "cs_subsystem列表");
        mv.addObject(NormalExcelConstants.CLASS, Subsystem.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_subsystem列表数据", "导出人:Jeecg", "导出信息"));
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
                List<Subsystem> listSubsystems = ExcelImportUtil.importExcel(file.getInputStream(), Subsystem.class, params);
                subsystemService.saveBatch(listSubsystems);
                return Result.ok("文件导入成功！数据行数:" + listSubsystems.size());
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

    @GetMapping("subsystemSelect")
    public Result<List<Subsystem>> subsystemSelect() {
        // DO: 2022/1/4 加入权限控制
        //LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //List<String> codes = roleAdditionalUtils.getListSystemCodesByUserId(user.getId());
        //if (CollectionUtils.isEmpty(codes)){
        //    return Result.ok(new ArrayList<>());
        //}
        Result<List<Subsystem>> result = new Result<List<Subsystem>>();
        List<Subsystem> lineList = subsystemService.list(new LambdaQueryWrapper<Subsystem>().eq(Subsystem::getDelFlag, 0));
                //.in(Subsystem::getSystemCode,codes));
        result.setResult(lineList);
        return result;
    }


    /**
     * @Author: lhz
     * @Date: 2021-09-27
     * @Version: V1.0
     */
    @GetMapping("subsystemSelectWithDevice")
    public Result<List<Subsystem>> subsystemSelectWithDevice() {
        List<Subsystem> subsystemList = subsystemService.lambdaQuery().eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0).list();

        if (CollectionUtils.isNotEmpty(subsystemList)) {

            Map<String, String> map = subsystemList.stream().collect(Collectors.toMap(Subsystem::getSystemCode, Subsystem::getSystemName));

            List<DeviceType> typeList = deviceTypeService.lambdaQuery()
                    .eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .orderByDesc(DeviceType::getCreateTime)
                    .list();



            typeList.forEach(type -> {
                if (type.getCreateBy() != null) {

                    LoginUser loginUser = sysBaseAPI.getUserById(type.getUpdateBy());

                    if (loginUser != null) {
                        type.setCreateBy(loginUser.getRealname());
                    }
                }
                if (type.getUpdateBy() != null) {

                    LoginUser loginUser = sysBaseAPI.getUserById(type.getUpdateBy());
                    if (loginUser != null) {
                        type.setUpdateBy(loginUser.getRealname());
                    }
                }
                type.setSystemName(map.get(type.getSystemCode()));
            });

            Map<String, List<DeviceType>> typeMap = null;
            if (CollectionUtils.isNotEmpty(typeList)) {
                typeMap = typeList.stream().collect(Collectors.groupingBy(DeviceType::getSystemCode));
            }

            Map<String, List<DeviceType>> finalTypeMap = typeMap;

            subsystemList.forEach(l -> {
                        if (finalTypeMap != null) {
                            List<DeviceType> types = finalTypeMap.get(l.getSystemCode());
                            l.setDeviceTypeList(types!=null?types:new ArrayList<>());
                        } else {
                            l.setDeviceTypeList(new ArrayList<>());
                        }
                    }
            );
        }
        return Result.ok(subsystemList);
    }

    @GetMapping("getUserData")
    public Result<List<LoginUser>> getUserData() {
        String roleCode = "jishuyuan";
        Result<List<LoginUser>> result = new Result<List<LoginUser>>();
        // todo
        List<LoginUser> userList = null;
        //userService.selectUsersByRoleCode(roleCode);
        result.setResult(userList);
        return result;
    }

}
