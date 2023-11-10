package com.aiurt.boot.task.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.mapper.PatrolCheckResultMapper;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStationMapper;
import com.aiurt.boot.task.param.CustomCellMergeHandler;
import com.aiurt.boot.task.service.IPatrolTaskPrintService;
import com.aiurt.common.util.FilePrintUtils;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.aspose.cells.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description: patrol_task print
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Slf4j
@Service
public class PatrolTaskToPrintServiceImpl implements IPatrolTaskPrintService {

    @Value("${jeecg.path.upload:/opt/upFiles}")
    private String path;

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private PatrolCheckResultMapper patrolCheckResultMapper;
    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    public void printPatrolTaskToPdf(String id, String standardId, HttpServletResponse response) {
        PatrolTask patrolTask = patrolTaskMapper.selectById(id);

        PatrolStandard patrolStandard = patrolStandardMapper.selectOne(new LambdaQueryWrapper<PatrolStandard>()
                .eq(PatrolStandard::getDelFlag,0)
                .in(PatrolStandard::getId,standardId)
                .orderByDesc(PatrolStandard::getPrintTemplate).last("LIMIT 1"));
        String excelName = null;
        DictModel excelDictModel = new DictModel();
        if (StrUtil.isNotEmpty(patrolStandard.getPrintTemplate())){
            excelDictModel = sysBaseApi.dictById(patrolStandard.getPrintTemplate());
            excelName = excelDictModel.getValue();
        }else {
            excelName = "telephone_system.xlsx";
        }
        if (excelName.contains("telephone_system")){
             printPatrolTaskByCommonTpl(id,null,standardId,response);
             return;
        }else if (excelName.contains("patrol-type8")){
             printPatrolTaskByCommonTpl(id,"patrolType8",standardId,response);
            return;
        } else if (excelName.contains("wireless11")) {
             printPatrolTaskByCommonTpl(id,"wireless11",standardId,response);
            return;
        }
        // 模板文件路径
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);

        // 填充数据后的文件路径
        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        fileName = fileName.replaceAll("[/*?:\"<>|]", "-");
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;
        //获取头部数据
        PrintPatrolTaskDTO taskDTO = getHeaderData(patrolTask);
        //填充头部Map
        Map<String, Object> headerMap = getHeaderMap(patrolTask, taskDTO,patrolStandard.getName());
        //获取显示图片位置
        List<String> imageList = null;
        if (ObjectUtil.isNotEmpty(excelDictModel.getDescription())&&excelDictModel.getDescription().contains(",")){
            imageList = Arrays.asList(excelDictModel.getDescription().split(","));
        }
        //文件打印签名
        Map<String, Object> imageMap = getSignImageMap(taskDTO,imageList);

