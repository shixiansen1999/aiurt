package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;


import com.aiurt.boot.modules.fault.param.SparePartStockParam;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SpareMaterialVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartStockMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.common.enums.MaterialTypeEnum;
import com.aiurt.common.result.SparePartStockResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 备件库存
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class SparePartStockServiceImpl extends ServiceImpl<SparePartStockMapper, SparePartStock> implements ISparePartStockService {

    @Resource
    private SparePartStockMapper sparePartStockMapper;

    @Resource
    private ISubsystemService subsystemService;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

//    @Resource
//    private ISysUserService sysUserService;

    /**
     * 分页查询
     * @param page
     * @param sparePartStockDTO
     * @return
     */
    @Override
    public IPage<SparePartStockDTO> queryPageList(IPage<SparePartStockDTO> page, SparePartStockDTO sparePartStockDTO) {
        IPage<SparePartStockDTO> sparePartStockDTOIPage = sparePartStockMapper.queryPageList(page, sparePartStockDTO);
        for (SparePartStockDTO record : sparePartStockDTOIPage.getRecords()) {
            if(record.getMaterialType()!=null){
                record.setMaterialTypeString(MaterialTypeEnum.getNameByCode(record.getMaterialType()));
            }
            if (StringUtils.isNotBlank(record.getSystemCode())){
                record.setSystemCode(subsystemService.getOne(new QueryWrapper<Subsystem>().eq(Subsystem.SYSTEM_CODE,record.getSystemCode()),false).getSystemName());
            }
        }
        return sparePartStockDTOIPage;
    }

    /**
     * 物料信息-查询
     * @param req
     * @return
     */
    @Override
    public List<SpareMaterialVO> queryMaterialByWarehouse(HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        // todo 后期修改
        String orgId = "";
//        String orgId = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, userId), false).getOrgId();

        List<SpareMaterialVO> list = sparePartStockMapper.queryMaterialByWarehouse(orgId);
        list.forEach(e->{
            e.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
        });
        return list;
    }

    /**
     * 查询本班组的备件信息
     * @param param
     * @return
     */
    @Override
    public IPage<SparePartStockResult> queryStockList(IPage<SparePartStockResult> page, SparePartStockParam param) {
        IPage<SparePartStockResult> results = sparePartStockMapper.selectStockList(page,param);
        return results;
    }

    /**
     * 添加备注
     * @param id
     * @param remark
     * @return
     */
    @Override
    public Result addRemark(Integer id, String remark) {
        sparePartStockMapper.addRemark(id,remark);
        return Result.ok();
    }
}
