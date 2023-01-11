package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyPlanStatusDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearAddDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalYearMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalYearService;
import com.aiurt.boot.rehearsal.service.strategy.AuditContext;
import com.aiurt.boot.rehearsal.service.strategy.NodeAudit;
import com.aiurt.boot.rehearsal.service.strategy.NodeFactory;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.export.styler.ExcelExportStylerDefaultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
@Slf4j
public class EmergencyRehearsalYearServiceImpl extends ServiceImpl<EmergencyRehearsalYearMapper, EmergencyRehearsalYear> implements IEmergencyRehearsalYearService, IFlowableBaseUpdateStatusService {

    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private IEmergencyRehearsalMonthService emergencyRehearsalMonthService;
    @Autowired
    private EmergencyRehearsalYearMapper emergencyRehearsalYearMapper;

    @Override
    public IPage<EmergencyRehearsalYear> queryPageList(Page<EmergencyRehearsalYear> page, EmergencyRehearsalYearDTO emergencyRehearsalYearDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        Page<EmergencyRehearsalYear> pageList = emergencyRehearsalYearMapper.queryPageList(page, emergencyRehearsalYearDTO, new EmergencyPlanStatusDTO(), loginUser.getUsername());
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        EmergencyRehearsalYear rehearsalYear = new EmergencyRehearsalYear();
        BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, rehearsalYear);
        // 构造年计划编号
        String code = "NDYJ" + DateUtil.format(new Date(), "yyyyMMdd-");
        EmergencyRehearsalYear emergencyRehearsalYear = this.lambdaQuery().like(EmergencyRehearsalYear::getCode, code)
                .orderByDesc(EmergencyRehearsalYear::getCode)
                .last("limit 1")
                .one();
        if (ObjectUtil.isEmpty(emergencyRehearsalYear)) {
            code += String.format("%02d", 1);
        } else {
            String yearCode = emergencyRehearsalYear.getCode();
            Integer serialNo = Integer.valueOf(yearCode.substring(yearCode.lastIndexOf("-") + 1));
            if (serialNo >= 99) {
                code += (serialNo + 1);
            } else {
                code += String.format("%02d", (serialNo + 1));
            }
        }
        rehearsalYear.setCode(code);
        this.save(rehearsalYear);

