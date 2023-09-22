package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.dto.SparePartRequisitionDetailDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionAddReqDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionListReqDTO;
import com.aiurt.modules.sparepart.entity.dto.resp.SparePartRequisitionListRespDTO;
import com.aiurt.modules.sparepart.service.SparePartRequisitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 三级库申领的service的实现类，因为用到的实体类是领料单，因此不继承ServiceImpl
 *
 * @author 华宜威
 * @date 2023-09-21 09:52:12
 */
@Service
public class SparePartRequisitionServiceImpl implements SparePartRequisitionService {

    @Autowired
    private IMaterialRequisitionService materialRequisitionService;
    @Autowired
    private IMaterialRequisitionDetailService materialRequisitionDetailService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到当前为未登录状态，请先登录！");
        }
        // 三级库申领，添加一条数据到领料单
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, materialRequisition);
        // 因为是三级库申领，所以有一些信息是写死的
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
        List<SparePartRequisitionDetailDTO> requisitionDetailDTOList = sparePartRequisitionAddReqDTO.getSparePartRequisitionDetailDTOS();
        List<MaterialRequisitionDetail> requisitionDetailList = requisitionDetailDTOList.stream().map(detailDTO -> {
            MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
            BeanUtils.copyProperties(detailDTO, requisitionDetail);
            requisitionDetail.setMaterialRequisitionId(materialRequisition.getId());
            return requisitionDetail;
        }).collect(Collectors.toList());
        materialRequisitionDetailService.saveBatch(requisitionDetailList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO){
        // 将DTO转化成实体类，并更新领料单
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, materialRequisition);
        materialRequisitionService.updateById(materialRequisition);
        // 物资清单，删除原来的，再新增
        LambdaQueryWrapper<MaterialRequisitionDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialRequisitionDetail::getMaterialRequisitionId, materialRequisition.getId());
        materialRequisitionDetailService.remove(queryWrapper);
        // 再保存物资清单
        List<SparePartRequisitionDetailDTO> requisitionDetailDTOList = sparePartRequisitionAddReqDTO.getSparePartRequisitionDetailDTOS();
        List<MaterialRequisitionDetail> requisitionDetailList = requisitionDetailDTOList.stream().map(detailDTO -> {
            MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
            BeanUtils.copyProperties(detailDTO, requisitionDetail);
            requisitionDetail.setMaterialRequisitionId(materialRequisition.getId());
            return requisitionDetail;
        }).collect(Collectors.toList());
        materialRequisitionDetailService.saveBatch(requisitionDetailList);
    }

    @Override
    public Page<SparePartRequisitionListRespDTO> pageList(SparePartRequisitionListReqDTO sparePartRequisitionListReqDTO) {
        // 将请求DTO转化成实体类进行查询
        int pageNo = sparePartRequisitionListReqDTO.getPageNo();
        int pageSize = sparePartRequisitionListReqDTO.getPageSize();
        Page<MaterialRequisition> page = new Page<>(pageNo, pageSize);
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(sparePartRequisitionListReqDTO, materialRequisition);
        QueryWrapper<MaterialRequisition> queryWrapper = QueryGenerator.initQueryWrapper(materialRequisition, null);
        // 时间范围的搜索, DateUtil.beginOfDay不能放到queryWrapper.le()里面，因为在里面DateUtil.beginOfDay总是执行，可能会报空指针
        Date searchBeginTime = sparePartRequisitionListReqDTO.getSearchBeginTime();
        Date searchEndTime = sparePartRequisitionListReqDTO.getSearchEndTime();
        DateTime beginTime = searchBeginTime != null ? DateUtil.beginOfDay(searchBeginTime) : null;
        DateTime endTime = searchEndTime != null ? DateUtil.endOfDay(searchEndTime) : null;
        queryWrapper.lambda().ge(searchBeginTime != null, MaterialRequisition::getApplyTime, beginTime);
        queryWrapper.lambda().le(searchEndTime != null, MaterialRequisition::getApplyTime, endTime);
        // 排序
        queryWrapper.lambda().orderByDesc(MaterialRequisition::getPlanApplyTime).orderByDesc(MaterialRequisition::getId);

        materialRequisitionService.page(page, queryWrapper);

        // 将查询结果转化成响应DTO返回
        Page<SparePartRequisitionListRespDTO> pageList = new Page<>();
        BeanUtils.copyProperties(page, pageList);
        List<SparePartRequisitionListRespDTO> respRecords = page.getRecords().stream().map(record -> {
            SparePartRequisitionListRespDTO respDTO = new SparePartRequisitionListRespDTO();
            BeanUtils.copyProperties(record, respDTO);
            return respDTO;
        }).collect(Collectors.toList());
        pageList.setRecords(respRecords);
        return pageList;
    }
}
