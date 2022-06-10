package com.aiurt.boot.modules.worklog.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.worklog.entity.WorkLogEnclosure;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 日志附件
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface WorkLogEnclosureMapper extends BaseMapper<WorkLogEnclosure> {

    /**
     * 查询附件列表
     * @param id
     * @param type
     * @return
     */
    List<String> query(@Param("id") Integer id,@Param("type") Integer type);

    /**
     * 删除附件
     * @param
     * @return
     */
    int deleteByName(Long id);

}
