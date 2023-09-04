package com.aiurt.modules.train.question.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.ExcelUtils;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.train.question.dto.BdQuestionDTO;
import com.aiurt.modules.train.question.dto.BdQuestionImportExcelDTO;
import com.aiurt.modules.train.question.dto.BdQuestionOptionImportExcelDTO;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.entity.BdQuestionOptions;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.question.mapper.BdQuestionCategoryMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsAttMapper;
import com.aiurt.modules.train.question.mapper.BdQuestionOptionsMapper;
import com.aiurt.modules.train.question.service.IBdQuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: bd_question
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdQuestionServiceImpl extends ServiceImpl<BdQuestionMapper, BdQuestion> implements IBdQuestionService {

    @Autowired
    private BdQuestionMapper bdQuestionMapper;

    @Autowired
    private BdQuestionOptionsAttMapper bdQuestionOptionsAttMapper;

    @Autowired
    private BdQuestionOptionsMapper bdQuestionOptionsMapper;

    @Autowired
    private BdQuestionCategoryMapper bdQuestionCategoryMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Value("${jeecg.minio.bucketName}")
    private String bucketName;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    public Page<BdQuestion> queryPageList(Page<BdQuestion> pageList,BdQuestion condition) {
        List<BdQuestion> questionList = bdQuestionMapper.list(pageList,condition);
        boolean b = GlobalThreadLocal.setDataFilter(false);
        questionList.forEach(e -> {
            List<BdQuestionOptionsAtt> bdQuestionOptionsActs = bdQuestionMapper.listss(e.getId());
                e.setPic("无");
                e.setVideo("无");
                e.setOther("无");
            if (CollectionUtil.isNotEmpty(bdQuestionOptionsActs)){
            List<String> collect = bdQuestionOptionsActs.stream().map(BdQuestionOptionsAtt::getType).collect(Collectors.toList());
            for (String s:collect) {
                if ("pic".equals(s)) {
                    e.setPic("有");
                }
                if ("video".equals(s)) {
                    e.setVideo("有");
                }
                if ("other".equals(s)) {
                    e.setOther("有");
                }
            }}

            List<BdQuestionOptions> lists = bdQuestionMapper.lists(e.getId());
            e.setExamAllQuestionOptionList(lists);
        });
        GlobalThreadLocal.setDataFilter(b);
        return pageList.setRecords(questionList);
    }

    @Override
    public void addBdQuestion(BdQuestion bdQuestion) {
        // 将习题的所属班组设置为当前登录人所在的班组
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        bdQuestion.setOrgCode(loginUser.getOrgCode());

        bdQuestionMapper.insert(bdQuestion);
        modifyDelete(bdQuestion);
    }

    @Override
    public void updateBdQuestion(BdQuestion bdQuestion) {
        String ids = bdQuestion.getId();
        //删除原来的试题图片
        bdQuestionMapper.deletequestionoptionsatt(ids);

        //删除原来的答案
        bdQuestionMapper.deletquestionoptions(ids);
        bdQuestionMapper.updateById(bdQuestion);
        modifyDelete(bdQuestion);
    }

    @Override
    public BdQuestion bdQuestion(String id) {
        BdQuestion bdQuestion = bdQuestionMapper.bdQuestion(id);
        String queTypeName = iSysBaseAPI.translateDict("que_type", String.valueOf(bdQuestion.getQueType()));
        bdQuestion.setQueTypeName(queTypeName);
        List<BdQuestionOptions> lists = bdQuestionMapper.lists(id);
        List<BdQuestionOptionsAtt> enclosures = bdQuestionMapper.listss(id);
        if (ObjectUtil.isNotNull(bdQuestion)){
        bdQuestion.setEnclosureList(enclosures);
        bdQuestion.setExamAllQuestionOptionList(lists);
        }
        return bdQuestion;
    }

    @Override
    public List<BdQuestion> getLearningMaterials(String id) {
        //根据试卷id获取习题id
        List<String> quesId = bdQuestionMapper.getQuesId(id);
        //根据习题id获取正确的选项内容
        List<BdQuestion> options = bdQuestionMapper.getOptions(quesId);

        options.forEach(bdQuestion -> {
            List<BdQuestionOptionsAtt> att = bdQuestionOptionsAttMapper.getAtt(bdQuestion.getId());
            //根据文件类型分类
            List<BdQuestionOptionsAtt> imageList = att.stream().filter(a -> ("pic").equals(a.getType())).collect(Collectors.toList());
            List<BdQuestionOptionsAtt> videoList = att.stream().filter(a -> ("video").equals(a.getType())).collect(Collectors.toList());
            List<BdQuestionOptionsAtt> materialsList = att.stream().filter(a -> ("other").equals(a.getType())).collect(Collectors.toList());
            bdQuestion.setImageList(imageList);
            bdQuestion.setVideoList(videoList);
            bdQuestion.setMaterialsList(materialsList);
        });
        return options;
    }

    @Override
    public List<BdQuestion> randomSelectionQuestion(String categoryIds, Integer choiceQuestionNum, Integer shortAnswerQuestionNum) {
        List<BdQuestion> questionList = bdQuestionMapper.randomSelectionQuestion(StrUtil.isNotBlank(categoryIds) ? StrUtil.splitTrim(categoryIds, ",") : null, choiceQuestionNum, shortAnswerQuestionNum);
        List<DictModel> queType = iSysBaseAPI.queryDictItemsByCode("que_type");
        Map<String, String> queTypeMap = queType.stream().collect(Collectors.toMap(DictModel::getValue,DictModel::getText));
        //查找试题
        List<String> questionIds = questionList.stream().map(BdQuestion::getId).collect(Collectors.toList());
        LambdaQueryWrapper<BdQuestionOptionsAtt> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BdQuestionOptionsAtt::getQuestionId, questionIds);
        List<BdQuestionOptionsAtt> bdQuestionOptionsActs = bdQuestionOptionsAttMapper.selectList(wrapper);
        questionList.forEach(e -> {
            e.setQueTypeName(queTypeMap.get(e.getQueType().toString()));
            e.setPic("无");
            e.setVideo("无");
            e.setOther("无");
            List<BdQuestionOptionsAtt> optionsAtts = Optional.ofNullable(bdQuestionOptionsActs).orElse(CollUtil.newArrayList()).stream().filter(b -> b.getQuestionId().equals(e.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(optionsAtts)){
                List<String> collect = optionsAtts.stream().map(BdQuestionOptionsAtt::getType).collect(Collectors.toList());
                for (String s:collect) {
                    if ("pic".equals(s)) {
                        e.setPic("有");
                    }
                    if ("video".equals(s)) {
                        e.setVideo("有");
                    }
                    if ("other".equals(s)) {
                        e.setOther("有");
                    }
                }}
        });
        return questionList;
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        try{
            MultipartFile file = multipartRequest.getFile("file");
            if (file == null) {
                throw new AiurtBootException("导入文件不能为空!");
            }
            String filename = file.getOriginalFilename();
            if (StrUtil.isEmpty(filename) || !StrUtil.containsAny(filename, "xls", "xlsx")){
                throw new AiurtBootException("只能导入excel文件!");
            }
            // 导入excel
            ImportParams params = new ImportParams();
            // 表头的索引行
            params.setTitleRows(2);
            // 表头占据多少行
            params.setHeadRows(2);
            // 这个设置好像是保存临时文件
            params.setNeedSave(true);
            List<BdQuestionImportExcelDTO> list = ExcelImportUtil.importExcel(file.getInputStream(), BdQuestionImportExcelDTO.class, params);
            // 删除list里面的空数据行
            dealEmptyData(list);
            // 校验数据
            validData(list);
            // list为空
            if (CollUtil.isEmpty(list)){
                return ExcelUtils.importReturnRes(0, 0, false, null, "导入数据为空!");
            }
            // 错误行数
            int errorLines = (int) list.stream().filter(dto -> StrUtil.isNotEmpty(dto.getErrorMessage())).count();
            if (errorLines > 0) {
                // 错误清单
                return getErrorExcel(list, errorLines);
            }else{
                // 保存数据
                saveImportData(list);
                return ExcelUtils.importReturnRes(0, list.size(), true, null, "文件导入成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ExcelUtils.importReturnRes(0, 0, false, null, "导入失败!");
        }
    }

    /**考卷习题导入->错误清单*/
    private Result<?> getErrorExcel(List<BdQuestionImportExcelDTO> list, int errorLines) throws IOException {
        // 从minio获取错误清单模板
        InputStream inputStream = MinioUtil.getMinioFile(bucketName, "excel/template/考卷习题导入错误清单模板.xlsx");
        // 创建临时文件
        File fileTemp= new File("/templates/exercisesError.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(inputStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        TemplateExportParams exportParams = new TemplateExportParams(fileTemp.getAbsolutePath());
        Map<String, Object> errorMap = new HashMap<String, Object>(1);
        List<Map<String, Object>> listMap = new ArrayList<>();
        // 根据模板写入数据，先每行一个数据，后面再合并单元格
        list.forEach(dto->{
            List<BdQuestionOptionImportExcelDTO> optionList = dto.getBdQuestionOptionImportExcelDTOList();
            optionList.forEach(option->{
                Map<String, Object> map = new HashMap<>(8);
                map.put("orgCode", dto.getOrgCode());
                map.put("categoryName", dto.getCategoryName());
                map.put("content", dto.getContent());
                map.put("queTypeString", dto.getQueTypeString());
                map.put("answer", dto.getAnswer());
                map.put("optionContent", option.getContent());
                map.put("isRightString", option.getIsRightString());
                map.put("errorMessage", dto.getErrorMessage());
                listMap.add(map);
            });
        });
        errorMap.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);

        // 合并单元格
        Sheet sheet = workbook.getSheetAt(0);
        // 合并开始行数，数据从第5行(索引行)开始的
        int firstRow = 4;
        // 合并结束行数
        int lastRow;
        for (BdQuestionImportExcelDTO dto : list) {
            // 有多少个选项，就合并多少行
            int size = dto.getBdQuestionOptionImportExcelDTOList().size();
            lastRow = firstRow + size - 1;
            // 合并前5列和第8列的导入失败原因
            for (int i = 0; i < 5; i++) {
                PoiMergeCellUtil.addMergedRegion(sheet, firstRow, lastRow, i, i);
            }
            PoiMergeCellUtil.addMergedRegion(sheet, firstRow, lastRow, 7, 7);
            // 合并完后firstRow要重新赋值到下一个数据
            firstRow = lastRow + 1;
        }

        String url = null;
        // 错误清单不放到minio里了，还是按照原来的放到服务器里，不然前端要改通用下载错误清单的组件
        String fileName = "考卷习题导入错误清单"+"_" + System.currentTimeMillis()+".xlsx";
        try (FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName)){
            url = fileName;
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 关闭临时文件
            boolean delete = fileTemp.delete();
            inputStream.close();
        }

        return ExcelUtils.importReturnRes(errorLines, list.size()-errorLines, false, url, "有数据错误文件导入失败！");
    }

    /**
     * 考卷习题导入->保存导入的，经过处理的数据，
     * @param list
     */
    @Transactional(rollbackFor = Exception.class)
    void saveImportData(List<BdQuestionImportExcelDTO> list) {
        for (BdQuestionImportExcelDTO bdQuestionImportExcelDTO : list) {
            BdQuestion bdQuestion = new BdQuestion();
            BeanUtils.copyProperties(bdQuestionImportExcelDTO, bdQuestion);
            this.save(bdQuestion);
            // validData(list)里已经把简答题和填空题的选项设置为空值了
            if (CollUtil.isEmpty(bdQuestionImportExcelDTO.getBdQuestionOptionImportExcelDTOList())) {
                BdQuestionOptions options = new BdQuestionOptions();
                options.setQuestionId(bdQuestion.getId());
                options.setContent(bdQuestion.getAnswer());
                options.setIsRight(1);
                bdQuestionOptionsMapper.insert(options);
            }else {
                bdQuestionImportExcelDTO.getBdQuestionOptionImportExcelDTOList().forEach(optionDTO -> {
                    BdQuestionOptions options = new BdQuestionOptions();
                    options.setQuestionId(bdQuestion.getId());
                    options.setContent(optionDTO.getContent());
                    options.setIsRight(optionDTO.getIsRight());
                    options.setIsort(optionDTO.getIsort());
                    bdQuestionOptionsMapper.insert(options);
                });
            }
        }
    }

    /**
     * 考卷习题导入数据的校验
     * @param list
     */
    private void validData(List<BdQuestionImportExcelDTO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // 获取所有班组的orgCode供验证使用
        List<SysDepartModel> departList = iSysBaseAPI.getAllSysDepart();
        List<String> orgCodeList = departList.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        // 获取所有题目类别供验证使用->注意这是题目类“别”,题目类别是唯一的。categoryMap中name为key,id为value
        List<TreeNode> categoryList = bdQuestionCategoryMapper.queryPageList();
        Map<String, String> categoryMap = categoryList.stream().collect(Collectors.toMap(TreeNode::getName, TreeNode::getId));
        Set<String> categoryNameSet = categoryMap.keySet();
        // 获取所有题目类型供验证使用->注意这是题目类“型”，题目类型是唯一的。queTypeMap中类型名称为key,字典值为value
        List<DictModel> queTypeDictList = iSysBaseAPI.queryDictItemsByCode("que_type");
        Map<String, String> queTypeMap = queTypeDictList.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
        Set<String> queTypeStringSet = queTypeMap.keySet();

        list.forEach(dto->{
            String orgCode = dto.getOrgCode();
            String categoryName = dto.getCategoryName();
            String content = dto.getContent();
            String queTypeString = dto.getQueTypeString();
            String answer = dto.getAnswer();
            List<BdQuestionOptionImportExcelDTO> optionDTOList = dto.getBdQuestionOptionImportExcelDTOList();

            StringBuilder errorMessageBuilder = new StringBuilder();
            // 验证所属班组是否正确
            if (StrUtil.isEmpty(orgCode)){
                errorMessageBuilder.append("所属班组不能为空");
                errorMessageBuilder.append(";");
            }else if (!orgCodeList.contains(orgCode)) {
                errorMessageBuilder.append("所属班组不存在");
                errorMessageBuilder.append(";");
            }
            // 验证题目类别
            if (StrUtil.isEmpty(categoryName)){
                errorMessageBuilder.append("题目类别不能为空");
                errorMessageBuilder.append(";");
            }else if (!categoryNameSet.contains(categoryName)){
                errorMessageBuilder.append("题目类别不存在");
                errorMessageBuilder.append(";");
            }else {
                dto.setCategoryId(categoryMap.get(categoryName));
            }
            // 验证题目
            if (StrUtil.isEmpty(content)){
                errorMessageBuilder.append("题目不能为空");
                errorMessageBuilder.append(";");
            }
            // 验证题目类型
            if (StrUtil.isEmpty(queTypeString)){
                errorMessageBuilder.append("题目类型不能为空");
                errorMessageBuilder.append(";");
            }else if (!queTypeStringSet.contains(queTypeString)){
                errorMessageBuilder.append("题目类型不存在");
                errorMessageBuilder.append(";");
            }else {
                dto.setQueType(Integer.valueOf(queTypeMap.get(queTypeString)));
            }
            // 是否是简答题或填空题
            boolean isAnswerQuestion = "简答题".equals(queTypeString) || "填空题".equals(queTypeString);
            // 是否是选择题或多选题或判断题
            boolean isOptionQuestion = "选择题".equals(queTypeString) || "多选题".equals(queTypeString) || "判断题".equals(queTypeString);
            // 验证答案内容，当题目类型是填空题或者简答题是不能为空
            if (isAnswerQuestion && StrUtil.isEmpty(answer)) {
                errorMessageBuilder.append("填空题或者简答题的答案内容不能为空");
                errorMessageBuilder.append(";");
            }
            // 验证选项，当题目类型是选择题、多选题、判断题时，选项不能为空，并且不同的题要进一步判断
            if (isOptionQuestion && (CollUtil.isEmpty(optionDTOList) || !(optionDTOList.size() >= 2 && optionDTOList.size() <= 5))){
                errorMessageBuilder.append("选项不能为空且数量为2~5");
                errorMessageBuilder.append(";");
            }
            // 选择中答案的数量
            long count = optionDTOList.stream().filter(option -> "是".equals(option.getIsRightString())).count();
            // 选择题，多个选项中只能有一个答案选项
            if ("选择题".equals(queTypeString) && count != 1L){
                errorMessageBuilder.append("选择题只能有一个答案");
                errorMessageBuilder.append(";");
            }
            // 多选题，没啥验证的
            // 判断题，只能有两个选项，只能有一个答案，选项内容只能是对或者错
            if ("判断题".equals(queTypeString)){
                if (optionDTOList.size() != 2){
                    errorMessageBuilder.append("判断题选项数目只能为2");
                    errorMessageBuilder.append(";");
                }
                if (count != 1L){
                    errorMessageBuilder.append("判断题只能有一个答案");
                    errorMessageBuilder.append(";");
                }
                Set<String> set = optionDTOList.stream().map(BdQuestionOptionImportExcelDTO::getContent).collect(Collectors.toSet());
                if (!(set.size() == 2 && set.contains("对") && set.contains("错"))) {
                    errorMessageBuilder.append("判断题选项只能是对和错");
                    errorMessageBuilder.append(";");
                }
            }
            // 没有错误
            if (errorMessageBuilder.length() == 0) {
                // 将简答题和填空题的选项设置为null，方便保存数据入库
                if (isAnswerQuestion) {
                    dto.setBdQuestionOptionImportExcelDTOList(null);
                }else {
                    optionDTOList.forEach(option->option.setIsRight("是".equals(option.getIsRightString()) ? 1 : 0));
                }
            }else{
                // 有错误
                dto.setErrorMessage(errorMessageBuilder.toString());
            }

        });
    }

    /**
     * 处理考卷习题导入的空数据行
     * @param list
     */
    private void dealEmptyData(List<BdQuestionImportExcelDTO> list) throws NoSuchFieldException, IllegalAccessException {
        // 判断list的所有字段是不是都是空的，注意，bdQuestionOptionImportExcelDTOList就是有数据，其他是空的，也是空的，
        // 所以不判断bdQuestionOptionImportExcelDTOList
        // 有过BdQuestionImportExcelDTO其他字段是null，bdQuestionOptionImportExcelDTOList不是null，但里面的对象的字段都是null
        Field[] fields = BdQuestionImportExcelDTO.class.getDeclaredFields();

        Iterator<BdQuestionImportExcelDTO> iterator = list.iterator();
        while (iterator.hasNext()){
            BdQuestionImportExcelDTO next = iterator.next();
            boolean isEmptyFlag = true;
            for (Field field : fields) {
                field.setAccessible(true);
                if ("bdQuestionOptionImportExcelDTOList".equals(field.getName())) {
                    continue;
                }
                // 这个字段不是空的, 跳出字段判断的循环
                if (field.get(next) != null && StrUtil.isNotEmpty(field.get(next).toString())) {
                    isEmptyFlag = false;
                    break;
                }
            }
            if (isEmptyFlag){
                iterator.remove();
            }
        }
    }

    @Override
    public void downloadTemplateExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 从minio获取模板文件
        InputStream inputStream = MinioUtil.getMinioFile(bucketName, "excel/template/考卷习题导入模板.xlsx");
        // 根据模板文件，创建一个Workbook对象
        Workbook workbook = WorkbookFactory.create(inputStream);
        // 添加下拉列表
        // 题目类别，类别名称全表唯一的
        List<TreeNode> treeNodes = bdQuestionCategoryMapper.queryPageList();
        List<String> categoryNameList = treeNodes.stream().map(TreeNode::getName).collect(Collectors.toList());
        ExcelUtils.selectList(workbook, 0, 4, 1, 1, categoryNameList);
        // 题目类型，从数据字典获取
        List<DictModel> typeDictList = iSysBaseAPI.queryDictItemsByCode("que_type");
        List<String> typeList = typeDictList.stream().map(DictModel::getText).collect(Collectors.toList());
        ExcelUtils.selectList(workbook, 0, 4, 3, 3, typeList);
        // 是否是答案下拉列表
        ExcelUtils.selectList(workbook, 0, 4, 6, 6, Arrays.asList("是", "否"));

        // 将数据写入响应
        String fileName = new String("考卷习题导入模板.xlsx".getBytes(), StandardCharsets.ISO_8859_1);
        try (BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream())) {
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BdQuestionDTO getQuestionNum(String categoryIds) {

        Integer choiceQuestionNum = bdQuestionMapper.getQuestionNum(StrUtil.isNotBlank(categoryIds) ? StrUtil.splitTrim(categoryIds, ",") : null, 1);
        Integer shortAnswerQuestionNum = bdQuestionMapper.getQuestionNum(StrUtil.isNotBlank(categoryIds) ? StrUtil.splitTrim(categoryIds, ",") : null, 2);
        BdQuestionDTO dto = new BdQuestionDTO();
        dto.setChoiceQuestionNum(choiceQuestionNum);
        dto.setShortAnswerQuestionNum(shortAnswerQuestionNum);

        return dto;
    }

    private void modifyDelete(BdQuestion bdQuestion) {
        if( bdQuestion.getImageList() != null){
            for (BdQuestionOptionsAtt bdQuestionOptionsAtt : bdQuestion.getImageList()){
                BdQuestionOptionsAtt bdQuestionOptionsAtt1 = new BdQuestionOptionsAtt();
                bdQuestionOptionsAtt1.setQuestionId(bdQuestion.getId());
                bdQuestionOptionsAtt1.setPath(bdQuestionOptionsAtt.getPath());
                bdQuestionOptionsAtt1.setName(bdQuestionOptionsAtt.getName());
                bdQuestionOptionsAtt1.setType("pic");
                bdQuestionOptionsAttMapper.insert(bdQuestionOptionsAtt1);
            }
        }
        if( bdQuestion.getVideoList() != null){
            for (BdQuestionOptionsAtt bdQuestionOptionsAtt : bdQuestion.getVideoList()){
                BdQuestionOptionsAtt bdQuestionOptionsAtt1 = new BdQuestionOptionsAtt();
                bdQuestionOptionsAtt1.setQuestionId(bdQuestion.getId());
                bdQuestionOptionsAtt1.setPath(bdQuestionOptionsAtt.getPath());
                bdQuestionOptionsAtt1.setName(bdQuestionOptionsAtt.getName());
                bdQuestionOptionsAtt1.setType("video");
                bdQuestionOptionsAttMapper.insert(bdQuestionOptionsAtt1);
            }
        }
        if( bdQuestion.getMaterialsList() != null){
            for (BdQuestionOptionsAtt bdQuestionOptionsAtt : bdQuestion.getMaterialsList()){
                BdQuestionOptionsAtt bdQuestionOptionsAtt1 = new BdQuestionOptionsAtt();
                bdQuestionOptionsAtt1.setQuestionId(bdQuestion.getId());
                bdQuestionOptionsAtt1.setPath(bdQuestionOptionsAtt.getPath());
                bdQuestionOptionsAtt1.setName(bdQuestionOptionsAtt.getName());
                bdQuestionOptionsAtt1.setType("other");
                bdQuestionOptionsAttMapper.insert(bdQuestionOptionsAtt1);
            }
        }
        //选择题
        if (bdQuestion.getSelectList() != null){
            for (String bdQuestionOptions : bdQuestion.getSelectList()){
                BdQuestionOptions bdQuestionOptions1 = new BdQuestionOptions();
                bdQuestionOptions1.setQuestionId(bdQuestion.getId());
                bdQuestionOptions1.setContent(bdQuestionOptions);
                if (bdQuestion.getQueType().equals(1) && null!=bdQuestion.getAnswer() && bdQuestion.getAnswer().contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(1);
                }
                if (bdQuestion.getQueType().equals(1) && null!=bdQuestion.getAnswer() && !bdQuestion.getAnswer().contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(0);
                }
                if (bdQuestion.getQueType().equals(2) && null!=bdQuestion.getAnswer() && Arrays.asList( bdQuestion.getAnswer().split(",")).contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(1);
                }
                if (bdQuestion.getQueType().equals(2) && null!=bdQuestion.getAnswer() && !Arrays.asList( bdQuestion.getAnswer().split(",")).contains(bdQuestionOptions)){
                    bdQuestionOptions1.setIsRight(0);
                }
                bdQuestionOptionsMapper.insert(bdQuestionOptions1);
            }
        }
        //简答题
        if (bdQuestion.getSelectList().length ==0){
            BdQuestionOptions bdQuestionOptions1 = new BdQuestionOptions();
            bdQuestionOptions1.setQuestionId(bdQuestion.getId());
            bdQuestionOptions1.setContent(bdQuestion.getAnswer());
            bdQuestionOptions1.setIsRight(1);
            bdQuestionOptionsMapper.insert(bdQuestionOptions1);
        }
    }
}
