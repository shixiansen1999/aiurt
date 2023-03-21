package com.aiurt.modules.sparepart.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
* @Description: spare_part_out_order
* @Author: aiurt
* @Date:   2022-07-26
* @Version: V1.0
*/
@Api(tags="备件管理-备件出库")
@RestController
@RequestMapping("/sparepart/sparePartOutOrder")
@Slf4j
public class SparePartOutOrderController extends BaseController<SparePartOutOrder, ISparePartOutOrderService> {
   @Autowired
   private ISparePartOutOrderService sparePartOutOrderService;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
   /**
    * 分页列表查询
    *
    * @param sparePartOutOrder
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询备件出库",permissionUrl = "/sparepart/sparePartOutOrder/list")
   @ApiOperation(value="spare_part_out_order-分页列表查询", notes="spare_part_out_order-分页列表查询")
   @GetMapping(value = "/list")
   @PermissionData(pageComponent = "sparePartsFor/SparePartOutOrderList")
   public Result<IPage<SparePartOutOrder>> queryPageList(SparePartOutOrder sparePartOutOrder,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       Page<SparePartOutOrder> page = new Page<SparePartOutOrder>(pageNo, pageSize);
       List<SparePartOutOrder> list = sparePartOutOrderService.selectList(page, sparePartOutOrder);
       page.setRecords(list);
       return Result.OK(page);
   }
    /**
     * 备件出库-获取出库仓库查询条件
     *
     * @param sparePartOutOrder
     * @param req
     * @return
     */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件出库-获取出库仓库查询条件",permissionUrl = "/sparepart/sparePartOutOrder/list")
    @ApiOperation(value="备件出库-获取出库仓库查询条件", notes="备件出库-获取出库仓库查询条件")
    @GetMapping(value = "/selectList")
    @PermissionData(pageComponent = "sparePartsFor/SparePartOutOrderList")
    public Result<?> selectList(SparePartOutOrder sparePartOutOrder, HttpServletRequest req) {
        List<SparePartOutOrder> list = sparePartOutOrderService.selectList(null, sparePartOutOrder);
        List<String> newList = list.stream().map(SparePartOutOrder::getWarehouseName).collect(Collectors.toList());
        newList = newList.stream().distinct().collect(Collectors.toList());
        newList.remove(null);
        return Result.OK(newList);
    }
    /**
     * 登录人所选班组的已出库的备件
     *
     * @param

     * @return
     */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "登录人所选班组的已出库的备件",permissionUrl = "/sparepart/sparePartReturnOrder/list")
    @ApiOperation(value="备件管理-备件退库管理-登录人所选班组的已出库的备件", notes="备件管理-备件退库管理-登录人所选班组的已出库的备件")
    @GetMapping(value = "/getMaterialCode")
    public Result<?> getMaterialCode(SparePartOutOrder sparePartOutOrder) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sparePartOutOrder.setOrgId(loginUser.getOrgId());
        List<SparePartOutOrder> list = sparePartOutOrderService.selectMaterial(null, sparePartOutOrder);
        return Result.OK(list);
    }
   /**
    *   添加
    *
    * @param sparePartOutOrder
    * @return
    */
   @AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件出库",permissionUrl = "/sparepart/sparePartOutOrder/list")
   @ApiOperation(value="spare_part_out_order-添加", notes="spare_part_out_order-添加")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody SparePartOutOrder sparePartOutOrder) {
       LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
       sparePartOutOrder.setApplyUserId(user.getUsername());
       sparePartOutOrder.setSysOrgCode(user.getOrgCode());
       sparePartOutOrderService.save(sparePartOutOrder);
       List<SparePartOutOrder> orderList = sparePartOutOrderService.list(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode,sparePartOutOrder.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode,sparePartOutOrder.getWarehouseCode()));
       if(!orderList.isEmpty()){
           sparePartOutOrder.setUnused(orderList.get(0).getUnused());
       }
       sparePartOutOrderService.updateById(sparePartOutOrder);

       try {
           //根据仓库编号获取仓库组织机构code
           String orgCode = sysBaseApi.getDepartByWarehouseCode(sparePartOutOrder.getWarehouseCode());
           String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(orgCode), Collections.singletonList(RoleConstant.FOREMAN));

           //发送通知
           MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件出库-确认" + DateUtil.today(), null);

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
           map.put("realName",user.getRealname());

           /*messageDTO.setData(map);
           //业务类型，消息类型，消息模板编码，摘要，发布内容
           messageDTO.setTemplateCode(CommonConstant.SPAREPARTOUTORDER_SERVICE_NOTICE);
           SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
           messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
           messageDTO.setMsgAbstract("备件库出库申请");
           messageDTO.setPublishingContent("备件出库申请，请确认");
           messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
           sysBaseApi.sendTemplateMessage(messageDTO);*/
           //发送待办
           TodoDTO todoDTO = new TodoDTO();
           todoDTO.setData(map);
           SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
           todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
           todoDTO.setTitle("备件出库-确认" + DateUtil.today());
           todoDTO.setMsgAbstract("备件库出库申请");
           todoDTO.setPublishingContent("备件出库申请，请确认");
           todoDTO.setCurrentUserName(userName);
           todoDTO.setBusinessKey(sparePartOutOrder.getId());
           todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_OUT.getType());
           todoDTO.setCurrentUserName(userName);
           todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_OUT.getType());
           todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
           todoDTO.setTemplateCode(CommonConstant.SPAREPARTOUTORDER_SERVICE_NOTICE);

           isTodoBaseAPI.createTodoTask(todoDTO);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return Result.OK("添加成功！");
   }

   /**
    *  确认
    *
    * @param sparePartOutOrder
    * @return
    */
   @AutoLog(value = "确认",operateType = 3,operateTypeAlias = "确认备件出库",permissionUrl = "/sparepart/sparePartOutOrder/list")
   @ApiOperation(value="spare_part_out_order-确认", notes="spare_part_out_order-确认")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<?> edit(@RequestBody SparePartOutOrder sparePartOutOrder) {
       SparePartOutOrder one = sparePartOutOrderService.getById(sparePartOutOrder.getId());
       LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
       try {
           LoginUser userById = sysBaseApi.getUserByName(one.getApplyUserId());
           //发送通知
           MessageDTO messageDTO = new MessageDTO(user.getUsername(),userById.getUsername(), "备件出库成功" + DateUtil.today(), null);

           //构建消息模板
           HashMap<String, Object> map = new HashMap<>();
           map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, one.getId());
           map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_OUT.getType());
           map.put("materialCode",one.getMaterialCode());
           String materialName= sysBaseApi.getMaterialNameByCode(one.getMaterialCode());
           map.put("name",materialName);
           map.put("num",one.getNum());
           String warehouseName= sysBaseApi.getWarehouseNameByCode(one.getWarehouseCode());
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
           isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_OUT.getType(), one.getId(), user.getUsername(), "1");
       } catch (Exception e) {
           e.printStackTrace();
       }

       return sparePartOutOrderService.update(sparePartOutOrder);
   }

    /**
     * APP-出库
     *
     * @return
     */
    @AutoLog(value = "APP-出库",operateType = 1,operateTypeAlias = "App备件库存出库",permissionUrl = "/sparepart/sparePartStock/list")
    @ApiOperation(value="APP-出库", notes="APP-出库")
    @GetMapping(value = "/appOutbound")
    public Result<IPage<SparePartStock>> appOutbound(SparePartStock sparePartStock) {
        sparePartOutOrderService.appOutbound(sparePartStock);
        return Result.OK("出库成功");
    }
   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件出库",permissionUrl = "/sparepart/sparePartOutOrder/list")
   @ApiOperation(value="spare_part_out_order-通过id查询", notes="spare_part_out_order-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SparePartOutOrder> queryById(@RequestParam(name="id",required=true) String id) {
       SparePartOutOrder sparePartOutOrder = sparePartOutOrderService.getById(id);

       List<SparePartOutOrder> list = sparePartOutOrderService.selectListById(sparePartOutOrder);
       list = list.stream().filter(sparePartOutOrder1 -> sparePartOutOrder1.getId().equals(id)).distinct().collect(Collectors.toList());
       if(CollUtil.isNotEmpty(list)){
           for (SparePartOutOrder partOutOrder : list) {
               sparePartOutOrder = partOutOrder;
           }
       }
       if(sparePartOutOrder==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(sparePartOutOrder);
   }

    /**
     * 查询本班组的信息出库的物资
     */
    @ApiOperation(value="查询本班组的信息出库的物资", notes="查询本班组的信息出库的物资")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialCode", value = "物资编号")
    })
    @GetMapping("/querySparePartOutOrder")
    public Result<List<SparePartOutOrder>> querySparePartOutOrder(@RequestParam(value = "materialCode", required = false) String materialCode) {
        List<SparePartOutOrder> list = sparePartOutOrderService.querySparePartOutOrder(materialCode);
        return Result.OK(list);
    }

}
