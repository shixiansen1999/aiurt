package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.dto.StockLevel2RequisitionDetailDTO;
import com.aiurt.modules.stock.service.StockLevel2RequisitionService;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 二级库申领的service的实现类，因为用到的实体类是领料单，因此不继承ServiceImpl
 *
 * @author 华宜威
 * @date 2023-09-21 09:52:12
 */
@Service
public class StockLevel2RequisitionServiceImpl implements StockLevel2RequisitionService {

    @Autowired
    private IMaterialRequisitionService materialRequisitionService;
    @Autowired
    private IMaterialRequisitionDetailService materialRequisitionDetailService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到当前为未登录状态，请先登录！");
        }
        // 二级库申领，添加一条数据到领料单
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(stockLevel2RequisitionAddReqDTO, materialRequisition);
        // 因为是二级库申领，所以有一些信息是写死的
        materialRequisition.setMaterialRequisitionType(MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL2);
        materialRequisition.setApplyType(MaterialRequisitionConstant.APPLY_TYPE_SPECIAL);
        materialRequisition.setStatus(MaterialRequisitionConstant.STATUS_TO_BE_SUBMITTED);
        materialRequisition.setCommitStatus(MaterialRequisitionConstant.COMMIT_STATUS_NOT_SUBMITTED);
        // 添加其他信息
        Date applyTime = new Date();
        materialRequisition.setApplyTime(applyTime);
        materialRequisition.setApplyUserId(loginUser.getId());
        String name = loginUser.getOrgName() + "-" + loginUser.getRealname() + "-" +
                DateUtil.format(applyTime, "yyyy-MM-dd") + "-" + "领料单";
        materialRequisition.setName(name);
        // TODO: 申领单号后面修改
        materialRequisition.setCode("LY001");
        // 先保存领料单
        materialRequisitionService.save(materialRequisition);
        // 再保存物资清单
        List<StockLevel2RequisitionDetailDTO> requisitionDetailDTOList = stockLevel2RequisitionAddReqDTO.getStockLevel2RequisitionDetailDTOList();
        List<MaterialRequisitionDetail> requisitionDetailList = requisitionDetailDTOList.stream().map(detailDTO -> {
            MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
            BeanUtils.copyProperties(detailDTO, requisitionDetail);
            requisitionDetail.setMaterialRequisitionId(materialRequisition.getId());
            return requisitionDetail;
        }).collect(Collectors.toList());
        materialRequisitionDetailService.saveBatch(requisitionDetailList);
    }
}
