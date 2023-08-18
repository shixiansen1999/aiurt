package com.aiurt.modules.train.trainarchive.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.train.trainarchive.dto.ArchiveExcelDTO;
import com.aiurt.modules.train.trainarchive.dto.RecordData;
import com.aiurt.modules.train.trainarchive.dto.RecordExcelListener;
import com.aiurt.modules.train.trainarchive.dto.TrainArchiveDTO;
import com.aiurt.modules.train.trainarchive.entity.TrainArchive;
import com.aiurt.modules.train.trainarchive.mapper.TrainArchiveMapper;
import com.aiurt.modules.train.trainarchive.service.ITrainArchiveService;
import com.aiurt.modules.train.traindegreerecord.dto.DegreeRecordExcelDTO;
import com.aiurt.modules.train.traindegreerecord.entity.TrainDegreeRecord;
import com.aiurt.modules.train.traindegreerecord.service.ITrainDegreeRecordService;
import com.aiurt.modules.train.trainjobchangerecord.dto.JobChangeRecordExcelDTO;
import com.aiurt.modules.train.trainjobchangerecord.entity.TrainJobChangeRecord;
import com.aiurt.modules.train.trainjobchangerecord.service.ITrainJobChangeRecordService;
import com.aiurt.modules.train.trainrecord.dto.RecordExcelDTO;
import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
import com.aiurt.modules.train.trainrecord.service.ITrainRecordService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: train_archive
 * @Author: aiurt
 * @Date: 2023-06-25
 * @Version: V1.0
 */
@Service
public class TrainArchiveServiceImpl extends ServiceImpl<TrainArchiveMapper, TrainArchive> implements ITrainArchiveService {

    @Autowired
    private TrainArchiveMapper archiveMapper;
    @Autowired
    private ITrainDegreeRecordService degreeRecordService;
    @Autowired
    private ITrainJobChangeRecordService changeRecordService;
    @Autowired
    private ITrainRecordService recordService;
    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> add(TrainArchiveDTO trainArchiveDTO) {
        TrainArchive trainArchive = archiveMapper.selectOne(new LambdaQueryWrapper<TrainArchive>()
                .eq(TrainArchive::getUserId, trainArchiveDTO.getUserId()).eq(TrainArchive::getDelFlag,CommonConstant.DEL_FLAG_0));
        if (ObjectUtil.isNotEmpty(trainArchive)) {
            throw new AiurtBootException("系统已添加过该用户！");
        }
        //添加主表
        TrainArchive archive = new TrainArchive();
        BeanUtils.copyProperties(trainArchiveDTO, archive);
        archiveMapper.insert(archive);
        trainArchiveDTO.setId(archive.getId());
        saveArchive(trainArchiveDTO);
        return Result.OK("添加成功!");
    }

    private void saveArchive(TrainArchiveDTO trainArchiveDTO) {
        //添加学历
        TrainDegreeRecord firstDegree = trainArchiveDTO.getFirstDegree();
        if (ObjectUtil.isNotEmpty(firstDegree)) {
            firstDegree.setTrainArchiveId(trainArchiveDTO.getId());
            firstDegree.setDegreeType(0);
            degreeRecordService.save(firstDegree);
        }
        TrainDegreeRecord highestDegree = trainArchiveDTO.getHighestDegree();
        if (ObjectUtil.isNotEmpty(highestDegree)) {
            highestDegree.setTrainArchiveId(trainArchiveDTO.getId());
            highestDegree.setDegreeType(1);
            degreeRecordService.save(highestDegree);
        }
        //添加岗位变动
        List<TrainJobChangeRecord> changeRecordList = trainArchiveDTO.getChangeRecordList();
        if (CollUtil.isNotEmpty(changeRecordList)) {
            changeRecordList.forEach(c -> c.setTrainArchiveId(trainArchiveDTO.getId()));
            changeRecordService.saveBatch(changeRecordList);
        }
        //添加培训记录
        List<TrainRecord> trainRecordList = trainArchiveDTO.getTrainRecordList();
        if (CollUtil.isNotEmpty(trainRecordList)) {
            trainRecordList.forEach(t -> t.setTrainArchiveId(trainArchiveDTO.getId()));
            recordService.saveBatch(trainRecordList);
        }
    }