        InputStream minioFile2 = MinioUtil.getMinioFile("platform", templateFileName);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(filePath).withTemplate(minioFile2).build();
            int[] mergeColumnIndex = {0,1,2};
            CustomCellMergeHandler customCellMergeStrategy = new CustomCellMergeHandler(3,mergeColumnIndex);
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(customCellMergeStrategy).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.FALSE).build();
            //填充列表数据
            excelWriter =  fillData(id, excelName, excelWriter, writeSheet,headerMap,filePath,standardId);
            //填充表头
            excelWriter.fill(headerMap, writeSheet);
            //填充图片
            excelWriter.fill(imageMap, writeSheet);
            excelWriter.finish();
            //对已填充数据的文件进行后处理
            processFilledFile(filePath);

            MinioUtil.upload(new FileInputStream(filePath),relatiePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setFileName(fileName);
        sysAttachment.setFilePath(relatiePath);
        sysAttachment.setType("minio");
        sysBaseApi.saveSysAttachment(sysAttachment);

        //excel转换成pdf输出流
        fileToPdf(filePath, response);
    }


    /**
     * 通用模板数据封装
     *
     * @param id
     * @param type
     * @param standardId
     * @return 打印文件的minio路径
     */
    public void printPatrolTaskByCommonTpl(String id, String type, String standardId,HttpServletResponse response) {
        PatrolTask patrolTask = patrolTaskMapper.selectById(id);
        PatrolStandard patrolStandard = patrolStandardMapper.selectOne(new LambdaQueryWrapper<PatrolStandard>()
                .eq(PatrolStandard::getDelFlag,0)
                .in(PatrolStandard::getId,standardId)
                .orderByDesc(PatrolStandard::getPrintTemplate).last("LIMIT 1"));
        String excelName = null;
        DictModel excelDictModel = new DictModel();
        if (StrUtil.isNotEmpty(patrolStandard.getPrintTemplate())){
            excelDictModel = sysBaseApi.dictById(patrolStandard.getPrintTemplate());
            excelName = excelDictModel.getValue();
        }else {
            excelName = "telephone_system.xlsx";
        }
        // 填充list 的时候还要注意 模板中{.} 多了个点 表示list
        // 如果填充list的对象是map,必须包涵所有list的key,哪怕数据为null，必须使用map.put(key,null)
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);
        InputStream minioFile = MinioUtil.getMinioFile("platform",templateFileName);
        Workbook workbookTpl = null;
        CellRangeAddress mergeRegion = null;
        Integer firstColumn = null;
        Integer lastColumn = null;
        CellAddress cellByText = null;
        CellAddress cellByText2 = null;
        Integer remarkColumn = null;

        try {
//            inputStreamTemplate = new FileInputStream(templateFileName);
            workbookTpl = WorkbookFactory.create(minioFile);
            Sheet sheet = workbookTpl.getSheetAt(0);
            //查询巡检标准列所在的合并区域的列起始位置
            mergeRegion = FilePrintUtils.findMergeRegions(sheet, 1,3,"巡检标准");
            firstColumn = mergeRegion.getFirstColumn();
            lastColumn = mergeRegion.getLastColumn();

            //查询项目列所在合并区域的结束列
            cellByText = FilePrintUtils.findCellByText(sheet, 3, 3, "{list.content}");
            //查询项目列所在合并区域的结束列
            cellByText2 = FilePrintUtils.findCellByText(sheet, 3, 3, "{list.remark}");
            remarkColumn = cellByText2.getColumn();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 全部放到内存里面 并填充
        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        fileName = fileName.replaceAll("[/*?:\"<>|]", "-");
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;
        //获取头部数据
        PrintPatrolTaskDTO taskDTO = getHeaderData(patrolTask);
        //填充头部Map
        Map<String, Object> headerMap = getHeaderMap(patrolTask, taskDTO, patrolStandard.getName());
        //获取签字图片Map
        //获取显示图片位置
        List<String> imageList = null;
        if (ObjectUtil.isNotEmpty(excelDictModel.getDescription())&&excelDictModel.getDescription().contains(",")){
            imageList = Arrays.asList(excelDictModel.getDescription().split(","));
        }
        Map<String, Object> imageMap = getSignImageMap(taskDTO,imageList);

        //查询巡视标准详情
        List<PrintDTO> patrolData = print(id,standardId);
        int startRow = 3;
        int endRow = startRow;
        if (CollUtil.isNotEmpty(patrolData)){
            endRow = startRow+patrolData.size()-1;
        }
        InputStream minioFile2 = MinioUtil.getMinioFile("platform",templateFileName);
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).withTemplate(minioFile2).build()) {
            //获取需要纵向合并相同值单元格的列
            int[] mergeColumnIndex = getMergeColumnIndexArr(type, cellByText);
            //填充数据到模板，并关闭文件流
            fillData(headerMap, imageMap, patrolData, excelWriter, mergeColumnIndex);

            try (InputStream inputStream = new FileInputStream(filePath);
                 Workbook workbook = WorkbookFactory.create(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);
                // 打印设置
//                FilePrintUtils.printSet(sheet);
                // 对已填充数据的文件进行后处理
                processFilledFile(type, firstColumn, lastColumn,remarkColumn, cellByText, filePath, startRow, endRow, workbook, sheet);
            }

//            MinioUtil.upload(new FileInputStream(filePath),relatiePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setFileName(fileName);
        sysAttachment.setFilePath(relatiePath);
        sysAttachment.setType("minio");
        sysBaseApi.saveSysAttachment(sysAttachment);
        //excel转换成pdf输出流
        fileToPdf(filePath, response);
    }


    /**
     * 将文件转化成pdf流直接放到response响应
     * 此函数主要是给上面打印将excel转成pdf流输出使用
     * @param filePath 文件路径
     * @param response 响应
     */
    public void fileToPdf(String filePath, HttpServletResponse response){
        //excel转PDF流输出
        response.setCharacterEncoding("UTF-8");
        try (FileInputStream in = new FileInputStream(filePath)) {
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            com.aspose.cells.Workbook w = new com.aspose.cells.Workbook(in);
            //自动调整行高
            // w.getWorksheets().get(0).autoFitRows();
            pdfSaveOptions.setOnePagePerSheet(true);
            pdfSaveOptions.setAllColumnsInOnePagePerSheet(true);

            Worksheet worksheet = w.getWorksheets().get(0);
            // 遍历单元格并减小字体大小与设置行高
            for (int rowIndex = 0; rowIndex <= worksheet.getCells().getMaxDataRow(); rowIndex++) {
                // 该行的行高，默认15，当有更大的时候，行高取最大的
                int maxRowHeight = 15;

                for (int colIndex = 0; colIndex <= worksheet.getCells().getMaxDataColumn(); colIndex++) {
                    Cell cell = worksheet.getCells().get(rowIndex, colIndex);

                    // 单元格的行高，单元格行高与最大行高，谁大取谁，但是如果是合并单元格，并且是合并了多行的单元格不进行比较
                    Range mergedRange = cell.getMergedRange();
                    int mergedRowCount = mergedRange != null ? mergedRange.getRowCount(): 1;
                    int cellHeight = cell.getHeightOfValue();
                    if (mergedRowCount == 1 && cellHeight > maxRowHeight){
                        maxRowHeight = cellHeight;
                    }

                    // 将字体减小一号，不然pdf输出有的单元格显示内容不全
                    Style style = cell.getStyle();
                    Font font = style.getFont();
                    // 设置字体名称为宋体
                    // font.setName("SimSun");
                    int reducedFontSize = font.getSize() - 1;
                    font.setSize(reducedFontSize);
                    cell.setStyle(style);
                }

                // 设置行高
                // log.info("第{}行的行高是{}", rowIndex, maxRowHeight);
                worksheet.getCells().getRows().get(rowIndex).setHeight(maxRowHeight);
            }

            // 获取最后一行的高度
            int lastRowIndex = w.getWorksheets().get(0).getCells().getMaxDataRow();
            // 设置最后一行的高度，您可以根据需要调整高度值
            double newHeight = 80;
            w.getWorksheets().get(0).getCells().setRowHeight(lastRowIndex, newHeight);

            w.save(response.getOutputStream(), pdfSaveOptions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据模板类型获取纵向合并列设置
     * @param type
     * @param cellByText
     * @return
     */
    @NotNull
    private static int[] getMergeColumnIndexArr(String type, CellAddress cellByText) {
        int[] mergeColumnIndex;
        if ("patrolType8".equals(type)) {
            if (ObjectUtil.isEmpty(cellByText)){
                mergeColumnIndex = new int[]{0};
            }else{
                mergeColumnIndex = new int[]{0, 1};
            }

        }else{
            mergeColumnIndex = new int[]{0, 1, 2};
        }
        return mergeColumnIndex;
    }

    /**
     * 填充通用模板业务数据
     * @param headerMap
     * @param imageMap
     * @param patrolData
     * @param excelWriter
     * @param mergeColumnIndex
     */
    private static void fillData(Map<String, Object> headerMap, Map<String, Object> imageMap, List<PrintDTO> patrolData, ExcelWriter excelWriter, int[] mergeColumnIndex) {
        CustomCellMergeHandler customCellMergeStrategy = new CustomCellMergeHandler(3, mergeColumnIndex);
        WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(customCellMergeStrategy).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        //填充列表数据
        excelWriter.fill(new FillWrapper("list", patrolData),fillConfig, writeSheet);
        //填充表头
        excelWriter.fill(headerMap, writeSheet);
        //填充图片
        excelWriter.fill(imageMap, writeSheet);
        excelWriter.finish();
    }

    /**
     * 填充业务数据
     *
     * @param taskId
     * @param excelName
     * @param excelWriter
     * @param writeSheet
     * @param headerMap
     * @param standardId
     * @return
     */
   private ExcelWriter fillData(String taskId, String excelName, ExcelWriter excelWriter, WriteSheet writeSheet, Map<String, Object> headerMap,String filePath, String standardId){
       List<PrintDTO> patrolData = new ArrayList<>();
       if ("safty_produce_check.xlsx".equals(excelName)){
           patrolData = getPrintOthers(taskId,headerMap,standardId);
           //填充列表数据
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }else if ("wireless_system.xlsx".equals(excelName) || "wireless_system1.xlsx".equals(excelName)||
               "wireless_system2.xlsx".equals(excelName)||"wireless_system3.xlsx".equals(excelName)){
           patrolData = getWirelessSystem(taskId,headerMap,excelName,standardId);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }else if ("pis_system.xlsx".equals(excelName) || "pis_system1.xlsx".equals(excelName)){
           patrolData = getRemark(taskId,headerMap,excelName,standardId);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       } else if("cctv_system.xlsx".equals(excelName)){
           patrolData = getCctvSystem(taskId,headerMap,standardId);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }else if("network_manage.xlsx".equals(excelName)){
           patrolData = printPatrolTaskByNetworkManage(taskId,standardId,headerMap);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }

       return excelWriter;
    }

    private List<PrintDTO> getCctvSystem(String taskId, Map<String, Object> headerMap, String standardId) {
        List<PrintDTO> getCctvSystem = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = getBillGangedInfo(taskId,standardId);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            if (CollUtil.isEmpty(dto.getBillInfo())){
                continue;
            }
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll = patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            //子级
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            //父级
            List<PatrolCheckResultDTO> parentDTOList = checkResultAll.stream()
                    .filter(c -> Objects.nonNull(c)
                            &&Objects.nonNull(c.getCheck())&& c.getCheck() == 0)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(parentDTOList)) {
                continue;
            }
            List<String> pisSystem = sysBaseApi.getDictItems("cctv_system").stream().map(w-> w.getText()).collect(Collectors.toList());
            pisSystem.forEach(str-> {
                PrintDTO printDTO = new PrintDTO();
                PatrolCheckResultDTO patrolCheckResultDTO = parentDTOList.stream().filter(p -> p.getContent().replaceAll("\n", " ").equals(str)).findFirst().orElse(null);
                if (ObjectUtil.isEmpty(patrolCheckResultDTO)){
                    printDTO.setRemark(null);
                }else {
                    String oldId = patrolCheckResultDTO.getOldId();
                    StringBuffer stringBuffer = new StringBuffer();
                    AtomicBoolean flag = new AtomicBoolean(false);
                    //子级
                    List<PatrolCheckResultDTO> childDTOs =  checkDTOs.stream()
                            .filter(c -> c.getCheck() == 1)
                            .filter(c -> c.getParentId().equals(oldId))
                            .collect(Collectors.toList());
                    List<String> procMethodsList = new ArrayList<>() ;
                    childDTOs.forEach(c->{
                        if(Objects.nonNull(c) && ObjectUtil.isNotEmpty(c.getCheckResult()) && c.getCheckResult().equals(0)){
                            flag.set(true);
                            stringBuffer.append(c.getQualityStandard()).append(":异常").append("\n (");
                            String writeValue = "";
                            if (PatrolConstant.DATE_TYPE_SPECIALCHAR.equals(c.getInputType())) {
                                writeValue =c.getSpecialCharacters().replaceAll("[/#&$]", "");
                            }else if (PatrolConstant.DEVICE_OUT.equals(c.getInputType())) {
                                writeValue = c.getWriteValue();
                            }else if (PatrolConstant.DEVICE_INP_TYPE.equals(c.getInputType())) {
                                writeValue = patrolCheckResultMapper.getSysDict(c.getDictCode(),c.getOptionValue());
                            }
                            if (StrUtil.isNotEmpty(c.getRemark())){
                                writeValue =writeValue+",";
                            }
                            if (StrUtil.isNotEmpty(writeValue)){
                                stringBuffer.append(writeValue);
                            }
                            stringBuffer.append(c.getRemark()).append(")");
                            stringBuffer.append(",");
                        }
                        procMethodsList.add(c.getProcMethods());
                    });
                    List<String> stringList = procMethodsList.stream().distinct().collect(Collectors.toList());
                    String procMethods = stringList.stream().collect(Collectors.joining(","));
                    if(flag.get()){
                        stringBuffer.deleteCharAt(stringBuffer.length()-1);
                        headerMap.put(str,stringBuffer.toString());
                        if (StrUtil.isNotEmpty(procMethods) && !"null".equals(procMethods)){
                            headerMap.put("procMethods"+str,procMethods);
                        }else {
                            headerMap.put("procMethods"+str,null);
                        }
                    }else{
                        headerMap.put(str,null);
                        if (StrUtil.isNotEmpty(procMethods) && !"null".equals(procMethods)){
                            headerMap.put("procMethods"+str,procMethods);
                        }else {
                            headerMap.put("procMethods"+str,null);
                        }
                    }
                }
                getCctvSystem.add(printDTO);
            });
        }
        return getCctvSystem;
    }

    /**
     * 网管模板
     *
     * @param id
     * @param headerMap
     * @return
     */
    public  List<PrintDTO> printPatrolTaskByNetworkManage(String id, String standardId, Map<String, Object> headerMap){
        List<PrintDTO> getNetworkManage = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = getBillGangedInfo(id,standardId);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll = patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            //父级
            List<PatrolCheckResultDTO> parentDTOList = checkResultAll.stream()
                    .filter(c -> Objects.nonNull(c)
                            &&Objects.nonNull(c.getCheck())&& c.getCheck() == 1)
                    .collect(Collectors.toList());
            List<String> networkManage = sysBaseApi.getDictItems("network_manage").stream().map(w-> w.getText()).collect(Collectors.toList());
            StringBuilder remark = new StringBuilder(); AtomicInteger i = new AtomicInteger(1);
            networkManage.forEach(str-> {
                PatrolCheckResultDTO patrolCheckResultDTO = checkDTOs.stream().filter(p -> p.getOldCode().equals(str)).findFirst().orElse(null);
                if (ObjectUtil.isEmpty(patrolCheckResultDTO)){
                    headerMap.put(str,"☐正常 ☐异常");
                  //  printDTO.setResult("☐正常 ☐异常");
                    headerMap.put("procMethods"+str,null);
                }else if(ObjectUtil.isNotEmpty(patrolCheckResultDTO.getCheckResult())){
                    if(patrolCheckResultDTO.getCheckResult().equals(0)){
                        remark.append(i).append(".").append(patrolCheckResultDTO.getContent()).append("-")
                                .append(patrolCheckResultDTO.getQualityStandard()).append(":").append(patrolCheckResultDTO.getRemark()).append("\n");
                        headerMap.put(str,"☐正常 ☑异常");
                        i.getAndIncrement();
                        headerMap.put("procMethods"+str,patrolCheckResultDTO.getProcMethods());
                    }else {
                        headerMap.put(str,"☑正常 ☐异常");
                        headerMap.put("procMethods"+str,patrolCheckResultDTO.getProcMethods());
                    }
                }
                //getNetworkManage.add(printDTO);
            });
            headerMap.put("remark",remark.toString());
        }
        return getNetworkManage;
    }

    /**
     * 获取头部数据
     * @param patrolTask
     * @return
     */
    @NotNull
    private PrintPatrolTaskDTO getHeaderData(PatrolTask patrolTask) {
        // 查询头部数据
        PrintPatrolTaskDTO taskDTO = new PrintPatrolTaskDTO();

        Assert.notNull(patrolTask, "未找到对应记录！");
        taskDTO.setId(patrolTask.getId());
        taskDTO.setTitle(patrolTask.getName());
        // 站点信息
        List<PatrolTaskStationDTO> stationInfo = patrolTaskStationMapper.selectStationByTaskCode(patrolTask.getCode());
        taskDTO.setStationNames(stationInfo.stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.joining()));
        if (StrUtil.isNotEmpty(patrolTask.getEndUserId())) {
            taskDTO.setUserName(patrolTaskMapper.getUsername(patrolTask.getEndUserId()));
        }
        if (StrUtil.isNotEmpty(patrolTask.getSpotCheckUserId())) {
            taskDTO.setSpotCheckUserName(patrolTaskMapper.getUsername(patrolTask.getSpotCheckUserId()));
        }
        taskDTO.setSignUrl(patrolTask.getSignUrl());
        return taskDTO;
    }

    /**
     * 填充头部Map
     *
     * @param patrolTask
     * @param taskDTO
     * @param name
     * @return
     */
    @NotNull
    private static Map<String, Object> getHeaderMap(PatrolTask patrolTask, PrintPatrolTaskDTO taskDTO, String name) {
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("title", name.replaceAll("\n", ""));
        map.put("patrolStation", taskDTO.getStationNames());
        map.put("patrolPerson", taskDTO.getUserName());
        map.put("checkUserName", taskDTO.getSpotCheckUserName());
        map.put("patrolDate", DateUtil.format(patrolTask.getSubmitTime(),"yyyy-MM-dd"));
        map.put("patrolTime", DateUtil.format(patrolTask.getSubmitTime(),"HH:mm"));
        map.put("year",DateUtil.format(patrolTask.getSubmitTime(),"yyyy"));
        map.put("month",DateUtil.format(patrolTask.getSubmitTime(),"MM"));
        map.put("day",DateUtil.format(patrolTask.getSubmitTime(),"dd"));
        if (ObjectUtil.isNotEmpty(patrolTask.getSpotCheckTime())) {
            map.put("yearSpot",DateUtil.format(patrolTask.getSpotCheckTime(),"yyyy"));
            map.put("monthSpot",DateUtil.format(patrolTask.getSpotCheckTime(),"MM"));
            map.put("daySpot",DateUtil.format(patrolTask.getSpotCheckTime(),"dd"));
        }
        return map;
    }


    /**
     * 获取签字图片Map
     *
     * @param taskDTO
     * @param columnRangeList
     * @return
     */
    @NotNull
    private Map<String, Object> getSignImageMap(PrintPatrolTaskDTO taskDTO, List<String> columnRangeList) {
        Map<String, Object> imageMap = MapUtils.newHashMap();
        if(StrUtil.isNotEmpty(taskDTO.getSignUrl())&& taskDTO.getSignUrl().indexOf("?")!=-1){
            int index =  taskDTO.getSignUrl().indexOf("?");
            SysAttachment sysAttachment = sysBaseApi.getFilePath(taskDTO.getSignUrl().substring(0, index));
            InputStream inputStream = MinioUtil.getMinioFile("platform",sysAttachment.getFilePath());
            if(ObjectUtil.isEmpty(inputStream)){
                imageMap.put("signImage",null);
            } else {
                try {
                    byte[] convert = FilePrintUtils.convert(inputStream);
                    WriteCellData writeImageData = FilePrintUtils.writeCellImageData(convert,columnRangeList);
                    imageMap.put("signImage",writeImageData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            imageMap.put("signImage",null);
        }
        return imageMap;
    }
    /**
     * 通用模板的文件后处理
     * @param type
     * @param firstColumn
     * @param lastColumn
     * @param cellByText
     * @param filePath
     * @param startRow
     * @param endRow
     * @param workbook
     * @param sheet
     * @throws IOException
     */

    private static void processFilledFile(String type, Integer firstColumn, Integer lastColumn,Integer remarkColumn, CellAddress cellByText, String filePath, int startRow, int endRow, Workbook workbook, Sheet sheet) throws IOException {
        if ("patrolType8".equals(type)){
            //如果项目列占用多列，则需合并
            if (ObjectUtil.isEmpty(cellByText)){
                FilePrintUtils.setWrapText(workbook,7, startRow, endRow,0,0,false);
            }else{
                FilePrintUtils.setWrapText(workbook,7, startRow, endRow,0,1,false);
            }
            //自动换行
            FilePrintUtils.setWrapText(workbook,7,1,1,1,1,true);
            //设置备注列自动换行
            FilePrintUtils.setWrapText(workbook,7,startRow,endRow,remarkColumn,remarkColumn,false);

            //合并指定范围行的单元格
            FilePrintUtils.mergeCellsInColumnRange(workbook,true, startRow, endRow, firstColumn, lastColumn);
            FilePrintUtils.mergeCellsInColumnRange(workbook,true, startRow, endRow, lastColumn +1, lastColumn +3);
            //设置列宽
            FilePrintUtils.setColumnWidth(sheet,0,15);
        }else if("wireless11".equals(type)){
            //自动换行
            // setWrapText(workbook,1,startRow,endRow,0,0);
            FilePrintUtils.setWrapText(workbook,15,1,1,1,1,true);
            FilePrintUtils.setWrapText(workbook,7, startRow, endRow,0, 1,false);
            //设置备注列自动换行
            FilePrintUtils.setWrapText(workbook,7,startRow,endRow,remarkColumn,remarkColumn,false);
            //合并指定范围行的单元格
            FilePrintUtils.mergeCellsInColumnRange(workbook,true, startRow, endRow, firstColumn, lastColumn);
            //设置第一列列宽
            FilePrintUtils.setColumnWidth(sheet,0,17);
        }
        else {
            //自动换行
            // setWrapText(workbook,1,startRow,endRow,0,0);
            FilePrintUtils.addReturn(workbook, startRow, endRow,0,0);
            FilePrintUtils.setWrapText(workbook,7,1,1,1,1,true);
            FilePrintUtils.setWrapText(workbook,7, startRow, endRow,1, firstColumn >3?3:2,false);
            //设置备注列自动换行
            FilePrintUtils.setWrapText(workbook,7,startRow,endRow,remarkColumn,remarkColumn,false);

            //合并指定范围行的单元格
            FilePrintUtils.mergeCellsInColumnRange(workbook,true, startRow, endRow, firstColumn, lastColumn);
            //设置第一列列宽
            FilePrintUtils.setColumnWidth(sheet,0,10);
        }
        OutputStream outputStream = null;
        // 保存修改后的Excel文件
        try{
            outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
        }finally {
            if (null!=outputStream){
                outputStream.close();
            }

            if (null!=workbook){
                workbook.close();
            }
        }
    }

    /**
     * 定制模板的文件后处理
     * @param filePath
     * @throws IOException
     */
    private static void processFilledFile(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet  = workbook.getSheetAt(0);
            //打印设置
//            FilePrintUtils.printSet(sheet);

            // 保存修改后的Excel文件
            OutputStream outputStream = null;
            try{
                outputStream = new FileOutputStream(filePath);
                workbook.write(outputStream);
            }finally {
                if (null!=inputStream){
                    inputStream.close();
                }
                if (null!=outputStream){
                    outputStream.close();
                }
                if (null!=workbook){
                    workbook.close();
                }
            }
        }
    }
    private List<PrintDTO> getPrintOthers(String id, Map<String, Object> map, String standardId) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = getBillGangedInfo(id,standardId);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll =  patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            List<String> safty = sysBaseApi.getDictItems("safty_produce_check").stream().map(w-> w.getText()).collect(Collectors.toList());
            boolean result = checkDTOs.stream().filter(c-> c.getContent().equals("电暖气")).anyMatch(f-> f.getCheckResult()!=0);
            if (result){
                map.put("isTrue","☑有");
                map.put("isFalse","☐无");
            }else {
                map.put("isFalse","☑无");
                map.put("isTrue","☐有");
            }
            safty.add(6,"");
            int size = safty.size();
            for (int i = 0; i < size; i++) {
                String s = safty.get(i);
                PrintDTO printDTO = new PrintDTO();
                if (s.contains("干粉灭火器") || s.contains("二氧化碳灭火器")){
                    List<PatrolCheckResultDTO> patrolCheckResultDTOList = checkDTOs.stream().filter(p -> p.getContent().replaceAll(" |-", "").equals(s)).collect(Collectors.toList());
                    if (ObjectUtil.isEmpty(patrolCheckResultDTOList.get(1))){
                        printDTO.setResult("☐是 ☐否");
                        getPrint.add(printDTO);
                    }else {
                        if(ObjectUtil.isEmpty(patrolCheckResultDTOList.get(1).getCheckResult())){
                            printDTO.setResult("☐是 ☐否");
                        }else {
                            printDTO.setResult(patrolCheckResultDTOList.get(1).getCheckResult()==0?"☐是 ☑否":"☑是 ☐否");
                        }
                        String writeValue = "";
                        if (PatrolConstant.DATE_TYPE_SPECIALCHAR.equals(patrolCheckResultDTOList.get(0).getInputType())) {
                            writeValue =patrolCheckResultDTOList.get(0).getSpecialCharacters().replaceAll("[/#&$]", "");
                        }else if (PatrolConstant.DEVICE_OUT.equals(patrolCheckResultDTOList.get(0).getInputType())) {
                            writeValue = patrolCheckResultDTOList.get(0).getWriteValue();
                        }else if (PatrolConstant.DEVICE_INP_TYPE.equals(patrolCheckResultDTOList.get(0).getInputType())) {
                            writeValue = patrolCheckResultMapper.getSysDict(patrolCheckResultDTOList.get(0).getDictCode(),patrolCheckResultDTOList.get(0).getOptionValue());
                        }
                        if (StrUtil.isNotEmpty(patrolCheckResultDTOList.get(1).getRemark())){
                            writeValue =writeValue+",";
                        }
                        printDTO.setRemark(writeValue+patrolCheckResultDTOList.get(1).getRemark());
                        getPrint.add(printDTO);
                    }
                }else {
                    PatrolCheckResultDTO patrolCheckResultDTO = checkDTOs.stream().filter(p -> p.getContent().replaceAll(" |-", "").equals(s)).findFirst().orElse(null);
                    if (ObjectUtil.isEmpty(patrolCheckResultDTO)){
                        printDTO.setResult("☐是 ☐否");
                        getPrint.add(printDTO);
                    }else {
                        // 如果是最后一次循环，执行其他操作
                        if (i == size - 1) {
                            map.put("remark",patrolCheckResultDTO.getRemark());
                        }else {
                            if(ObjectUtil.isEmpty(patrolCheckResultDTO.getCheckResult())){
                                printDTO.setResult("☐是 ☐否");
                            }else {
                                printDTO.setResult(patrolCheckResultDTO.getCheckResult()==0?"☐是 ☑否":"☑是 ☐否");
                            }
                            printDTO.setRemark(patrolCheckResultDTO.getRemark());
                            getPrint.add(printDTO);
                        }
                    }
                }
            }
        }
        return getPrint;
    }



    private List<PrintDTO> getRemark(String id, Map<String,Object> map, String excelName, String standardId) {
        List<PrintDTO> getRemark = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = getBillGangedInfo(id,standardId);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll = patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            //父级
            List<PatrolCheckResultDTO> parentDTOList = checkResultAll.stream()
                    .filter(c -> Objects.nonNull(c)
                       &&Objects.nonNull(c.getCheck())&& c.getCheck() == 0)
                    .collect(Collectors.toList());
            String strName = excelName.substring(0, excelName.indexOf("."));
            List<String> pisSystem = sysBaseApi.getDictItems(strName).stream().map(w-> w.getText()).collect(Collectors.toList());
            PrintDTO printDTO = new PrintDTO();
            pisSystem.forEach(str-> {
                PatrolCheckResultDTO patrolCheckResultDTO = parentDTOList.stream().filter(p -> p.getOldCode().equals(str)).findFirst().orElse(null);
                if (ObjectUtil.isEmpty(patrolCheckResultDTO)){
                    map.put("result"+str,"☐是 ☑否");
                    map.put("resultTrue"+str,"☐正常");
                    map.put("resultFalse"+str,"☐异常");
                    map.put("remark"+str,null);
                    map.put("procMethods"+str,null);
//                    printDTO.setResult("");
//                    printDTO.setResultTrue();
//                    printDTO.setResultFalse("☐异常");
//                    printDTO.setRemark(null);
                }else {
                    String oldId = patrolCheckResultDTO.getOldId();
                    StringBuffer stringBuffer = new StringBuffer();
                    AtomicBoolean flag = new AtomicBoolean(false);
                    //子级
                    List<PatrolCheckResultDTO> childDTOs =  checkDTOs.stream()
                            .filter(c -> c.getParentId().equals(oldId))
                            .collect(Collectors.toList());
                    List<String> procMethodsList = new ArrayList<>() ;
                    childDTOs.forEach(c->{
                        if(ObjectUtil.isNotEmpty(c)&& ObjectUtil.isNotEmpty(c.getCheckResult()) && c.getCheckResult().equals(0)){
                            flag.set(true);
                            stringBuffer.append(c.getQualityStandard()).append(":异常").append("\n (").append(c.getRemark()).append(")");
                            stringBuffer.append(",");
                        }
                        procMethodsList.add(c.getProcMethods());
                    });
                    List<String> stringList = procMethodsList.stream().distinct().collect(Collectors.toList());
                    String procMethods = stringList.stream().collect(Collectors.joining(","));
                    if(flag.get()){
                        map.put("result"+str,"☑是 ☐否");
                        map.put("resultTrue"+str,"☐正常");
                        map.put("resultFalse"+str,"☑异常");
                        stringBuffer.deleteCharAt(stringBuffer.length()-1);
                        map.put("remark"+str,stringBuffer.toString());
                        if (StrUtil.isNotEmpty(procMethods) && !"null".equals(procMethods)){
                            map.put("procMethods"+str,procMethods);
                        }else {
                            map.put("procMethods"+str,null);
                        }
//                        printDTO.setResult("☑是 ☐否");
//                        printDTO.setResultTrue("☐正常");
//                        printDTO.setResultFalse("☑异常");
//                        printDTO.setRemark(stringBuffer.toString());
                    }else{
                        map.put("procMethods"+str,null);
                        map.put("result"+str,"☑是 ☐否");
                        map.put("resultTrue"+str,"☑正常");
                        map.put("resultFalse"+str,"☐异常");
                        if (ObjectUtil.isNotEmpty(patrolCheckResultDTO.getRemark())){
                            map.put("remark"+str,patrolCheckResultDTO.getContent()+"("+patrolCheckResultDTO.getRemark()+")");
                        }else {
                            map.put("remark"+str,null);
                        }
                        if (StrUtil.isNotEmpty(procMethods) && !"null".equals(procMethods)){
                            map.put("procMethods"+str,procMethods);
                        }else {
                            map.put("procMethods"+str,null);
                        }
//                        printDTO.setResult("☑是 ☐否");
//                        printDTO.setResultTrue("☑正常");
//                        printDTO.setResultFalse("☐异常");
                    }
                }
               // getRemark.add(printDTO);
            });
        }
        return getRemark;
    }

    private List<PrintDTO> getWirelessSystem(String id, Map<String, Object> map, String excelName, String standardId) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = getBillGangedInfo(id,standardId);
        Set<String> set = new LinkedHashSet<>() ;
        StringBuilder text  = new StringBuilder();
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            //获取本站是否有此设备
            String str = new String();
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll =  patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            String strName = excelName.substring(0, excelName.indexOf("."));
            List<String> wirelessSystem = sysBaseApi.getDictItems(strName).stream().map(w-> w.getText()).collect(Collectors.toList());
            List<String> result = wirelessSystem.stream().filter(w -> !checkResultAll.stream().anyMatch(c -> c.getContent().equals(w))).collect(Collectors.toList());
            if (!result.isEmpty()){
                for (int i = 0; i < result.size(); i++) {
                     text.append(i+1).append(".").append(result.get(i)).append(" ");
                     if (i+1 == result.size()){
                         text.append("( 无 )");
                     }
                }
            }
            int num = 0;
            for (int i = 0; i < checkDTOs.size(); i++) {
                if (i==0){
                    text.append("\n \n 异常情况:");
                }
                if(Objects.nonNull(checkDTOs.get(i).getCheckResult())&&checkDTOs.get(i).getCheckResult()==0){
                    text.append("\n").append(num+1).append(".").append(checkDTOs.get(i).getContent()).append(":异常").append("\n (").append(checkDTOs.get(i).getRemark()).append(")");
                    num++;
                }
                if (i+1 == checkDTOs.size()){
                    text.append("\n \n 其他正常");
                }
            }
        }
        map.put("remark","本站 : \n"+text);
        return getPrint;
    }



    /**
     * 获取打印的业务数据
     *
     * @param id
     * @return
     */
    private List<PrintDTO> print(String id,String standardId) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = getBillGangedInfo(id, standardId);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckByTaskDeviceIdAndParent(collect);
            for (PatrolCheckResultDTO c : checkResultList) {
                if (c.getCheck()==1 && c.getParentId().equals("0")){
                    PrintDTO printDTO = new PrintDTO();
                    String qualityStandard = "";
                    if (StrUtil.isNotEmpty(c.getQualityStandard())){
                        qualityStandard = c.getQualityStandard();
                    }
                    printDTO.setStandard(ObjectUtil.defaultIfEmpty(qualityStandard.replaceAll("[\n ]", ""), c.getContent()).replaceAll("[\n ]", ""));
                    printDTO.setEquipment(c.getContent());
                    printDTO.setContent(c.getContent());
                    printDTO.setProcMethods(c.getProcMethods());
                    if(ObjectUtil.isEmpty(c.getCheckResult())){
                        printDTO.setResultTrue("☐正常");
                        printDTO.setResultFalse("☐异常");
                        printDTO.setResult("☐正常 ☐异常");
                    }else {
                        printDTO.setResultTrue(c.getCheckResult()==0?"☐正常":"☑正常");
                        printDTO.setResultFalse(c.getCheckResult()==0?"☑异常":"☐异常");
                        printDTO.setResult(c.getCheckResult()==0?"☐正常 ☑异常":"☑正常 ☐异常");
                        if (ObjectUtil.isNotEmpty(c.getRemark())){
                            printDTO.setResultAndRemark(c.getCheckResult()==0?"☐正常 ☑异常"+"("+c.getRemark()+")":"☑正常 ☐异常"+"("+c.getRemark()+")");
                        }else {
                            printDTO.setResultAndRemark(c.getCheckResult()==0?"☐正常 ☑异常":"☑正常 ☐异常");
                        }
                    }
//                    printDTO.setResult(result);
//                    printDTO.setContentRemark(contentRemark);
                    String writeValue = "";
                    if (PatrolConstant.DATE_TYPE_SPECIALCHAR.equals(c.getInputType())) {
                        writeValue =c.getSpecialCharacters().replaceAll("[/#&$]", "");
                    }else if (PatrolConstant.DEVICE_OUT.equals(c.getInputType())) {
                        writeValue = c.getWriteValue();
                    }else if (PatrolConstant.DEVICE_INP_TYPE.equals(c.getInputType())) {
                        writeValue = patrolCheckResultMapper.getSysDict(c.getDictCode(),c.getOptionValue());
                    }
                    if (StrUtil.isNotEmpty(c.getRemark())){
                        writeValue =writeValue+",";
                    }
                    printDTO.setRemark(writeValue+c.getRemark());
                    printDTO.setLocation(dto.getStationName());
                    printDTO.setSubSystem(c.getSubsystemName());
                    if (ObjectUtil.isNotEmpty(printDTO.getStandard())){
                        getPrint.add(printDTO);
                    }
                }else {                List<PatrolCheckResultDTO> list = patrolCheckResultMapper.getQualityStandard(collect,c.getOldId());
//                list.stream().filter(l -> l.getCheckResult()==0).collect(Collectors.toList());
//                String result; String contentRemark = "";
//                if (CollectionUtil.isNotEmpty(list)){
//                    result = "☐正常\n☑异常";
//                    contentRemark =  String.join(",",list.stream().map(l-> l.getRemark()).collect(Collectors.toList()));
//                }else {
//                    result = "☑正常\n☐异常";
//
//                }
                    for (PatrolCheckResultDTO t :list){
                        PrintDTO printDTO = new PrintDTO();
                        String qualityStandard = "";
                        if (StrUtil.isNotEmpty(t.getQualityStandard())){
                            qualityStandard = t.getQualityStandard();
                        }
                        printDTO.setStandard(ObjectUtil.defaultIfEmpty(qualityStandard.replaceAll("[\n ]", ""), t.getContent()).replaceAll("[\n ]", ""));
                        printDTO.setEquipment(c.getContent());
                        printDTO.setContent(t.getContent());
                        printDTO.setProcMethods(t.getProcMethods());
                        if(ObjectUtil.isEmpty(t.getCheckResult())){
                            printDTO.setResultTrue("☐正常");
                            printDTO.setResultFalse("☐异常");
                            printDTO.setResult("☐正常 ☐异常");
                        }else {
                            printDTO.setResultTrue(t.getCheckResult()==0?"☐正常":"☑正常");
                            printDTO.setResultFalse(t.getCheckResult()==0?"☑异常":"☐异常");
                            printDTO.setResult(t.getCheckResult()==0?"☐正常 ☑异常":"☑正常 ☐异常");
                            if (ObjectUtil.isNotEmpty(t.getRemark())){
                                printDTO.setResultAndRemark(t.getCheckResult()==0?"☐正常 ☑异常"+"("+t.getRemark()+")":"☑正常 ☐异常"+"("+t.getRemark()+")");
                            }else {
                                printDTO.setResultAndRemark(t.getCheckResult()==0?"☐正常 ☑异常":"☑正常 ☐异常");
                            }
                        }
//                    printDTO.setResult(result);
//                    printDTO.setContentRemark(contentRemark);
                        String writeValue = "";
                        if (PatrolConstant.DATE_TYPE_SPECIALCHAR.equals(t.getInputType())) {
                            writeValue =t.getSpecialCharacters().replaceAll("[/#&$]", "");
                        }else if (PatrolConstant.DEVICE_OUT.equals(t.getInputType())) {
                            writeValue = t.getWriteValue();
                        }else if (PatrolConstant.DEVICE_INP_TYPE.equals(t.getInputType())) {
                            writeValue = patrolCheckResultMapper.getSysDict(t.getDictCode(),t.getOptionValue());
                        }
                        if (StrUtil.isNotEmpty(t.getRemark())){
                            writeValue =writeValue+",";
                        }
                        printDTO.setRemark(writeValue+t.getRemark());
                        printDTO.setLocation(dto.getStationName());
                        printDTO.setSubSystem(t.getSubsystemName());
                        if (ObjectUtil.isNotEmpty(printDTO.getStandard())){
                            getPrint.add(printDTO);
                        }
                    }}

            }
        }
        return getPrint;
    }

    private List<PrintDTO> getPrint(String id,String standardId) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = getBillGangedInfo(id,standardId);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckByTaskDeviceIdAndParent(collect);
            for (PatrolCheckResultDTO c : checkResultList) {
                List<PatrolCheckResultDTO> list = patrolCheckResultMapper.getQualityStandard(collect,c.getOldId());
                for (PatrolCheckResultDTO t :list){
                    PrintDTO printDTO = new PrintDTO();
                    printDTO.setStandard(t.getQualityStandard());
                    printDTO.setEquipment(c.getContent());
                    printDTO.setContent(t.getContent());
                    if(ObjectUtil.isEmpty(t.getCheckResult())){
                        printDTO.setResultTrue("☐正常");
                        printDTO.setResultFalse("☐异常");
                    }else {
                        printDTO.setResultTrue(t.getCheckResult()==0?"☐正常":"☑正常");
                        printDTO.setResultFalse(t.getCheckResult()==0?"☑异常":"☐异常");
                    }
                    printDTO.setRemark(t.getRemark());
                    printDTO.setLocation(dto.getStationName());
                    printDTO.setSubSystem(t.getSubsystemName());
                    if (ObjectUtil.isNotEmpty(printDTO.getStandard())){
                        getPrint.add(printDTO);
                    }
                }
            }
        }
        return getPrint;
    }
    public List<PatrolStationDTO> getBillGangedInfo(String taskId, String standardId) {
        List<PatrolBillDTO> billGangedInfo = patrolTaskDeviceMapper.getBillGanged(taskId,standardId);
        billGangedInfo.forEach(b->{b.setTableName(b.getTableName().replaceAll("\n", ""));});
        Map<String, List<PatrolBillDTO>> collect = billGangedInfo.stream().filter((t) -> StrUtil.isNotBlank(t.getStationCode())).collect(Collectors.groupingBy(PatrolBillDTO::getStationCode));
        List<PatrolStationDTO> stationList = new ArrayList<>();
        for (Map.Entry<String, List<PatrolBillDTO>> entry : collect.entrySet()) {
            String stationCode = entry.getKey();
            if (ObjectUtil.isEmpty(stationCode)) {
                continue;
            }
            PatrolStationDTO station = new PatrolStationDTO();
            station.setStationCode(stationCode);
            station.setStationName(patrolTaskDeviceMapper.getStationName(stationCode));
            station.setBillInfo(entry.getValue());
            stationList.add(station);
        }
        return stationList;
    }
}
