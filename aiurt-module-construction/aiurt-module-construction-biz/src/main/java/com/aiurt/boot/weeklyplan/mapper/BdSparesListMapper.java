package com.aiurt.boot.weeklyplan.mapper;

import com.aiurt.boot.weeklyplan.entity.BdSparesList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: bd_spares_list
 * @Author: jeecg-boot
 * @Date:   2021-06-17
 * @Version: V1.0
 */
public interface BdSparesListMapper extends BaseMapper<BdSparesList> {
    /**
     * 根据业务id和业务类型查找消息id
     * @param id
     * @param messageType
     * @return
     */
    String getMessage(@Param("id") String id, @Param("messageType") int messageType);

    /**
     * 根据消息id修改消息的状态
     * @param antId
     */
    void updateMessage(String antId);
}