    @Override
    public Result<String> edit(TrainArchiveDTO trainArchiveDTO) {
        TrainArchive trainArchive = archiveMapper.selectOne(new LambdaQueryWrapper<TrainArchive>()
                .eq(TrainArchive::getUserId, trainArchiveDTO.getUserId())
                .eq(TrainArchive::getDelFlag, CommonConstant.DEL_FLAG_0)
                .ne(TrainArchive::getId, trainArchiveDTO.getId()));
        if (ObjectUtil.isNotEmpty(trainArchive)) {
            throw new AiurtBootException("系统已添加过该用户！");
        }
        //删除学历
        List<TrainDegreeRecord> degreeRecordList = degreeRecordService.list(new LambdaQueryWrapper<TrainDegreeRecord>()
                .eq(TrainDegreeRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(TrainDegreeRecord::getTrainArchiveId, trainArchiveDTO.getId()));
        if (CollUtil.isNotEmpty(degreeRecordList)) {
            degreeRecordService.removeByIds(degreeRecordList);
        }
        //删除岗位变动
        List<TrainJobChangeRecord> jobChangeRecords = changeRecordService.list(
                new LambdaQueryWrapper<TrainJobChangeRecord>()
                        .eq(TrainJobChangeRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(TrainJobChangeRecord::getTrainArchiveId, trainArchiveDTO.getId()));
        if (CollUtil.isNotEmpty(jobChangeRecords)) {
            changeRecordService.removeByIds(jobChangeRecords);
        }
        //删除培训记录
        List<TrainRecord> recordList = recordService.list(new LambdaQueryWrapper<TrainRecord>()
                .eq(TrainRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(TrainRecord::getTrainArchiveId, trainArchiveDTO.getId()));
        if (CollUtil.isNotEmpty(recordList)) {
            recordService.removeByIds(recordList);
        }
        //更新主表
        TrainArchive archive = new TrainArchive();
        BeanUtils.copyProperties(trainArchiveDTO, archive);
        archiveMapper.updateById(archive);
        saveArchive(trainArchiveDTO);
        return Result.OK("编辑成功!");
    }

    @Override
    public IPage<TrainArchiveDTO> pageList(Page<TrainArchiveDTO> page, TrainArchiveDTO trainArchiveDTO) {
        if (ObjectUtil.isNotEmpty(trainArchiveDTO.getOrgCode())) {
            boolean b = GlobalThreadLocal.setDataFilter(true);
            List<String> sysDepartList = iSysBaseApi.getSysDepartList(trainArchiveDTO.getOrgCode());
            GlobalThreadLocal.setDataFilter(b);
            trainArchiveDTO.setOrgCodeList(sysDepartList);
        }
        List<TrainArchiveDTO> list = archiveMapper.pageList(page, trainArchiveDTO);
        return page.setRecords(list);
    }

    @Override
    public Result<String> delete(String id) {
        //删除学历
        List<TrainDegreeRecord> degreeRecordList = degreeRecordService.list(
                new LambdaQueryWrapper<TrainDegreeRecord>().eq(TrainDegreeRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(TrainDegreeRecord::getTrainArchiveId, id));
        if (CollUtil.isNotEmpty(degreeRecordList)) {
            degreeRecordList.forEach(f -> f.setDelFlag(1));
            degreeRecordService.updateBatchById(degreeRecordList);
        }
        //删除岗位变动
        List<TrainJobChangeRecord> jobChangeRecords = changeRecordService.list(
                new LambdaQueryWrapper<TrainJobChangeRecord>()
                        .eq(TrainJobChangeRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(TrainJobChangeRecord::getTrainArchiveId, id));
        if (CollUtil.isNotEmpty(jobChangeRecords)) {
            jobChangeRecords.forEach(j -> j.setDelFlag(1));
            changeRecordService.updateBatchById(jobChangeRecords);
        }
        //删除培训记录
        List<TrainRecord> recordList = recordService.list(new LambdaQueryWrapper<TrainRecord>()
                .eq(TrainRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(TrainRecord::getTrainArchiveId, id));
        if (CollUtil.isNotEmpty(recordList)) {
            recordList.forEach(r -> r.setDelFlag(1));
            recordService.updateBatchById(recordList);
        }
        //删除主表
        TrainArchive archive = archiveMapper.selectOne(new LambdaQueryWrapper<TrainArchive>().eq(TrainArchive::getId, id));
        archive.setDelFlag(1);
        archiveMapper.updateById(archive);
        return Result.OK("删除成功!");
    }

    @Override
    public Result<TrainArchiveDTO> queryById(String id) {
        TrainArchiveDTO archiveDTO = archiveMapper.queryById(id);
        //学历
        List<TrainDegreeRecord> degreeRecordList = degreeRecordService.getList(id);
        if (CollUtil.isNotEmpty(degreeRecordList)) {
            List<TrainDegreeRecord> firstDegreeList = degreeRecordList.stream().filter(d -> 0 == d.getDegreeType()).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(firstDegreeList)) {
                archiveDTO.setFirstDegree(firstDegreeList.get(0));
            }
            List<TrainDegreeRecord> highestDegreeList = degreeRecordList.stream().filter(d -> 1 == d.getDegreeType()).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(highestDegreeList)) {
                archiveDTO.setHighestDegree(highestDegreeList.get(0));
            }
        }
        //岗位变动
        List<TrainJobChangeRecord> changeRecordList = changeRecordService.getList(id);
        if (CollUtil.isNotEmpty(changeRecordList)) {
            archiveDTO.setChangeRecordList(changeRecordList);
        }
        //培训记录
        List<TrainRecord> recordList = recordService.getList(id);
        if (CollUtil.isNotEmpty(recordList)) {
            archiveDTO.setTrainRecordList(recordList);
        }
        return Result.OK(archiveDTO);
    }

    @Override
    public Result<String> exportXls(HttpServletRequest request, HttpServletResponse response, TrainArchiveDTO trainArchiveDTO) throws IOException {
        List<TrainArchiveDTO> list = archiveMapper.pageList(null, trainArchiveDTO);
        TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/trainarchiveexport.xlsx");
        OutputStream outputStream = response.getOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (TrainArchiveDTO archiveDTO : list) {
            Map<String, Object> errorMap = CollUtil.newHashMap();
            List<Map<String, String>> firstDegreeList = CollUtil.newArrayList();
            List<Map<String, String>> highestDegreeList = CollUtil.newArrayList();
            List<Map<String, String>> changeRecordList = CollUtil.newArrayList();
            List<Map<String, String>> trainRecordList = CollUtil.newArrayList();
            errorMap.put("realname", archiveDTO.getRealname());
            errorMap.put("salaryCode", archiveDTO.getSalaryCode());
            errorMap.put("sexName", "男");
            errorMap.put("birthday", DateUtil.format(archiveDTO.getBirthday(), "yyyy-MM-dd"));
            errorMap.put("workingTime", DateUtil.format(archiveDTO.getWorkingTime(), "yyyy-MM-dd"));
            errorMap.put("entryDate", DateUtil.format(archiveDTO.getEntryDate(), "yyyy-MM-dd"));
            List<TrainDegreeRecord> degreeRecords = degreeRecordService.getList(archiveDTO.getId());
            if (CollUtil.isNotEmpty(degreeRecords)) {
                for (TrainDegreeRecord degreeRecord : degreeRecords) {
                    Map<String, String> map = new HashMap<>(32);
                    map.put("teDegreeName", "学历");
                    map.put("degreeName", degreeRecord.getDegreeName());
                    map.put("teGraduationForm", "毕业形式");
                    map.put("graduationFormName", degreeRecord.getGraduationFormName());
                    map.put("teGraduationTime", "毕业时间");
                    map.put("graduationTime", DateUtil.format(degreeRecord.getGraduationTime(), "yyyy-MM-dd"));
                    map.put("teSchool", "毕业学校");
                    map.put("school", degreeRecord.getGraduationSchool());
                    map.put("teMajor", "所学专业");
                    map.put("major", degreeRecord.getMajorsStudied());
                    if (1 == degreeRecord.getDegreeType()) {
                        highestDegreeList.add(map);
                    } else {
                        firstDegreeList.add(map);
                    }
                }
            }
            List<TrainJobChangeRecord> jobChangeRecords = changeRecordService.getList(archiveDTO.getId());
            if (CollUtil.isNotEmpty(jobChangeRecords)) {
                for (TrainJobChangeRecord changeRecord : jobChangeRecords) {
                    Map<String, String> map = new HashMap<>(32);
                    map.put("jobTime", DateUtil.format(changeRecord.getJobTime(), "yyyy-MM-dd"));
                    map.put("departName", changeRecord.getDepartName());
                    map.put("jorName", changeRecord.getJobName());
                    map.put("jobGrade", String.valueOf(changeRecord.getJobGrade()));
                    changeRecordList.add(map);
                }
            }
            List<TrainRecord> recordList = recordService.getList(archiveDTO.getId());
            if (CollUtil.isNotEmpty(recordList)) {
                for (TrainRecord trainRecord : recordList) {
                    Map<String, String> map = new HashMap<>(32);
                    map.put("trainTime", DateUtil.format(trainRecord.getTrainTime(), "yyyy-MM-dd"));
                    map.put("trainContent", trainRecord.getTrainContent());
                    map.put("taskGradeName", trainRecord.getTaskGradeName());
                    map.put("hour", String.valueOf(trainRecord.getHour()));
                    map.put("checkGrade", trainRecord.getCheckGrade());
                    map.put("isAnnualPlanName", trainRecord.getIsAnnualPlanName());
                    map.put("taskCode", trainRecord.getTaskCode());
                    trainRecordList.add(map);
                }
            }
            errorMap.put("firstDegreeList", firstDegreeList);
            errorMap.put("highestDegreeList", highestDegreeList);
            errorMap.put("changeRecordList", changeRecordList);
            errorMap.put("trainRecordList", trainRecordList);
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(32);
            sheetsMap.put(0, errorMap);
            Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            String fileName = archiveDTO.getRealname() + "" + "(个人档案).xlsx";
            try {
                ZipEntry entry = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(entry);
                workbook.write(zipOutputStream);
                zipOutputStream.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        zipOutputStream.close();
        outputStream.flush();
        outputStream.close();
        return Result.OK("导出成功!");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        //成功条数
        Integer successLines = 0;
        // 失败条数
        Integer errorLines = 0;
        // 失败导出的excel下载地址
        String failReportUrl = "";
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            // 判断是否xls、xlsx两种类型的文件，不是则直接返回
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return importReturnRes(errorLines, successLines, false, failReportUrl, "文件导入失败，文件类型不对");
            }
            RecordExcelListener recordExcelListener = new RecordExcelListener();
            //读取数据
            try {
                EasyExcel.read(file.getInputStream(), RecordData.class, recordExcelListener).sheet().doRead();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArchiveExcelDTO excelDTO = recordExcelListener.getExcelDTO();
            if (ObjectUtil.isEmpty(excelDTO.getWorkNo()) && ObjectUtil.isEmpty(excelDTO.getRealname())) {
                return Result.error("文件导入失败:数据为空！");
            }
            DegreeRecordExcelDTO firstDegree = excelDTO.getFirstDegree();
            DegreeRecordExcelDTO highestDegree = excelDTO.getHighestDegree();
            List<JobChangeRecordExcelDTO> changeRecordList = excelDTO.getChangeRecordList();
            List<RecordExcelDTO> recordList = excelDTO.getTrainRecordList();
            if (judgeObjIsNull(firstDegree)) {
                excelDTO.setFirstMistake(null);
            }
            if (judgeObjIsNull(highestDegree)) {
                excelDTO.setHighestMistake(null);
            }
            if (CollUtil.isNotEmpty(changeRecordList)) {
                Iterator<JobChangeRecordExcelDTO> iterator = changeRecordList.iterator();
                if (CollUtil.isNotEmpty(iterator)) {
                    while (iterator.hasNext()) {
                        JobChangeRecordExcelDTO model = iterator.next();
                        boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                        if (a) {
                            iterator.remove();
                        }
                    }
                }
            }
            if (CollUtil.isNotEmpty(recordList)) {
                Iterator<RecordExcelDTO> iterator = recordList.iterator();
                if (CollUtil.isNotEmpty(iterator)) {
                    while (iterator.hasNext()) {
                        RecordExcelDTO model = iterator.next();
                        boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                        if (a) {
                            iterator.remove();
                        }
                    }
                }
            }
            if (ObjectUtil.isNotEmpty(excelDTO.getArchiveMistake()) || ObjectUtil.isNotEmpty(excelDTO.getFirstMistake())
                    || ObjectUtil.isNotEmpty(excelDTO.getHighestMistake()) || ObjectUtil.isNotEmpty(excelDTO.getChangeMistake())
                    || ObjectUtil.isNotEmpty(excelDTO.getRecordMistake())) {
                String archiveMistake = excelDTO.getArchiveMistake() == null ? "" : excelDTO.getArchiveMistake().substring(0, excelDTO.getArchiveMistake().length() - 1);
                String firstMistake = excelDTO.getFirstMistake() == null ? "" : excelDTO.getFirstMistake().substring(0, excelDTO.getFirstMistake().length() - 1);
                String highestMistake = excelDTO.getHighestMistake() == null ? "" : excelDTO.getHighestMistake().substring(0, excelDTO.getHighestMistake().length() - 1);
                String changeMistake = excelDTO.getChangeMistake() == null ? "" : excelDTO.getChangeMistake().substring(0, excelDTO.getChangeMistake().length() - 1);
                String recordMistake = excelDTO.getRecordMistake() == null ? "" : excelDTO.getRecordMistake().substring(0, excelDTO.getRecordMistake().length() - 1);
                excelDTO.setArchiveMistake(archiveMistake);
                excelDTO.setFirstMistake(firstMistake);
                excelDTO.setHighestMistake(highestMistake);
                excelDTO.setChangeMistake(changeMistake);
                excelDTO.setRecordMistake(recordMistake);
                excelDTO.getFirstDegree().setDegreeType(0);
                excelDTO.getHighestDegree().setDegreeType(1);
                return getErrorExcel(excelDTO.getErrorLines(), excelDTO.getSuccessLines(), excelDTO, failReportUrl, type);
            } else {
                StringBuilder archiveMistake = new StringBuilder();
                TrainArchive archive = new TrainArchive();
                List<TrainDegreeRecord> degreeRecords = new ArrayList<>();
                List<TrainJobChangeRecord> jobChangeRecords = new ArrayList<>();
                List<TrainRecord> trainRecordList = new ArrayList<>();
                errorLines = checkArchive(archiveMistake, excelDTO, archive, errorLines);
                errorLines = checkDegree(firstDegree, degreeRecords, 0, errorLines);
                errorLines = checkDegree(highestDegree, degreeRecords, 1, errorLines);
                errorLines = checkChangeRecord(changeRecordList, jobChangeRecords, errorLines);
                errorLines = checkTrainRecord(recordList, trainRecordList, errorLines);
                if (errorLines > 0) {
                    return getErrorExcel(errorLines, excelDTO.getSuccessLines() - errorLines, excelDTO, failReportUrl, type);
                } else {
                    //保存主表
                    archiveMapper.insert(archive);
                    //添加学历
                    if (CollUtil.isNotEmpty(degreeRecords)) {
                        degreeRecords.forEach(c -> c.setTrainArchiveId(archive.getId()));
                        degreeRecordService.saveBatch(degreeRecords);
                    }
                    //添加岗位变动
                    if (CollUtil.isNotEmpty(jobChangeRecords)) {
                        jobChangeRecords.forEach(c -> c.setTrainArchiveId(archive.getId()));
                        changeRecordService.saveBatch(jobChangeRecords);
                    }
                    //添加培训记录
                    if (CollUtil.isNotEmpty(trainRecordList)) {
                        trainRecordList.forEach(t -> t.setTrainArchiveId(archive.getId()));
                        recordService.saveBatch(trainRecordList);
                    }
                    return importReturnRes(errorLines, excelDTO.getSuccessLines(), true, null, "文件导入成功！");
                }

            }

        }
        return null;
    }

    private Result<?> getErrorExcel(Integer errorLines, Integer successLines, ArchiveExcelDTO excelDTO, String failReportUrl, String type) throws IOException {
        Resource resource = new ClassPathResource("/templates/trainarchiveerror.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/trainarchiveerror.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<String, Object> errorMap = new HashMap<>(16);
        List<Map<String, String>> listMap = new ArrayList<>();
        errorMap.put("realname", excelDTO.getRealname());
        errorMap.put("workNo", excelDTO.getWorkNo());
        errorMap.put("archiveMistake", excelDTO.getArchiveMistake());
        List<Map<String, String>> firstDegreeList = CollUtil.newArrayList();
        List<Map<String, String>> highestDegreeList = CollUtil.newArrayList();
        List<Map<String, String>> changeRecordList = CollUtil.newArrayList();
        List<Map<String, String>> trainRecordList = CollUtil.newArrayList();
        List<DegreeRecordExcelDTO> degreeRecords = new ArrayList<>();
        DegreeRecordExcelDTO firstDegree = excelDTO.getFirstDegree();
        DegreeRecordExcelDTO highestDegree = excelDTO.getHighestDegree();
        if (ObjectUtil.isNotEmpty(firstDegree)) {
            degreeRecords.add(firstDegree);
        }
        if (ObjectUtil.isNotEmpty(highestDegree)) {
            degreeRecords.add(highestDegree);
        }

        if (CollUtil.isNotEmpty(degreeRecords)) {
            for (DegreeRecordExcelDTO degreeRecordExcelDTO : degreeRecords) {
                Map<String, String> map = new HashMap<>(32);
                map.put("teDegreeName", "学历");
                map.put("degreeName", degreeRecordExcelDTO.getDegreeName());
                map.put("teGraduationForm", "毕业形式");
                map.put("graduationFormName", degreeRecordExcelDTO.getGraduationFormName());
                map.put("teGraduationTime", "毕业时间");
                map.put("graduationTime", degreeRecordExcelDTO.getSchoolEndTime());
                map.put("teSchool", "毕业学校");
                map.put("school", degreeRecordExcelDTO.getGraduationSchool());
                map.put("teMajor", "所学专业");
                map.put("major", degreeRecordExcelDTO.getMajorsStudied());
                if (1 == degreeRecordExcelDTO.getDegreeType()) {
                    if (ObjectUtil.isNotEmpty(excelDTO.getHighestMistake())) {
                        errorMap.put("highestMistake", excelDTO.getHighestMistake());
                    } else {
                        errorMap.put("highestMistake", degreeRecordExcelDTO.getChangeMistake());
                    }
                    highestDegreeList.add(map);
                } else {
                    if (ObjectUtil.isNotEmpty(excelDTO.getFirstMistake())) {
                        errorMap.put("firstMistake", excelDTO.getFirstMistake());
                    } else {
                        errorMap.put("firstMistake", degreeRecordExcelDTO.getChangeMistake());
                    }
                    firstDegreeList.add(map);
                }
            }
        }
        List<JobChangeRecordExcelDTO> jobChangeRecords = excelDTO.getChangeRecordList();
        if (CollUtil.isNotEmpty(jobChangeRecords)) {
            for (JobChangeRecordExcelDTO changeRecordExcelDTO : jobChangeRecords) {
                Map<String, String> map = new HashMap<>(32);
                map.put("jobTime", changeRecordExcelDTO.getExcelJobTime());
                map.put("departName", changeRecordExcelDTO.getDepartName());
                map.put("jorName", changeRecordExcelDTO.getJobName());
                map.put("jobGrade", changeRecordExcelDTO.getJobGradeName());
                if (ObjectUtil.isNotEmpty(excelDTO.getChangeMistake())) {
                    map.put("changeMistake", excelDTO.getChangeMistake());
                } else {
                    map.put("changeMistake", changeRecordExcelDTO.getChangeMistake());
                }
                changeRecordList.add(map);
            }
        }
        List<RecordExcelDTO> recordList = excelDTO.getTrainRecordList();
        if (CollUtil.isNotEmpty(recordList)) {
            for (RecordExcelDTO recordExcelDTO : recordList) {
                Map<String, String> map = new HashMap<>(32);
                map.put("trainTime", recordExcelDTO.getExcelTrainTime());
                map.put("trainContent", recordExcelDTO.getTrainContent());
                map.put("taskGradeName", recordExcelDTO.getTaskGradeName());
                map.put("hour", recordExcelDTO.getExcelHour());
                map.put("checkGrade", recordExcelDTO.getCheckGrade());
                map.put("isAnnualPlanName", recordExcelDTO.getIsAnnualPlanName());
                map.put("taskCode", recordExcelDTO.getTaskCode());
                if (ObjectUtil.isNotEmpty(excelDTO.getRecordMistake())) {
                    map.put("recordMistake", excelDTO.getRecordMistake());
                } else {
                    map.put("recordMistake", recordExcelDTO.getRecordMistake());
                }
                trainRecordList.add(map);
            }
        }
        errorMap.put("firstDegreeList", firstDegreeList);
        errorMap.put("highestDegreeList", highestDegreeList);
        errorMap.put("changeRecordList", changeRecordList);
        errorMap.put("trainRecordList", trainRecordList);
        errorMap.put("maplist", listMap);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        sheetsMap.put(0, errorMap);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        try {
            String fileName = "个人档案导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            failReportUrl = fileName;
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return importReturnRes(errorLines, successLines, false, failReportUrl, "文件失败，数据有错误。");

    }

    private Integer checkTrainRecord(List<RecordExcelDTO> recordList, List<TrainRecord> trainRecordList, Integer errorLines) {
        if (CollUtil.isNotEmpty(recordList)) {
            List<DictModel> taskGrades = iSysBaseApi.getDictItems("training_classification");
            List<DictModel> plans = iSysBaseApi.getDictItems("is_annual_plan");
            Map<String, DictModel> taskGradeMap = taskGrades.stream().collect(Collectors.toMap(DictModel::getText, Function.identity(), (key1, key2) -> key2));
            Map<String, DictModel> plansMap = plans.stream().collect(Collectors.toMap(DictModel::getText, Function.identity(), (key1, key2) -> key2));
            for (RecordExcelDTO trainRecord : recordList) {
                StringBuilder recordMistake = new StringBuilder();
                TrainRecord record = new TrainRecord();
                if (ObjectUtil.isNotEmpty(trainRecord.getExcelTrainTime()) && ObjectUtil.isNotEmpty(trainRecord.getTrainContent())
                        && ObjectUtil.isNotEmpty(trainRecord.getTaskGradeName()) && ObjectUtil.isNotEmpty(trainRecord.getExcelHour())
                        && ObjectUtil.isNotEmpty(trainRecord.getCheckGrade()) && ObjectUtil.isNotEmpty(trainRecord.getIsAnnualPlanName())
                        && ObjectUtil.isNotEmpty(trainRecord.getTaskCode())) {
                    BeanUtils.copyProperties(trainRecord, record);
                    boolean legalDate = isLegalDate(trainRecord.getExcelTrainTime().length(), trainRecord.getExcelTrainTime());
                    if (legalDate) {
                        record.setTrainTime(DateUtil.parse(trainRecord.getExcelTrainTime(), "yyyy-MM-dd"));
                    } else {
                        recordMistake.append("培训时间填写有误，");
                    }
                    DictModel taskGrade = taskGradeMap.get(trainRecord.getTaskGradeName());
                    DictModel plan = plansMap.get(trainRecord.getIsAnnualPlanName());
                    if (ObjectUtil.isNotEmpty(taskGrade)) {
                        record.setTaskGrade(Integer.valueOf(taskGrade.getValue()));
                    } else {
                        recordMistake.append("培训分级填写有误，");

                    }
                    if (ObjectUtil.isNotEmpty(plan)) {
                        record.setIsAnnualPlan(Integer.valueOf(plan.getValue()));
                    } else {
                        recordMistake.append("是否为计划内填写有误，");

                    }
                    String pattern = "^[0-9]*$";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(trainRecord.getExcelHour());
                    if (m.find()) {
                        record.setHour(Integer.valueOf(trainRecord.getExcelHour()));
                    } else {
                        recordMistake.append("考核成绩填写有误，");
                    }
                    trainRecordList.add(record);
                    if (recordMistake.length() > 0) {
                        // 截取字符
                        recordMistake = recordMistake.deleteCharAt(recordMistake.length() - 1);
                        trainRecord.setRecordMistake(recordMistake.toString());
                        errorLines++;
                    }
                } else {
                    recordMistake.append("培训时间、培训内容、培训分级、课时、考核成绩、是否为计划内、记录编号、错误原因");
                    trainRecord.setRecordMistake(recordMistake.toString());
                    errorLines++;
                }
            }
        }
        return errorLines;
    }

    private Integer checkChangeRecord(List<JobChangeRecordExcelDTO> changeRecordList, List<TrainJobChangeRecord> jobChangeRecords, Integer errorLines) {
        if (CollUtil.isNotEmpty(changeRecordList)) {
            List<SysDepartModel> allSysDepart = iSysBaseApi.getAllSysDepart();
            Map<String, SysDepartModel> departMap = allSysDepart.stream().collect(Collectors.toMap(SysDepartModel::getDepartName, Function.identity(), (key1, key2) -> key2));
            List<DictModel> jobStatus = iSysBaseApi.getDictItems("job_status");
            Map<String, DictModel> jobMap = jobStatus.stream().collect(Collectors.toMap(DictModel::getText, Function.identity(), (key1, key2) -> key2));
            for (JobChangeRecordExcelDTO changeRecord : changeRecordList) {
                StringBuilder changeMistake = new StringBuilder();
                TrainJobChangeRecord jobChangeRecord = new TrainJobChangeRecord();
                if (ObjectUtil.isNotEmpty(changeRecord.getExcelJobTime()) && ObjectUtil.isNotEmpty(changeRecord.getJobGradeName())
                        && ObjectUtil.isNotEmpty(changeRecord.getJobName()) && ObjectUtil.isNotEmpty(changeRecord.getDepartName())) {
                    boolean legalDate = isLegalDate(changeRecord.getExcelJobTime().length(), changeRecord.getExcelJobTime());
                    if (legalDate) {
                        jobChangeRecord.setJobTime(DateUtil.parse(changeRecord.getExcelJobTime(), "yyyy-MM-dd"));
                    } else {
                        changeMistake.append("上岗时间填写有误，");
                    }
                    List<String> orgNames = StrUtil.splitTrim(changeRecord.getDepartName(), "/");
                    boolean isOrgNameTrue = true;
                    for (int i = 0; i < orgNames.size(); i++) {
                        SysDepartModel departModel = departMap.get(orgNames.get(i));
                        if (ObjectUtil.isEmpty(departModel)) {
                            isOrgNameTrue = false;
                            break;
                        }
                        if (ObjectUtil.isNotEmpty(departModel) && i == orgNames.size() - 1) {
                            jobChangeRecord.setJobOrgCode(departModel.getOrgCode());
                        }
                    }
                    if (!isOrgNameTrue) {
                        changeMistake.append("上岗部门填写有误，");
                    }
                    DictModel jobStatusName = jobMap.get(changeRecord.getJobName());
                    if (ObjectUtil.isNotEmpty(jobStatusName)) {
                        jobChangeRecord.setJobStatus(Integer.valueOf(jobStatusName.getValue()));
                    } else {
                        changeMistake.append("上岗岗位填写有误，");

                    }
                    String pattern = "^[0-9]*$";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(changeRecord.getJobGradeName());
                    if (m.find()) {
                        jobChangeRecord.setJobGrade(Integer.valueOf(changeRecord.getJobGradeName()));
                    } else {
                        changeMistake.append("上岗成绩填写有误，");
                    }
                    jobChangeRecords.add(jobChangeRecord);
                    if (changeMistake.length() > 0) {
                        // 截取字符
                        changeMistake = changeMistake.deleteCharAt(changeMistake.length() - 1);
                        changeRecord.setChangeMistake(changeMistake.toString());
                        errorLines++;
                    }

                } else {
                    changeRecord.setChangeMistake(changeMistake.append("上岗时间、上岗部门、上岗岗位、上岗成绩，不为空").toString());
                    errorLines++;
                }
            }
        }
        return errorLines;
    }

    private Integer checkDegree(DegreeRecordExcelDTO degreeRecordExcelDTO, List<TrainDegreeRecord> degreeRecords, Integer degreeType, Integer errorLines) {
        List<DictModel> gdForms = iSysBaseApi.getDictItems("graduation_form");
        List<DictModel> degrees = iSysBaseApi.getDictItems("degree");
        Map<String, DictModel> gdFormsMap = gdForms.stream().collect(Collectors.toMap(DictModel::getText, Function.identity(), (key1, key2) -> key2));
        Map<String, DictModel> degreesMap = degrees.stream().collect(Collectors.toMap(DictModel::getText, Function.identity(), (key1, key2) -> key2));
        if (!judgeObjIsNull(degreeRecordExcelDTO)) {
            StringBuilder errorMessage = new StringBuilder();
            if (ObjectUtil.isNotEmpty(degreeRecordExcelDTO.getDegreeName()) && ObjectUtil.isNotEmpty(degreeRecordExcelDTO.getGraduationFormName())
                    && ObjectUtil.isNotEmpty(degreeRecordExcelDTO.getGraduationSchool()) && ObjectUtil.isNotEmpty(degreeRecordExcelDTO.getSchoolEndTime()) &&
                    ObjectUtil.isNotEmpty(degreeRecordExcelDTO.getMajorsStudied())) {

                DictModel gdForm = gdFormsMap.get(degreeRecordExcelDTO.getGraduationFormName());
                DictModel degree = degreesMap.get(degreeRecordExcelDTO.getDegreeName());
                if (ObjectUtil.isEmpty(degree)) {
                    errorMessage.append("学历填写有误，");
                } else {
                    degreeRecordExcelDTO.setDegree(Integer.valueOf(degree.getValue()));
                }
                if (ObjectUtil.isEmpty(gdForm)) {
                    errorMessage.append("毕业形式填写有误，");
                } else {
                    degreeRecordExcelDTO.setGraduationForm(Integer.valueOf(gdForm.getValue()));
                }
                boolean legalDate = isLegalDate(degreeRecordExcelDTO.getSchoolEndTime().length(), degreeRecordExcelDTO.getSchoolEndTime());
                if (legalDate) {
                    degreeRecordExcelDTO.setGraduationTime(DateUtil.parse(degreeRecordExcelDTO.getSchoolEndTime(), "yyyy-MM-dd"));
                } else {
                    errorMessage.append("毕业时间填写有误，");
                }
                if (errorMessage.length() > 0) {
                    // 截取字符
                    errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                    degreeRecordExcelDTO.setChangeMistake(errorMessage.toString());
                    errorLines++;
                }
            } else {
                degreeRecordExcelDTO.setChangeMistake(errorMessage.append("学历、毕业形式、毕业时间、毕业院校、所学专业都不能为空").toString());
                errorLines++;
            }
            degreeRecordExcelDTO.setDegreeType(degreeType);
            TrainDegreeRecord degreeRecord = new TrainDegreeRecord();
            BeanUtils.copyProperties(degreeRecordExcelDTO, degreeRecord);
            degreeRecords.add(degreeRecord);
        }
        return errorLines;

    }

    private boolean isLegalDate(int length, String sDate) {
        String format = "yyyy-MM-dd";
        if ((sDate == null) || (sDate.length() != length)) {
            return false;
        }
        DateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    private Integer checkArchive(StringBuilder errorMessage, ArchiveExcelDTO excelDTO, TrainArchive archive, Integer errorLines) {
        JSONObject user = iSysBaseApi.queryByWorkNoUser(excelDTO.getWorkNo());
        if (ObjectUtil.isNotEmpty(user)) {
            String realName = "realname";
            String userId = user.getString("id");
            TrainArchive trainArchive = archiveMapper.selectOne(new LambdaQueryWrapper<TrainArchive>().eq(TrainArchive::getUserId, userId).eq(TrainArchive::getDelFlag,CommonConstant.DEL_FLAG_0));
            if(ObjectUtil.isNotEmpty(trainArchive)){
                errorMessage.append("系统已添加该用户,");
            }
            if (!user.getString(realName).equals(excelDTO.getRealname())) {
                errorMessage.append("姓名填写错误，");
            }
         if(errorMessage.length()>0){
             errorMessage = errorMessage.deleteCharAt(errorMessage.length()-1);
             excelDTO.setArchiveMistake(errorMessage.toString());
             errorLines++;
         }
            archive.setUserId(user.getString("id"));
            archive.setOrgCode(user.getString("orgCode"));
        }
        return errorLines;
    }

    private Result<?> importReturnRes(Integer errorLines, Integer successLines, boolean isSucceed, String failReportUrl, String message) {
        JSONObject result = new JSONObject(5);
        result.put("isSucceed", isSucceed);
        result.put("errorCount", errorLines);
        result.put("successCount", successLines);
        result.put("failReportUrl", failReportUrl);
        int totalCount = successLines + errorLines;
        result.put("totalCount", totalCount);
        Result res = Result.ok(result);
        res.setMessage(message);
        res.setCode(200);
        return res;
    }

    private boolean judgeObjIsNull(Object object) {
        //定义标志flag
        boolean flag = true;
        if (null == object) {
            return true;
        }
        // 得到类对象
        Class clazz = object.getClass();
        // 利用反射得到所有属性
        Field[] str = clazz.getDeclaredFields();
        //循环遍历反射得到的属性数组，判断每个属性值是否为空
        for (Field f : str) {
            //由于考虑到某些私有属性直接访问肯能访问不到，此属性设置为true确保可以访问到
            f.setAccessible(true);
            Object fieldValue = null;
            try {
                //得到属性值
                fieldValue = f.get(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //只要有一个属性值不为null 就返回false 表示对象不为null
            if (fieldValue != null) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
