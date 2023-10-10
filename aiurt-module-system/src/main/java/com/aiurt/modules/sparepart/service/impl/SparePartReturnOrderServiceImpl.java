package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.*;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.sparepart.service.ISparePartReturnOrderService;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_return_order
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Service
public class SparePartReturnOrderServiceImpl extends ServiceImpl<SparePartReturnOrderMapper, SparePartReturnOrder> implements ISparePartReturnOrderService {
    @Autowired
    private SparePartReturnOrderMapper sparePartReturnOrderMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private ISparePartInOrderService sparePartInOrderService;
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private ISparePartOutOrderService sparePartOutOrderService;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private SparePartStockNumMapper sparePartStockNumMapper;
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    /**
     * 查询列表
     * @param page
     * @param sparePartReturnOrder
     * @return
     */
    @Override
    public List<SparePartReturnOrder> selectList(Page page, SparePartReturnOrder sparePartReturnOrder){
        //权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserDepartModel> departModels = sysBaseApi.getDepartByUserId(user.getId());
        if(!user.getRoleCodes().contains("admin")&&departModels.size()==0){
            return CollUtil.newArrayList();
        }
        if(!user.getRoleCodes().contains("admin")&&departModels.size()!=0){
            List<String> orgCodes = departModels.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
            sparePartReturnOrder.setOrgCodes(orgCodes);
        }
        List<SparePartReturnOrder> sparePartReturnOrders = sparePartReturnOrderMapper.readAll(page, sparePartReturnOrder);
        List<SparePartReturnOrder> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(sparePartReturnOrders)){
            List<SparePartReturnOrder> collect = sparePartReturnOrders.stream().distinct().collect(Collectors.toList());
            list.addAll(collect);
        }
        return list;
    }

    /**
     * 查询列表不分页
     * @param sparePartReturnOrder
     * @return
     */
    @Override
    public List<SparePartReturnOrder> selectListById(SparePartReturnOrder sparePartReturnOrder){
        List<SparePartReturnOrder> sparePartReturnOrders = sparePartReturnOrderMapper.readAll(sparePartReturnOrder);
        List<SparePartReturnOrder> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(sparePartReturnOrders)){
            List<SparePartReturnOrder> collect = sparePartReturnOrders.stream().distinct().collect(Collectors.toList());
            list.addAll(collect);
        }
        return list;
    }
    /**
     * 修改
     *
     * @param sparePartReturnOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartReturnOrder sparePartReturnOrder) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        SparePartReturnOrder returnOrder = getById(sparePartReturnOrder.getId());
        //更新已出库库存数量,做减法
        List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode,sparePartReturnOrder.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode,sparePartReturnOrder.getWarehouseCode()));
        if(!orderList.isEmpty()){
            for(int i =0;i<orderList.size();i++){
                SparePartOutOrder order = orderList.get(i);
                if(Integer.parseInt(order.getUnused())>=returnOrder.getNum()){
                    Integer number = Integer.parseInt(order.getUnused())-returnOrder.getNum();
                    order.setUnused(number+"");
                    updateOrder(order);
                }else{
                    return Result.error("剩余数量不足！");
                }
            }
        }
        //1.更改状态为“已确认”
        returnOrder.setConfirmId(user.getUsername());
        returnOrder.setConfirmTime(date);
        returnOrder.setStatus(sparePartReturnOrder.getStatus());
        sparePartReturnOrderMapper.updateById(returnOrder);
        //2.库存做对应的加法
        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,returnOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,returnOrder.getWarehouseCode()));
        if(null!=sparePartStock){
            sparePartStock.setNum(sparePartStock.getNum()+returnOrder.getNum());
            sparePartStockMapper.updateById(sparePartStock);

            //先获取该备件的数量记录,更新已使用数量
            LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper = new LambdaQueryWrapper<>();
            numLambdaQueryWrapper.eq(SparePartStockNum::getMaterialCode, returnOrder.getMaterialCode())
                    .eq(SparePartStockNum::getWarehouseCode, returnOrder.getWarehouseCode())
                    .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
            SparePartStockNum stockNum = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper);
            if (ObjectUtil.isNotNull(stockNum)) {
                stockNum.setUsedNum(stockNum.getUsedNum() + returnOrder.getNum());
                sparePartStockNumMapper.updateById(stockNum);
            }
        }
        //3.插入备件入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(returnOrder.getMaterialCode());
        sparePartInOrder.setWarehouseCode(returnOrder.getWarehouseCode());
        sparePartInOrder.setNum(returnOrder.getNum());
        sparePartInOrder.setOrgId(user.getOrgId());
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_1);
        sparePartInOrder.setConfirmId(user.getId());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrder.setUsedNum(returnOrder.getNum());
        sparePartInOrderService.save(sparePartInOrder);

        return Result.OK("操作成功！");


    }
    @Override
    public void updateOrder(SparePartOutOrder sparePartOutOrder){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sparePartOutOrder.setConfirmUserId(user.getId());
        sparePartOutOrder.setConfirmTime(new Date());
        sparePartOutOrder.setSysOrgCode(user.getOrgCode());
        sparePartOutOrderService.updateById(sparePartOutOrder);
    }

    @Override
    public void add(SparePartReturnOrder sparePartReturnOrder) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getWarehouseCode,sparePartReturnOrder.getMaterialCode());
        wrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockInfo stockInfo = sparePartStockInfoMapper.selectOne(wrapper);
        if(null!=stockInfo){
            sparePartReturnOrder.setOrgId(stockInfo.getOrganizationId());
        }
        sparePartReturnOrder.setSysOrgCode(user.getOrgCode());
        sparePartReturnOrder.setUserId(user.getUsername());
        this.save(sparePartReturnOrder);

        try {
            //根据仓库编号获取仓库组织机构code
            String orgCode = sysBaseApi.getDepartByWarehouseCode(sparePartReturnOrder.getWarehouseCode());
            String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(orgCode), Collections.singletonList(RoleConstant.FOREMAN));

            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件退库-确认" + DateUtil.today(), null);

            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, sparePartReturnOrder.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_BACK.getType());
            map.put("materialCode",sparePartReturnOrder.getMaterialCode());
            String materialName= sysBaseApi.getMaterialNameByCode(sparePartReturnOrder.getMaterialCode());
            map.put("name",materialName);
            map.put("num",sparePartReturnOrder.getNum());
            String warehouseName= sysBaseApi.getWarehouseNameByCode(sparePartReturnOrder.getWarehouseCode());
            map.put("warehouseName",warehouseName);
            map.put("realName",user.getRealname());

            messageDTO.setData(map);
            //发送待办
            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setData(map);
            SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
            todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
            todoDTO.setTitle("备件退库-确认" + DateUtil.today());
            todoDTO.setMsgAbstract("备件退库申请");
            todoDTO.setPublishingContent("备件退库申请，请确认");
            todoDTO.setCurrentUserName(userName);
            todoDTO.setBusinessKey(sparePartReturnOrder.getId());
            todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_BACK.getType());
            todoDTO.setCurrentUserName(userName);
            todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_BACK.getType());
            todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
            todoDTO.setTemplateCode(CommonConstant.SPAREPARTRETURN_SERVICE_NOTICE);

            isTodoBaseAPI.createTodoTask(todoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
