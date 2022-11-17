package com.aiurt.boot.weeklyplan.service;

import com.aiurt.boot.weeklyplan.entity.BdLine;
import com.aiurt.modules.position.entity.CsLine;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: bd_line
 * @Author: wgp
 * @Date:   2021-03-26
 * @Version: V1.0
 */
public interface IBdLineService extends IService<CsLine> {
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
