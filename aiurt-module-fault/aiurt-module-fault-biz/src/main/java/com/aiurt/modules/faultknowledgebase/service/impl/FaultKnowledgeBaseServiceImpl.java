package com.aiurt.modules.faultknowledgebase.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Service
public class FaultKnowledgeBaseServiceImpl extends ServiceImpl<FaultKnowledgeBaseMapper, FaultKnowledgeBase> implements IFaultKnowledgeBaseService {

    @Autowired
    private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
    @Autowired
    private FaultAnalysisReportMapper faultAnalysisReportMapper;

    @Override
    public IPage<FaultKnowledgeBase> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        LambdaQueryWrapper<FaultKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();
        List<FaultKnowledgeBase> bases = faultKnowledgeBaseMapper.selectList(queryWrapper.eq(FaultKnowledgeBase::getDelFlag, "0"));
        List<String> ids = bases.stream().map(FaultKnowledgeBase::getId).distinct().collect(Collectors.toList());
        List<String> rolesByUsername = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        //根据用户角色是否显示未通过的知识库
        if (!rolesByUsername.contains(FaultConstant.ADMIN)&&!rolesByUsername.contains(FaultConstant.MAINTENANCE_WORKER)&&!rolesByUsername.contains(FaultConstant.PROFESSIONAL_TECHNICAL_DIRECTOR)) {
            faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
        }
        //工班长只能看到审核通过的和自己创建的未审核通过的
        if (rolesByUsername.size()==1 && rolesByUsername.contains(FaultConstant.MAINTENANCE_WORKER)) {
            faultKnowledgeBase.setCreateBy(sysUser.getUsername());
        }
        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        String id = faultKnowledgeBase.getId();
        //根据id条件查询时，jeecg前端会传一个id结尾带逗号的id，所以先去掉结尾id
        if (StringUtils.isNotBlank(id)) {
            String substring = id.substring(0, id.length() - 1);
            faultKnowledgeBase.setId(substring);
        }

        List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.readAll(page, faultKnowledgeBase,ids);
        GlobalThreadLocal.setDataFilter(b);
        faultKnowledgeBases.forEach(f->{
            String faultCodes = f.getFaultCodes();
            if (StrUtil.isNotBlank(faultCodes)) {
                String[] split = faultCodes.split(",");
                List<String> list = Arrays.asList(split);
                f.setFaultCodeList(list);
            }
        });
        //正序
        String asc = "asc";
        if (asc.equals(faultKnowledgeBase.getOrder())) {
            List<FaultKnowledgeBase> reportList = faultKnowledgeBases.stream().sorted(Comparator.comparing(FaultKnowledgeBase::getCreateTime)).collect(Collectors.toList());
            return page.setRecords(reportList);
        }

