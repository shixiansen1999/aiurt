package com.aiurt.modules.faultknowledgebasetype.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faultknowledgebasetype.dto.SubSystemDTO;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 故障知识分类
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@EnableDataPerm
public interface FaultKnowledgeBaseTypeMapper extends BaseMapper<FaultKnowledgeBaseType> {

    /**
     * 获取用户拥有的系统
     * @param userId
     * @return
     */
    List<String> getAllSubSystem(@Param("userId")String userId);

    /**
     * 获取专业
     * @param majorCodes
     * @return
     */
    List<MajorDTO> getAllMajor(@Param("majorCodes")List<String> majorCodes);

    /**
     * 根据用户获取专业
     * @param userId
     * @return
     */
    List<String> getMajorByUser(@Param("userId")String userId);

    /**
     * 根据用户获取子系统
     * @param userId
     * @param majorCode
     * @return
     */
    List<SubSystemDTO> getSubSystemByUser(@Param("userId")String userId,@Param("majorCode")String majorCode);

    /**
     * 根据子系统code获取子系统信息
     * @param systemCodes
     * @return
     */
    List<SubSystemDTO> getSubSystemByCode(@Param("systemCodes")List<String> systemCodes);
}
