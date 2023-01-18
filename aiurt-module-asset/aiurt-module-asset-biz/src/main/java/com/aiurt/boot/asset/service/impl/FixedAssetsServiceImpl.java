package com.aiurt.boot.asset.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.asset.dto.FixedAssetsDTO;
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
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.mapper.FixedAssetsCheckRecordMapper;
import com.aiurt.common.constant.CommonConstant;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        }
        //资产分类查询
        if(ObjectUtil.isNotEmpty(fixedAssetsDTO.getCategoryCode())){
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
                    List<String> categoryCodes =checkCategoryList.stream().map(FixedAssetsCheckCategory::getCategoryCode).collect(Collectors.toList());
                    List<FixedAssetsCheckDept> checkDeptList = checkDeptMapper.selectList(new LambdaQueryWrapper<FixedAssetsCheckDept>().eq(FixedAssetsCheckDept::getCheckId, check.getId()));
                    List<String> orgCodes =checkDeptList.stream().map(FixedAssetsCheckDept::getOrgCode).collect(Collectors.toList());
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
        List<FixedAssetsCheck> fixedAssetsChecks = assetsCheckMapper.selectList(new LambdaQueryWrapper<FixedAssetsCheck>().eq(FixedAssetsCheck::getStatus, 4).eq(FixedAssetsCheck::getDelFlag, CommonConstant.DEL_FLAG_0));
        for (FixedAssetsCheck assetsCheck : fixedAssetsChecks) {
            FixedAssetsCheckRecordDTO assetsCheckDTO = new FixedAssetsCheckRecordDTO();
            BeanUtils.copyProperties(assetsCheck, assetsCheckDTO);
            //2.获取盘点任务下的资产编码一样的盘点记录
            LambdaQueryWrapper<FixedAssetsCheckRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FixedAssetsCheckRecord::getCheckId, assetsCheck.getId());
            queryWrapper.eq(FixedAssetsCheckRecord::getAssetCode, dto.getAssetCode());
            queryWrapper.last("limit 1");
            FixedAssetsCheckRecord record = recordMapper.selectOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(record)) {
                assetsCheckDTO.setNumber(record.getNumber());
                assetsCheckDTO.setActualSurplusLoss(record.getActualNumber() - record.getNumber());
                assetsCheckDTO.setOneselfAssetNumber(record.getOneselfAssetNumber());
                assetsCheckDTO.setOthersAssetNumber(record.getOthersAssetNumber());
                assetsCheckDTO.setAccumulatedDepreciation(record.getAccumulatedDepreciation());
                assetsCheckDTO.setLeisureAssetNumber(record.getLeisureAssetNumber());
                assetsCheckDTO.setLeisureArea(record.getLeisureArea());
                assetsCheckDTO.setHypothecate(record.getHypothecate());
                assetsCheckDTO.setRemark(record.getRemark());
                assetsCheckDTOS.add(assetsCheckDTO);
            }
        }
        dto.setRecordDTOList(assetsCheckDTOS);
        return Result.OK(dto);
    }
}
