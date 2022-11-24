package com.aiurt.boot.standard.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.standard.dto.InspectionStandardDto;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.dto.PatrolStandardItemsExport;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolStandardServiceImpl extends ServiceImpl<PatrolStandardMapper, PatrolStandard> implements IPatrolStandardService {
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Autowired
    private PatrolStandardItemsMapper patrolStandardItemsMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Override
    public IPage<PatrolStandardDto> pageList(Page page, PatrolStandard patrolStandard) {
        List<PatrolStandardDto> page1 = patrolStandardMapper.pageList(page, patrolStandard);
        // 以下包含的代码权限拦截局部过滤
        boolean filter = GlobalThreadLocal.setDataFilter(false);
        page1.forEach(a -> {
            a.setNumber(baseMapper.number(a.getCode()));
        });
        // 以上包含的代码权限拦截局部过滤
        GlobalThreadLocal.setDataFilter(filter);
        return page.setRecords(page1);
    }

    @Override
    public IPage<PatrolStandardDto> pageLists(Page page, PatrolStandardDto patrolStandard) {
        List<PatrolStandardDto> page1 = patrolStandardMapper.pageLists(page, patrolStandard, patrolStandard.getStations());
        return page.setRecords(page1);
    }

    @Override
    public List<InspectionStandardDto> lists(String professionCode, String subsystemCode) {
        List<InspectionStandardDto> list = patrolStandardMapper.list(professionCode, subsystemCode);
        return list;
    }

    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, PatrolStandard patrolStandard) {
        List<PatrolStandard> patrolStandardList = patrolStandardMapper.getList(patrolStandard);
        for (PatrolStandard standard : patrolStandardList) {
            JSONObject csMajor = iSysBaseAPI.getCsMajorByCode(standard.getProfessionCode());
            List<DictModel> deviceType = iSysBaseAPI.getDictItems("patrol_device_type");
            deviceType = deviceType.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getDeviceType()))).collect(Collectors.toList());
            String deviceTypeName = deviceType.stream().map(DictModel::getText).collect(Collectors.joining());
            List<DictModel> status = iSysBaseAPI.getDictItems("patrol_standard_status");
            status = status.stream().filter(e -> e.getValue().equals(String.valueOf(standard.getStatus()))).collect(Collectors.toList());
            String statusName = status.stream().map(DictModel::getText).collect(Collectors.joining());
            standard.setStatusName(statusName);
            standard.setDeviceTypeNames(deviceTypeName);
            standard.setProfessionCode(csMajor.getString("majorName"));
            List<PatrolStandardItems> patrolStandardItemsList = patrolStandardItemsMapper.selectList(new LambdaQueryWrapper<PatrolStandardItems>().
                    eq(PatrolStandardItems::getStandardId, standard.getId()).eq(PatrolStandardItems::getDelFlag, CommonConstant.DEL_FLAG_0).
                    eq(PatrolStandardItems::getHierarchyType,CommonConstant.DEL_FLAG_0));
            standard.setPatrolStandardItemsList(patrolStandardItemsList);
            for (PatrolStandardItems patrolStandardItems : patrolStandardItemsList) {
                 PatrolStandardItems translate = translate(patrolStandardItems);
                BeanUtils.copyProperties(translate,patrolStandardItems);
                List<PatrolStandardItems> itemsList = patrolStandardItemsMapper.selectList(new LambdaQueryWrapper<PatrolStandardItems>().eq(PatrolStandardItems::getParentId, patrolStandardItems.getId()));
                if (CollUtil.isNotEmpty(itemsList)) {
                    List<PatrolStandardItemsExport> exportList = itemsList.stream().map(
                            todo -> {
                                PatrolStandardItems standardItems = translate(todo);
                                PatrolStandardItemsExport to = new PatrolStandardItemsExport();
                                BeanUtils.copyProperties(standardItems, to);
                                return to;
                            }
                    ).collect(Collectors.toList());
                    patrolStandardItems.setItemsExportList(exportList);
                }


            }
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "巡检表数据";
        ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), ExcelType.XSSF);
        //调用ExcelExportUtil.exportExcel方法生成workbook
        Workbook wb = ExcelExportUtil.exportExcel(exportParams, PatrolStandard.class, patrolStandardList);
        String fileName = "巡检表数据";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            //xlsx格式设置
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            wb.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public PatrolStandardItems translate(PatrolStandardItems items) {
        List<DictModel> hierarchyType = iSysBaseAPI.getDictItems("patrol_hierarchy_type");
        hierarchyType = hierarchyType.stream().filter(e -> e.getValue().equals(String.valueOf(items.getHierarchyType()))).collect(Collectors.toList());
        String hierarchyTypeName = hierarchyType.stream().map(DictModel::getText).collect(Collectors.joining());
        List<DictModel> patrolCheck = iSysBaseAPI.getDictItems("patrol_check");
        patrolCheck = patrolCheck.stream().filter(e -> e.getValue().equals(String.valueOf(items.getCheck()))).collect(Collectors.toList());
        String patrolCheckName = patrolCheck.stream().map(DictModel::getText).collect(Collectors.joining());
        items.setHierarchyTypeName(hierarchyTypeName);
        items.setCheckName(patrolCheckName);
        return items;
    }
    }
