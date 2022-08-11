package com.aiurt.modules.sparepart.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
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
   private SparePartStockMapper sparePartStockMapper;

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
      // QueryWrapper<SparePartOutOrder> queryWrapper = QueryGenerator.initQueryWrapper(sparePartOutOrder, req.getParameterMap());
       Page<SparePartOutOrder> page = new Page<SparePartOutOrder>(pageNo, pageSize);
       List<SparePartOutOrder> list = sparePartOutOrderService.selectList(page, sparePartOutOrder);
       list = list.stream().distinct().collect(Collectors.toList());
       page.setRecords(list);
       return Result.OK(page);
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
    public Result<?> getMaterialCode() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<SparePartOutOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply(" warehouse_code = ( SELECT warehouse_code  FROM spare_part_stock_info  WHERE organization_id = '"+loginUser.getOrgId()+"'  AND del_flag = "+ CommonConstant.DEL_FLAG_0+") ");
        List<SparePartOutOrder> list = sparePartOutOrderService.list(wrapper);
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
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param sparePartOutOrder
    * @return
    */
   @AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件出库",permissionUrl = "/sparepart/sparePartOutOrder/list")
   @ApiOperation(value="spare_part_out_order-编辑", notes="spare_part_out_order-编辑")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody SparePartOutOrder sparePartOutOrder) {
       LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
       SparePartOutOrder outOrder = sparePartOutOrderService.getById(sparePartOutOrder.getId());
       // 更新备件库存数据（原库存数-出库数量）
       SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,outOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,outOrder.getWarehouseCode()));
       if(null!=sparePartStock){
           sparePartStock.setNum(sparePartStock.getNum()-outOrder.getNum());
           sparePartStockMapper.updateById(sparePartStock);
       }
       sparePartOutOrder.setConfirmUserId(user.getUsername());
       sparePartOutOrder.setConfirmTime(new Date());
       sparePartOutOrder.setSysOrgCode(user.getOrgCode());
       sparePartOutOrderService.updateById(sparePartOutOrder);
       return Result.OK("编辑成功!");
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
       if(sparePartOutOrder==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(sparePartOutOrder);
   }

}
