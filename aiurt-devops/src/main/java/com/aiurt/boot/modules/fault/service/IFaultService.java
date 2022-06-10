package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.result.FaultResult;
import com.swsc.copsms.modules.fault.dto.FaultDTO;
import com.swsc.copsms.modules.fault.entity.Fault;
import com.swsc.copsms.modules.fault.param.FaultParam;
import io.swagger.models.auth.In;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 故障表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IFaultService extends IService<Fault> {


    /**
     * 查询故障列表
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<FaultResult> pageList(IPage<FaultResult> page, Wrapper<FaultResult> queryWrapper, FaultParam param);

    /**
     * 故障登记
     * @param fault
     */
    public Result add(FaultDTO fault, HttpServletRequest req);

    /**
     * 根据code查询故障信息
     * @param code
     * @return
     */
    FaultResult getFaultDetail(String code);

    /**
     * 挂起
     * @param id
     * @return
     */
    public Result hang(Integer id, String remark);

    /**
     * 取消挂起
     * @param id
     * @return
     */
    public Result cancelHang(Integer id);

}
