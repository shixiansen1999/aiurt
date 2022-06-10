package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.common.result.FaultCodesResult;
import com.swsc.copsms.common.result.FaultResult;
import com.swsc.copsms.modules.fault.entity.Fault;
import com.swsc.copsms.modules.fault.param.FaultParam;
import org.apache.ibatis.annotations.Param;


/**
 * @Description: 故障表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultMapper extends BaseMapper<Fault> {

    /**
     * 查询故障
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<FaultResult> queryFault(IPage<FaultResult> page, Wrapper<FaultResult> queryWrapper,
                                  @Param("param") FaultParam param);


    /**
     * 根据code查询故障
     * @param code
     * @return
     */
    Fault selectByCode(String code);

    /**
     * 根据code查询故障详情
     * @param code
     * @return
     */
    FaultResult selectDetailByCode(String code);

    /**
     * 根据code查询信息
     * @param code
     * @return
     */
    FaultCodesResult selectCodeDetail(String code);

    /**
     * 挂起
     * @param id
     * @return
     */
    int hang(@Param("id") Integer id, String remark);

    /**
     * 取消挂起
     * @param id
     * @return
     */
    int cancel(@Param("id") Integer id);

    /**
     * 指派
     * @param code
     * @return
     */
    int assign(@Param("code") String code);

    /**
     * 重新指派
     * @param code
     * @return
     */
    int assignAgain(@Param("code") String code);
}
