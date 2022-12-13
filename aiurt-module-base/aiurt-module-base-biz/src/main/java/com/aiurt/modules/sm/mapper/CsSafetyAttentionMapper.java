package com.aiurt.modules.sm.mapper;

import java.util.List;


import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.common.system.vo.CsUserMajorModel;

/**
 * @Description: 安全事项
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
public interface CsSafetyAttentionMapper extends BaseMapper<CsSafetyAttention> {
    /**
     * 查询专业
     * @return
     * @param majorCodeName
     */
    CsUserMajorModel selectCsMajor(@Param("majorCodeName") String majorCodeName);

    /**
     * 查询子系统
     *
     * @param code
     * @param systemName
     * @return
     */
    String selectSystemCode(@Param("systemName") String  systemName,@Param("code")String code);
    /**
     * 查询子系统
     * @param
     * @param majorCode
     * @return
     */
    List<String> selectSystemCodes(@Param("majorCode")List<String>majorCode);
}
