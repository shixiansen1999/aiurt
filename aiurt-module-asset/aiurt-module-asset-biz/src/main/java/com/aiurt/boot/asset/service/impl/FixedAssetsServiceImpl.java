package com.aiurt.boot.asset.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.asset.dto.FixedAssetsDTO;
import com.aiurt.boot.asset.dto.FixedAssetsModel;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.mapper.FixedAssetsMapper;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.mapper.FixedAssetsCategoryMapper;
import com.aiurt.boot.check.dto.FixedAssetsCheckRecordDTO;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.mapper.FixedAssetsCheckCategoryMapper;
import com.aiurt.boot.check.mapper.FixedAssetsCheckDeptMapper;
import com.aiurt.boot.check.mapper.FixedAssetsCheckMapper;
import com.aiurt.boot.constant.FixedAssetsConstant;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.mapper.FixedAssetsCheckRecordMapper;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Service
public class FixedAssetsServiceImpl extends ServiceImpl<FixedAssetsMapper, FixedAssets> implements IFixedAssetsService {

    private static final Logger log = LoggerFactory.getLogger(FixedAssetsServiceImpl.class);
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;
    @Autowired
    private FixedAssetsMapper assetsMapper;
    @Autowired
    private FixedAssetsCategoryMapper categoryMapper;
    @Autowired
    private FixedAssetsCheckMapper assetsCheckMapper;
    @Autowired
    private FixedAssetsCheckRecordMapper recordMapper;
    @Autowired
    private FixedAssetsCheckCategoryMapper checkCategoryMapper;
    @Autowired
    private FixedAssetsCheckDeptMapper checkDeptMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public Page<FixedAssetsDTO> pageList(Page<FixedAssetsDTO> pageList, FixedAssetsDTO fixedAssetsDTO) {
        //线路查询
        if(ObjectUtil.isNotEmpty(fixedAssetsDTO.getLocation())){
            List<String> stationCodeByLineCode = sysBaseAPI.getStationCodeByLineCode(fixedAssetsDTO.getLocation());
            if (CollUtil.isNotEmpty(stationCodeByLineCode)) {
                stationCodeByLineCode.add(fixedAssetsDTO.getLocation());
                fixedAssetsDTO.setLineStations(stationCodeByLineCode);
                fixedAssetsDTO.setIsLine(true);
            } else {
                fixedAssetsDTO.setIsLine(false);
            }
        }
        //资产分类查询
        if (ObjectUtil.isNotEmpty(fixedAssetsDTO.getCategoryCode())) {
            //获取该节点下的所有字节点
            LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<FixedAssetsCategory> list = categoryMapper.selectList(queryWrapper);
            FixedAssetsCategory category = categoryMapper.selectOne(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getCategoryCode, fixedAssetsDTO.getCategoryCode()));
            List<FixedAssetsCategory> categoryList = new ArrayList<>();
            List<FixedAssetsCategory> allChildren = treeMenuList(list, category, categoryList);
            allChildren.add(category);
            List<String> allChildrenCode = allChildren.stream().map(FixedAssetsCategory::getCategoryCode).collect(Collectors.toList());
            fixedAssetsDTO.setTreeCode(allChildrenCode);
        }
        List<FixedAssetsDTO> list = assetsMapper.pageList(pageList, fixedAssetsDTO);
        for (FixedAssetsDTO dto : list) {
            //翻译存放地点
            if (ObjectUtil.isNotEmpty(dto.getLocation())) {
                JSONObject csStation = sysBaseAPI.getCsStationByCode(dto.getLocation());
                String position = sysBaseAPI.getPosition(dto.getLocation());
                if (ObjectUtil.isNotEmpty(csStation)) {
                    dto.setLocationName(csStation.getString("lineName") + position);
                } else {
                    if (ObjectUtil.isNotEmpty(position)) {
                        dto.setLocationName(position);
                    }
                }
            }
            //翻译责任人
            if (ObjectUtil.isNotEmpty(dto.getResponsibilityId())) {
                String[] userIds = dto.getResponsibilityId().split(",");
                List<LoginUser> loginUsers = sysBaseAPI.queryAllUserByIds(userIds);
                String userName = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                dto.setResponsibilityName(userName);
            }
        }
        return pageList.setRecords(list);
    }

    /**
     * 获取某个父节点下面的所有子节点
     *
     * @param list
     * @param assetsCategory
     * @param allChildren
     * @return
     */
    public static List<FixedAssetsCategory> treeMenuList(List<FixedAssetsCategory> list, FixedAssetsCategory assetsCategory, List<FixedAssetsCategory> allChildren) {

        for (FixedAssetsCategory category : list) {
            //遍历出父id等于参数的id，add进子节点集合
            if (category.getPid().equals(assetsCategory.getId())) {
                //递归遍历下一级
                treeMenuList(list, category, allChildren);
                allChildren.add(category);
            }
        }
        return allChildren;
    }

    @Override
    public Result<FixedAssetsDTO> detail(String code) {
        FixedAssetsDTO dto = new FixedAssetsDTO();
        FixedAssets assets = assetsMapper.selectOne(new LambdaQueryWrapper<FixedAssets>().eq(FixedAssets::getAssetCode, code));
        BeanUtils.copyProperties(assets, dto);
        //翻译存放地点
        if (ObjectUtil.isNotEmpty(dto.getLocation())) {
            JSONObject csStation = sysBaseAPI.getCsStationByCode(dto.getLocation());
            String position = sysBaseAPI.getPosition(dto.getLocation());
            if (ObjectUtil.isNotEmpty(csStation)) {
                dto.setLocationName(csStation.getString("lineName") + position);
            } else {
                if (ObjectUtil.isNotEmpty(position)) {
                    dto.setLocationName(position);
                }
            }
        }
        //翻译责任人
        if (ObjectUtil.isNotEmpty(dto.getResponsibilityId())) {
            String[] userIds = dto.getResponsibilityId().split(",");
            List<LoginUser> loginUsers = sysBaseAPI.queryAllUserByIds(userIds);
            String userName = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
            dto.setResponsibilityName(userName);
        }
        //查询该资产编码下的盘点记录
        List<FixedAssetsCheckRecordDTO> assetsCheckDTOS = new ArrayList<>();
        //1.获取已经完成的盘点任务
        List<FixedAssetsCheck> fixedAssetsChecks = assetsCheckMapper.selectList(new LambdaQueryWrapper<FixedAssetsCheck>().eq(FixedAssetsCheck::getStatus, FixedAssetsConstant.STATUS_3).eq(FixedAssetsCheck::getDelFlag, CommonConstant.DEL_FLAG_0));
        for (FixedAssetsCheck assetsCheck : fixedAssetsChecks) {
            LoginUser checkName = sysBaseAPI.getUserById(assetsCheck.getCheckId());
            LoginUser auditName = sysBaseAPI.getUserById(assetsCheck.getAuditId());
            FixedAssetsCheckRecordDTO assetsCheckDTO = new FixedAssetsCheckRecordDTO();
            BeanUtils.copyProperties(assetsCheck, assetsCheckDTO);
            //2.获取盘点任务下的资产编码一样的盘点记录
            LambdaQueryWrapper<FixedAssetsCheckRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCheckRecord::getCheckId, assetsCheck.getId());
            queryWrapper.eq(FixedAssetsCheckRecord::getAssetCode, dto.getAssetCode());
            queryWrapper.last("limit 1");
            FixedAssetsCheckRecord record = recordMapper.selectOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(record)) {
                assetsCheckDTO.setCheckName(checkName.getRealname());
                assetsCheckDTO.setAuditName(auditName.getRealname());
                assetsCheckDTO.setNumber(record.getNumber());
                assetsCheckDTO.setActualNumber(record.getActualNumber());
                assetsCheckDTO.setActualSurplusLoss(record.getActualNumber() - record.getNumber());
                assetsCheckDTO.setOneselfAssetNumber(record.getOneselfAssetNumber());
                assetsCheckDTO.setOthersAssetNumber(record.getOthersAssetNumber());
                assetsCheckDTO.setAccumulatedDepreciation(record.getAccumulatedDepreciation());
                assetsCheckDTO.setLeisureAssetNumber(record.getLeisureAssetNumber());
                assetsCheckDTO.setLeisureArea(record.getLeisureArea());
                assetsCheckDTO.setHypothecate(record.getHypothecate());
                assetsCheckDTO.setAuditIdTime(assetsCheck.getAuditTime());
                assetsCheckDTO.setRemark(record.getRemark());
                assetsCheckDTOS.add(assetsCheckDTO);
            }
        }
        dto.setRecordDTOList(assetsCheckDTOS);
        return Result.OK(dto);
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);

            List<String> errorMessage = new ArrayList<>();
            int successLines = 0;
            // 错误信息
            int  errorLines = 0;

            try {
                String type = FilenameUtils.getExtension(file.getOriginalFilename());
                if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                    return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
                }

                List<FixedAssetsModel> fixedAssetsModels = ExcelImportUtil.importExcel(file.getInputStream(), FixedAssetsModel.class, params);
                Iterator<FixedAssetsModel> iterator = fixedAssetsModels.iterator();
                while (iterator.hasNext()) {
                    FixedAssetsModel model = iterator.next();
                    boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
                    if (b) {
                        iterator.remove();
                    }
                }
                if (CollUtil.isEmpty(fixedAssetsModels)) {
                    return Result.error("文件导入失败:文件内容不能为空！");
                }
                Map<String, String> data = new HashMap<>();
                ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
                for (FixedAssetsModel fixedAssetsModel : fixedAssetsModels) {
                    threadPoolExecutor.execute(() -> this.executeMethod(data,fixedAssetsModel));
                }
                threadPoolExecutor.shutdown();
                try {
                    // 等待线程池中的任务全部完成
                    threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // 处理中断异常
                    log.info("循环方法的线程中断异常", e.getMessage());
                }

                errorLines = (int) fixedAssetsModels.stream().filter(f -> StrUtil.isNotBlank(f.getMistake())).count();
                if (errorLines > 0) {
                    //存在错误，导出错误清单
                    return getErrorExcel(errorLines, errorMessage, fixedAssetsModels, successLines, null, type);
                }

                //校验通过，添加数据
                for (FixedAssetsModel fixedAssetsModel : fixedAssetsModels) {
                    FixedAssets fixedAssets = new FixedAssets();
                    BeanUtils.copyProperties(fixedAssetsModel, fixedAssets);
                    this.save(fixedAssets);
                }
                return Result.ok("文件导入成功！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Result.ok("文件导入失败！");
    }
    private void executeMethod( Map<String, String> data,FixedAssetsModel fixedAssetsModel) {
        StringBuilder stringBuilder = new StringBuilder();
        //数据重复性校验
        String s = data.get(fixedAssetsModel.getAssetCode());
        if (StrUtil.isNotEmpty(s)) {
            stringBuilder.append("该数据存在相同数据，");
        } else {
            data.put(fixedAssetsModel.getAssetCode(), fixedAssetsModel.getAssetName());
        }
        //必填数据校验
        checkRequired(stringBuilder, fixedAssetsModel);
        //非必填数据校验
        checkNotRequired(stringBuilder, fixedAssetsModel);

        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            fixedAssetsModel.setMistake(stringBuilder.toString());
        }
    }

    /**必填数据校验*/
    private void checkRequired(StringBuilder stringBuilder, FixedAssetsModel fixedAssetsModel) {
        String assetName = fixedAssetsModel.getAssetName();
        String categoryName = fixedAssetsModel.getCategoryName();
        String assetCode = fixedAssetsModel.getAssetCode();
        String orgName = fixedAssetsModel.getOrgName();
        Integer number = fixedAssetsModel.getNumber();

        if (StrUtil.isEmpty(assetName)) {
            stringBuilder.append("资产名称不能为空，");
        }

        if (StrUtil.isNotEmpty(categoryName)) {
            List<String> list = StrUtil.splitTrim(categoryName, "/");
            String pid = "0";
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                FixedAssetsCategory assetsCategory = categoryMapper.getAssetsCategory(s, pid);
                if (ObjectUtil.isNotNull(assetsCategory)) {
                    pid = assetsCategory.getId();
                    if (i == list.size() - 1) {
                        fixedAssetsModel.setCategoryCode(assetsCategory.getCategoryCode());
                    }
                }else {
                    stringBuilder.append("系统不存在该资产分类，");
                    break;
                }
            }

        }else {
            stringBuilder.append("资产分类不能为空，");
        }

        if (StrUtil.isNotEmpty(assetCode)) {
            LambdaQueryWrapper<FixedAssets> queryWrapper = new LambdaQueryWrapper<FixedAssets>();
            queryWrapper.eq(FixedAssets::getAssetCode, assetCode);
            FixedAssets assets = this.getOne(queryWrapper);
            if(ObjectUtil.isNotEmpty(assets)){
                stringBuilder.append("资产编号已存在，");
            }
        }else {
            stringBuilder.append("资产编号不能为空，");
        }

        if (StrUtil.isNotEmpty(orgName)) {
            List<String> list = StrUtil.splitTrim(orgName, "/");
            String id = null;
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                //根据部门名称和父id找部门
                JSONObject depart = sysBaseAPI.getDepartByNameAndParentId(s, id);
                if (ObjectUtil.isNotNull(depart)) {
                    id = depart.getString("id");
                    fixedAssetsModel.setOrgCode(depart.getString("orgCode"));
                }else {
                    stringBuilder.append("系统不存在该组织机构，");
                    break;
                }
            }
        }else {
            stringBuilder.append("使用组织机构不能为空，");
        }

        if (number == null) {
            stringBuilder.append("账面数量不能为空，");
        }

    }

    /**非必填数据校验*/
    private void checkNotRequired(StringBuilder stringBuilder, FixedAssetsModel fixedAssetsModel) {
        String responsibilityName = fixedAssetsModel.getResponsibilityName();
        if (StrUtil.isNotBlank(responsibilityName) && StrUtil.isNotBlank(fixedAssetsModel.getOrgCode())) {
            List<String> list = StrUtil.splitTrim(responsibilityName, "，");
            StringBuilder responsibilityId = new StringBuilder();
            for (String s : list) {
                List<String> list1 = StrUtil.splitTrim(s, "/");
                if (list1.size() == 2) {
                    List<LoginUser> userByRealName = sysBaseAPI.getUserByRealName(list1.get(0), list1.get(1));
                    if (CollUtil.isNotEmpty(userByRealName)) {
                        LoginUser loginUser = userByRealName.get(0);
                        responsibilityId.append(loginUser.getId()).append("，");
                        if (!loginUser.getOrgCode().equals(fixedAssetsModel.getOrgCode())) {
                            stringBuilder.append("组织机构不存在该责任人，");
                        }
                    }else {
                        stringBuilder.append("责任人填写格式错误，");
                    }
                } else {
                    stringBuilder.append("责任人填写格式错误，");
                }

            }
            if (responsibilityId.length() > 0) {
                // 截取字符
                responsibilityId = responsibilityId.deleteCharAt(responsibilityId.length() - 1);
                fixedAssetsModel.setResponsibilityId(responsibilityId.toString());
            }
        }

        String locationName = fixedAssetsModel.getLocationName();
        if (StrUtil.isNotBlank(locationName)) {
            List<String> list = StrUtil.splitTrim(locationName, "/");
            if (list.size() == 2) {
                JSONObject lineByName = sysBaseAPI.getLineByName(list.get(0));
                JSONObject stationByName = sysBaseAPI.getStationByName(list.get(1));
                if (ObjectUtil.isNotEmpty(lineByName) && ObjectUtil.isNotEmpty(stationByName)) {
                    fixedAssetsModel.setLocation(stationByName.getString("stationCode"));
                }else if(ObjectUtil.isNotEmpty(lineByName) && ObjectUtil.isEmpty(stationByName)){
                    fixedAssetsModel.setLocation(lineByName.getString("lineCode"));
                } else {
                    stringBuilder.append("存放地点不存在，");
                }
            } else {
                stringBuilder.append("存放地点格式错误，");
            }
        }

        String statusName = fixedAssetsModel.getStatusName();
        if (StrUtil.isNotBlank(statusName)) {
            List<DictModel> post = sysBaseAPI.queryDictItemsByCode("fixed_assets_status");
            DictModel model = Optional.ofNullable(post).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(statusName)).findFirst().orElse(null);
            if (model != null) {
                fixedAssetsModel.setStatus(Convert.toInt(model.getValue()));
            } else {
                stringBuilder.append("系统不存在该启用状态，");
            }
        }

        String unitsName = fixedAssetsModel.getUnitsName();
        if (StrUtil.isNotBlank(unitsName)) {
            List<DictModel> post = sysBaseAPI.queryDictItemsByCode("materian_unit");
            DictModel model = Optional.ofNullable(post).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(unitsName)).findFirst().orElse(null);
            if (model != null) {
                fixedAssetsModel.setUnits(Convert.toInt(model.getValue()));
            } else {
                stringBuilder.append("系统不存在该计量单位，");
            }
        }
        BigDecimal zero = new BigDecimal(0);
        Integer number = fixedAssetsModel.getNumber();
        if (ObjectUtil.isNotEmpty(number) && number < 0) {
            stringBuilder.append("账面数量不能为负数，");
        }
        BigDecimal coveredArea = fixedAssetsModel.getCoveredArea();
        if (ObjectUtil.isNotEmpty(coveredArea) && coveredArea.compareTo(zero) < 0) {
            stringBuilder.append("建筑面积不能为负数，");
        }
        BigDecimal accumulatedDepreciation = fixedAssetsModel.getAccumulatedDepreciation();
        if (ObjectUtil.isNotEmpty(accumulatedDepreciation) && accumulatedDepreciation.compareTo(zero)<0) {
            stringBuilder.append("累计折旧不能为负数，");
        }
        BigDecimal assetOriginal = fixedAssetsModel.getAssetOriginal();
        if (ObjectUtil.isNotEmpty(assetOriginal) && assetOriginal.compareTo(zero)<0) {
            stringBuilder.append("账面原值不能为负数，");
        }

    }

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<FixedAssetsModel> fixedAssetsModels, int successLines, String url, String type) {
        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/fixedAssetsError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>();
            List<Map<String, Object>> mapList = new ArrayList<>();

            for (FixedAssetsModel fixedAssetsModel : fixedAssetsModels) {

                String buildBuyDate = fixedAssetsModel.getBuildBuyDate()!=null?DateUtil.format(fixedAssetsModel.getBuildBuyDate(), "yyyy-MM-dd"):"";
                String startDate =fixedAssetsModel.getStartDate()!=null?DateUtil.format(fixedAssetsModel.getStartDate(), "yyyy-MM-dd"):"";
                Map<String, Object> map = new HashMap<>();
                map.put("assetName", fixedAssetsModel.getAssetName());
                map.put("locationName", fixedAssetsModel.getLocationName());
                map.put("categoryName", fixedAssetsModel.getCategoryName());
                map.put("assetCode", fixedAssetsModel.getAssetCode());
                map.put("orgName", fixedAssetsModel.getOrgName());
                map.put("specification", fixedAssetsModel.getSpecification());
                map.put("number", fixedAssetsModel.getNumber());
                map.put("houseNumber", fixedAssetsModel.getHouseNumber());
                map.put("buildBuyDate", buildBuyDate);
                map.put("coveredArea", fixedAssetsModel.getCoveredArea());
                map.put("unitsName", fixedAssetsModel.getUnitsName());
                map.put("accumulatedDepreciation", fixedAssetsModel.getAccumulatedDepreciation());
                map.put("assetOriginal", fixedAssetsModel.getAssetOriginal());
                map.put("responsibilityName", fixedAssetsModel.getResponsibilityName());
                map.put("statusName", fixedAssetsModel.getStatusName());
                map.put("depreciableLife", fixedAssetsModel.getDepreciableLife());
                map.put("durableYears", fixedAssetsModel.getDurableYears());
                map.put("startDate", startDate);
                map.put("mistake", fixedAssetsModel.getMistake());
                mapList.add(map);
            }
            errorMap.put("maplist", mapList);

            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            String fileName = "固定资产导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
            url = File.separator+"errorExcelFiles"+ File.separator+fileName;
            workbook.write(out);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, true, url);
    }


    @Override
    public void exportFixedAssetsXls(HttpServletRequest request, HttpServletResponse response,FixedAssetsDTO fixedAssetsDTO) {
        Page<FixedAssetsDTO> pageList = new Page<>(1, Integer.MAX_VALUE);
        Page<FixedAssetsDTO> list = this.pageList(pageList, fixedAssetsDTO);
        List<FixedAssetsDTO> records = list.getRecords();
        List<FixedAssetsDTO> exportList = null;
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = records.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = records;
        }

        List<FixedAssetsModel> models = new ArrayList<>();
        if (CollUtil.isNotEmpty(exportList)) {
            for (FixedAssetsDTO record : exportList) {
                FixedAssetsModel fixedAssetsModel = new FixedAssetsModel();
                BeanUtil.copyProperties(record,fixedAssetsModel);
                String categoryName = sysBaseAPI.translateDictFromTable("fixed_assets_category", "category_name", "category_code", record.getCategoryCode());
                String departName = sysBaseAPI.translateDictFromTable("sys_depart", "depart_name", "org_code", record.getOrgCode());
                String unitsName = sysBaseAPI.translateDict("materian_unit", Convert.toStr(record.getUnits()));
                String statusName = sysBaseAPI.translateDict("fixed_assets_status", Convert.toStr(record.getStatus()));
                fixedAssetsModel.setCategoryName(categoryName);
                fixedAssetsModel.setOrgName(departName);
                fixedAssetsModel.setUnitsName(unitsName);
                fixedAssetsModel.setStatusName(statusName);
                models.add(fixedAssetsModel);
            }
        }
        Workbook wb = ExcelExportUtil.exportExcel( new ExportParams("固定资产报表",null, ExcelType.XSSF),FixedAssetsModel.class,models);
        String fileName = "固定资产";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            wb.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
