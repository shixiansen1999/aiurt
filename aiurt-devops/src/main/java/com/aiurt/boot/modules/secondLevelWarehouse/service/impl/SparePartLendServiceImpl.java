package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.*;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendParam;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartLendVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartLendEnclosureMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartLendMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartInOrderService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartLendService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartOutOrderService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.common.enums.LendStatusEnum;
import com.aiurt.common.enums.MaterialLendStatus;
import com.aiurt.common.enums.MaterialTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class SparePartLendServiceImpl extends ServiceImpl<SparePartLendMapper, SparePartLend> implements ISparePartLendService {

    @Resource
    private SparePartLendMapper sparePartLendMapper;
    @Resource
    private ISparePartStockService iSparePartStockService;
    @Resource
    private ISparePartOutOrderService iSparePartOutOrderService;
//    @Resource
//    private ISysUserService sysUserService;
    @Resource
    private ISparePartInOrderService iSparePartInOrderService;
    @Resource
    private ISysBaseAPI iSysBaseAPI;
    @Resource
    private SparePartLendEnclosureMapper sparePartLendEnclosureMapper;

    /**
     * 分页查询
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    @Override
    public IPage<SparePartLendVO> queryPageList(Page<SparePartLendVO> page, Wrapper<SparePartLendVO> queryWrapper, SparePartLendParam param) {
        IPage<SparePartLendVO> pageList=sparePartLendMapper.queryPageList(page,param);
        for (int i = 0; i < pageList.getRecords().size(); i++) {
            pageList.getRecords().get(i).setTypeName(MaterialTypeEnum.getNameByCode(pageList.getRecords().get(i).getType()));
            pageList.getRecords().get(i).setStatusDesc(LendStatusEnum.getNameByCode(pageList.getRecords().get(i).getStatus()));
        }
        return pageList;
    }

    /**
     * 备件借出表-添加
     * @param result
     * @param dto
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addLend(Result<?> result, SparePartLendDTO dto, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        // todo 后期修改
        String orgId = "";
//        String orgId = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, userId), false).getOrgId();

        //判断库存够不够
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq(SparePartStock.ORG_ID, orgId)
                .eq(SparePartStock.MATERIAL_CODE, dto.getMaterialCode()), false);
        if(ObjectUtil.isNotEmpty(one)){
            if(one.getNum()<dto.getLendNum()){
                throw new AiurtBootException("备件："+dto.getMaterialCode()+" 库存不足");
            }
        }
        //新增借出记录
        SparePartLend sparePartLend = new SparePartLend();
        sparePartLend.setOrgId(orgId);
        sparePartLend.setMaterialCode(dto.getMaterialCode());
        sparePartLend.setLendNum(dto.getLendNum());
        sparePartLend.setOutDepart(orgId);
        sparePartLend.setLendTime(dto.getLendTime());
        sparePartLend.setLendPerson(dto.getLendPerson());
        sparePartLend.setLendConfirm(0);
        sparePartLend.setLendDepart(dto.getLendDepart());
        sparePartLend.setRemarks(dto.getRemarks());
        sparePartLend.setStatus(MaterialLendStatus.OFF_THE_STOCK.getCode());
        sparePartLend.setCreateBy(userId);
        sparePartLend.setDelFlag(0);
        sparePartLendMapper.insert(sparePartLend);
        //插入附件表
        if (dto.getUrlList()!=null) {
            SparePartLendEnclosure enclosure = new SparePartLendEnclosure();
            List<String> urlList = dto.getUrlList();
            for (String s : urlList) {
                enclosure.setCreateBy(sparePartLend.getCreateBy());
                enclosure.setParentId(sparePartLend.getId());
                enclosure.setUrl(s);
                enclosure.setDelFlag(0);
                sparePartLendEnclosureMapper.insert(enclosure);
            }
        }
        return result.success("添加成功");
    }

    /**
     * 备件还回
     * @param sparePartLendEntity
     * @param returnNum  还回数量
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> returnMaterial(SparePartLend sparePartLendEntity, Integer returnNum,HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        //借出信息修改
        sparePartLendEntity.setStatus(MaterialLendStatus.RETURNED.getCode());
        sparePartLendEntity.setBackNum(returnNum);
        sparePartLendEntity.setBackTime(new Date());
        sparePartLendEntity.setUpdateBy(userId);
        sparePartLendMapper.updateById(sparePartLendEntity);
        //新增入库信息
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(sparePartLendEntity.getMaterialCode());
        sparePartInOrder.setNum(returnNum);
        sparePartInOrder.setConfirmStatus(0);
        sparePartInOrder.setOrgId(sparePartLendEntity.getOrgId());
        sparePartInOrder.setCreateBy(userId);
        sparePartInOrder.setUpdateBy(userId);
        iSparePartInOrderService.save(sparePartInOrder);
        if (returnNum > sparePartLendEntity.getConfirmNum()) {
            throw new AiurtBootException("还回数量不能超过确认借出数量");
        }
        //库存增加
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq(SparePartStock.ORG_ID, sparePartLendEntity.getOrgId())
                .eq(SparePartStock.MATERIAL_CODE, sparePartLendEntity.getMaterialCode()), false);
        one.setNum(one.getNum()+returnNum);
        one.setUpdateBy(userId);
        iSparePartStockService.updateById(one);
        return Result.ok();
    }

    /**
     * 借出确认
     * @param sparePartLend
     * @param confirmNum  确认数量
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> lendConfirm(SparePartLend sparePartLend, Integer confirmNum, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        sparePartLend.setLendConfirm(1);
        sparePartLend.setOutTime(new Date());
        sparePartLend.setConfirmNum(confirmNum);
        sparePartLendMapper.updateById(sparePartLend);
        //新增出库信息
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(sparePartLend.getMaterialCode());
        sparePartOutOrder.setNum(sparePartLend.getLendNum());
        sparePartOutOrder.setOrgId(sparePartLend.getOrgId());
        sparePartOutOrder.setUpdateBy(userId);
        sparePartOutOrder.setCreateBy(userId);
        sparePartOutOrder.setOutTime(new Date());
        iSparePartOutOrderService.save(sparePartOutOrder);
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq(SparePartStock.ORG_ID, sparePartLend.getOrgId())
                .eq(SparePartStock.MATERIAL_CODE, sparePartLend.getMaterialCode()), false);
        if (ObjectUtil.isEmpty(one)) {
            throw new AiurtBootException("确认失败");
        }
        if (confirmNum>sparePartLend.getLendNum()) {
            throw new AiurtBootException("借出数量不能超过申请数量");
        }
        //库存减少
        one.setNum(one.getNum()-sparePartLend.getLendNum());
        one.setUpdateBy(userId);
        iSparePartStockService.updateById(one);
        return Result.ok();
    }

    /**
     * 备件借出信息导出
     * @param param
     * @return
     */
    @Override
    public List<SparePartLendVO> exportXls(SparePartLendParam param) {
        List<SparePartLendVO> list = sparePartLendMapper.queryExportXls(param);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            list.get(i).setStatusDesc(MaterialLendStatus.getNameByCode(list.get(i).getStatus()));
            list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
        }
        return list;
    }

}
