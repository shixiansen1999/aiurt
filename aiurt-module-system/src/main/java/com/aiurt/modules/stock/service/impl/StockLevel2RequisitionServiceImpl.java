package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.constants.FlowApprovalType;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskCompleteDTO;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.mapper.MaterialRequisitionDetailMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionAddReqDTO;
import com.aiurt.modules.sparepart.service.impl.SparePartRequisitionServiceImpl;
import com.aiurt.modules.stock.dto.StockLevel2RequisitionDetailDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionListReqDTO;
import com.aiurt.modules.stock.dto.resp.StockLevel2RequisitionListRespDTO;
import com.aiurt.modules.stock.mapper.StockLevel2RequisitionMapper;
import com.aiurt.modules.stock.service.IStockInOrderLevel2Service;
import com.aiurt.modules.stock.service.StockLevel2RequisitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
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
    private MaterialRequisitionDetailMapper materialRequisitionDetailMapper;
    @Autowired
    private IMaterialRequisitionDetailService materialRequisitionDetailService;
    @Autowired
    private StockLevel2RequisitionMapper stockLevel2RequisitionMapper;
    @Autowired
    private IStockInOrderLevel2Service stockInOrderLevel2Service;
    @Autowired
    private FlowBaseApi flowBaseApi;
    @Autowired
    private IMaterialBaseService materialBaseService;
    @Autowired
    @Lazy
    private SparePartRequisitionServiceImpl sparePartRequisitionService;

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
        List<MaterialRequisitionDetail> requisitionDetailList = this.dealRequisitionDetail(requisitionDetailDTOList, materialRequisition.getId());
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
        List<MaterialRequisitionDetail> requisitionDetailList = this.dealRequisitionDetail(requisitionDetailDTOList, materialRequisition.getId());
        materialRequisitionDetailService.saveBatch(requisitionDetailList);
        return materialRequisition.getId();
    }

    /**
     * 将申领单物资清单的DTO列表转换成申领单物资清单实体类对象的列表，主要是为了填充一些必要字段
     * 主要在添加和编辑方法上使用
     * @param requisitionDetailDTOList 申领单物资清单的DTO列表，其中每个DTO列表必须要有物资的编码code或者物资的id(物资主数据的id)
     * @param materialRequisitionId 申领单物资清单所关联的申领单id
     * @return List<MaterialRequisitionDetail> 返回申领单物资清单实体类对象的列表
     */
    public List<MaterialRequisitionDetail> dealRequisitionDetail(List<StockLevel2RequisitionDetailDTO> requisitionDetailDTOList, String materialRequisitionId){
        // 要考虑到有些申领单物资清单的DTO列表只传物资code，没传物资id以及其他必要参数的情况
        // 必要参数包括：物资id、物资编号、物资名称、单位、参考单价、参考总价
        List<String> materialsIdList = requisitionDetailDTOList.stream()
                .map(StockLevel2RequisitionDetailDTO::getMaterialsId).collect(Collectors.toList());
        List<String> materialsCodeList = requisitionDetailDTOList.stream()
                .filter(m -> !materialsIdList.contains(m.getMaterialsId()))
                .map(StockLevel2RequisitionDetailDTO::getMaterialsCode).collect(Collectors.toList());
        // 如物资清单的code和id都没有,保存的清单只有DTO获取的数据
        if (CollUtil.isEmpty(materialsIdList) && CollUtil.isEmpty(materialsCodeList)) {
            return requisitionDetailDTOList.stream().map(detailDTO -> {
                MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
                BeanUtils.copyProperties(detailDTO, requisitionDetail);
                requisitionDetail.setMaterialRequisitionId(materialRequisitionId);
                return requisitionDetail;
            }).collect(Collectors.toList());
        }
        // 只有物资的id列表或者code列表不全为空时，才查询
        LambdaQueryWrapper<MaterialBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollUtil.isNotEmpty(materialsIdList),MaterialBase::getId, materialsIdList);
        queryWrapper.in(CollUtil.isNotEmpty(materialsCodeList),MaterialBase::getCode, materialsCodeList);
        queryWrapper.eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<MaterialBase> materialBaseList = materialBaseService.list(queryWrapper);
        // 将查询到的materialBaseList做成一个id为key的map和一个根据code为key的map
        Map<String, MaterialBase> materialBaseIdMap = materialBaseList.stream().collect(Collectors.toMap(MaterialBase::getId, v -> v));
        Map<String, MaterialBase> materialBaseCodeMap = materialBaseList.stream().collect(Collectors.toMap(MaterialBase::getCode, v -> v));
        // 保存的物资清单
        return requisitionDetailDTOList.stream().map(detailDTO -> {
            // 每个物资清单的物资主数据
            String materialsId = detailDTO.getMaterialsId();
            String materialsCode = detailDTO.getMaterialsCode();
            MaterialBase materialBase = materialsId != null ? materialBaseIdMap.get(materialsId) : materialBaseCodeMap.get(materialsCode);

            MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
            BeanUtils.copyProperties(detailDTO, requisitionDetail);
            requisitionDetail.setMaterialRequisitionId(materialRequisitionId);

            if (materialBase != null) {
                requisitionDetail.setMaterialsId(materialBase.getId());
                requisitionDetail.setMaterialsCode(materialBase.getCode());
                requisitionDetail.setMaterialsName(materialBase.getName());
                requisitionDetail.setUnit(materialBase.getUnit());
                requisitionDetail.setPrice(new BigDecimal(materialBase.getPrice()));
                requisitionDetail.setTotalPrices(requisitionDetail.getPrice().multiply(BigDecimal.valueOf(detailDTO.getApplyNum())));
            }
            return requisitionDetail;
        }).collect(Collectors.toList());
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

        String processInstanceId = stockLevel2RequisitionAddReqDTO.getProcessInstanceId();
        if(StrUtil.isEmpty(processInstanceId)){
            // 还没有流程，则发起流程
            StartBpmnDTO startBpmnDto  = new StartBpmnDTO();
            startBpmnDto.setModelKey("stock_level2_requisition");
            Map<String, Object> map = BeanUtil.beanToMap(stockLevel2RequisitionAddReqDTO);
            startBpmnDto.setBusData(map);
            FlowTaskCompleteCommentDTO flowTaskCompleteCommentDTO = new FlowTaskCompleteCommentDTO();
            startBpmnDto.setFlowTaskCompleteDTO(flowTaskCompleteCommentDTO);
            flowTaskCompleteCommentDTO.setApprovalType(FlowApprovalType.SAVE);
            flowBaseApi.startAndTakeFirst(startBpmnDto);
        }

        // 将流程通过提交申领单阶段
        TaskCompleteDTO taskCompleteDTO = stockLevel2RequisitionMapper.getFlowDataById(id);
        // 因为是提交，且上面以及有保存/提交了，就不用再保存/编辑业务数据了
        // Map<String,Object> detailMap = new HashMap<>(1);
        // detailMap.put("id",id);
        // taskCompleteDTO.setBusData(detailMap);
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        // businessKey就是申领单id
        String id = updateStateEntity.getBusinessKey();
        LambdaUpdateWrapper<MaterialRequisition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MaterialRequisition::getId, id);
        int states = updateStateEntity.getStates();
        switch (states){
            case 1:
                // 申领单提交
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_REVIEWING);
                break;
            case 2:
                // 主任审核通过
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_PASSED);
                break;
            case 3:
                // 主任审核驳回
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_REJECTED);
                break;
            case 4:
                // 审领人确认
                updateWrapper.set(MaterialRequisition::getStatus, MaterialRequisitionConstant.STATUS_COMPLETED);
                // 将申领单的物资清单中，将已入库数量改成申请数量
                materialRequisitionDetailMapper.updateActualNumByMaterialRequisitionId(id);
                // 自动生成一条已确认的入库记录，并修改库存
                try {
                    stockInOrderLevel2Service.addCompleteOrderFromRequisition(id);
                }catch (Exception e){
                    throw new AiurtBootException("申领人确认失败，无法自动生成一条已确认的入库记录");
                }
                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                //如果该二级库申领是由三级库产生，则补充完整二级库出库，三级库入出库
                //获取二级库领用单
                MaterialRequisition byId = materialRequisitionService.getById(id);
                QueryWrapper<MaterialRequisitionDetail> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(MaterialRequisitionDetail::getMaterialRequisitionId, id).eq(MaterialRequisitionDetail::getDelFlag, CommonConstant.DEL_FLAG_0);
                List<MaterialRequisitionDetail> materialRequisitionDetails = materialRequisitionDetailService.getBaseMapper().selectList(wrapper);
                //根据不同申领单补充出入库记录
                if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(byId.getMaterialRequisitionType())) {
                    //获取维修领用单
                    QueryWrapper<MaterialRequisition>  queryWrapper = new QueryWrapper<>();
                    MaterialRequisition one = materialRequisitionService.getOne(queryWrapper.lambda()
                            .eq(MaterialRequisition::getIsUsed, MaterialRequisitionConstant.UNUSED)
                            .eq(MaterialRequisition::getId,byId.getMaterialRequisitionPid())
                            .eq(MaterialRequisition::getDelFlag, CommonConstant.DEL_FLAG_0));
                    SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO = new SparePartRequisitionAddReqDTO();
                    BeanUtils.copyProperties(one, sparePartRequisitionAddReqDTO);
                    //三级库申领
                    sparePartRequisitionService.addLevel3Requisition(materialRequisitionDetails, sparePartRequisitionAddReqDTO, one);
                    //生成三级库出库
                    QueryWrapper<MaterialRequisitionDetail> wrapper2 = new QueryWrapper<>();
                    wrapper2.lambda().eq(MaterialRequisitionDetail::getMaterialRequisitionId, one.getId()).eq(MaterialRequisitionDetail::getDelFlag, CommonConstant.DEL_FLAG_0);
                    List<MaterialRequisitionDetail> requisitionDetails = materialRequisitionDetailService.getBaseMapper().selectList(wrapper2);
                    sparePartRequisitionService.addSparePartOutOrder(requisitionDetails, loginUser, one,false);
                    //维修申领单变更为已完成,
                    one.setIsUsed(MaterialRequisitionConstant.IS_USED);
                    one.setStatus(MaterialRequisitionConstant.STATUS_COMPLETED);
                    materialRequisitionService.updateById(one);
                    // 再保存物资清单
                    for (MaterialRequisitionDetail materialRequisitionDetail : requisitionDetails) {
                        materialRequisitionDetail.setActualNum(materialRequisitionDetail.getApplyNum());
                    }
                    materialRequisitionDetailService.updateBatchById(requisitionDetails);
                } else if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(byId.getMaterialRequisitionType())) {
                    //二级库出库
                    String outOrderCode = sparePartRequisitionService.addStockOutOrderLevel2(byId, materialRequisitionDetails,false);
                    //三级库入库
                    sparePartRequisitionService.addSparePartInOrder(materialRequisitionDetails, byId, outOrderCode,loginUser);
                }

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
        String id = stockLevel2RequisitionAddReqDTO.getId();
        if (StrUtil.isEmpty(id)){
            // 添加
            id = this.add(stockLevel2RequisitionAddReqDTO);
        }else {
            // 编辑
            this.edit(stockLevel2RequisitionAddReqDTO);
        }
        return id;
    }

    @Override
    public Page<StockLevel2RequisitionListRespDTO> pageList(StockLevel2RequisitionListReqDTO stockLevel2RequisitionListReqDTO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
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
        queryWrapper.lambda().orderByDesc(MaterialRequisition::getApplyTime).orderByDesc(MaterialRequisition::getId);

        materialRequisitionService.page(page, queryWrapper);

        // 将查询结果转化成响应DTO返回
        Page<StockLevel2RequisitionListRespDTO> pageList = new Page<>();
        BeanUtils.copyProperties(page, pageList);

        // 空数据，直接返回
        if (CollUtil.isEmpty(pageList.getRecords())) {
            return pageList;
        }

        // 根据申领单id查询业务数据相关的流程信息，主要是实例id和任务id，并转成一个map, 以申领单Id为key
        List<String> idList = page.getRecords().stream().map(MaterialRequisition::getId).collect(Collectors.toList());
        Map<String, StockLevel2RequisitionListRespDTO> flowDataMap = stockLevel2RequisitionMapper.getFlowDataByIdsAndUsername(idList, sysUser.getUsername())
                .stream().collect(Collectors.toMap(StockLevel2RequisitionListRespDTO::getId, v -> v));

        // 将查询结果转化成响应DTO返回
        List<StockLevel2RequisitionListRespDTO> respRecords = page.getRecords().stream().map(record -> {
            StockLevel2RequisitionListRespDTO respDTO = new StockLevel2RequisitionListRespDTO();
            BeanUtils.copyProperties(record, respDTO);
            // 设置流程相关的数据
            StockLevel2RequisitionListRespDTO flowData = flowDataMap.get(respDTO.getId());
            if (flowData != null){
                respDTO.setProcessInstanceId(flowData.getProcessInstanceId());
                respDTO.setTaskId(flowData.getTaskId());
            }
            // 当前登录人是否是申领人
            respDTO.setLoginUserIsApplyUser(sysUser.getId().equalsIgnoreCase(respDTO.getApplyUserId()));
            return respDTO;
        }).collect(Collectors.toList());
        pageList.setRecords(respRecords);
        return pageList;
    }
}
