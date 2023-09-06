package com.aiurt.modules.modeler.mapper;

import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: flowable流程模板定义信息
 * @Author: aiurt
 * @Date: 2022-07-08
 * @Version: V1.0
 */
public interface ActCustomModelInfoMapper extends BaseMapper<ActCustomModelInfo> {
    /**
     * 根据自定义接口ID列表，统计匹配的记录数量。
     *
     * @param customInterfaceIds 包含自定义接口ID的列表，可以为空。
     * @return 匹配的记录数量。
     */
    int countByCustomInterfaceIds(@Param("customInterfaceIds") List<String> customInterfaceIds);
}

