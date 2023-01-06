package com.aiurt.common.converter;

import cn.hutool.core.convert.impl.DateConverter;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.unit.DataUnit;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 日期格式转换类
 * @author fgw
 */
public class CustomDateConverter extends DateConverter {

    private static final long serialVersionUID = 7154232864431409419L;

    private static final String YEAR_PATTERN = "^\\d{4}$";

    private static final String YEAR_MONTH_PATTERN = "^\\d{4}-((0([1-9]))|(1(0|1|2)))$";

    /**
     * 构造
     *
     * @param targetType 目标类型
     */
    public CustomDateConverter(Class<? extends Date> targetType) {
        super(targetType);
    }

    /**
     * 构造
     *
     * @param targetType 目标类型
     * @param format     日期格式
     */
    public CustomDateConverter(Class<? extends Date> targetType, String format) {
        super(targetType, format);
    }


    @Override
    protected Date convertInternal(Object value) {
        Long mills = null;
        if (value instanceof Calendar) {
            // Handle Calendar
            mills = ((Calendar) value).getTimeInMillis();
        } else if (value instanceof Number) {
            // Handle Number
            mills = ((Number) value).longValue();
        }else if (value instanceof TemporalAccessor) {
            return DateUtil.date((TemporalAccessor) value);
        } else {
            // 统一按照字符串处理
            final String valueStr = convertToStr(value);
            Date date = null;
            try {
                date = StrUtil.isBlank(super.getFormat()) //
                        ? parse(valueStr) //
                        : DateUtil.parse(valueStr, super.getFormat());
            } catch (Exception e) {
                // Ignore Exception
            }
            if(null != date){
                mills = date.getTime();
            }
        }

        if (null == mills) {
            return null;
        }

        // 返回指定类型
        if (java.util.Date.class == super.getTargetType()) {
            return new java.util.Date(mills);
        }
        if (DateTime.class.equals(super.getTargetType())) {
            return new DateTime(mills);
        } else if (java.sql.Date.class.equals(super.getTargetType())) {
            return new java.sql.Date(mills);
        } else if (java.sql.Time.class.equals(super.getTargetType())) {
            return new java.sql.Time(mills);
        } else if (java.sql.Timestamp.class.equals(super.getTargetType())) {
            return new java.sql.Timestamp(mills);
        }

        throw new UnsupportedOperationException(StrUtil.format("Unsupport Date type: {}", super.getTargetType().getName()));

    }


    public static DateTime parse(CharSequence dateCharSequence) {
        DateTime parse = null;
        // 先
        try {
             parse = DateUtil.parse(dateCharSequence);
             if (Objects.isNull(parse)) {
                 // 处理 yyyy， yyyy-MM
                 if (ReUtil.isMatch(YEAR_PATTERN , dateCharSequence)) {
                     parse = DateUtil.parse(dateCharSequence, "yyyy");
                 } else if (ReUtil.isMatch(YEAR_MONTH_PATTERN, dateCharSequence)) {
                     parse = DateUtil.parse(dateCharSequence, "yyyy-MM");
                 }
             }
        } catch (Exception e) {
            // 处理 yyyy， yyyy-MM
            if (ReUtil.isMatch(YEAR_PATTERN , dateCharSequence)) {
                parse = DateUtil.parse(dateCharSequence, "yyyy");
            } else if (ReUtil.isMatch(YEAR_MONTH_PATTERN, dateCharSequence)) {
                parse = DateUtil.parse(dateCharSequence, "yyyy-MM");
            }
        }
        return parse;
    }
}
