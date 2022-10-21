package com.aiurt.modules.weeklyplan.service;

import com.aiurt.modules.weeklyplan.entity.BdLine;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: bd_line
 * @Author: wgp
 * @Date:   2021-03-26
 * @Version: V1.0
 */
public interface IBdLineService extends IService<BdLine> {
    /**
     * 查询线路站点
     * @return
     */
//    List<LineStationDTO> queryLineStation();

    /**
     * 只查询 3、4、8 线路
     * @return
     */
//    List<Map<String,String>> queryLine();

    /**
     * 查询班组管辖站点
     * @return
     */
//    List<BdLine> queryLineByTeam();

    /**
     * 查询线路,只查询1，2，3，4，8号线
     * @return
     */
//    List<BdLine> queryTeamLine();
}
