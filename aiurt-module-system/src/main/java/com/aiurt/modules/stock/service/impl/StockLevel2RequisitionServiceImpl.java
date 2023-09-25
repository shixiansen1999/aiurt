package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.constants.FlowApprovalType;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskCompleteDTO;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.stock.dto.StockLevel2RequisitionDetailDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionListReqDTO;
import com.aiurt.modules.stock.dto.resp.StockLevel2RequisitionListRespDTO;
import com.aiurt.modules.stock.mapper.StockLevel2RequisitionMapper;
import com.aiurt.modules.stock.service.StockLevel2RequisitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 二级库申领的service的实现类，因为用到的实体类是领料单，因此不继承ServiceImpl
 *
 * @author 华宜威
 * @date 2023-09-21 09:52:12
 */
@Service
public class StockLevel2RequisitionServiceImpl implements StockLevel2RequisitionService, IFlowableBaseUpdateStatusService {

    @Autowired
    private IMaterialRequisitionService materialRequisitionService;
    @Autowired
    private IMaterialRequisitionDetailService materialRequisitionDetailService;
    @Autowired
    private StockLevel2RequisitionMapper stockLevel2RequisitionMapper;
    @Autowired
    private FlowBaseApi flowBaseApi;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String add(StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO) {
        // 二级库申领，添加一条数据到领料单
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(stockLevel2RequisitionAddReqDTO, materialRequisition);
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
        return materialRequisition.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String edit(StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO){
        // 将DTO转化成实体类，并更新领料单
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(stockLevel2RequisitionAddReqDTO, materialRequisition);
        materialRequisitionService.updateById(materialRequisition);
        // 物资清单，删除原来的，再新增
        LambdaQueryWrapper<MaterialRequisitionDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialRequisitionDetail::getMaterialRequisitionId, materialRequisition.getId());
        materialRequisitionDetailService.remove(queryWrapper);
        // 再保存物资清单
        List<StockLevel2RequisitionDetailDTO> requisitionDetailDTOList = stockLevel2RequisitionAddReqDTO.getStockLevel2RequisitionDetailDTOList();
        List<MaterialRequisitionDetail> requisitionDetailList = requisitionDetailDTOList.stream().map(detailDTO -> {
            MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
            BeanUtils.copyProperties(detailDTO, requisitionDetail);
            requisitionDetail.setMaterialRequisitionId(materialRequisition.getId());
            return requisitionDetail;
        }).collect(Collectors.toList());
        materialRequisitionDetailService.saveBatch(requisitionDetailList);
        return materialRequisition.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submit(StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO) {
        // 申领单id
        String id;
        // 将状态更改为审核中，并保存/编辑
        stockLevel2RequisitionAddReqDTO.setStatus(MaterialRequisitionConstant.STATUS_REVIEWING);
        if (StrUtil.isEmpty(stockLevel2RequisitionAddReqDTO.getId())) {
            // 没有id，是保存
            id = this.add(stockLevel2RequisitionAddReqDTO);
            stockLevel2RequisitionAddReqDTO.setId(id);
        }else{
            // 有id，是编辑
            id = this.edit(stockLevel2RequisitionAddReqDTO);
            stockLevel2RequisitionAddReqDTO.setId(id);
        }

        // 发起流程
        StartBpmnDTO startBpmnDto  = new StartBpmnDTO();
        startBpmnDto.setModelKey("stock_level2_requisition");
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        startBpmnDto.setBusData(map);
        FlowTaskCompleteCommentDTO flowTaskCompleteCommentDTO = new FlowTaskCompleteCommentDTO();
        startBpmnDto.setFlowTaskCompleteDTO(flowTaskCompleteCommentDTO);
        flowTaskCompleteCommentDTO.setApprovalType(FlowApprovalType.SAVE);
        flowBaseApi.startAndTakeFirst(startBpmnDto);
        // 将流程通过提交申领单阶段
        TaskCompleteDTO taskCompleteDTO = stockLevel2RequisitionMapper.getFlowDataById(id);
        Map<String,Object> detailMap = new HashMap<>(1);
        detailMap.put("id",id);
        taskCompleteDTO.setBusData(detailMap);
        FlowTaskCompleteCommentDTO commentDTO = new FlowTaskCompleteCommentDTO();
        commentDTO.setApprovalType(FlowApprovalType.AGREE);
        taskCompleteDTO.setFlowTaskCompleteDTO(commentDTO);
        flowBaseApi.completeTask(taskCompleteDTO);
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    /**
     * 此方法是给流程调用来改变状态值的
     * @param updateStateEntity 流程状态类
     */
    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        // businessKey就是申领单id
        String id = updateStateEntity.getBusinessKey();
        LambdaUpdateWrapper<MaterialRequisition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MaterialRequisition::getId, id);
        int states = updateStateEntity.getStates();
        switch (states){
            case 1:
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_REVIEWING);
                break;
            case 2:
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_PASSED);
                break;
            case 3:
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_REJECTED);
                break;
            case 4:
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_COMPLETED);
                break;
            default:
                return;
        }
        materialRequisitionService.update(updateWrapper);
    }

    /**
     * 此方法是给流程调用来将businessKey设置为申领单id
     * 从而给act_hi_procinst的BUSINESS_KEY_设置为申领单id
     * @param stockLevel2RequisitionAddReqDTO
     * @return
     */
    public String startProcess(StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO) {
        return stockLevel2RequisitionAddReqDTO.getId();
    }

    @Override
    public Page<StockLevel2RequisitionListRespDTO> pageList(StockLevel2RequisitionListReqDTO stockLevel2RequisitionListReqDTO) {
        // 将请求DTO转化成实体类进行查询
        int pageNo = stockLevel2RequisitionListReqDTO.getPageNo();
        int pageSize = stockLevel2RequisitionListReqDTO.getPageSize();
        Page<MaterialRequisition> page = new Page<>(pageNo, pageSize);
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(stockLevel2RequisitionListReqDTO, materialRequisition);
        QueryWrapper<MaterialRequisition> queryWrapper = QueryGenerator.initQueryWrapper(materialRequisition, null);
        // 时间范围的搜索, DateUtil.beginOfDay不能放到queryWrapper.le()里面，因为在里面DateUtil.beginOfDay总是执行，可能会报空指针
        Date searchBeginTime = stockLevel2RequisitionListReqDTO.getSearchBeginTime();
        Date searchEndTime = stockLevel2RequisitionListReqDTO.getSearchEndTime();
        DateTime beginTime = searchBeginTime != null ? DateUtil.beginOfDay(searchBeginTime) : null;
        DateTime endTime = searchEndTime != null ? DateUtil.endOfDay(searchEndTime) : null;
        queryWrapper.lambda().ge(searchBeginTime != null, MaterialRequisition::getApplyTime, beginTime);
        queryWrapper.lambda().le(searchEndTime != null, MaterialRequisition::getApplyTime, endTime);
        // 排序
        queryWrapper.lambda().orderByDesc(MaterialRequisition::getPlanApplyTime).orderByDesc(MaterialRequisition::getId);

        materialRequisitionService.page(page, queryWrapper);

        // 将查询结果转化成响应DTO返回
        Page<StockLevel2RequisitionListRespDTO> pageList = new Page<>();
        BeanUtils.copyProperties(page, pageList);
        List<StockLevel2RequisitionListRespDTO> respRecords = page.getRecords().stream().map(record -> {
            StockLevel2RequisitionListRespDTO respDTO = new StockLevel2RequisitionListRespDTO();
            BeanUtils.copyProperties(record, respDTO);
            return respDTO;
        }).collect(Collectors.toList());
        pageList.setRecords(respRecords);
        return pageList;
    }
}
