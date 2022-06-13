package com.aiurt.boot.modules.fault.service.impl;

import com.aiurt.boot.modules.fault.entity.OperationProcess;
import com.aiurt.boot.modules.fault.mapper.OperationProcessMapper;
import com.aiurt.boot.modules.fault.service.IOperationProcessService;
import com.aiurt.common.result.OperationProcessResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 运转流程
 * @Author: swsc
 * @Date:   2021-09-27
 * @Version: V1.0
 */
@Service
public class OperationProcessServiceImpl extends ServiceImpl<OperationProcessMapper, OperationProcess> implements IOperationProcessService {


    @Resource
    private OperationProcessMapper processMapper;

    /**
     * 根据code查询运转记录
     * @param code
     * @return
     */
    @Override
    public List<OperationProcessResult> getOperationProcess(String code) {

        List<OperationProcessResult> results = processMapper.selectByCode(code);
        //计算用时
        long nd = 24 * 60 * 60 * 1000;
        long nh = 60 * 60 * 1000;
        long nm = 60 * 1000;
        for (int i = 0; i < results.size(); i++) {
            if (i<results.size()-1) {
                long oldTime = results.get(i).getProcessTime().getTime();
                long newTime = results.get(i + 1).getProcessTime().getTime();
                long l = newTime - oldTime;
                long day = l / nd;
                long hour = l % nd /nh;
                long minute = l % nd % nh / nm;
                long second = l % nd % nh % nm / 1000;
                results.get(i).setDuration("用时"+day+"天"+hour+"小时"+minute+"分钟"+second+"秒");
                }
        }
        return results;
    }
}
