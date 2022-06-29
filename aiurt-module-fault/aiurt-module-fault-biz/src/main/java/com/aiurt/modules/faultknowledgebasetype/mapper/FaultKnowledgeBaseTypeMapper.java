package com.aiurt.modules.faultknowledgebasetype.mapper;

import java.util.List;

import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faultknowledgebasetype.dto.SubSystemDTO;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 故障知识分类
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface FaultKnowledgeBaseTypeMapper extends BaseMapper<FaultKnowledgeBaseType> {

    List<String> getAllSubSystem(@Param("userId")String userId);

    List<MajorDTO> getAllMajor(@Param("majorCodes")List<String> majorCodes);

    List<String> getMajorByUser(@Param("userId")String userId);

    List<SubSystemDTO> getSubSystemByUser(@Param("userId")String userId,@Param("majorCode")String majorCode);
}
