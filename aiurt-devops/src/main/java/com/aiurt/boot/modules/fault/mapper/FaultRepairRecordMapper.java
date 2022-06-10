package com.aiurt.boot.modules.fault.mapper;

import com.aiurt.boot.modules.fault.entity.FaultRepairRecord;
import com.aiurt.boot.modules.statistical.vo.UserAndAmountVO;
import com.aiurt.common.result.FaultPersonResult;
import com.aiurt.common.result.FaultRepairRecordResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultRepairRecordMapper extends BaseMapper<FaultRepairRecord> {

    /**
     * 根据code查询检修情况
     * @param code
     * @return
     */
    List<FaultRepairRecordResult> queryDetail(String code);

    /**
     * 根据code查询最后一条检修情况
     * @param code
     * @return
     */
    FaultRepairRecordResult queryLastDetail(String code);

    /**
     * 获取待办消息
     * @param
     * @return
     */
    List<FaultRepairRecordResult> getWaitMessage(@Param("userId")String userId,
                                                 @Param("startTime")String startTime,
                                                 @Param("endTime")String endTime);

    /**
     * 查询最后一条检修情况
     * @param id
     * @param code
     * @return
     */
    FaultRepairRecordResult selectLastRecord(String id,String code);

    /**
     * 查询详情
     * @param id
     * @return
     */
    FaultRepairRecord selectDetailById(Long id);


    /**
     * 人员处理故障数量
     * @param startTime
     * @param endTime
     * @param userName
     * @param userNameList
     * @return
     */
    List<UserAndAmountVO> selectFaultNum(@Param("startTime") String startTime, @Param("entTime") String endTime,
                                         @Param("userName") String userName, @Param("userNameList") List<String> userNameList);

    /**
     * 获取时间
     * @param startTime
     * @param endTime
     * @param userName
     * @return
     */
    List<FaultPersonResult> getFaultDate(@Param("startTime") String startTime, @Param("entTime") String endTime,
                                         @Param("userName") String userName);

}
