package com.aiurt.boot.task.service.impl;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.plan.mapper.PatrolPlanMapper;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.task.dto.PatrolCheckResultDTO;
import com.aiurt.boot.task.dto.PatrolStationDTO;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.aiurt.boot.task.dto.PrintDTO;
import com.aiurt.boot.task.dto.PrintPatrolTaskDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.mapper.PatrolCheckResultMapper;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskOrganizationMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStandardMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStationMapper;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import com.aiurt.boot.task.param.CustomCellMergeHandler;
import com.aiurt.boot.task.service.*;
import com.aiurt.common.util.FilePrintUtils;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.common.api.IBaseApi;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jetbrains.annotations.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @Description: patrol_task print
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Slf4j
@Service
public class PatrolTaskPrintServiceImpl implements IPatrolTaskPrintService {

    @Value("${jeecg.path.upload:/opt/upFiles}")
    private String path;

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private IPatrolTaskDeviceService patrolTaskDeviceService;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private IPatrolTaskUserService patrolTaskUserService;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private IPatrolCheckResultService patrolCheckResultService;
    @Autowired
    private PatrolCheckResultMapper patrolCheckResultMapper;
    @Autowired
    private IPatrolTaskOrganizationService patrolTaskOrganizationService;
    @Autowired
    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;
    @Autowired
    private IPatrolTaskStationService patrolTaskStationService;
    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private IPatrolTaskStandardService patrolTaskStandardService;
    @Autowired
    private PatrolTaskStandardMapper patrolTaskStandardMapper;

    @Autowired
    private PatrolPlanMapper patrolPlanMapper;
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Autowired
    private PatrolManager manager;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IBaseApi baseApi;

