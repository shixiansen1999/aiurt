package com.aiurt.modules.train.eaxm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.exam.entity.BdExamRecordDetail;
import com.aiurt.modules.train.task.vo.ShortQuesReqVo;

import java.util.List;

/**
 * @Description: 答题详情
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdExamRecordDetailMapper extends BaseMapper<BdExamRecordDetail> {

    /**
     *  获取考试详情根据简答题类型、考试记录id
     * @param shortQuesReqVo
     * @param date
     * @return
     */
    BdExamRecordDetail getByQuTypeAndExamRecordId(@Param("shortQuesReqVo") ShortQuesReqVo shortQuesReqVo,@Param("date") String date);

    /**
     * 获取考试答题记录
     * @param examRecordId
     * @param questionId
     * @param date
     * @return
     */
    List<BdExamRecordDetail> getAnswerList(@Param("examRecordId")String examRecordId,@Param("questionId")String questionId,@Param("date")String date);

    /**
     * 通过记录id查询
     * @param id
     * @param date
     * @param number
     * @return
     */
    List<BdExamRecordDetail> getByExamRecordId(@Param("id")String id,@Param("date")String date,@Param("number")Integer number);

    /**
     * 通过id获取详情
     * @param id
     * @return
     */
    List<String> getDetailById(@Param("id")String id);
}