        String id = rehearsalYear.getId();
        List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
        if (CollectionUtil.isNotEmpty(monthList)) {
            for (EmergencyRehearsalMonth month : monthList) {
                String monthCode = emergencyRehearsalMonthService.getMonthCode();
                month.setPlanId(id);
                month.setCode(monthCode);
                month.setYearWithin(EmergencyConstant.WITHIN_1);
                emergencyRehearsalMonthService.save(month);
            }
        }
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        EmergencyRehearsalYear rehearsalYear = this.getById(id);
        Assert.notNull(rehearsalYear, "未找到对应数据！");
        // 非待提审状态不允许删除
        if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
            throw new AiurtBootException("已提审的计划不允许删除！");
        }
        this.removeById(id);

        QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
        emergencyRehearsalMonthService.remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String edit(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        String id = emergencyRehearsalYearAddDTO.getId();
        Assert.notNull(id, "记录ID为空！");
        EmergencyRehearsalYear rehearsalYear = this.getById(id);
        Assert.notNull(rehearsalYear, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
            throw new AiurtBootException("已提审的计划不允许编辑！");
        }
        EmergencyRehearsalYear emergencyRehearsalYear = new EmergencyRehearsalYear();
        BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, emergencyRehearsalYear);
        this.updateById(emergencyRehearsalYear);

        QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
        emergencyRehearsalMonthService.remove(wrapper);
        List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
        if (CollectionUtil.isNotEmpty(monthList)) {
            for (EmergencyRehearsalMonth month : monthList) {
                String monthCode = emergencyRehearsalMonthService.getMonthCode();
                month.setPlanId(id);
                month.setCode(monthCode);
                month.setYearWithin(EmergencyConstant.WITHIN_1);
                emergencyRehearsalMonthService.save(month);
            }
        }
        return id;
    }

    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, String ids) {
        List<EmergencyRehearsalYear> rehearsalYears;
        if (StrUtil.isEmpty(ids)) {
            rehearsalYears = this.lambdaQuery()
                    .eq(EmergencyRehearsalYear::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .list();
        } else {
            List<String> split = StrUtil.split(ids, ',');
            rehearsalYears = this.lambdaQuery()
                    .eq(EmergencyRehearsalYear::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(EmergencyRehearsalYear::getId, split)
                    .list();
        }
        if (CollectionUtil.isEmpty(rehearsalYears)) {
            throw new AiurtBootException("没有可以导出的数据！");
        }

        List<Workbook> workbooks = new LinkedList<>();
        List<String> titles = new LinkedList<>();
        for (EmergencyRehearsalYear rehearsalYear : rehearsalYears) {
            List<EmergencyRehearsalMonth> rehearsalMonthList = emergencyRehearsalMonthService.lambdaQuery()
                    .eq(EmergencyRehearsalMonth::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(EmergencyRehearsalMonth::getPlanId, rehearsalYear.getId())
                    .list();
            SysDepartModel dept = iSysBaseApi.getDepartByOrgCode(rehearsalYear.getOrgCode());
            String title = ObjectUtil.isEmpty(dept) ? "" : dept.getDepartName() + rehearsalYear.getYear() + "年综合应急演练计划";
            // excel数据
            ExportParams exportParams = new ExportParams(title, null);
            // 添加索引
            exportParams.setAddIndex(true);
            // 设置自定义样式
            exportParams.setStyle(CustomExcelExportStylerImpl.class);
            Workbook exportExcel = ExcelExportUtil.exportExcel(exportParams, EmergencyRehearsalMonth.class, rehearsalMonthList);
            workbooks.add(exportExcel);
            titles.add(title);
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = null;
            InputStream inputStream = null;
            // 创建临时文件
            File zipTempFile = File.createTempFile("应急演练计划", ".zip");
            FileOutputStream fileOutputStream = new FileOutputStream(zipTempFile);
            CheckedOutputStream checkedOutputStream = new CheckedOutputStream(fileOutputStream, new Adler32());
            // 压缩成zip格式
            ZipOutputStream zipOutputStream = new ZipOutputStream(checkedOutputStream);

            for (int i = 0; i < workbooks.size(); i++) {
                Workbook workbook = workbooks.get(i);
                byteArrayOutputStream = new ByteArrayOutputStream();
                // 将excel写入字节数组输出流
                workbook.write(byteArrayOutputStream);
                // 转化为字节数据
                byte[] content = byteArrayOutputStream.toByteArray();
                // 写入输入流
                inputStream = new ByteArrayInputStream(content);
                // 添加Excel表数据
                String filename = titles.get(i) + "_" + System.nanoTime() + ".xls";
                zipOutputStream.putNextEntry(new ZipEntry(filename));
                int flag = 0;
                while ((flag = inputStream.read()) != -1) {
                    zipOutputStream.write(flag);
                }
            }
            zipOutputStream.close();
            inputStream.close();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            log.error("年演练计划导出异常！", e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 保存或者编辑年演练计划信息
     *
     * @param emergencyRehearsalYearAddDTO
     * @return
     */
    public String startProcess(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        String id = emergencyRehearsalYearAddDTO.getId();
        if (StrUtil.isEmpty(id)) {
            EmergencyRehearsalYear rehearsalYear = new EmergencyRehearsalYear();
            BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, rehearsalYear);
            // 构造年计划编号
            String code = "NDYJ" + DateUtil.format(new Date(), "yyyyMMdd-");
            EmergencyRehearsalYear emergencyRehearsalYear = this.lambdaQuery().like(EmergencyRehearsalYear::getCode, code)
                    .orderByDesc(EmergencyRehearsalYear::getCode)
                    .last("limit 1")
                    .one();
            if (ObjectUtil.isEmpty(emergencyRehearsalYear)) {
                code += String.format("%02d", 1);
            } else {
                String yearCode = emergencyRehearsalYear.getCode();
                Integer serialNo = Integer.valueOf(yearCode.substring(yearCode.lastIndexOf("-") + 1));
                if (serialNo >= 99) {
                    code += (serialNo + 1);
                } else {
                    code += String.format("%02d", (serialNo + 1));
                }
            }
            rehearsalYear.setCode(code);
            this.save(rehearsalYear);

            String planId = rehearsalYear.getId();
            List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
            if (CollectionUtil.isNotEmpty(monthList)) {
                for (EmergencyRehearsalMonth month : monthList) {
                    String monthCode = emergencyRehearsalMonthService.getMonthCode();
                    month.setPlanId(planId);
                    month.setCode(monthCode);
                    month.setYearWithin(EmergencyConstant.WITHIN_1);
                    emergencyRehearsalMonthService.save(month);
                }
            }
            return planId;
        } else {
            EmergencyRehearsalYear rehearsalYear = this.getById(id);
            Assert.notNull(rehearsalYear, "未找到对应数据！");
            // 代提审才允许编辑
            if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
                throw new AiurtBootException("已提审的计划不允许编辑！");
            }
            EmergencyRehearsalYear emergencyRehearsalYear = new EmergencyRehearsalYear();
            BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, emergencyRehearsalYear);
            this.updateById(emergencyRehearsalYear);

            QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
            emergencyRehearsalMonthService.remove(wrapper);
            List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
            if (CollectionUtil.isNotEmpty(monthList)) {
                for (EmergencyRehearsalMonth month : monthList) {
                    String monthCode = emergencyRehearsalMonthService.getMonthCode();
                    month.setPlanId(id);
                    month.setCode(monthCode);
                    month.setYearWithin(EmergencyConstant.WITHIN_1);
                    emergencyRehearsalMonthService.save(month);
                }
            }
            return id;
        }
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        EmergencyRehearsalYear rehearsalYear = this.getById(businessKey);
        if (ObjectUtil.isEmpty(rehearsalYear)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        int states = updateStateEntity.getStates();
//        switch (states) {
//            case 2:
//                // 演练计划负责人审批
//                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_2);
//                break;
//            case 3:
//                // 演练计划负责人驳回，更新状态为待提交状态
//                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_1);
//                break;
//            case 4:
//                // 已通过
//                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_3);
//                break;
//        }
//        this.updateById(rehearsalYear);
        AuditContext context = new AuditContext(NodeFactory.getNode(states));
        EmergencyRehearsalYear emergencyRehearsalYear = context.doAudit(rehearsalYear);
        this.updateById(emergencyRehearsalYear);

    }

}
