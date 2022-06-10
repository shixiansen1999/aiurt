package com.aiurt.boot.modules.standardManage.inspectionStrategy.mapper;

import com.aiurt.boot.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 检修策略管理
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface InspectionCodeContentMapper extends BaseMapper<InspectionCodeContent> {

    @Select("select * from inspection_code_content where inspection_code_id = #{id} and del_flag = 0")
    List<InspectionCodeContent> selectListById(String id);
}
