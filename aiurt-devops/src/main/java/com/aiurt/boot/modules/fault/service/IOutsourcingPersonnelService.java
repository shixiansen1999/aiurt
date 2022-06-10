package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.fault.entity.OutsourcingPersonnel;
import com.swsc.copsms.modules.fault.param.OutsourcingPersonnelParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 委外人员
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
public interface IOutsourcingPersonnelService extends IService<OutsourcingPersonnel> {

    /**
     * 新增委外人员
     * @param personnel
     * @return
     */
    public Result add(OutsourcingPersonnel personnel, HttpServletRequest req);

    /**
     * 查询委外人员
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<OutsourcingPersonnel> pageList(IPage<OutsourcingPersonnel> page, Wrapper<OutsourcingPersonnel> queryWrapper, OutsourcingPersonnelParam param);

    /**
     * 根据id假删除
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 查询所有委外人员
     * @return
     */
    List<OutsourcingPersonnel> queryAll();

}
