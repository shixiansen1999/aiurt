package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartLend;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: spare_part_lend
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
public interface ISparePartLendService extends IService<SparePartLend> {
    /**
     * 查询列表
     * @param page
     * @param sparePartLend
     * @return
     */
    List<SparePartLend> selectList(Page page, SparePartLend sparePartLend);
    /**
     * 查询列表不分页
     * @param sparePartLend
     * @return
     */
    List<SparePartLend> selectListById(SparePartLend sparePartLend);
    /**
     * 添加
     *
     * @param sparePartLend
     * @return
     */
    Result<?> add(SparePartLend sparePartLend);
    /**
     * 借出确认
     *
     * @param sparePartLend
     * @return
     */
    Result<?> lendConfirm(SparePartLend sparePartLend);
    /**
     * 归还确认
     *
     * @param sparePartLend
     * @return
     */
    Result<?> backConfirm(SparePartLend sparePartLend);

    /**
     * 校验
     * @return
     */
    Result<?> check();
}
