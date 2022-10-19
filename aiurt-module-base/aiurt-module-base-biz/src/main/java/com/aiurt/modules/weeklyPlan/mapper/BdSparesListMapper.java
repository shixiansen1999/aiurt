package com.aiurt.modules.weeklyPlan.mapper;

import com.aiurt.modules.weeklyPlan.entity.BdSparesList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Description: bd_spares_list
 * @Author: jeecg-boot
 * @Date:   2021-06-17
 * @Version: V1.0
 */
public interface BdSparesListMapper extends BaseMapper<BdSparesList> {
    // 根据业务id和业务类型查找消息id
    String getMessage(@Param("id") String id, @Param("messageType") int messageType);
    // 根据消息id修改消息的状态
    void updateMessage(String antId);
}
