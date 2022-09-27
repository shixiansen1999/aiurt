package com.aiurt.modules.situation.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.situation.entity.SysAnnouncement;
import com.aiurt.modules.situation.mapper.SysInfoListMapper;
import com.aiurt.modules.situation.service.SysInfoListService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: bd_info_list
 * @Author: jeecg-boot
 * @Date: 2021-04-19
 * @Version: V1.0
 */
@Service
public class SysInfoListServiceImpl extends ServiceImpl<SysInfoListMapper, SysAnnouncement> implements SysInfoListService {

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Override
    public ModelAndView reportExport(HttpServletRequest request, SysAnnouncement sysAnnouncement, Class<SysAnnouncement> clazz, String title) {

        // Step.1 组装查询条件
        QueryWrapper<SysAnnouncement> queryWrapper = QueryGenerator.initQueryWrapper(sysAnnouncement, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<SysAnnouncement> pageList = this.list(queryWrapper);
        List<SysAnnouncement> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(getId(item))).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }
        exportList.forEach(s->{
            if (ObjectUtil.isNotNull(s.getSender())) {
                LoginUser userByName = iSysBaseAPI.getUserByName(s.getSender());
                if (ObjectUtil.isNotNull(userByName)) {
                    s.setSender(userByName.getRealname());
                    getUserNames(s);
                }
            }
        });
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
        ExportParams exportParams=new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title);
        exportParams.setImageBasePath(upLoadPath);
        //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
        mv.addObject(NormalExcelConstants.PARAMS,exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    private String getId(SysAnnouncement item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getUserNames(@RequestBody SysAnnouncement sysAnnouncement) {
        if (StrUtil.isNotBlank(sysAnnouncement.getUserIds())) {
            String[] split = sysAnnouncement.getUserIds().split(",");
            if (split.length > 0) {
                StringBuilder str = new StringBuilder();
                for (String s : split) {
                    if (!Objects.isNull(s)) {
                        LoginUser userById = iSysBaseAPI.getUserByName(s);
                        if (!ObjectUtils.isEmpty(userById)) {
                            str.append(userById.getRealname()).append(",");
                        }
                    }
                }
                if (StrUtil.isNotBlank(str)) {
                    sysAnnouncement.setUserNames(str.deleteCharAt(str.length() - 1).toString());
                }
            }
        }
    }
}
