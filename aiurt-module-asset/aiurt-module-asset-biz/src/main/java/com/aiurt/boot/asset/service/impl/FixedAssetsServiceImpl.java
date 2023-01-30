package com.aiurt.boot.asset.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollUtil;
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
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    }

    /**非必填数据校验*/
    private void checkNotRequired(StringBuilder stringBuilder, FixedAssetsModel fixedAssetsModel) {

    }

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<FixedAssetsModel> fixedAssetsModels, int successLines, Object o, String type) {
        return null;
    }
}
