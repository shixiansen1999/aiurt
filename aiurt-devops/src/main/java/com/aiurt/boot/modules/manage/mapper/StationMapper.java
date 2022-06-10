package com.aiurt.boot.modules.manage.mapper;

import com.aiurt.boot.modules.manage.entity.Station;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Description: cs_station
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface StationMapper extends BaseMapper<Station> {

    /**
     * 根据班组名称查询站点信息
     * @param id
     * @return
     */
    Station selectNameById(String id);


    /**
     * 修改状态值
     * @param id
     */
    @Update("update cs_station set team_id = '', team_name = ''  where team_id = #{id,jdbcType=VARCHAR}")
    void updateStationDeaprt(@Param("id") String id);

    List<Integer> getIdsByLineCode(@Param("lineCode") String lineCode);

    List<Station> getStationsInOrdered();
}
