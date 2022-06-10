package com.aiurt.boot.modules.system.controller;

import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.system.util.JwtUtil;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.PasswordUtil;
import com.aiurt.boot.common.util.RedisUtil;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.manage.service.ISubsystemUserService;
import com.aiurt.boot.modules.shiro.vo.DefContants;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.entity.SysUserDepart;
import com.aiurt.boot.modules.system.entity.SysUserRole;
import com.aiurt.boot.modules.system.model.DepartIdModel;
import com.aiurt.boot.modules.system.model.SysUserSysDepartModel;
import com.aiurt.boot.modules.system.service.*;
import com.aiurt.boot.modules.system.vo.SysDepartUsersVO;
import com.aiurt.boot.modules.system.vo.SysUserRoleVO;
import com.aiurt.boot.modules.system.vo.UserChildrenVO;
import com.aiurt.boot.modules.system.vo.UserTreeVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author swsc
 * @since 2018-12-20
 */
@Slf4j
@RestController
@Api(tags = "系统用户")
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysUserDepartService sysUserDepartService;

    @Autowired
    private ISysUserRoleService userRoleService;

    @Autowired
    private ISysPositionService sysPositionService;

    @Autowired
    private IStationService stationService;

    @Autowired
    private ISubsystemService subsystemService;

    @Autowired
    private ISubsystemUserService subsystemUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${support.downFilePath.userExcelPath}")
    private String excelPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysUser>> queryPageList(SysUser user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String token = req.getHeader(DefContants.X_ACCESS_TOKEN);
        String username = JwtUtil.getUsername(token);
        IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    //@RequiresPermissions("user:add")
    public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        String selectedSchoolPeriods = jsonObject.getString("selectedSchoolPeriods");
        String selectedRoles = jsonObject.getString("selectedroles");
        String selectedDeparts = jsonObject.getString("selecteddeparts");

        try {
            SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
            String orgId = user.getOrgId();
            JSONArray systemCodes = jsonObject.getJSONArray("systemCodes");
            JSONArray departmentIds = jsonObject.getJSONArray("departmentIds");
            String systemCode = "";
            for (Object object : systemCodes) {
                systemCode += (String) object + ",";
            }
            String departmentId = "";
            List<String> ids = departmentIds.toJavaList(String.class);
            boolean boo = ids.contains(orgId);
            for (String id : ids) {
                departmentId += id + ",";
            }
            if (!boo) {
                departmentId += orgId + ",";
            }
            user.setSystemCodes(systemCode.substring(0, systemCode.length() - 1));
            user.setDepartmentIds(departmentId.substring(0, departmentId.length() - 1));
           /* selectedDeparts = user.getOrgId();
            SysDepart sysDepart = sysDepartService.getById(user.getOrgId());
            user.setAccount(user.getUsername());*/
            user.setCreateTime(new Date());//设置创建时间
            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
            user.setPassword(passwordEncode);
            user.setStatus(1);
            user.setDelFlag("0");
            /*user.setOrgCode(sysDepart.getOrgCode());
            if ("3".equals(sysDepart.getOrgCategory())) {
                //分园
                user.setOrgType("1");
            } else {
                //总园
                user.setOrgType("0");
            }

            user.setPostId(sysPositionService.getOne(new QueryWrapper<SysPosition>().eq("code", user.getPost()).eq("del_flag", 0)).getId());*/
            SysDepart sysDepart = sysDepartService.getById(user.getOrgId());
            user.setOrgName(sysDepart.getDepartName());
            user.setOrgCode(sysDepart.getOrgCode());
            sysUserService.addUserWithRole(user, selectedRoles);
            sysUserService.addUserWithDepart(user, selectedDeparts);
            redisUtil.set(CommonConstant.PREFIX_USER_DEPARTMENT_IDS + user.getId(), user.getDepartmentIds());
            redisUtil.set(CommonConstant.PREFIX_USER_SYSTEM_CODES + user.getId(), user.getSystemCodes());
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    //@RequiresPermissions("user:edit")
    public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
            sysBaseAPI.addLog("编辑用户，id： " + jsonObject.getString("id"), CommonConstant.LOG_TYPE_2, 2);
            if (sysUser == null) {
                result.onnull("未找到对应实体");
            } else {
                SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
                String orgId = user.getOrgId();
                user.setUpdateTime(new Date());
                //String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
                user.setPassword(sysUser.getPassword());
                String selectedSchoolPeriods = jsonObject.getString("selectedSchoolPeriods");
                JSONArray systemCodes = jsonObject.getJSONArray("systemCodes");
                JSONArray departmentIds = jsonObject.getJSONArray("departmentIds");
                String systemCode = "";
                for (Object object : systemCodes) {
                    systemCode += (String) object + ",";
                }
                String departmentId = "";
                List<String> ids = departmentIds.toJavaList(String.class);
                boolean boo = ids.contains(orgId);
                for (String id : ids) {
                    departmentId += id + ",";
                }
                if (!boo) {
                    departmentId += orgId + ",";
                }
                user.setSystemCodes(systemCode.substring(0, systemCode.length() - 1));
                user.setDepartmentIds(departmentId.substring(0, departmentId.length() - 1));
                String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
                SysDepart sysDepart = sysDepartService.getById(jsonObject.getString("orgId"));
                user.setOrgId(sysDepart.getId());
                user.setOrgName(sysDepart.getDepartName());
                user.setOrgCode(sysDepart.getOrgCode());
                user.setPost(jsonObject.getString("post"));
                sysUserService.editUserWithRole(user, roles);
                sysUserService.editUserWithDepart(user, departs);
                redisUtil.set(CommonConstant.PREFIX_USER_DEPARTMENT_IDS + sysUser.getId(), sysUser.getDepartmentIds());
                redisUtil.set(CommonConstant.PREFIX_USER_SYSTEM_CODES + sysUser.getId(), sysUser.getSystemCodes());
                result.success("修改成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @Description: 批量设置用户权限
     * @author: niuzeyu
     * @date: 2021/12/30 11:12
     * @Return: com.swsc.copsms.common.api.vo.Result<?>
     */
    @RequestMapping(value = "/batchEdit", method = RequestMethod.PUT)
    public Result<SysUser> batchEdit(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            List<String> systemCodes = jsonObject.getJSONArray("systemCodes").toJavaList(String.class);
            List<String> departmentIds = jsonObject.getJSONArray("departmentIds").toJavaList(String.class);
            String idsString = jsonObject.getString("ids");
            String[] ids = idsString.substring(0, idsString.length() - 1).split(",");
            List<String> idsList = Stream.of(ids).collect(Collectors.toList());
            this.sysUserService.updateBatchUsersPermission(idsList, departmentIds, systemCodes);
            result.success("权限修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 删除用户
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysBaseAPI.addLog("删除用户，id： " + id, CommonConstant.LOG_TYPE_2, 3);
        this.sysUserService.deleteUser(id);
        return Result.ok("删除用户成功");
    }

    /**
     * 批量删除用户
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        sysBaseAPI.addLog("批量删除用户， ids： " + ids, CommonConstant.LOG_TYPE_2, 3);
        this.sysUserService.deleteBatchUsers(ids);
        return Result.ok("批量删除用户成功");
    }

    /**
     * 冻结&解冻用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
    public Result<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            String ids = jsonObject.getString("ids");
            String status = jsonObject.getString("status");
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
                            new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId, id));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;

    }

    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        log.info(result.toString());
        return result;
    }

    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }

    //TODO
    @RequestMapping(value = "/queryUserSystemCodes", method = RequestMethod.GET)
    public Result<List<String>> queryUserSystemCodes(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }

    /**
     * 校验用户账号是否唯一<br>
     * 可以校验其他 需要检验什么就传什么。。。
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(SysUser sysUser) {
        Result<Boolean> result = new Result<>();
        //如果此参数为false则程序发生异常
        result.setResult(true);
        try {
            //通过传入信息查询新的用户信息
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("用户账号已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/changPassword", method = RequestMethod.PUT)
    public Result<?> changPassword(@RequestBody SysUser sysUser) {
        SysUser u = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (u == null) {
            return Result.error("用户不存在！");
        }
        sysUser.setId(u.getId());
        return sysUserService.changePassword(sysUser);
    }

    /**
     * 查询指定用户和部门关联的数据
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("查找成功");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("查找失败");
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("查找过程中出现了异常: " + e.getMessage());
            return result;
        }

    }

    /**
     * 生成在添加用户情况下没有主键的问题,返回给前端,根据该id绑定部门数据
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("我执行了,生成用户ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * 根据部门id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<SysUser>> queryUserByDepId(@RequestParam(name = "id", required = true) String id) {
        Result<List<SysUser>> result = new Result<>();
        List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 查询所有用户所对应的角色信息
     *
     * @return
     */
    @RequestMapping(value = "/queryUserRoleMap", method = RequestMethod.GET)
    public Result<Map<String, String>> queryUserRole() {
        Result<Map<String, String>> result = new Result<>();
        Map<String, String> map = userRoleService.queryUserRole();
        result.setResult(map);
        result.setSuccess(true);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据--------------------
        String selections = request.getParameter("selections");
        if (!oConvertUtils.isEmpty(selections)) {
            queryWrapper.in("id", selections.split(","));
        }
        //update-end--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据----------------------
        List<SysUser> pageList = sysUserService.list(queryWrapper);

        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户列表数据", "导出人:" + user.getRealname(), "导出信息"));
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

    /*@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            //params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (SysUser user : listSysUsers) {
                    if (user.getPassword() == null) {
                        // 密码默认为“123456”
                        user.setPassword("12345678");
                    }

                    //SysDepart sysDepart = sysDepartService.getById(user.getOrgId());
                    user.setCreateTime(new Date());//设置创建时间
                    String salt = oConvertUtils.randomGen(8);
                    user.setSalt(salt);
                    String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
                    user.setPassword(passwordEncode);
                    user.setStatus(1);
                    user.setDelFlag("0");
                    //user.setOrgCode(sysDepart.getOrgCode());
                    *//*if ("3".equals(sysDepart.getOrgCategory())) {
                        //分园
                        user.setOrgType("1");
                    } else {
                        //总园
                        user.setOrgType("0");
                    }
                    user.setPostId(sysPositionService.getOne(new QueryWrapper<SysPosition>().eq("code", user.getPost()).eq("del_flag", 0)).getId());*//*
                    //sysUserService.addUserWithRole(user, selectedRoles);
                    //sysUserService.addUserWithDepart(user, user.getOrgId());
                    sysUserService.save(user);
                }
                return Result.ok("文件导入成功！数据行数：" + listSysUsers.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("抱歉! 您导入的数据中用户名已经存在.");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }*/
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        try {
            MultipartFile file = multipartRequest.getFile("file");
            InputStream inputStream = file.getInputStream();//获取后缀名
            String nameAndType[] = file.getOriginalFilename().split("\\.");
            String type = nameAndType[1];
            // todo wgp删除导入
            List<Map<Integer, String>> userData = new ArrayList<>();
            sysUserService.importUserExcel(userData, request);
            return Result.ok("文件导入成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("文件导入失败:" + e.getMessage());
        }
    }

    /**
     * @param userIds
     * @return
     * @功能：根据id 批量查询
     */
    @RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
    public Result<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
        Result<Collection<SysUser>> result = new Result<>();
        String[] userId = userIds.split(",");
        Collection<String> idList = Arrays.asList(userId);
        Collection<SysUser> userRole = sysUserService.listByIds(idList);
        result.setSuccess(true);
        result.setResult(userRole);
        return result;
    }

    /**
     * 首页用户重置密码
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public Result<?> changPassword(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String oldpassword = json.getString("oldpassword");
        String password = json.getString("password");
        String confirmpassword = json.getString("confirmpassword");
        SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在！");
        }
        return sysUserService.resetPassword(username, oldpassword, password, confirmpassword);
    }

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userRoleList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page, roleId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 给指定角色添加用户
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<String>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for (String sysUserId : sysUserRoleVO.getUserIdList()) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId, sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id", sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if (one == null) {
                    sysUserRoleService.save(sysUserRole);
                }

            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRole(@RequestParam(name = "roleId") String roleId,
                                              @RequestParam(name = "userId", required = true) String userId
    ) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id", userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name = "roleId") String roleId,
            @RequestParam(name = "userIds", required = true) String userIds) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id", Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 部门用户列表
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> departUserList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByDepId(page, depId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 若某个用户包含多个部门，则会显示多条记录，可自行处理成单条记录
     */
    @GetMapping("/queryByOrgCode")
    public Result<?> queryByDepartId(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode") String orgCode,
            SysUser userParams
    ) {
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, new Page(pageNo, pageSize));
        return Result.ok(pageList);
    }

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 针对通讯录模块做的接口，将多个部门的用户合并成一条记录，并转成对前端友好的格式
     */
    @GetMapping("/queryByOrgCodeForAddressList")
    public Result<?> queryByOrgCodeForAddressList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode") String orgCode,
            SysUser userParams
    ) {
        IPage page = new Page(pageNo, pageSize);
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, page);
        List<SysUserSysDepartModel> list = pageList.getRecords();

        // 记录所有出现过的 user, key = userId
        Map<String, JSONObject> hasUser = new HashMap<>(list.size());

        JSONArray resultJson = new JSONArray(list.size());

        for (SysUserSysDepartModel item : list) {
            String userId = item.getSysUser().getId();
            // userId
            JSONObject getModel = hasUser.get(userId);
            // 之前已存在过该用户，直接合并数据
            if (getModel != null) {
                String departName = getModel.get("departName").toString();
                getModel.put("departName", (departName + " | " + item.getSysDepart().getDepartName()));
            } else {
                // 将用户对象转换为json格式，并将部门信息合并到 json 中
                JSONObject json = JSON.parseObject(JSON.toJSONString(item.getSysUser()));
                json.remove("id");
                json.put("userId", userId);
                json.put("departId", item.getSysDepart().getId());
                json.put("departName", item.getSysDepart().getDepartName());

                resultJson.add(json);
                hasUser.put(userId, json);
            }
        }

        IPage<JSONObject> result = new Page<>(pageNo, pageSize, pageList.getTotal());
        result.setRecords(resultJson.toJavaList(JSONObject.class));
        return Result.ok(result);
    }

    /**
     * 给指定部门添加对应的用户
     */
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public Result<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for (String sysUserId : sysDepartUsersVO.getUserIdList()) {
                SysUserDepart sysUserDepart = new SysUserDepart(null, sysUserId, sysDepId);
                QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id", sysUserId);
                SysUserDepart one = sysUserDepartService.getOne(queryWrapper);
                if (one == null) {
                    sysUserDepartService.save(sysUserDepart);
                }
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepart(@RequestParam(name = "depId") String depId,
                                                    @RequestParam(name = "userId", required = true) String userId
    ) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id", userId);
            sysUserDepartService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepartBatch(
            @RequestParam(name = "depId") String depId,
            @RequestParam(name = "userIds", required = true) String userIds) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id", Arrays.asList(userIds.split(",")));
            sysUserDepartService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 查询当前用户的所有部门/当前部门编码
     *
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String, Object>> getCurrentUserDeparts() {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<SysDepart> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setResult(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("查询失败！");
        }
        return result;
    }


    /**
     * 用户注册接口
     *
     * @param jsonObject
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUser user) {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        SysUser sysUser1 = sysUserService.getUserByName(username);
        if (sysUser1 != null) {
            result.setMessage("用户名已注册");
            result.setSuccess(false);
            return result;
        }
        SysUser sysUser2 = sysUserService.getUserByPhone(phone);

        if (sysUser2 != null) {
            result.setMessage("该手机号已注册");
            result.setSuccess(false);
            return result;
        }
        SysUser sysUser3 = sysUserService.getUserByEmail(email);
        if (sysUser3 != null) {
            result.setMessage("邮箱已被注册");
            result.setSuccess(false);
            return result;
        }

        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }

        try {
            user.setCreateTime(new Date());// 设置创建时间
            String salt = oConvertUtils.randomGen(8);
            String passwordEncode = PasswordUtil.encrypt(username, password, salt);
            user.setSalt(salt);
            user.setUsername(username);
            user.setRealname(username);
            user.setPassword(passwordEncode);
            user.setEmail(email);
            user.setPhone(phone);
            user.setStatus(1);
            user.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            //user.setActivitiSync(CommonConstant.ACT_SYNC_1);
            sysUserService.addUserWithRole(user, "ee8626f80f7c2619917b6236f3a7f02b");//默认临时角色 test
            result.success("注册成功");
        } catch (Exception e) {
            result.error500("注册失败");
        }
        return result;
    }

    /**
     * @return
     */
    @GetMapping("/querySysUser")
    public Result<Map<String, Object>> querySysUser(SysUser sysUser) {
        String phone = sysUser.getPhone();
        String username = sysUser.getUsername();
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        if (oConvertUtils.isNotEmpty(phone)) {
            SysUser user = sysUserService.getUserByPhone(phone);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        if (oConvertUtils.isNotEmpty(username)) {
            SysUser user = sysUserService.getUserByName(username);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        result.setSuccess(false);
        result.setMessage("验证失败");
        return result;
    }

    /**
     * 用户手机号验证
     */
    @PostMapping("/phoneVerification")
    public Result<String> phoneVerification(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<String>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }
        redisUtil.set(phone, smscode);
        result.setResult(smscode);
        result.setSuccess(true);
        return result;
    }

    /**
     * 用户更改密码
     */
    @GetMapping("/passwordChange")
    public Result<SysUser> passwordChange(@RequestParam(name = "username") String username,
                                          @RequestParam(name = "password") String password,
                                          @RequestParam(name = "smscode") String smscode,
                                          @RequestParam(name = "phone") String phone) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = new SysUser();
        Object object = redisUtil.get(phone);
        if (null == object) {
            result.setMessage("更改密码失败");
            result.setSuccess(false);
        }
        if (!smscode.equals(object)) {
            result.setMessage("更改密码失败");
            result.setSuccess(false);
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (sysUser == null) {
            result.setMessage("未找到对应实体");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMessage("密码修改完成！");
            return result;
        }
    }


    /**
     * 根据TOKEN获取用户的部分信息（返回的数据是可供表单设计器使用的数据）
     *
     * @return
     */
    @GetMapping("/getUserSectionInfoByToken")
    public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        try {
            String username = null;
            // 如果没有传递token，就从header中获取token并获取用户信息
            if (oConvertUtils.isEmpty(token)) {
                username = JwtUtil.getUserNameByToken(request);
            } else {
                username = JwtUtil.getUsername(token);
            }

            log.info(" ------ 通过令牌获取部分用户信息，当前用户： " + username);

            // 根据用户名查询用户信息
            SysUser sysUser = sysUserService.getUserByName(username);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sysUserId", sysUser.getId());
            map.put("sysUserCode", sysUser.getUsername()); // 当前登录用户登录账号
            map.put("sysUserName", sysUser.getRealname()); // 当前登录用户真实名称
            map.put("sysOrgCode", sysUser.getOrgCode()); // 当前登录用户部门编号

            log.info(" ------ 通过令牌获取部分用户信息，已获取的用户信息： " + map);

            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 获取用户列表  根据用户名和真实名 模糊匹配
     *
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/appUserList")
    public Result<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            //TODO 从查询效率上将不要用mp的封装的page分页查询 建议自己写分页语句
            LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
            //query.eq(SysUser::getActivitiSync, "1");
            query.eq(SysUser::getDelFlag, "0");
            query.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));

            Page<SysUser> page = new Page<>(pageNo, pageSize);
            IPage<SysUser> res = this.sysUserService.page(page, query);
            return Result.ok(res);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }

    }


    /**
     * 冻结&解冻用户
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/tableBatch", method = RequestMethod.PUT)
    public Result<SysUser> tableBatch(@RequestBody SysUser sysUser) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            sysUserService.updateById(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;

    }

    /**
     * 冻结&解冻用户
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addsignBatch", method = RequestMethod.PUT)
    public Result<SysUser> addsignBatch(@RequestBody SysUser sysUser) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            sysUserService.updateById(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;

    }

    /**
     * 冻结&解冻用户
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/lookOption", method = RequestMethod.PUT)
    public Result<SysUser> lookOption(@RequestBody SysUser sysUser) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            sysUserService.updateById(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;

    }

    /**
     * 获取当前登陆用户
     *
     * @return
     */
    @GetMapping("/queryUser")
    public Result<?> queryUser() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser userAllInfoByUserId = sysUserService.getUserAllInfoByUserId(sysUser.getId());
        return Result.ok(userAllInfoByUserId);
    }

    @PostMapping("/updateUserInfo")
    public Result<?> updateUserInfo(@RequestBody JSONObject jsonObject) {
        try {
            SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
            UpdateWrapper wrapper = new UpdateWrapper<SysUser>();
            wrapper.set("realname", user.getRealname());
            wrapper.set("phone", user.getPhone());
            wrapper.eq("username", user.getUsername());
            sysUserService.update(wrapper);
            return Result.ok("修改成功");
        } catch (Exception e) {
            return Result.error("个人信息修改失败");
        }
    }

    @GetMapping("getUserByUsername")
    public Result<SysUser> getUserByUsername(@RequestParam(name = "username", required = true) String username) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            SysUser user = sysUserService.getUserByName(username);
            result.setCode(0);
            result.setSuccess(true);
            result.setResult(user);
        } catch (Exception e) {
            result.setCode(-1);
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 下载模板
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @AutoLog(value = "下载模板")
    @ApiOperation(value = "下载模板", notes = "下载模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        ClassPathResource classPathResource = new ClassPathResource("template/userInfo.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }

    @ApiOperation(value = "根据班组id查询用户", notes = "根据班组id查询用户")
    @RequestMapping(value = "/queryUserListByOrgId", method = RequestMethod.GET)
    public Result<List<SysUser>> queryUserListByOrgId(@RequestParam(name = "orgId", required = true) String orgId) {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        List<SysUser> sysUserList = sysUserService.list(new QueryWrapper<SysUser>().eq("org_id", orgId)
                .eq("status",CommonConstant.STATUS_1));
        if (sysUserList.size() > 0 && sysUserList != null) {
            result.setResult(sysUserList);
            result.setSuccess(true);
        } else {
            //result.onnull("未找到对应实体");
            return Result.ok(new ArrayList<>());
        }
        return result;
    }

    @ApiOperation(value = "根据人名模糊查询人员列表", notes = "根据人名模糊查询人员列表")
    @GetMapping(value = "/queryUserListByName")
    public Result<List<SysUser>> queryUserListByName(@RequestParam(name = "name", required = false) String name) {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        List<SysUser> sysUserList = sysUserService.list(new QueryWrapper<SysUser>().like("realname", name).eq("status", 1).eq("del_flag", 0));
        if (sysUserList.size() > 0 && sysUserList != null) {
            result.setResult(sysUserList);
            result.setSuccess(true);
        } else {
            return Result.ok(new ArrayList<>());
        }
        return result;
    }


    @ApiOperation(value = "获取所有班组人员树形结构", notes = "获取所有班组人员树形结构")
    @GetMapping(value = "/queryTreeByTeam")
    public Result<List<UserTreeVO>> queryTreeByTeam() {

        List<UserTreeVO> voList = new ArrayList<>();
        List<SysUser> list = this.sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysUser::getStatus, CommonConstant.STATUS_ENABLE)
                .select(SysUser::getRealname, SysUser::getId, SysUser::getOrgId, SysUser::getOrgName)
        );
        Map<String, List<SysUser>> map = list.stream().collect(Collectors.groupingBy(SysUser::getOrgId));

        for (String orgId : map.keySet()) {
            List<SysUser> sysUsers = map.get(orgId);
            if (CollectionUtils.isNotEmpty(sysUsers)) {
                UserTreeVO userTreeVO = new UserTreeVO();
                userTreeVO.setUserFlag(0);
                userTreeVO.setId(sysUsers.get(0).getOrgId());
                userTreeVO.setName(sysUsers.get(0).getOrgName());
                List<UserChildrenVO> childrenList = new ArrayList<>();
                for (SysUser user : sysUsers) {
                    UserChildrenVO children = new UserChildrenVO();
                    children.setKey(user.getId()).setTitle(user.getRealname()).setUserFlag(1);
                    childrenList.add(children);
                }
                userTreeVO.setChildren(childrenList);
                voList.add(userTreeVO);
            }

        }

        return Result.ok(voList);
    }


    @ApiOperation(value = "根据线路id获取所有人员", notes = "根据线路id获取所有人员")
    @GetMapping(value = "/queryUserListByLineId")
    public Result<List<SysUser>> queryUserListByLineId(HttpServletRequest request,
                                                       @RequestParam("lineId") @NotNull(message = "线路id不能为空") Integer lineId) {
        List<Station> list = stationService.list(new LambdaQueryWrapper<Station>()
                .eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(Station::getLineId, lineId)
                .select(Station::getTeamId)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> collect = list.stream().map(Station::getTeamId).collect(Collectors.toList());
            List<SysUser> userList = this.sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(SysUser::getOrgId, collect)
                    .select(SysUser::getId, SysUser::getRealname, SysUser::getOrgId, SysUser::getOrgName)
            );
            if (CollectionUtils.isNotEmpty(userList)) {
                return Result.ok(userList);
            }
        }

        return Result.ok(new ArrayList<>());
    }


    @ApiOperation(value = "根据站点id获取所有人员", notes = "根据站点id获取所有人员")
    @GetMapping(value = "/queryUserListByStationId")
    public Result<List<SysUser>> queryUserListByStationId(HttpServletRequest request,
                                                          @RequestParam("lineId") @NotNull(message = "站点id不能为空") Integer stationId) {
        List<Station> list = stationService.list(new LambdaQueryWrapper<Station>()
                .eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(Station::getId, stationId)
                .select(Station::getTeamId)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> collect = list.stream().map(Station::getTeamId).collect(Collectors.toList());
            List<SysUser> userList = this.sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(SysUser::getOrgId, collect)
                    .select(SysUser::getId, SysUser::getRealname, SysUser::getOrgId, SysUser::getOrgName)
            );
            if (CollectionUtils.isNotEmpty(userList)) {
                return Result.ok(userList);
            }
        }

        return Result.ok(new ArrayList<>());
    }


    @ApiOperation(value = "根据班组ids获取所有班组人员(包含子集)", notes = "根据班组ids获取所有班组人员(包含子集)")
    @PostMapping(value = "/queryTreeByTeamId")
    public Result<List<SysUser>> queryTreeByTeamId(HttpServletRequest req,
                                                   @RequestBody @Size(min = 1, message = "id数量不能少于1") List<String> departIds) {
        Set<String> departSet = new HashSet<>(departIds);
        while (true) {
            List<SysDepart> departList = this.sysDepartService.list(new LambdaQueryWrapper<SysDepart>()
                    .eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(SysDepart::getParentId, departIds)
                    .select(SysDepart::getId)
            );
            if (CollectionUtils.isNotEmpty(departList)) {
                departIds = departList.stream().map(SysDepart::getId).collect(Collectors.toList());
                departSet.addAll(departIds);
            } else {
                break;
            }
        }
        List<SysUser> list = this.sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysUser::getStatus, CommonConstant.STATUS_ENABLE)
                .in(SysUser::getOrgId, departSet)
                .select(SysUser::getId, SysUser::getRealname, SysUser::getOrgId, SysUser::getOrgName));
        return Result.ok(list);
    }

}
