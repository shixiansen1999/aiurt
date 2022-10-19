package com.aiurt.modules.weeklyPlan.mapper;

import com.aiurt.modules.weeklyPlan.dto.TeamByIdDTO;
import com.aiurt.modules.weeklyPlan.entity.BdTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description: 班组记录表
 * @Author: wgp
 * @Date:   2021-03-31
 * @Version: V1.0
 */
@Component
public interface BdTeamMapper extends BaseMapper<BdTeam> {
    /**
     * 根据id查询班组信息
     * @param id
     * @return
     */
    TeamByIdDTO queryTeamById(@Param("id") String id);

    /**
     * 管辖班组列表
     * @return
     */
    List<BdTeam> queryManagedTeam(Integer id);





    /**
     * 根据用户查询班组id
     * @return
     */
    Integer queryByUserId(String userId);

}
