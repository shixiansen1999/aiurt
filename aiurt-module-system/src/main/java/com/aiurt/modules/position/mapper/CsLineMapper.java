package com.aiurt.modules.position.mapper;

import java.util.List;

import com.aiurt.modules.position.entity.CsLine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @Description: cs_line
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Component
public interface CsLineMapper extends BaseMapper<CsLine> {
    /**
     * 查询code
     * @param lineCode
     * @return
     */
    @Select("SELECT id,line_code FROM cs_line where line_code =#{lineCode} and del_flag =0 " +
            " UNION " +
            "SELECT id,station_code FROM  cs_station where station_code =#{lineCode}  and del_flag =0 " +
            " UNION " +
            "SELECT id,position_code FROM cs_station_position where  del_flag =0 and position_code =#{lineCode}")
    List<CsLine> selectCode(@Param("lineCode") String lineCode);
}
