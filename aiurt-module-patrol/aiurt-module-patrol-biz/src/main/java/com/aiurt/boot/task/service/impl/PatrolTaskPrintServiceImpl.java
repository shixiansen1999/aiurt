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
        if ("telephone_system1.xlsx".equals(excelName)||"telephone_system.xlsx".equals(excelName)){
           return printPatrolTaskById(id);
        }
        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        // 填充list 的时候还要注意 模板中{.} 多了个点 表示list
        // 如果填充list的对象是map,必须包涵所有list的key,哪怕数据为null，必须使用map.put(key,null)
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);
//        InputStream minioFile = MinioUtil.getMinioFile("platform",templateFileName);
//        try {
////            inputStreamTemplate = new FileInputStream(templateFileName);
//            workbookTpl = WorkbookFactory.create(minioFile);
//            Sheet sheet = workbookTpl.getSheetAt(0);
//            mergeRegion = FilePrintUtils.findMergeRegions(sheet, "巡检标准");
//            firstColumn = mergeRegion.getFirstColumn();
//            lastColumn = mergeRegion.getLastColumn();
//
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        // 全部放到内存里面 并填充
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

//            Workbook workbook = WorkbookFactory.create(filePath);
//            Sheet sheet  = workbook.getSheetAt(0);
//            //打印设置
//            FilePrintUtils.printSet(sheet);

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
       }else if ("wireless_system.xlsx".equals(excelName)){
           patrolData = getWirelessSystem(taskId,headerMap);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }else if ("pis_system.xlsx".equals(excelName)){
           patrolData = getRemark(taskId);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }else if ("wireless_system1.xlsx".equals(excelName)){
           patrolData = getWirelessSystem1(taskId,headerMap);
           excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
       }

       return excelWriter;
    }

    private List<PrintDTO> getWirelessSystem1(String taskId, Map<String, Object> headerMap) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(taskId);
        Set<String> set = new LinkedHashSet<>() ;
        StringBuilder text  = new StringBuilder();
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            String str;
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll =  patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            List<String> wirelessSystem = sysBaseApi.getDictItems("wireless_system1").stream().map(w-> w.getText()).collect(Collectors.toList());
            List<String> result = wirelessSystem.stream().filter(w -> !checkDTOs.stream().anyMatch(c -> c.getContent().equals(w))).collect(Collectors.toList());
            if (!result.isEmpty()){
                str =  result.stream().filter(s -> s.contains("：") || s.contains(":") )
                        .map(s -> s.split("[：:]")[0])
                        .collect(Collectors.joining(",")) + "( 无 )";
                set.add(str);
            }
            checkDTOs.forEach(c-> {
                if(c.getCheckResult()==0){
                    text.append("\n").append(c.getContent()).append(":异常");
                }
            });
        }
        headerMap.put("remark","本站 : \n"+set.toString()+text);
        return getPrint;
    }

    /**
     * 电话系统模板
     * @param patrolTask
     * @param excelName
     * @param patrolData
     * @return
     * @throws IOException
     */
    public String printPatrolTaskTelephoneSystem(PatrolTask patrolTask,String excelName,List<PrintDTO> patrolData) throws IOException {
        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        // 填充list 的时候还要注意 模板中{.} 多了个点 表示list
        // 如果填充list的对象是map,必须包涵所有list的key,哪怕数据为null，必须使用map.put(key,null)
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);
        InputStream minioFile = MinioUtil.getMinioFile("platform",templateFileName);
        Workbook workbookTpl = null;
        CellRangeAddress mergeRegion = null;
        Integer firstColumn = null;
        Integer lastColumn = null;
        try {
            workbookTpl = WorkbookFactory.create(minioFile);
            Sheet sheet = workbookTpl.getSheetAt(0);
            mergeRegion = FilePrintUtils.findMergeRegions(sheet, "巡检标准");
            firstColumn = mergeRegion.getFirstColumn();
            lastColumn = mergeRegion.getLastColumn();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;

        SysAttachment sysAttachment = fillTemplateData(patrolTask,patrolData, templateFileName);
        reProcessFile(patrolData,firstColumn, lastColumn, filePath);

        return sysAttachment.getId()+"?fileName="+sysAttachment.getFileName();
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
     * 填充模板的数据
     * @param patrolTask
     * @param templateFileName
     * @return
     */
    @NotNull
    private SysAttachment fillTemplateData(PatrolTask patrolTask,List<PrintDTO> patrolData, String templateFileName) {
        // 全部放到内存里面 并填充
        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;
        //获取头部数据
        PrintPatrolTaskDTO taskDTO = getHeaderData(patrolTask);
        //填充头部Map
        Map<String, Object> map = getHeaderMap(patrolTask, taskDTO);
        //文件打印签名
        Map<String, Object> imageMap = getSignImageMap(taskDTO);

        //查询巡视标准详情
//        List<PrintDTO> patrolData = getPrint(id,map);
        InputStream minioFile2 = MinioUtil.getMinioFile("platform", templateFileName);
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).withTemplate(minioFile2).build()) {
            int[] mergeColumnIndex = {0,1,2};
            CustomCellMergeHandler customCellMergeStrategy = new CustomCellMergeHandler(3,mergeColumnIndex);
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(customCellMergeStrategy).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.FALSE).build();
            //填充列表数据
            excelWriter.fill(new FillWrapper("list",patrolData),fillConfig, writeSheet);
            //填充表头
            excelWriter.fill(map, writeSheet);
            //填充图片
            excelWriter.fill(imageMap, writeSheet);
            excelWriter.finish();

            MinioUtil.upload(new FileInputStream(filePath),relatiePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setFileName(fileName);
        sysAttachment.setFilePath(relatiePath);
        sysAttachment.setType("minio");
        sysBaseApi.saveSysAttachment(sysAttachment);
        return sysAttachment;
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
     * 再处理模板数据
     * @param patrolData
     * @param firstColumn
     * @param lastColumn
     * @param filePath
     * @throws IOException
     */
    private static void reProcessFile(List<PrintDTO> patrolData,Integer firstColumn, Integer lastColumn, String filePath) throws IOException {
        int startRow = 3;
        int endRow = startRow;
        if (!ObjectUtil.isEmpty(patrolData)){
            endRow = startRow+patrolData.size()-1;
        }
        try (InputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            FilePrintUtils.printSet(sheet);
            //自动换行
           // setWrapText(workbook,1,startRow,endRow,0,0);
            FilePrintUtils.addReturn(workbook, startRow, endRow,0,0);
            FilePrintUtils.setWrapText(workbook,7,1,1,1,1,true);
            FilePrintUtils.setWrapText(workbook,7, startRow, endRow,1, firstColumn >3?3:2,false);
            //合并指定范围行的单元格
            FilePrintUtils.mergeCellsInColumnRange(workbook,40, startRow, endRow, firstColumn, lastColumn);

            //设置第一列列宽
            FilePrintUtils.setColumnWidth(sheet,0,10);
            // 保存修改后的Excel文件
            try (OutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }


    @NotNull
    private Map<String, Object> getSignImageMap(PrintPatrolTaskDTO taskDTO) {
        Map<String, Object> imageMap = MapUtils.newHashMap();
        if(StrUtil.isNotEmpty(taskDTO.getSignUrl())){
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
    private List<PrintDTO> getRemark(String id) {
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
                    .filter(c -> Objects.nonNull(c) && c.getHierarchyType() == 0).collect(Collectors.toList());
            if (CollUtil.isEmpty(parentDTOList)) {
                continue;
            }
            for (PatrolCheckResultDTO parentDTO : parentDTOList) {
                PrintDTO printDTO = new PrintDTO();
                String oldId = parentDTO.getOldId();
                StringBuffer stringBuffer = new StringBuffer();
                AtomicBoolean flag = new AtomicBoolean(false);
                //子级
                List<PatrolCheckResultDTO> childDTOs =  checkDTOs.stream()
                        .filter(c -> c.getHierarchyType() == 1)
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
                    printDTO.setResultTrue("☐正常");
                    printDTO.setResultFalse("☑异常");
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                    printDTO.setRemark(stringBuffer.toString());
                }else{
                    printDTO.setResultTrue("☑正常");
                    printDTO.setResultFalse("☐异常");
                }
                getRemark.add(printDTO);
            }
        }
        return getRemark;
    }

    private List<PrintDTO> getWirelessSystem(String id,Map<String, Object> map) {
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
            List<String> wirelessSystem = sysBaseApi.getDictItems("wireless_system").stream().map(w-> w.getText()).collect(Collectors.toList());
            List<String> result = wirelessSystem.stream().filter(w -> !checkDTOs.stream().anyMatch(c -> c.getContent().equals(w))).collect(Collectors.toList());
            if (!result.isEmpty()){
                str =  result.stream().filter(s -> s.contains("：") || s.contains(":") )
                        .map(s -> s.split("[：:]")[0])
                        .collect(Collectors.joining(",")) + "( 无 )";
                set.add(str);
            }
            checkDTOs.forEach(c-> {
                if(c.getCheckResult()==0){
                    text.append("\n").append(c.getContent()).append(":异常");
                }
            });
        }
        map.put("remark","本站 : \n"+set.toString()+text);
        return getPrint;
    }
    public String printPatrolTaskById(String id) {
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

        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        // 填充list 的时候还要注意 模板中{.} 多了个点 表示list
        // 如果填充list的对象是map,必须包涵所有list的key,哪怕数据为null，必须使用map.put(key,null)
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);
        InputStream minioFile = MinioUtil.getMinioFile("platform",templateFileName);
        Workbook workbookTpl = null;
        CellRangeAddress mergeRegion = null;
        Integer firstColumn = null;
        Integer lastColumn = null;
        try {
//            inputStreamTemplate = new FileInputStream(templateFileName);
            workbookTpl = WorkbookFactory.create(minioFile);
            Sheet sheet = workbookTpl.getSheetAt(0);
            mergeRegion = FilePrintUtils.findMergeRegions(sheet, "巡检标准");
            firstColumn = mergeRegion.getFirstColumn();
            lastColumn = mergeRegion.getLastColumn();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 全部放到内存里面 并填充
        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;
        // 这里 会填充到第一个sheet， 然后文件流会自动关闭
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
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("title",patrolTask.getName());
        map.put("patrolStation", taskDTO.getStationNames());
        map.put("patrolPerson", taskDTO.getUserName());
        map.put("checkUserName",taskDTO.getSpotCheckUserName());
        map.put("patrolDate", DateUtil.format(patrolTask.getSubmitTime(),"yyyy-MM-dd"));
        map.put("patrolTime", DateUtil.format(patrolTask.getSubmitTime(),"HH:mm"));
        Map<String, Object> imageMap = MapUtils.newHashMap();
        if(StrUtil.isNotEmpty(taskDTO.getSignUrl())){
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

//        String imagePath = "C:\\Users\\14719\\Desktop\\1685182072119.jpg";
//        // 设置图片数据
//        File[] files = {new File(imagePath)};


        //查询巡视标准详情
        List<PrintDTO> patrolData = print(id);
        InputStream minioFile2 = MinioUtil.getMinioFile("platform",templateFileName);
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).withTemplate(minioFile2).build()) {
            int[] mergeColumnIndex = {0,1,2};
            CustomCellMergeHandler customCellMergeStrategy = new CustomCellMergeHandler(3,mergeColumnIndex);
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(customCellMergeStrategy).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            //填充列表数据
            excelWriter.fill(new FillWrapper("list",patrolData),fillConfig, writeSheet);
            //填充表头
            excelWriter.fill(map, writeSheet);
            //填充图片
            excelWriter.fill(imageMap, writeSheet);
            excelWriter.finish();
            int startRow = 3;
            int endRow = startRow;
            if (CollUtil.isNotEmpty(patrolData)){
                endRow = startRow+patrolData.size()-1;
            }

            try (InputStream inputStream = new FileInputStream(filePath);
                 Workbook workbook = WorkbookFactory.create(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);
//                sheet.setMargin(Sheet.TopMargin, 0.5); // 上边距
//                sheet.setMargin(Sheet.BottomMargin, 0.5); // 下边距
//                sheet.setMargin(Sheet.LeftMargin, 1); // 左边距
//                sheet.setMargin(Sheet.RightMargin, 1); // 右边距
                FilePrintUtils.printSet(sheet);
                // 设置边距（单位为英寸）
                // 设置打印边距
                //自动换行
                // setWrapText(workbook,1,startRow,endRow,0,0);
                FilePrintUtils.addReturn(workbook,startRow,endRow,0,0);
                FilePrintUtils.setWrapText(workbook,7,1,1,1,1,true);
                FilePrintUtils.setWrapText(workbook,7,startRow,endRow,1,firstColumn>3?3:2,false);
                //合并指定范围行的单元格
                FilePrintUtils.mergeCellsInColumnRange(workbook,40,startRow,endRow,firstColumn,lastColumn);

                //设置第一列列宽
                FilePrintUtils.setColumnWidth(sheet,0,10);
                // 保存修改后的Excel文件
                try (OutputStream outputStream = new FileOutputStream(filePath)) {
                    workbook.write(outputStream);
                }
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

    private List<PrintDTO> print(String id) {
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