    /**
     * 处理文件打印数据，并返回打印文件路径
     * @param id
     * @return打印文件路径
     */
    public String printPatrolTask(String id) {
        PatrolTask patrolTask = patrolTaskMapper.selectById(id);
        List<PatrolTaskStandard> patrolTaskStandard = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>()
                .eq(PatrolTaskStandard::getDelFlag,0).eq(PatrolTaskStandard::getTaskId,patrolTask.getId()));
        PatrolStandard patrolStandard = patrolStandardMapper.selectOne(new LambdaQueryWrapper<PatrolStandard>()
                .eq(PatrolStandard::getDelFlag,0)
                .in(PatrolStandard::getCode,patrolTaskStandard.stream().map(PatrolTaskStandard::getStandardCode).collect(Collectors.toList()))
                .orderByDesc(PatrolStandard::getPrintTemplate).last("LIMIT 1"));
        String excelName = null;
        if (StrUtil.isNotEmpty(patrolStandard.getPrintTemplate())){
            excelName = sysBaseApi.dictById(patrolStandard.getPrintTemplate()).getValue();
        }else {
            excelName = "telephone_system.xlsx";
        }
        if (excelName.contains("telephone_system")){
           return printPatrolTaskByCommonTpl(id,null);
        }else if (excelName.contains("patrol-type8")){
           return printPatrolTaskByCommonTpl(id,"patrolType8");
        }
        // 模板文件路径
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);

        // 填充数据后的文件路径
        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;

        //获取头部数据
        PrintPatrolTaskDTO taskDTO = getHeaderData(patrolTask);
        //填充头部Map
        Map<String, Object> headerMap = getHeaderMap(patrolTask, taskDTO);
        //文件打印签名
        Map<String, Object> imageMap = getSignImageMap(taskDTO);

        InputStream minioFile2 = MinioUtil.getMinioFile("platform", templateFileName);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(filePath).withTemplate(minioFile2).build();
            int[] mergeColumnIndex = {0,1,2};
            CustomCellMergeHandler customCellMergeStrategy = new CustomCellMergeHandler(3,mergeColumnIndex);
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(customCellMergeStrategy).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.FALSE).build();
            //填充列表数据
            excelWriter =  fillData(id, excelName, excelWriter, writeSheet,headerMap,filePath,templateFileName);
            //填充表头
            excelWriter.fill(headerMap, writeSheet);
            //填充图片
            excelWriter.fill(imageMap, writeSheet);
            excelWriter.finish();
            //对已填充数据的文件进行后出来
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

        return sysAttachment.getId()+"?fileName="+sysAttachment.getFileName();
    }


    /**
     * 通用模板数据封装
     * @param id
     * @param type
     * @return 打印文件的minio路径
     */
    public String printPatrolTaskByCommonTpl(String id,String type) {
        PatrolTask patrolTask = patrolTaskMapper.selectById(id);
        List<PatrolTaskStandard> patrolTaskStandard = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>()
                .eq(PatrolTaskStandard::getDelFlag,0).eq(PatrolTaskStandard::getTaskId,patrolTask.getId()));
        PatrolStandard patrolStandard = patrolStandardMapper.selectOne(new LambdaQueryWrapper<PatrolStandard>()
                .eq(PatrolStandard::getDelFlag,0)
                .in(PatrolStandard::getCode,patrolTaskStandard.stream().map(PatrolTaskStandard::getStandardCode).collect(Collectors.toList()))
                .orderByDesc(PatrolStandard::getPrintTemplate).last("LIMIT 1"));
        String excelName = null;
        if (StrUtil.isNotEmpty(patrolStandard.getPrintTemplate())){
            excelName = sysBaseApi.dictById(patrolStandard.getPrintTemplate()).getValue();
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

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 全部放到内存里面 并填充
        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;
        //获取头部数据
        PrintPatrolTaskDTO taskDTO = getHeaderData(patrolTask);
        //填充头部Map
        Map<String, Object> headerMap = getHeaderMap(patrolTask, taskDTO);
        //获取签字图片Map
        Map<String, Object> imageMap = getSignImageMap(taskDTO);

        //查询巡视标准详情
        List<PrintDTO> patrolData = print(id);
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
                FilePrintUtils.printSet(sheet);
                // 对已填充数据的文件进行后处理
                processFilledFile(type, firstColumn, lastColumn, cellByText, filePath, startRow, endRow, workbook, sheet);
            }

            MinioUtil.upload(new FileInputStream(filePath),relatiePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setFileName(fileName);
        sysAttachment.setFilePath(relatiePath);
        sysAttachment.setType("minio");
        sysBaseApi.saveSysAttachment(sysAttachment);
        return sysAttachment.getId()+"?fileName="+sysAttachment.getFileName();
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
     * @param filePath
     * @param templateFileName
     * @return
     */
   private ExcelWriter fillData(String taskId, String excelName, ExcelWriter excelWriter, WriteSheet writeSheet, Map<String, Object> headerMap, String filePath, String templateFileName){
       List<PrintDTO> patrolData = new ArrayList<>();
       if ("safty_produce_check.xlsx".equals(excelName)){
           patrolData = getPrintOthers(taskId,headerMap);
           //填充列表数据
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }else if ("wireless_system.xlsx".equals(excelName) || "wireless_system1.xlsx".equals(excelName)){
           patrolData = getWirelessSystem(taskId,headerMap,excelName);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }else if ("pis_system.xlsx".equals(excelName) || "pis_system1.xlsx".equals(excelName)){
           patrolData = getRemark(taskId,headerMap,excelName);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       } else if("cctv_system.xlsx".equals(excelName)){
           patrolData = getCctvSystem(taskId,headerMap);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }

       return excelWriter;
    }

    private List<PrintDTO> getCctvSystem(String taskId, Map<String, Object> headerMap) {
        List<PrintDTO> getCctvSystem = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(taskId);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            if (CollUtil.isEmpty(dto.getBillInfo())){
                continue;
            }
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll = patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
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
            PrintDTO printDTO = new PrintDTO();
            pisSystem.forEach(str-> {
                PatrolCheckResultDTO patrolCheckResultDTO = parentDTOList.stream().filter(p -> p.getCheck().equals(str)).findFirst().orElse(null);
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
                    childDTOs.forEach(c->{
                        if(c.getCheckResult().equals(0)){
                            flag.set(true);
                            stringBuffer.append(c.getQualityStandard()).append(":异常");
                            stringBuffer.append(",");

                        }
                    });
                    if(flag.get()){
                        stringBuffer.deleteCharAt(stringBuffer.length()-1);
                        printDTO.setRemark(stringBuffer.toString());
                    }else{
                        printDTO.setRemark(null);
                    }
                }
                getCctvSystem.add(printDTO);
            });
        }
        return getCctvSystem;
    }

    /**
     * 网管模板
     * @param id
     * @return
     */
    public String printPatrolTaskByNetworkManage(String id){
        Map<String, Object> fillDataMap = MapUtils.newHashMap();
        fillDataMap.put("设备指示灯-设备指示灯","⬜正常");

        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll =  patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());

        }
          return null;
     //   return sysAttachment.getId()+"?fileName="+sysAttachment.getFileName();
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
     * @param patrolTask
     * @param taskDTO
     * @return
     */
    @NotNull
    private static Map<String, Object> getHeaderMap(PatrolTask patrolTask, PrintPatrolTaskDTO taskDTO) {
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("title", patrolTask.getName());
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
     * @param taskDTO
     * @return
     */
    @NotNull
    private Map<String, Object> getSignImageMap(PrintPatrolTaskDTO taskDTO) {
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
                    WriteCellData writeImageData = FilePrintUtils.writeCellImageData(convert);
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

    private static void processFilledFile(String type, Integer firstColumn, Integer lastColumn, CellAddress cellByText, String filePath, int startRow, int endRow, Workbook workbook, Sheet sheet) throws IOException {
        if ("patrolType8".equals(type)){
            //如果项目列占用多列，则需合并
            if (ObjectUtil.isEmpty(cellByText)){
                FilePrintUtils.setWrapText(workbook,7, startRow, endRow,0,0,false);
            }else{
                FilePrintUtils.setWrapText(workbook,7, startRow, endRow,0,1,false);
            }
            //自动换行
            FilePrintUtils.setWrapText(workbook,7,1,1,1,1,true);

            //合并指定范围行的单元格
            FilePrintUtils.mergeCellsInColumnRange(workbook,true, startRow, endRow, firstColumn, lastColumn);
            FilePrintUtils.mergeCellsInColumnRange(workbook,true, startRow, endRow, lastColumn +1, lastColumn +3);
            //设置列宽
            FilePrintUtils.setColumnWidth(sheet,0,15);
        }else{
            //自动换行
            // setWrapText(workbook,1,startRow,endRow,0,0);
            FilePrintUtils.addReturn(workbook, startRow, endRow,0,0);
            FilePrintUtils.setWrapText(workbook,7,1,1,1,1,true);
            FilePrintUtils.setWrapText(workbook,7, startRow, endRow,1, firstColumn >3?3:2,false);
            //合并指定范围行的单元格
            FilePrintUtils.mergeCellsInColumnRange(workbook,true, startRow, endRow, firstColumn, lastColumn);
            //设置第一列列宽
            FilePrintUtils.setColumnWidth(sheet,0,10);
        }

        // 保存修改后的Excel文件
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
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
            FilePrintUtils.printSet(sheet);

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
    private List<PrintDTO> getPrintOthers(String id, Map<String, Object> map) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll =  patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            boolean result = checkDTOs.stream().anyMatch(f -> f.getContent().contains("电暖气"));
            if (result){
                map.put("isTrue","☑有");
                map.put("isFalse","☐无");
            }else {
                map.put("isFalse","☑无");
                map.put("isTure","☐有");
            }
            AtomicInteger i = new AtomicInteger();
            checkDTOs.forEach(c->{
                i.getAndIncrement();
                PrintDTO printDTO = new PrintDTO();
                if(ObjectUtil.isEmpty(c.getCheckResult())){
                    printDTO.setResult("☐是 ☐否");
                }else {
                    printDTO.setResult(c.getCheckResult()==0?"☐是 ☑否":"☑是 ☐否");
                }
                printDTO.setRemark(c.getRemark());
                if (ObjectUtil.isNotEmpty(c.getQualityStandard())){
                    getPrint.add(printDTO);
                }
                if (!result && i.get()==5 ){
                    printDTO.setResult("☐是 ☐否");
                    getPrint.add(printDTO);
                    getPrint.add(printDTO);
                    getPrint.add(new PrintDTO());
                }
                if (result && i.get()==7){
                    getPrint.add(new PrintDTO());
                }
            });
        }
        return getPrint;
    }



    private List<PrintDTO> getRemark(String id, Map<String,Object> map, String excelName) {
        List<PrintDTO> getRemark = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            if (CollUtil.isEmpty(dto.getBillInfo())){
                continue;
            }
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll = patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            //父级
            List<PatrolCheckResultDTO> parentDTOList = checkResultAll.stream()
                    .filter(c -> Objects.nonNull(c)
                       &&Objects.nonNull(c.getCheck())&& c.getCheck() == 0)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(parentDTOList)) {
                continue;
            }
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
                    childDTOs.forEach(c->{
                        if(c.getCheckResult().equals(0)){
                            flag.set(true);
                            stringBuffer.append(c.getQualityStandard()).append(":异常");
                            stringBuffer.append(",");
                        }
                    });
                    if(flag.get()){
                        map.put("result"+str,"☑是 ☐否");
                        map.put("resultTrue"+str,"☐正常");
                        map.put("resultFalse"+str,"☑异常");
                        stringBuffer.deleteCharAt(stringBuffer.length()-1);
                        map.put("remark"+str,stringBuffer.toString());
//                        printDTO.setResult("☑是 ☐否");
//                        printDTO.setResultTrue("☐正常");
//                        printDTO.setResultFalse("☑异常");
//                        printDTO.setRemark(stringBuffer.toString());
                    }else{
                        map.put("result"+str,"☑是 ☐否");
                        map.put("resultTrue"+str,"☑正常");
                        map.put("resultFalse"+str,"☐异常");
                        map.put("remark"+str,null);
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

    private List<PrintDTO> getWirelessSystem(String id, Map<String, Object> map, String excelName) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        Set<String> set = new LinkedHashSet<>() ;
        StringBuilder text  = new StringBuilder();
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            String str = new String();
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll =  patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            String strName = excelName.substring(0, excelName.indexOf("."));
            List<String> wirelessSystem = sysBaseApi.getDictItems(strName).stream().map(w-> w.getText()).collect(Collectors.toList());
            List<String> result = wirelessSystem.stream().filter(w -> !checkResultAll.stream().anyMatch(c -> c.getContent().equals(w))).collect(Collectors.toList());
            if (!result.isEmpty()){
                str =  result.stream().filter(s -> s.contains("：") || s.contains(":") )
                        .map(s -> s.split("[：:]")[0])
                        .collect(Collectors.joining(",")) + "( 无 )";
                set.add(str);
            }
            checkDTOs.forEach(c-> {
                if(Objects.nonNull(c.getCheckResult())&&c.getCheckResult()==0){
                    text.append("\n").append(c.getContent()).append(":异常");
                }
            });
        }
        map.put("remark","本站 : \n"+set.toString()+text);
        return getPrint;
    }



    /**
     * 获取打印的业务数据
     * @param id
     * @return
     */
    private List<PrintDTO> print(String id) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckByTaskDeviceIdAndParent(collect);
            for (PatrolCheckResultDTO c : checkResultList) {
                List<PatrolCheckResultDTO> list = patrolCheckResultMapper.getQualityStandard(collect,c.getOldId());
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
                    printDTO.setStandard(t.getQualityStandard());
                    printDTO.setEquipment(c.getContent());
                    printDTO.setContent(t.getContent());
                    printDTO.setProcMethods(t.getProcMethods());
                    if(ObjectUtil.isEmpty(t.getCheckResult())){
                        printDTO.setResultTrue("☐正常");
                        printDTO.setResultFalse("☐异常");
                        printDTO.setResult("⬜正常\n⬜异常");
                    }else {
                        printDTO.setResultTrue(t.getCheckResult()==0?"☐正常":"☑正常");
                        printDTO.setResultFalse(t.getCheckResult()==0?"☑异常":"☐异常");
                        printDTO.setResult(t.getCheckResult()==0?"☑正常\n⬜异常":"⬜正常\n☑异常");
                    }
//                    printDTO.setResult(result);
//                    printDTO.setContentRemark(contentRemark);
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

    private List<PrintDTO> getPrint(String id) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
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
}
