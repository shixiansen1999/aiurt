package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class SparePartOutOrderServiceImpl extends ServiceImpl<SparePartOutOrderMapper, SparePartOutOrder> implements ISparePartOutOrderService {
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISparePartStockInfoService sparePartStockInfoService;

    @Autowired
    private IMaterialBaseService materialBaseService;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    /**
     * 查询列表
     * @param page
     * @param sparePartOutOrder
     * @return
     */
    @Override
    public List<SparePartOutOrder> selectList(Page page, SparePartOutOrder sparePartOutOrder){
        return sparePartOutOrderMapper.readAll(page,sparePartOutOrder);
    }

    /**
     * 查询列表
     * @param sparePartOutOrder
     * @return
     */
    @Override
    public List<SparePartOutOrder> selectListById( SparePartOutOrder sparePartOutOrder){
        return sparePartOutOrderMapper.readAll(sparePartOutOrder);
    }
    /**
     * 查询已出库的物资编号
     * @param page
     * @param sparePartOutOrder
     * @return
     */
    @Override
    public List<SparePartOutOrder> selectMaterial(Page page, SparePartOutOrder sparePartOutOrder){
        return sparePartOutOrderMapper.selectMaterial(page,sparePartOutOrder);
    }
    /**
     * 确认
     *
     * @param sparePartOutOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartOutOrder sparePartOutOrder) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartOutOrder outOrder = getById(sparePartOutOrder.getId());
        // 更新备件库存数据（原库存数-出库数量）
        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,outOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,outOrder.getWarehouseCode()));
        if(null!=sparePartStock && sparePartStock.getNum()>=outOrder.getNum()){
            sparePartStock.setNum(sparePartStock.getNum()-outOrder.getNum());
            sparePartStockMapper.updateById(sparePartStock);
            //查询出库表同一仓库、同一备件是否有出库记录，没有则更新剩余数量为出库数量；有则更新同一仓库、同一备件所有数据的剩余数量=剩余数量+出库数量
            List<SparePartOutOrder> orderList = list(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode,outOrder.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode,outOrder.getWarehouseCode()));
            if(orderList.isEmpty()){
                sparePartOutOrder.setUnused(outOrder.getNum()+"");
                updateOrder(sparePartOutOrder);
            }else{
                orderList.forEach(order -> {
                    Integer n = Integer.parseInt(order.getUnused())+outOrder.getNum();
                    order.setStatus(sparePartOutOrder.getStatus());
                    order.setUnused(n+"");
                    updateOrder(order);
                });
            }
            return Result.OK("操作成功!");
        }else{
            return Result.error("库存数量不足!");
        }

    }
    public void updateOrder(SparePartOutOrder sparePartOutOrder){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setConfirmTime(new Date());
        sparePartOutOrder.setSysOrgCode(user.getOrgCode());
        updateById(sparePartOutOrder);
    }

    /**
     *
     * @param materialCode 物资编码
     * @return
     */
    @Override
    public List<SparePartOutOrder> querySparePartOutOrder(String materialCode) {

        if (StrUtil.isBlank(materialCode)) {
            return Collections.emptyList();
        }

        // 获取当前登录人所属机构， 根据所属机构擦查询管理二级管理仓库
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        String orgId = loginUser.getOrgId();

        if (StrUtil.isBlank(orgId)) {
            log.info("该用户没绑定机构：{}-{}", loginUser.getRealname(), loginUser.getUsername());
            return Collections.emptyList();
        }
        // todo 能否查询下级机构的仓库信息
        SysDepart sysDepart = sysDepartService.getById(orgId);
        if (Objects.isNull(sysDepart)) {
            log.info("该机构不存在：{}", orgId);
            return Collections.emptyList();
        }

        // 查询仓库
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId, orgId);
        List<SparePartStockInfo> stockInfoList = sparePartStockInfoService.getBaseMapper().selectList(wrapper);
        if (CollectionUtil.isEmpty(stockInfoList)) {
            return Collections.emptyList();
        }

        List<String> wareHouseCodeList = stockInfoList.stream().map(SparePartStockInfo::getWarehouseCode).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(wareHouseCodeList)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<SparePartOutOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SparePartOutOrder::getMaterialCode, materialCode)
                .in(SparePartOutOrder::getWarehouseCode, wareHouseCodeList).eq(SparePartOutOrder::getStatus, 2);
        List<SparePartOutOrder> outOrders = baseMapper.selectList(queryWrapper);
        outOrders.stream().forEach(sparePartOutOrder -> {
            MaterialBase materialBase = materialBaseService.selectByCode(sparePartOutOrder.getMaterialCode());
            if (Objects.nonNull(materialBase)) {
                sparePartOutOrder.setName(materialBase.getName());
            }
        });

        // 备件名称
        return outOrders;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appOutbound(SparePartStock sparePartStock) {
        //改为出库已确认
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sparePartOutOrder.setMaterialCode(sparePartStock.getMaterialCode());
        sparePartOutOrder.setWarehouseCode(sparePartStock.getWarehouseCode());
        sparePartOutOrder.setNum(sparePartStock.getNum());
        sparePartOutOrder.setApplyUserId(user.getUsername());
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setConfirmTime(new Date());
        sparePartOutOrder.setApplyOutTime(new Date());
        sparePartOutOrder.setSysOrgCode(user.getOrgCode());
        sparePartOutOrder.setStatus(2);
        sparePartOutOrderMapper.insert(sparePartOutOrder);
        List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode,sparePartOutOrder.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode,sparePartOutOrder.getWarehouseCode()));
        if(!orderList.isEmpty()){
            sparePartOutOrder.setUnused(orderList.get(0).getUnused());
        }
        sparePartOutOrderMapper.updateById(sparePartOutOrder);
        //发一条出库消息
        try {
            LoginUser userById = sysBaseApi.getUserByName(sparePartOutOrder.getApplyUserId());
            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userById.getUsername(), "备件出库成功" + DateUtil.today(), null);

            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, sparePartOutOrder.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_OUT.getType());
            map.put("materialCode",sparePartOutOrder.getMaterialCode());
            String materialName= sysBaseApi.getMaterialNameByCode(sparePartOutOrder.getMaterialCode());
            map.put("name",materialName);
            map.put("num",sparePartOutOrder.getNum());
            String warehouseName= sysBaseApi.getWarehouseNameByCode(sparePartOutOrder.getWarehouseCode());
            map.put("warehouseName",warehouseName);
            map.put("realName",userById.getRealname());

            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            messageDTO.setTemplateCode(CommonConstant.SPAREPARTOUTORDER_SERVICE_NOTICE);
            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setMsgAbstract("备件出库申请通过");
            messageDTO.setPublishingContent("备件出库申请通过");
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
            sysBaseApi.sendTemplateMessage(messageDTO);
            // 更新待办
            isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_OUT.getType(), sparePartOutOrder.getId(), user.getUsername(), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 更新备件库存数据（原库存数-出库数量）
        SparePartStock stock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,sparePartOutOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,sparePartOutOrder.getWarehouseCode()));
        if(null!=sparePartStock && stock.getNum()>=sparePartOutOrder.getNum()){
            stock.setNum(stock.getNum()-sparePartOutOrder.getNum());
            sparePartStockMapper.updateById(stock);
            //查询出库表同一仓库、同一备件是否有出库记录，没有则更新剩余数量为出库数量；有则更新同一仓库、同一备件所有数据的剩余数量=剩余数量+出库数量
            List<SparePartOutOrder> outOrders = list(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode,sparePartOutOrder.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode,sparePartOutOrder.getWarehouseCode()));
            if(outOrders.isEmpty()){
                sparePartOutOrder.setUnused(sparePartStock.getNum()+"");
                updateOrder(sparePartOutOrder);
            }else{
                outOrders.forEach(order -> {
                    Integer n = Integer.parseInt(order.getUnused())+sparePartOutOrder.getNum();
                    order.setStatus(sparePartOutOrder.getStatus());
                    order.setUnused(n+"");
                    updateOrder(order);
                });
            }
        }else{
            throw new AiurtBootException("库存数量不足!");
        }
    }
}
