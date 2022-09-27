package com.aiurt.boot.index.dto;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.util.DateUtils;

import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description:
 * 用于判断是否是一整月的查询
 * 如果是一整个月查询，那么返回的dayBegin是这个月的第一周的开始时间，dayEnd是这个月最后一周的结束时间
 * @date 2022/9/2312:02
 */
public class JudgeIsMonthQuery {
    private Date startDate;
    private Date endDate;
    private Date dayBegin;
    private Date dayEnd;

    public JudgeIsMonthQuery(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getDayBegin() {
        return dayBegin;
    }

    public Date getDayEnd() {
        return dayEnd;
    }

    public JudgeIsMonthQuery invoke() {
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return this;
        }
        // 重置开始时间和结束时间
        dayBegin = DateUtil.beginOfDay(startDate);
        dayEnd = DateUtil.endOfDay(endDate);

        // 月的开始时间和月的结束时间
        DateTime monthBegin = DateUtil.beginOfMonth(dayEnd);
        DateTime monthEnd = DateUtil.endOfMonth(dayEnd);

        // 代表是一整个月的查询,按照月的第一周开始时间和最后一周的结束时间来计算
        if (dayBegin.equals(monthBegin) && dayEnd.equals(monthEnd)) {
            // 获取某年某月第一周的开始、结束时间
            Date[] dateByMonthAndWeek = DateUtils.getDateByMonthAndWeek(DateUtil.year(dayEnd), DateUtil.month(dayEnd)+1, 1);

            // 重新赋值
            dayBegin =  dateByMonthAndWeek[0];
            dayEnd = DateUtil.endOfWeek(dayEnd);
        }
        return this;
    }
}
