package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.fault.entity.RepairRecordEnclosure;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 维修记录-附件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface RepairRecordEnclosureMapper extends BaseMapper<RepairRecordEnclosure> {


    /**
     * 根据code查询附件/签名列表
     * @param repairRecordId
     * @param type
     * @return
     */
    List<String> queryDetail(Long repairRecordId,Integer type);

    /**
     * 删除附件
     * @param id
     */
    void deleteByName(@Param("id") Long id);

}
