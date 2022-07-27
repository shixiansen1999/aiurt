package com.aiurt.modules.sparepart.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
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
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @Description: spare_part_out_order
* @Author: aiurt
* @Date:   2022-07-26
* @Version: V1.0
*/
@Api(tags="spare_part_out_order")
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
   //@AutoLog(value = "spare_part_out_order-分页列表查询")
   @ApiOperation(value="spare_part_out_order-分页列表查询", notes="spare_part_out_order-分页列表查询")
   @GetMapping(value = "/list")
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
    *   添加
    *
    * @param sparePartOutOrder
    * @return
    */
   @AutoLog(value = "spare_part_out_order-添加")
   @ApiOperation(value="spare_part_out_order-添加", notes="spare_part_out_order-添加")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody SparePartOutOrder sparePartOutOrder) {
       sparePartOutOrderService.save(sparePartOutOrder);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param sparePartOutOrder
    * @return
    */
   @AutoLog(value = "spare_part_out_order-编辑")
   @ApiOperation(value="spare_part_out_order-编辑", notes="spare_part_out_order-编辑")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody SparePartOutOrder sparePartOutOrder) {
       // 更新备件库存数据（原库存数-出库数量）
       SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,sparePartOutOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,sparePartOutOrder.getWarehouseCode()));
       if(null!=sparePartStock){
           sparePartStock.setNum(sparePartStock.getNum()-sparePartOutOrder.getNum());
           sparePartStockMapper.updateById(sparePartStock);
       }
       sparePartOutOrderService.updateById(sparePartOutOrder);
       return Result.OK("编辑成功!");
   }


   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "spare_part_out_order-通过id查询")
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
