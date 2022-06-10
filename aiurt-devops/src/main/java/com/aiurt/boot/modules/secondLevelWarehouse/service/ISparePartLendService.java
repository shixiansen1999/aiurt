package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartLend;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendParam;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartLendVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface ISparePartLendService extends IService<SparePartLend> {

    /**
     * 分页查询
     * @param page
     * @param param
     * @return
     */
    IPage<SparePartLendVO> queryPageList(Page<SparePartLendVO> page, Wrapper<SparePartLendVO> queryWrapper, SparePartLendParam param);


    /**
     * 备件借出表-添加
     * @param result
     * @param dto
     * @param req
     * @return
     */
    Result<?> addLend(Result<?> result, SparePartLendDTO dto, HttpServletRequest req);

    /**
     * 备件还回
     * @param sparePartLendEntity
     * @param returnNum  还回数量
     * @param req
     * @return
     */
    Result<?> returnMaterial(SparePartLend sparePartLendEntity, Integer returnNum,HttpServletRequest req);

    /**
     * 借出确认
     * @param sparePartLend
     * @param confirmNum  确认数量
     * @param req
     * @return
     */
    Result<?> lendConfirm(SparePartLend sparePartLend, Integer confirmNum,HttpServletRequest req);

    /**
     * 备件借出信息导出
     * @param param
     * @return
     */
    List<SparePartLendVO> exportXls(SparePartLendParam param);
}
