package com.aiurt.boot.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 *
 * @description: LocalDateUtil
 * @author: Mr.zhao
 * @date: 2021/9/20 16:42
 **/
public class LocalDateUtil {

	private static final String ZONE_CN = "+8";


	/**
	 * LocalDate -> str
	 *
	 * @param localDate 字符串类型时间
	 * @param pattern 格式
	 * @return {@link LocalDate}
	 */
	public static String strToLocalDate(LocalDate localDate, String pattern) {
		if (localDate == null) {
			return null;
		}


		return localDateTimeToString(localDate.atTime(0,0,0),pattern);
	}


	/**
	 * str指定格式转换 LocalDate
	 *
	 * @param dateStr 字符串类型时间
	 * @param pattern 格式
	 * @return {@link LocalDate}
	 */
	public static LocalDate strToLocalDate(String dateStr, String pattern) {
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * str指定格式转换LocalDateTime
	 *
	 * @param dateStr 字符串类型时间
	 * @param pattern 格式
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime strToLocalDateTime(String dateStr, String pattern) {
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 将毫秒或秒值转换为LocalDateTime
	 *
	 * @param mill 秒/毫秒
	 * @return LocalDateTime
	 */
	public static LocalDateTime longToLocalDateTime(Long mill) {
		if (mill.toString().length() == 10) {
			mill = mill * 1000;
		}
		return new Date(mill).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
	}

	/**
	 * LocalDateTime -> String
	 *
	 * @param date date
	 * @return String
	 */
	public static String localDateTimeToString(LocalDateTime date, String pattern) {
		if (date == null) {
			return null;
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
		return df.format(date);
	}

	/**
	 * @param startDatetime 开始时间
	 * @param endDatetime   结束时间
	 * @return Long
	 */
	public static Long secondsBwtDatetime(LocalDateTime startDatetime, LocalDateTime endDatetime) {
		return Duration.between(startDatetime, endDatetime).getSeconds();
	}


	/**
	 * Date转LocalDateTime
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime dateToLocalDateTime(Date date) {
		//获取系统默认的时区
		ZoneId zoneId = ZoneId.of(ZONE_CN);
		return date.toInstant().atZone(zoneId).toLocalDateTime();
	}

	/**
	 * LocalDateTime转Date
	 *
	 * @param localDateTime
	 * @return
	 */
	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		ZoneId zoneId = ZoneId.of(ZONE_CN);
		return Date.from(localDateTime.atZone(zoneId).toInstant());
	}

	/**
	 * 获取本日的0点 00:00:00
	 *
	 * @param date
	 * @return
	 */
	public static Date dateToZeroTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(date).substring(0, 11) + "00:00:00";
		try {
			return sdf.parse(format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取本日的0点 12:00:00
	 *
	 * @param date
	 * @return
	 */
	public static Date dateToTwelveTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(date).substring(0, 11) + "12:00:00";
		try {
			return sdf.parse(format);
		} catch (ParseException ignored) {
		}
		return null;
	}

	/**
	 * 日期转换成指定格式
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(Date date, String format) {
		try {
			return new SimpleDateFormat(format).format(date);
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * 指定日期格式转换成时间
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date strToDate(String date, String format) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (Exception ignored) {
		}
		return null;
	}


}
