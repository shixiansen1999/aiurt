package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.enums.ProductiveTypeEnum;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartReturnOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartReturnQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartReturnVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartReturnOrderMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartReturnOrderService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author WangHongTao
 * @Date 2021/11/15
 */
@Service
public class SparePartReturnOrderServiceImpl extends ServiceImpl<SparePartReturnOrderMapper, SparePartReturnOrder> implements ISparePartReturnOrderService {

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    @Resource
    private ISparePartStockService iSparePartStockService;

    @Resource
    private SparePartReturnOrderMapper sparePartReturnOrderMapper;

    @Resource
    private ISysUserService sysUserService;

    /**
     * 分页查询
     * @param page
     * @param wrapper
     * @param sparePartReturnQuery
     * @return
     */
    @Override
    public IPage<SparePartReturnVO> pageList(IPage<SparePartReturnVO> page, Wrapper<SparePartReturnVO> wrapper, SparePartReturnQuery sparePartReturnQuery) {
        IPage<SparePartReturnVO> pageList = sparePartReturnOrderMapper.queryPageList(page, wrapper, sparePartReturnQuery);
        for (int i = 0; i < pageList.getRecords().size(); i++) {
            pageList.getRecords().get(i).setTypeName(ProductiveTypeEnum.findMessage(pageList.getRecords().get(i).getType()));
        }
        return pageList;
    }

    /**
     * 导出excel
     * @param sparePartReturnQuery
     * @return
     */
    @Override
    public List<SparePartReturnVO> exportXls(SparePartReturnQuery sparePartReturnQuery) {
        List<SparePartReturnVO> pageList = sparePartReturnOrderMapper.exportXls(sparePartReturnQuery);
        for (int i = 0; i < pageList.size(); i++) {
            pageList.get(i).setSerialNumber(i+1);
            pageList.get(i).setTypeName(ProductiveTypeEnum.findMessage(pageList.get(i).getType()));
        }
        return pageList;
    }

    /**
     * 备件退库表-添加
     * @param result
     * @param sparePartReturnOrder
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addReturnOrder(Result<?> result, SparePartReturnOrder sparePartReturnOrder, HttpServletRequest request) {
        String userId = TokenUtils.getUserId(request, iSysBaseAPI);
        String orgId = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, userId), false).getOrgId();
        //获取备件库存信息
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq(SparePartStock.ORG_ID, orgId)
                .eq(SparePartStock.MATERIAL_CODE, sparePartReturnOrder.getMaterialCode()), false);
        if (ObjectUtil.isEmpty(one)){
            return result.error500("备件：" + sparePartReturnOrder.getMaterialCode() + " 备件库存中没有该物资");
        }
        //新增退库信息
        SparePartReturnOrder order = new SparePartReturnOrder();
        order.setMaterialCode(sparePartReturnOrder.getMaterialCode());
        order.setNum(sparePartReturnOrder.getNum());
        order.setRemarks(sparePartReturnOrder.getRemarks());
        order.setOrgId(orgId);
        order.setReturnTime(sparePartReturnOrder.getReturnTime());
        order.setDelFlag(0);
        order.setCreateBy(userId);
        this.save(order);
        //库存增加
        one.setNum(one.getNum()+sparePartReturnOrder.getNum());
        one.setUpdateBy(userId);
        iSparePartStockService.updateById(one);
        return result.success("添加成功");
    }
}
