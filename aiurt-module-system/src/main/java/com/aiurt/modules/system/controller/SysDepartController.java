package com.aiurt.modules.system.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CacheConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.util.JwtUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.model.DepartIdModel;
import com.aiurt.modules.system.model.SysDepartTreeModel;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.ISysUserDepartService;
import com.aiurt.modules.system.service.ISysUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 部门表 前端控制器
 * <p>
 *
 * @Author: Steve @Since： 2019-01-22
 */
@Api(tags = "部门表")
@RestController
@RequestMapping("/sys/sysDepart")
@Slf4j
public class SysDepartController {

    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysUserDepartService sysUserDepartService;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    /**
     * 查询数据 查出我的部门,并以树结构数据格式响应给前端
     *
     * @return
     */
    @ApiOperation(value = "部门管理-查询我的部门", notes = "部门管理-查询我的部门")
    @RequestMapping(value = "/queryMyDeptTreeList", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> queryMyDeptTreeList() {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        try {
            if (oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2)) {
                //update-begin--Author:liusq  Date:20210624  for:部门查询ids为空后的前端显示问题 issues/I3UD06
                String departIds = user.getDepartIds();
                if (StringUtils.isNotBlank(departIds)) {
                    List<SysDepartTreeModel> list = sysDepartService.queryMyDeptTreeList(departIds);
                    result.setResult(list);
                }
                //update-end--Author:liusq  Date:20210624  for:部门查询ids为空后的前端显示问题 issues/I3UD06
                result.setMessage(CommonConstant.USER_IDENTITY_2.toString());
                result.setSuccess(true);
            } else {
                result.setMessage(CommonConstant.USER_IDENTITY_1.toString());
                result.setSuccess(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 查询数据 查出所有部门,并以树结构数据格式响应给前端
     *
     * @return
     */
    @ApiOperation(value = "部门管理-查询所有部门", notes = "部门管理-查询所有部门")
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    @PermissionData(pageComponent = "system/DepartList")
    public Result<List<SysDepartTreeModel>> queryTreeList(@RequestParam(name = "ids", required = false) String ids, @RequestParam(name = "sign", required = false) String sign, @RequestParam(name = "name", required = false) String name) {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            // 从内存中读取
            if (oConvertUtils.isNotEmpty(ids)) {
                List<SysDepartTreeModel> departList = sysDepartService.queryTreeList(ids);
                result.setResult(departList);
            } else {
                boolean flag=false;
                List<SysDepartTreeModel> list = sysDepartService.queryTreeList(flag);
                result.setResult(list);
            }
            if (StrUtil.isNotBlank(sign)) {
                List<SysDepartTreeModel> list1 = sysDepartService.querySignTreeList(sign);
                result.setResult(list1);
            }
            //做树形搜索处理
            if (StrUtil.isNotBlank(name) && CollUtil.isNotEmpty(result.getResult())) {
                sysDepartService.processingTreeList(name, result.getResult());
            }
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 查询数据 查出所有部门,并以树结构数据格式响应给前端
     *
     * @return
     */
    @ApiOperation(value = "部门管理-查询账号部门", notes = "部门管理-查询账号部门")
    @RequestMapping(value = "/queryDepartList", method = RequestMethod.GET)
    @PermissionData(pageComponent = "system/DepartList")
    public Result<List<SysDepartTreeModel>> queryTreeList(@RequestParam(name = "name", required = false) String name) {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            boolean flag=true;
            List<SysDepartTreeModel> list = sysDepartService.queryTreeList(flag);
            result.setResult(list);
            //做树形搜索处理
            if (StrUtil.isNotBlank(name) && CollUtil.isNotEmpty(result.getResult())) {
                sysDepartService.processingTreeList(name, result.getResult());
            }
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 异步查询部门list
     *
     * @param parentId 父节点 异步加载时传递
     * @param ids      前端回显是传递
     * @return
     */
    @RequestMapping(value = "/queryDepartTreeSync", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> queryDepartTreeSync(@RequestParam(name = "pid", required = false) String parentId, @RequestParam(name = "ids", required = false) String ids) {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            List<SysDepartTreeModel> list = sysDepartService.queryTreeListByPid(parentId, ids);
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 获取某个部门的所有父级部门的ID
     *
     * @param departId 根据departId查
     * @param orgCode  根据orgCode查，departId和orgCode必须有一个不为空
     */
    @GetMapping("/queryAllParentId")
    public Result queryParentIds(
            @RequestParam(name = "departId", required = false) String departId,
            @RequestParam(name = "orgCode", required = false) String orgCode
    ) {
        try {
            JSONObject data;
            if (oConvertUtils.isNotEmpty(departId)) {
                data = sysDepartService.queryAllParentIdByDepartId(departId);
            } else if (oConvertUtils.isNotEmpty(orgCode)) {
                data = sysDepartService.queryAllParentIdByOrgCode(orgCode);
            } else {
                return Result.error("departId 和 orgCode 不能都为空！");
            }
            return Result.OK(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加新数据 添加用户新建的部门对象数据,并保存到数据库
     *
     * @param sysDepart
     * @return
     */
    @ApiOperation(value = "部门管理-添加部门", notes = "部门管理-添加部门")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> add(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
        Result<SysDepart> result = new Result<>();
        String username = JwtUtil.getUserNameByToken(request);
        try {
            sysDepart.setCreateBy(username);
            sysDepartService.saveDepartData(sysDepart, username);
            //清除部门树内存
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑数据 编辑部门的部分数据,并保存到数据库
     *
     * @param sysDepart
     * @return
     */
    @ApiOperation(value = "部门管理-编辑部门", notes = "部门管理-编辑部门")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> edit(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
        String username = JwtUtil.getUserNameByToken(request);
        sysDepart.setUpdateBy(username);
        Result<SysDepart> result = new Result<SysDepart>();
        SysDepart sysDepartEntity = sysDepartService.getById(sysDepart.getId());
        if (sysDepartEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDepartService.updateDepartDataById(sysDepart, username);
            // TODO 返回false说明什么？
            if (ok) {
                //清除部门树内存
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
    @AutoLog(value = "部门管理-通过id删除", operateType = 4, operateTypeAlias = "通过id删除", permissionUrl = "/isystem/depart")
    @ApiOperation(value = "部门管理-通过id删除", notes = "部门管理-通过id删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> delete(@RequestParam(name = "id", required = true) String id) {

        Result<SysDepart> result = new Result<SysDepart>();
        SysDepart sysDepart = sysDepartService.getById(id);
        if (sysDepart == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDepartService.delete(id);
            if (ok) {
                //清除部门树内存
                result.success("删除成功!");
            }
        }
        return result;
    }


    /**
     * 批量删除 根据前端请求的多个ID,对数据库执行删除相关部门数据的操作
     *
     * @param ids
     * @return
     */
	@AutoLog(value = "部门管理-批量删除", operateType = 4, operateTypeAlias = "批量删除", permissionUrl = "/isystem/depart")
	@ApiOperation(value = "部门管理-批量删除", notes = "部门管理-批量删除")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

        Result<SysDepart> result = new Result<SysDepart>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysDepartService.deleteBatchWithChildren(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 查询数据 添加或编辑页面对该方法发起请求,以树结构形式加载所有部门的名称,方便用户的操作
     *
     * @return
     */
    @ApiOperation(value = "部门管理-以树结构形式加载所有部门", notes = "部门管理-以树结构形式加载所有部门")
    @RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> queryIdTree() {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> list = sysDepartService.queryDepartIdTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * <p>
     * 部门搜索功能方法,根据关键字模糊搜索相关部门
     * </p>
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/searchBy", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord", required = true) String keyWord, @RequestParam(name = "myDeptSearch", required = false) String myDeptSearch) {
        Result<List<SysDepartTreeModel>> result = new Result<List<SysDepartTreeModel>>();
        //部门查询，myDeptSearch为1时为我的部门查询，登录用户为上级时查只查负责部门下数据
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String departIds = null;
        if (oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2)) {
            departIds = user.getDepartIds();
        }
        List<SysDepartTreeModel> treeList = this.sysDepartService.searchByKeyWord(keyWord, myDeptSearch, departIds);
        if (treeList == null || treeList.size() == 0) {
            result.setSuccess(false);
            result.setMessage("未查询匹配数据！");
            return result;
        }
        result.setResult(treeList);
        return result;
    }


    /**
     * 导出excel
     *
     * @param request
     */
    @AutoLog(value = "部门管理-导出")
    @ApiOperation(value = "部门管理-导出", notes = "部门管理-导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysDepart sysDepart, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysDepart> queryWrapper = QueryGenerator.initQueryWrapper(sysDepart, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysDepart> pageList = sysDepartService.list(queryWrapper);
        //按字典排序
        Collections.sort(pageList, new Comparator<SysDepart>() {
            @Override
            public int compare(SysDepart arg0, SysDepart arg1) {
                return arg0.getOrgCode().compareTo(arg1.getOrgCode());
            }
        });
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "部门列表");
        mv.addObject(NormalExcelConstants.CLASS, SysDepart.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("部门列表数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }


    @AutoLog(value = "部门管理-下载部门导入模板")
    @ApiOperation(value = "部门管理-下载部门导入模板", notes = "部门管理-下载部门导入模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        sysDepartService.departmentEXls(response);
    }

    /**
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "部门管理-导入")
    @ApiOperation(value = "部门管理-导入", notes = "部门管理-导入")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return sysDepartService.importExcel(request, response);
    }


    /**
     * 查询所有部门信息
     *
     * @return
     */
    @GetMapping("listAll")
    public Result<List<SysDepart>> listAll(@RequestParam(name = "id", required = false) String id) {
        Result<List<SysDepart>> result = new Result<>();
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        query.orderByAsc(SysDepart::getOrgCode);
        if (oConvertUtils.isNotEmpty(id)) {
            String[] arr = id.split(",");
            query.in(SysDepart::getId, arr);
        }
        List<SysDepart> ls = this.sysDepartService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 查询数据 查出所有部门,并以树结构数据格式响应给前端
     *
     * @return
     */
    @RequestMapping(value = "/queryTreeByKeyWord", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeByKeyWord(@RequestParam(name = "keyWord", required = false) String keyWord) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            Map<String, Object> map = new HashMap(5);
            List<SysDepartTreeModel> list = sysDepartService.queryTreeByKeyWord(keyWord);
            //根据keyWord获取用户信息
            LambdaQueryWrapper<SysUser> queryUser = new LambdaQueryWrapper<SysUser>();
            queryUser.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0);
            queryUser.and(i -> i.like(SysUser::getUsername, keyWord).or().like(SysUser::getRealname, keyWord));
            List<SysUser> sysUsers = this.sysUserService.list(queryUser);
            map.put("userList", sysUsers);
            map.put("departList", list);
            result.setResult(map);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 根据部门编码获取部门信息
     *
     * @param orgCode
     * @return
     */
    @GetMapping("/getDepartName")
    public Result<SysDepart> getDepartName(@RequestParam(name = "orgCode") String orgCode) {
        Result<SysDepart> result = new Result<>();
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<>();
        query.eq(SysDepart::getOrgCode, orgCode);
        SysDepart sysDepart = sysDepartService.getOne(query);
        result.setSuccess(true);
        result.setResult(sysDepart);
        return result;
    }

    /**
     * 根据部门id获取用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getUsersByDepartId")
    public Result<List<SysUser>> getUsersByDepartId(@RequestParam(name = "id") String id) {
        Result<List<SysUser>> result = new Result<>();
        List<SysUser> sysUsers = sysUserDepartService.queryUserByDepId(id);
        result.setSuccess(true);
        result.setResult(sysUsers);
        return result;
    }
}
