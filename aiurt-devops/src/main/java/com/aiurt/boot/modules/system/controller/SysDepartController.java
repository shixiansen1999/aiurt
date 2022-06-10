package com.aiurt.boot.modules.system.controller;

import com.aiurt.boot.common.constant.CacheConstant;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.util.JwtUtil;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.schedule.service.IScheduleRecordService;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.model.DepartIdModel;
import com.aiurt.boot.modules.system.model.SysDepartTreeModel;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 部门表 前端控制器
 * <p>
 *
 * @Author: Steve @Since： 2019-01-22
 */
@RestController
@RequestMapping("/sys/sysDepart")
@Slf4j
public class SysDepartController {

    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private IStationService stationService;
    @Autowired
    ISysUserService sysUserService;
    @Autowired
    IScheduleRecordService scheduleRecordService;

    /**
     * 获取所有部门列表
     * @return
     */
    @ApiOperation(value = "获取所有部门列表")
    @GetMapping(value = "/rootDepartList")
    public Result<List<SysDepart>> queryRootDepar() {
        Result<List<SysDepart>> result = new Result<>();
        try {
            List<SysDepart> list = sysDepartService.queryRootDepar();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * @return
     */
    @GetMapping(value = "/departSelect")
    public Result<List<SysDepart>> departSelect() {
        Result<List<SysDepart>> result = new Result<>();
        try {
            List<SysDepart> list = sysDepartService.list(new QueryWrapper<SysDepart>()
                    .eq("del_flag", CommonConstant.DEL_FLAG_0)
                    .select("id", "depart_name")
                    .orderByAsc("depart_order"));
            result.setResult(list);
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
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> queryTreeList() {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            // 从内存中读取
//			List<SysDepartTreeModel> list =FindsDepartsChildrenUtil.getSysDepartTreeList();
//			if (CollectionUtils.isEmpty(list)) {
//				list = sysDepartService.queryTreeList();
//			}
            List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 添加新数据 添加用户新建的部门对象数据,并保存到数据库
     *
     * @param sysDepart
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> add(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
        Result<SysDepart> result = new Result<SysDepart>();
        String username = JwtUtil.getUserNameByToken(request);
        try {
            sysDepart.setCreateBy(username);
            sysDepartService.saveDepartData(sysDepart, username);
            //清除部门树内存
            // FindsDepartsChildrenUtil.clearSysDepartTreeList();
            // FindsDepartsChildrenUtil.clearDepartIdModel();
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
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> edit(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
        String username = JwtUtil.getUserNameByToken(request);
        sysDepart.setUpdateBy(username);
        Result<SysDepart> result = new Result<SysDepart>();
        SysDepart sysDepartEntity = sysDepartService.getById(sysDepart.getId());
        if (sysDepartEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = sysDepartService.updateDepartDataById(sysDepart, username);
            if (ok) {
                //清除部门树内存
                //FindsDepartsChildrenUtil.clearSysDepartTreeList();
                //FindsDepartsChildrenUtil.clearDepartIdModel();
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
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> delete(@RequestParam(name = "id", required = true) String id) {

        Result<SysDepart> result = new Result<SysDepart>();
        SysDepart sysDepart = sysDepartService.getById(id);
        if (sysDepart == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = sysDepartService.delete(id);
            if (ok) {
                stationService.updateStationDeaprt(id);
                //清除部门树内存
                //FindsDepartsChildrenUtil.clearSysDepartTreeList();
                // FindsDepartsChildrenUtil.clearDepartIdModel();
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
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<SysDepart> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

        Result<SysDepart> result = new Result<SysDepart>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            String departName = this.sysDepartService.deleteBatchWithChildren(Arrays.asList(ids.split(",")));
            if (departName == null) {
                result.success("删除成功!");
            } else {
                result.error500(departName + "部门下有员工,不能删除!");
            }
        }
        return result;
    }

    /**
     * 查询数据 添加或编辑页面对该方法发起请求,以树结构形式加载所有部门的名称,方便用户的操作
     *
     * @return
     */
    @RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> queryIdTree() {
//		Result<List<DepartIdModel>> result = new Result<List<DepartIdModel>>();
//		List<DepartIdModel> idList;
//		try {
//			idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//			if (idList != null && idList.size() > 0) {
//				result.setResult(idList);
//				result.setSuccess(true);
//			} else {
//				sysDepartService.queryTreeList();
//				idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//				result.setResult(idList);
//				result.setSuccess(true);
//			}
//			return result;
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//			result.setSuccess(false);
//			return result;
//		}
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
    public Result<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord", required = true) String keyWord) {
        Result<List<SysDepartTreeModel>> result = new Result<List<SysDepartTreeModel>>();
        try {
            List<SysDepartTreeModel> treeList = this.sysDepartService.searhBy(keyWord);
            if (treeList.size() == 0 || treeList == null) {
                throw new Exception();
            }
            result.setSuccess(true);
            result.setResult(treeList);
            return result;
        } catch (Exception e) {
            e.fillInStackTrace();
            result.setSuccess(false);
            result.setMessage("查询失败或没有您想要的任何数据!");
            return result;
        }
    }


    /**
     * 导出excel
     *
     * @param request
     * @param
     */
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

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
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
                // orgCode编码长度
                int codeLength = 3;
                List<SysDepart> listSysDeparts = ExcelImportUtil.importExcel(file.getInputStream(), SysDepart.class, params);
                //按长度排序
                Collections.sort(listSysDeparts, new Comparator<SysDepart>() {
                    @Override
                    public int compare(SysDepart arg0, SysDepart arg1) {
                        return arg0.getOrgCode().length() - arg1.getOrgCode().length();
                    }
                });
                for (SysDepart sysDepart : listSysDeparts) {
                    String orgCode = sysDepart.getOrgCode();
                    if (orgCode.length() > codeLength) {
                        String parentCode = orgCode.substring(0, orgCode.length() - codeLength);
                        QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<SysDepart>();
                        queryWrapper.eq("org_code", parentCode);
                        try {
                            SysDepart parentDept = sysDepartService.getOne(queryWrapper);
                            if (!parentDept.equals(null)) {
                                sysDepart.setParentId(parentDept.getId());
                            } else {
                                sysDepart.setParentId("");
                            }
                        } catch (Exception e) {
                            //没有查找到parentDept
                        }
                    } else {
                        sysDepart.setParentId("");
                    }
                    sysDepartService.save(sysDepart);
                }
                return Result.ok("文件导入成功！数据行数：" + listSysDeparts.size());
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
        return Result.error("文件导入失败！");
    }

    @Resource
    private RoleAdditionalUtils roleAdditionalUtils;

    /**
     * 查询数据 查出加权限下的所有部门,并以列表结构数据格式响应给前端
     *
     * @return
     */
    @GetMapping(value = "/queryListByDataRole")
    public Result<List<SysDepart>> queryListByDataRole() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> departIdsByUserId = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());

        List<SysDepart> list = sysDepartService.lambdaQuery()
                .eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(CollectionUtils.isNotEmpty(departIdsByUserId),SysDepart::getId, departIdsByUserId)
                .list();

        return Result.ok(list);
    }

    @ApiOperation(value = "根据班组查询站点信息")
    @GetMapping("/queryStationListByOrgId")
    public Result<List<Station>> queryStationListByOrgId(@RequestParam("orgId")@NotNull(message = "班组id不能为空") String orgId){
        List<Station> list = stationService.lambdaQuery().eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).eq(Station::getTeamId, orgId).list();
        return Result.ok(list);
    }

}
