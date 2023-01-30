package com.aiurt.boot.asset.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
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
import com.aiurt.boot.check.entity.FixedAssetsCheckCategory;
import com.aiurt.boot.check.entity.FixedAssetsCheckDept;
import com.aiurt.boot.check.mapper.FixedAssetsCheckCategoryMapper;
import com.aiurt.boot.check.mapper.FixedAssetsCheckDeptMapper;
import com.aiurt.boot.check.mapper.FixedAssetsCheckMapper;
import com.aiurt.boot.constant.FixedAssetsConstant;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.mapper.FixedAssetsCheckRecordMapper;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Service
public class FixedAssetsServiceImpl extends ServiceImpl<FixedAssetsMapper, FixedAssets> implements IFixedAssetsService {
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
        List<String> stationCodeByLineCode = sysBaseAPI.getStationCodeByLineCode(fixedAssetsDTO.getLocation());
        if (CollUtil.isNotEmpty(stationCodeByLineCode)) {
            stationCodeByLineCode.add(fixedAssetsDTO.getLocation());
            fixedAssetsDTO.setLineStations(stationCodeByLineCode);
            fixedAssetsDTO.setIsLine(true);
        } else {
            fixedAssetsDTO.setIsLine(false);
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
            String[] status = {"1", "2", "3"};
            List<FixedAssetsCheck> assetsChecks = assetsCheckMapper.selectList(new LambdaQueryWrapper<FixedAssetsCheck>().in(FixedAssetsCheck::getStatus, status).eq(FixedAssetsCheck::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(list)) {
                for (FixedAssetsCheck check : assetsChecks) {
                    List<FixedAssetsCheckCategory> checkCategoryList = checkCategoryMapper.selectList(new LambdaQueryWrapper<FixedAssetsCheckCategory>().eq(FixedAssetsCheckCategory::getCheckId, check.getId()));
                    List<String> categoryCodes = checkCategoryList.stream().map(FixedAssetsCheckCategory::getCategoryCode).collect(Collectors.toList());
                    List<FixedAssetsCheckDept> checkDeptList = checkDeptMapper.selectList(new LambdaQueryWrapper<FixedAssetsCheckDept>().eq(FixedAssetsCheckDept::getCheckId, check.getId()));
                    List<String> orgCodes = checkDeptList.stream().map(FixedAssetsCheckDept::getOrgCode).collect(Collectors.toList());
                    boolean haveOrgCode = orgCodes.contains(dto.getOrgCode());
                    boolean haveCategoryCode = categoryCodes.contains(dto.getCategoryCode());
                    if (haveOrgCode && haveCategoryCode) {
                        dto.setIsNotEdit(false);
                    } else {
                        dto.setIsNotEdit(true);
                    }
                }
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
            LoginUser auditName = sysBaseAPI.getUserById(assetsCheck.getCheckId());
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
                for (FixedAssetsModel fixedAssetsModel : fixedAssetsModels) {
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
                        errorLines++;
                    }
                }
                if (errorLines > 0) {
                    //存在错误，导出错误清单
                    return getErrorExcel(errorLines, errorMessage, fixedAssetsModels, successLines, null, type);
                }

                //校验通过，添加数据
                for (FixedAssetsModel fixedAssetsModel : fixedAssetsModels) {
                    FixedAssets fixedAssets = new FixedAssets();
                    BeanUtils.copyProperties(fixedAssetsModel, fixedAssets);
                    fixedAssets.setOrgCode(fixedAssetsModel.getOrgCode());
                    fixedAssets.setLocation(fixedAssetsModel.getLocation());
                    fixedAssets.setStatus(fixedAssetsModel.getStatus());
                    fixedAssets.setUnits(fixedAssetsModel.getUnits());
                    this.save(fixedAssets);
                }
                return Result.ok("文件导入成功！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Result.ok("文件导入失败！");
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
            String orgCode = null;
            for (String s : list) {
                //根据部门名称和父id找部门
                JSONObject depart = sysBaseAPI.getDepartByNameAndParentId(s, id);
                if (ObjectUtil.isNotNull(depart)) {
                    id = depart.getString("parentId");
                    orgCode = depart.getString("orgCode");
                }else {
                    stringBuilder.append("系统不存在该组织机构，");
                    break;
                }
            }
            fixedAssetsModel.setOrgCode(orgCode);
        }else {
            stringBuilder.append("使用组织机构不能为空，");
        }

        if (number == null) {
            stringBuilder.append("账面数量不能为空，");
        }

    }

    /**非必填数据校验*/
    private void checkNotRequired(StringBuilder stringBuilder, FixedAssetsModel fixedAssetsModel) {
        String locationName = fixedAssetsModel.getLocationName();
        if (StrUtil.isNotBlank(locationName)) {
            List<String> list = StrUtil.splitTrim(locationName, "/");
            if (list.size() != 3) {
                JSONObject lineByName = sysBaseAPI.getLineByName(list.get(0));
                JSONObject stationByName = sysBaseAPI.getStationByName(list.get(1));
                if (ObjectUtil.isNotEmpty(lineByName) && ObjectUtil.isNotEmpty(stationByName)) {
                    JSONObject positionByName = sysBaseAPI.getPositionByName(list.get(2),lineByName.getString("lineCode"),stationByName.getString("stationCode"));
                    if (ObjectUtil.isEmpty(positionByName)) {
                        stringBuilder.append("存放地点不存在，");
                    } else {
                        fixedAssetsModel.setLocation(positionByName.getString("positionCode"));
                    }
                } else {
                    stringBuilder.append("存放地点不存在，");
                }
            } else {
                stringBuilder.append("存放地点格式错误，");
            }
        }

        String statusName = fixedAssetsModel.getStatusName();
        if (StrUtil.isNotEmpty(statusName)) {
            List<DictModel> post = sysBaseAPI.queryDictItemsByCode("fixed_assets_status");
            DictModel model = Optional.ofNullable(post).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(statusName)).findFirst().orElse(null);
            if (model != null) {
                fixedAssetsModel.setStatus(Convert.toInt(model.getValue()));
            } else {
                stringBuilder.append("系统不存在该启用状态，");
            }
        }

        String unitsName = fixedAssetsModel.getUnitsName();
        if (StrUtil.isNotEmpty(unitsName)) {
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
        if (number < 0) {
            stringBuilder.append("账面数量不能为负数，");
        }
        BigDecimal coveredArea = fixedAssetsModel.getCoveredArea();
        if (coveredArea.compareTo(zero)<0) {
            stringBuilder.append("建筑面积不能为负数，");
        }
        BigDecimal accumulatedDepreciation = fixedAssetsModel.getAccumulatedDepreciation();
        if (accumulatedDepreciation.compareTo(zero)<0) {
            stringBuilder.append("累计折旧不能为负数，");
        }
        BigDecimal assetOriginal = fixedAssetsModel.getAssetOriginal();
        if (assetOriginal.compareTo(zero)<0) {
            stringBuilder.append("账面原值不能为负数，");
        }

    }

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<FixedAssetsModel> fixedAssetsModels, int successLines, String url, String type) {
        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/emergencyTrainingProgramError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>();
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            for (FixedAssetsModel fixedAssetsModel : fixedAssetsModels) {
                map.put("assetName", fixedAssetsModel.getAssetName());
                map.put("locationName", fixedAssetsModel.getLocationName());
                map.put("categoryName", fixedAssetsModel.getCategoryName());
                map.put("assetCode", fixedAssetsModel.getAssetCode());
                map.put("orgName", fixedAssetsModel.getOrgName());
                map.put("specification", fixedAssetsModel.getSpecification());
                map.put("number", fixedAssetsModel.getNumber());
                map.put("houseNumber", fixedAssetsModel.getHouseNumber());
                map.put("buildBuyDate", fixedAssetsModel.getBuildBuyDate());
                map.put("coveredArea", fixedAssetsModel.getCoveredArea());
                map.put("unitsName", fixedAssetsModel.getUnitsName());
                map.put("accumulatedDepreciation", fixedAssetsModel.getAccumulatedDepreciation());
                map.put("assetOriginal", fixedAssetsModel.getAssetOriginal());
                map.put("responsibilityName", fixedAssetsModel.getResponsibilityName());
                map.put("statusName", fixedAssetsModel.getStatusName());
                map.put("depreciableLife", fixedAssetsModel.getDepreciableLife());
                map.put("durableYears", fixedAssetsModel.getDurableYears());
                map.put("startDate", fixedAssetsModel.getStartDate());
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
}
