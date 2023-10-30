package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.util.CodeGenerateUtils;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.*;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartReturnOrderService;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Service
public class SparePartScrapServiceImpl extends ServiceImpl<SparePartScrapMapper, SparePartScrap> implements ISparePartScrapService {
    @Autowired
    private SparePartScrapMapper sparePartScrapMapper;
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private ISparePartReturnOrderService sparePartReturnOrderService;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private ISparePartInOrderService sparePartInOrderService;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartStockNumMapper sparePartStockNumMapper;
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    /**
     * 查询列表
     * @param page
     * @param sparePartScrap
     * @return
     */
    @Override
    public List<SparePartScrap> selectList(Page page, SparePartScrap sparePartScrap){
        //权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserDepartModel> departModels = sysBaseApi.getDepartByUserId(user.getId());
        if(!user.getRoleCodes().contains("admin")&&departModels.size()==0){
            return CollUtil.newArrayList();
        }
        if(!user.getRoleCodes().contains("admin")&&departModels.size()!=0){
            List<String> orgCodes = departModels.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
            List<SparePartStockInfo> stockInfoList = sparePartStockInfoMapper.selectList(new LambdaQueryWrapper<SparePartStockInfo>().in(SparePartStockInfo::getOrgCode, orgCodes));
            if(ObjectUtil.isEmpty(stockInfoList)){
                return CollUtil.newArrayList();
            }
            List<String> wareHouses = stockInfoList.stream().map(SparePartStockInfo::getWarehouseCode).collect(Collectors.toList());
            sparePartScrap.setWareHouses(wareHouses);
        }
        return sparePartScrapMapper.readAll(page, sparePartScrap);
    }
    /**
     * 查询列表不分页
     * @param sparePartScrap
     * @return
     */
    @Override
    public List<SparePartScrap> selectListById( SparePartScrap sparePartScrap){
        return sparePartScrapMapper.readAll(sparePartScrap);
    }
    /**
     * 修改
     *
     * @param sparePartScrap
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartScrap sparePartScrap) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sparePartScrap.setSysOrgCode(user.getOrgCode());
        SparePartScrap scrap = getById(sparePartScrap.getId());
        sparePartScrap.setConfirmId(user.getId());
        sparePartScrap.setConfirmTime(new Date());
        if(sparePartScrap.getStatus().equals(CommonConstant.SPARE_PART_SCRAP_STATUS_3) || sparePartScrap.getStatus().equals(CommonConstant.SPARE_PART_SCRAP_STATUS_2)){


            //更新已出库库存数量,做减法
            List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>()
                    .eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(SparePartOutOrder::getMaterialCode,sparePartScrap.getMaterialCode())
                    .eq(SparePartOutOrder::getWarehouseCode,sparePartScrap.getWarehouseCode()));
            //如果是故障过来的出库记录不需要更新
            if (!orderList.isEmpty() && StrUtil.isBlank(scrap.getFaultCode())) {
                for (int i = 0; i < orderList.size(); i++) {
                    SparePartOutOrder order = orderList.get(i);
                    if (Integer.parseInt(order.getUnused()) >= scrap.getNum()) {
                        Integer number = Integer.parseInt(order.getUnused()) - scrap.getNum();
                        order.setUnused(number + "");
                        sparePartReturnOrderService.updateOrder(order);
                    } else {
                        return Result.error("剩余数量不足！");
                    }
                }
            }
        }
        try {
            LoginUser userByName = sysBaseApi.getUserByName(scrap.getCreateBy());
            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userByName.getUsername(), "备件处置成功" + DateUtil.today(), null);

            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, scrap.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_SCRAP.getType());
            map.put("materialCode",scrap.getMaterialCode());
            String materialName= sysBaseApi.getMaterialNameByCode(scrap.getMaterialCode());
            map.put("name",materialName);
            map.put("num",scrap.getNum());
            map.put("realName",userByName.getRealname());
            map.put("scrapTime", DateUtil.format(scrap.getScrapTime(),"yyyy-MM-dd HH:mm:ss"));

            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            messageDTO.setTemplateCode(CommonConstant.SPAREPARTSCRAP_SERVICE_NOTICE);
            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setMsgAbstract("备件处置申请-请处置");
            messageDTO.setPublishingContent("备件处置申请通过");
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
            sysBaseApi.sendTemplateMessage(messageDTO);
            // 更新待办
            isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_SCRAP.getType(), scrap.getId(), user.getUsername(), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sparePartScrapMapper.updateById(sparePartScrap);
        return Result.OK("操作成功！");
    }

    @Override
    public Page<SparePartScrap> queryAllScrapForRepair(Page<SparePartScrap> page, SparePartScrap sparePartScrap) {
        // 只展示给角色为送修经办人的用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = sysUser.getRoleCodes();
        boolean repairAgent = roleCodes.contains(RoleConstant.REPAIR_AGENT);
        if(!repairAgent){
            return page;
        }

        // 查询所有状态为“已报损”且“故障单号"不为空的数据
        List<SparePartScrap> list = sparePartScrapMapper.queryAllScrapForRepair(page, sparePartScrap);
        if (CollUtil.isEmpty(list)) {
            return page;
        }
        // 查询三级库仓库关联的各个部门下的工班长（负责人），多个用逗号拼接
        Map<String, String> responsibleUserNameMap = this.baseMapper.queryResponsibleUserName(null).stream().collect(Collectors.toMap(SparePartScrap::getWarehouseCode, SparePartScrap::getResponsibleUserName));
        // 查询角色为送修经办人的所有用户的realname
        String manageUserName = this.baseMapper.queryManageUserName();
        // 获取是否易耗品字典map
        Map<String, String> consumablesTypeMap = sysBaseApi.queryDictItemsByCode("consumables_type").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 获取备件处置单状态字典map
        Map<String, String> spareScrapStatusMap = sysBaseApi.queryDictItemsByCode("spare_scrap_status").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 获取物资类型字典map
        Map<String, String> materialTypeMap = sysBaseApi.queryDictItemsByCode("material_type").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 获取单位字典map
        Map<String, String> materianUnitMap = sysBaseApi.queryDictItemsByCode("materian_unit").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 获取线路map
        Map<String, String> lineMap = sysBaseApi.queryTableDictItemsByCode("cs_line", "line_name", "line_code").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 获取站点map
        Map<String, String> stationMap = sysBaseApi.queryTableDictItemsByCode("cs_station", "station_name", "station_code").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 获取位置map
        Map<String, String> positionMap = sysBaseApi.queryTableDictItemsByCode("cs_station_position", "position_name", "position_code").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        //翻译名称等
        list.forEach((e) -> {
            e.setResponsibleUserName(responsibleUserNameMap.get(e.getWarehouseCode()));
            e.setManageUserName(manageUserName);
            e.setConsumablesTypeName(consumablesTypeMap.get(Convert.toStr(e.getConsumablesType())));
            e.setStatusName(spareScrapStatusMap.get(Convert.toStr(e.getStatus())));
            e.setTypeName(materialTypeMap.get(Convert.toStr(e.getType())));
            e.setUnit(materianUnitMap.get(Convert.toStr(e.getUnitValue())));
            String stationName = stationMap.get(e.getFaultStationCode());
            String positionName = positionMap.get(e.getFaultStationPositionCode());
            String faultPositionName = stationName;
            if (StrUtil.isNotBlank(positionName)) {
                faultPositionName += CommonConstant.SYSTEM_SPLIT_STR + positionName;
            }
            e.setFaultPositionName(faultPositionName);
        });
        page.setRecords(list);
        return page;
    }

    @Override
    public void scrapRepairAcceptance(SparePartScrap sparePartScrap) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 更新备件处置单状态
        sparePartScrap.setRepairStatus(CommonConstant.SPARE_PART_SCRAP_REPAIR_STATUS_3);
        sparePartScrapMapper.updateById(sparePartScrap);

        // 插入备件入库管理表
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_0);
        sparePartInOrder.setMaterialCode(sparePartScrap.getMaterialCode());
        sparePartInOrder.setWarehouseCode(sparePartScrap.getWarehouseCode());
        sparePartInOrder.setNum(sparePartScrap.getNum());
        sparePartInOrder.setOrgId(user.getOrgId());
        sparePartInOrder.setSysOrgCode(user.getOrgCode());
        sparePartInOrder.setOutOrderCode(sparePartScrap.getOutOrderId());
        sparePartInOrder.setReoutsourceRepairNum(sparePartScrap.getNum());
        sparePartInOrder.setUsedNum(sparePartScrap.getNum());
        sparePartInOrderService.save(sparePartInOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> edit(SparePartScrap sparePartScrap) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartScrap scrap = getById(sparePartScrap.getId());
        //先获取该备件的数量记录,更新全新数量
        LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper = new LambdaQueryWrapper<>();
        numLambdaQueryWrapper.eq(SparePartStockNum::getMaterialCode, scrap.getMaterialCode())
                .eq(SparePartStockNum::getWarehouseCode, scrap.getWarehouseCode())
                .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockNum stockNum = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper);
        if (CommonConstant.SPARE_PART_SCRAP_HANDLE_WAY_0.equals(sparePartScrap.getHandleWay())) {
            scrap.setStatus(CommonConstant.SPARE_PART_SCRAP_STATUS_3);
            // 处置报损且故障编号不为空设置备件送修状态为待返修
            if (StrUtil.isNotBlank(scrap.getFaultCode())) {
                scrap.setRepairStatus(CommonConstant.SPARE_PART_SCRAP_REPAIR_STATUS_1);
            }
            //报损则委外送修数量增加
            stockNum.setOutsourceRepairNum(stockNum.getOutsourceRepairNum() + scrap.getNum());
        }
        if (CommonConstant.SPARE_PART_SCRAP_HANDLE_WAY_1.equals(sparePartScrap.getHandleWay())) {
            scrap.setStatus(CommonConstant.SPARE_PART_SCRAP_STATUS_2);
            //报废则待报废数量增加
            stockNum.setScrapNum(stockNum.getScrapNum() + scrap.getNum());
        }
        if (CommonConstant.SPARE_PART_SCRAP_HANDLE_WAY_2.equals(sparePartScrap.getHandleWay())) {
            scrap.setStatus(CommonConstant.SPARE_PART_SCRAP_STATUS_4);
            //生成重新入库记录
            // 插入备件入库管理表
            SparePartInOrder sparePartInOrder = new SparePartInOrder();
            sparePartInOrder.setOrderCode(CodeGenerateUtils.generateSingleCode("3RK", 5));
            sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_0);
            sparePartInOrder.setMaterialCode(scrap.getMaterialCode());
            sparePartInOrder.setWarehouseCode(scrap.getWarehouseCode());
            sparePartInOrder.setNum(scrap.getNum());
            sparePartInOrder.setOrgId(user.getOrgId());
            sparePartInOrder.setSysOrgCode(user.getOrgCode());
            sparePartInOrder.setOutOrderCode(scrap.getOutOrderId());
            sparePartInOrder.setUsedNum(scrap.getNum());
            sparePartInOrder.setInType(MaterialRequisitionConstant.NORMAL_IN);
            sparePartInOrderService.save(sparePartInOrder);
        }
        sparePartStockNumMapper.updateById(stockNum);

        return this.update(scrap);
    }
}