        return page.setRecords(faultKnowledgeBases);
    }

    @Override
    public IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        List<String> allSubSystem = faultKnowledgeBaseTypeMapper.getAllSubSystem(sysUser.getId());
        List<FaultDTO> faults = faultAnalysisReportMapper.getFault(page, faultDTO,allSubSystem,null);
        return page.setRecords(faults);
    }

    @Override
    public Result<String> approval(String approvedRemark, Integer approvedResult, String id) {
        if ( getRole()) {return Result.error("没有权限");}
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultKnowledgeBase faultKnowledgeBase = new FaultKnowledgeBase();
        faultKnowledgeBase.setId(id);
        faultKnowledgeBase.setApprovedRemark(approvedRemark);
        faultKnowledgeBase.setApprovedResult(approvedResult);
        faultKnowledgeBase.setApprovedTime(new Date());
        faultKnowledgeBase.setApprovedUserName(sysUser.getUsername());
        if (approvedResult.equals(FaultConstant.NO_PASS)) {
            faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
        } else {
            faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
        }
        this.updateById(faultKnowledgeBase);
        return Result.OK("审批成功!");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(String id) {
        FaultKnowledgeBase byId = this.getById(id);
        if (ObjectUtil.isEmpty(byId)) {
            return Result.error("没找到对应实体");
        }
        //获取知识库被使用的次数
        int num = faultKnowledgeBaseMapper.getNum(id);
        if (num > 0) {
            return Result.error("该知识库已经被使用，不能删除");
        } else {
            byId.setDelFlag(1);
            this.updateById(byId);
        }
        return Result.OK("删除成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteBatch(List<String> ids) {
        for (String id : ids) {
            FaultKnowledgeBase byId = this.getById(id);
            if (ObjectUtil.isEmpty(byId)) {
                return Result.error("没找到对应实体");
            }
            //获取知识库被使用的次数
            int num = faultKnowledgeBaseMapper.getNum(id);
            if (num > 0) {
                return Result.error("所选知识库中有已经被使用的知识库，不能删除");
            } else {
                byId.setDelFlag(1);
                this.updateById(byId);
            }
        }
        return  Result.OK("批量删除成功!");
    }

    @Override
    public void exportTemplateXls(HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/knowledgeBase.xlsx");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp= new File("/templates/knowledgeBase.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
        Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

        CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);
        //知识库类别下拉框
        List<DictModel> dictModels = bean.queryTableDictItemsByCode("fault_knowledge_base_type", "name", "code");
        Map<String, DictModel> collect = dictModels.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect1 = collect.values().stream().collect(Collectors.toList());
        selectList(workbook, "知识库类别", 0, 0, collect1);

        //设备类型下拉框
        List<DictModel> dictModels1 = bean.queryTableDictItemsByCode("device_Type", "name", "code");
        Map<String, DictModel> collect2 = dictModels1.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect3 = collect2.values().stream().collect(Collectors.toList());
        selectList(workbook, "设备类型", 1, 1, collect3);

        //设备组件下拉框
        List<DictModel> dictModels2 = bean.queryTableDictItemsByCode("device_assembly", "material_name", "material_code");
        Map<String, DictModel> collect4 = dictModels2.stream().collect(Collectors.toMap(DictModel::getValue, Function.identity(), (oldValue, newValue) -> newValue));
        List<DictModel> collect5 = collect4.values().stream().collect(Collectors.toList());
        selectList(workbook, "设备组件", 2, 2, collect5);

        String fileName = "故障知识库导入模板.xlsx";

        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename="+"故障知识库导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //下拉框
    private void selectList(Workbook workbook,String name,int firstCol, int lastCol,List<DictModel> modelList){
        Sheet sheet = workbook.getSheetAt(0);
        if (CollectionUtil.isNotEmpty(modelList)) {
            //将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
            int sheetTotal = workbook.getNumberOfSheets();
            String hiddenSheetName = name + "_hiddenSheet";
            List<String> collect = modelList.stream().map(DictModel::getText).collect(Collectors.toList());
            Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
            if (hiddenSheet == null) {
                hiddenSheet = workbook.createSheet(hiddenSheetName);
                //写入下拉数据到新的sheet页中
                for (int i = 0; i < collect.size(); i++) {
                    Row hiddenRow = hiddenSheet.createRow(i);
                    Cell hiddenCell = hiddenRow.createCell(0);
                    hiddenCell.setCellValue(collect.get(i));
                }
                workbook.setSheetHidden(sheetTotal, true);
            }

            // 下拉数据
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 65535, firstCol, lastCol);
            //  生成下拉框内容名称
            String strFormula = hiddenSheetName + "!$A$1:$A$65535";
            // 根据隐藏页面创建下拉列表
            XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
            DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
            //  对sheet页生效
            sheet.addValidationData(validation);
        }

    }

    public boolean getRole() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> rolesByUsername = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        if (!rolesByUsername.contains(FaultConstant.ADMIN)&&!rolesByUsername.contains(FaultConstant.MAINTENANCE_WORKER)&&!rolesByUsername.contains(FaultConstant.PROFESSIONAL_TECHNICAL_DIRECTOR)) {
            return true;
        }
        return false;
    }
}
