package com.aiurt.boot.check.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.entity.FixedAssetsCheckCategory;
import com.aiurt.boot.check.entity.FixedAssetsCheckDept;
import com.aiurt.boot.check.entity.FixedAssetsCheckDetail;
import com.aiurt.boot.check.mapper.FixedAssetsCheckDetailMapper;
import com.aiurt.boot.check.service.IFixedAssetsCheckCategoryService;
import com.aiurt.boot.check.service.IFixedAssetsCheckDeptService;
import com.aiurt.boot.check.service.IFixedAssetsCheckDetailService;
import com.aiurt.boot.check.service.IFixedAssetsCheckService;
import com.aiurt.boot.check.vo.FixedAssetsCheckDetailVO;
import com.aiurt.boot.constant.FixedAssetsConstant;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets_check_detail
 * @Author: aiurt
 * @Date: 2023-01-17
 * @Version: V1.0
 */
@Service
public class FixedAssetsCheckDetailServiceImpl extends ServiceImpl<FixedAssetsCheckDetailMapper, FixedAssetsCheckDetail> implements IFixedAssetsCheckDetailService {

    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IFixedAssetsCheckService fixedAssetsCheckService;
    @Autowired
    private IFixedAssetsCheckCategoryService fixedAssetsCheckCategoryService;
    @Autowired
    private IFixedAssetsCheckDeptService fixedAssetsCheckDeptService;
    @Autowired
    private FixedAssetsCheckDetailMapper fixedAssetsCheckDetailMapper;

    @Override
    public IPage<FixedAssetsCheckDetailVO> queryPageList(Page<FixedAssetsCheckDetailVO> page, String id) {
        FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.getById(id);
        Page<FixedAssetsCheckDetailVO> pageList = null;
        if (FixedAssetsConstant.STATUS_0.equals(fixedAssetsCheck.getStatus())) {
            List<FixedAssetsCheckCategory> categoryList = fixedAssetsCheckCategoryService.lambdaQuery()
                    .eq(FixedAssetsCheckCategory::getCheckId, id).list();
            List<FixedAssetsCheckDept> deptList = fixedAssetsCheckDeptService.lambdaQuery()
                    .eq(FixedAssetsCheckDept::getCheckId, id).list();
            List<String> categoryCodes = categoryList.stream().map(FixedAssetsCheckCategory::getCategoryCode).collect(Collectors.toList());
            List<String> orgCodes = deptList.stream().map(FixedAssetsCheckDept::getOrgCode).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(categoryCodes) || CollectionUtil.isEmpty(orgCodes)) {
                return page;
            }
            pageList = fixedAssetsCheckDetailMapper.queryPageList(page, categoryCodes, orgCodes);
        } else {
            LambdaQueryWrapper<FixedAssetsCheckDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FixedAssetsCheckDetail::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(FixedAssetsCheckDetail::getCheckId, id);
            Page<FixedAssetsCheckDetail> detailPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
            List<FixedAssetsCheckDetail> records = detailPage.getRecords();
            FixedAssetsCheckDetailVO detailVO = null;
            List<FixedAssetsCheckDetailVO> detailList = new ArrayList<>();
            for (FixedAssetsCheckDetail record : records) {
                detailVO = new FixedAssetsCheckDetailVO();
                BeanUtils.copyProperties(record, detailVO);
                detailList.add(detailVO);
            }
            page.setRecords(detailList);
            pageList = page;
        }
        for (FixedAssetsCheckDetailVO record : pageList.getRecords()) {
            String position = sysBaseApi.getPosition(record.getLocation());
            record.setLocationName(position);
        }
        return pageList;
    }
}
