package com.aiurt.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 编码生成工具类，使用redis锁
 * 编码生成规则：前缀 + 中间位 + 后缀  前缀传入的key，中间位一般是时间，后缀是序列号(自增顺序号)
 *
 * @author 华宜威
 * @date 2023-09-21 17:13:11
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CodeGenerateUtils {

    private static RedisUtil redisUtil;

    @Autowired
    public CodeGenerateUtils(RedisUtil r) {
        redisUtil = r;
    }

    /**
     * 生成一个code
     * @param codePrefix 编码前缀
     * @param snSize 编码顺序号数量，即生成多少位数的顺序号，不能小于1，当已经是该位数的最大值时，只能返回9999这种类似的
     * @return String 返回一个编码
     */
    public static String generateSingleCode(String codePrefix,  Integer snSize) {
        // 只生成一个编码，编码中间位使用当天的时间
        String middle = DateUtil.format(new Date(), "yyyyMMdd");
        List<String> list = CodeGenerateUtils.generateMultiCodes(codePrefix, middle, snSize, 1);
        return CollUtil.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 生成一个code
     * @param codePrefix 编码前缀
     * @param middle 编码中间位
     * @param snSize 编码顺序号数量，即生成多少位数的顺序号，不能小于1，当已经是该位数的最大值时，只能返回9999这种类似的
     * @return String 返回一个编码
     */
    public static String generateSingleCode(String codePrefix, String middle, Integer snSize) {
        // 只生成一个编码
        List<String> list = CodeGenerateUtils.generateMultiCodes(codePrefix, middle, snSize, 1);
        return CollUtil.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 生成多个编码， 使用redis锁，过期时间为1天, 中间位默认使用当天的时间
     * @param codePrefix 编码前缀
     * @param snSize 编码顺序号数量，即生成多少位数的顺序号，不能小于1，当已经是该位数的最大值时，只能返回9999这种类似的
     * @param codeNum 要生成多少个编码，不能小于1
     * @return List<String> 返回多个编码的code列表
     */
    public static List<String> generateMultiCodes(String codePrefix, Integer snSize, Integer codeNum) {
        String middle = DateUtil.format(new Date(), "yyyyMMdd");
        return CodeGenerateUtils.generateMultiCodes(codePrefix, middle, snSize, codeNum);
    }

    /**
     * 生成多个编码， 使用redis锁，过期时间为1天
     *
     * @param codePrefix 编码前缀
     * @param middle     编码中间位
     * @param snSize     编码顺序号数量，即生成多少位数的顺序号，不能小于1，当已经是该位数的最大值时，只能返回9999这种类似的
     * @param codeNum    要生成多少个编码，不能小于1
     * @return List<String> 返回多个编码的code列表
     */
    public static List<String> generateMultiCodes(String codePrefix, String middle, Integer snSize, Integer codeNum) {
        // 先验证snSize和codeNum
        if (snSize < 1 || codeNum < 1) {
            throw new AiurtBootException("生成编码顺序号数量和编码个数不能小于1");
        }

        List<String> codeList = new ArrayList<>();
        // 1、拼接成redis的key值
        String key = codePrefix + middle;
        try {
            if(RedisLockUtils.tryLock(key)){
                // 2、加锁，根据key从redis中获取最大顺序号
                String sn = (String) redisUtil.get(key);
                // 3、如果sn不为空，则为最大序列号，不然最大序列号就是从1开始
                long maxSn = StrUtil.isNotEmpty(sn) ? Long.parseLong(sn) : 0L;
                // 4、看要生成多少个编码
                for (int i = 0; i < codeNum; i++) {
                    maxSn = maxSn + 1;
                    String code = maxSn + "";
                    // 如果code的位数已经超过了顺序号数量，那就取9999这种类似的
                    if (code.length() > snSize) {
                        codeList.add(codePrefix + middle + getRepeatString("9", snSize));
                    } else {
                        code = getRepeatString("0", snSize) + code;
                        codeList.add(codePrefix + middle + code.substring(code.length() - snSize));
                    }
                }
                // 5、redis设置回最大值， 并且过期时间为1天
                redisUtil.set(key, maxSn + "", 86400L);
            }else {
                throw new AiurtBootException("获取锁失败，请稍后重试");
            }
        }catch (Exception e){
            throw new AiurtBootException("获取锁失败，请稍后重试");
        }finally {
            // 释放锁
            RedisLockUtils.unlock(key);
        }
        return codeList;
    }


    /**
     * 获取n个重复的字符串
     *
     * @param str 要重复的字符串
     * @param n   重复多少次
     * @return 返回n个重复的字符串，n要大于等于1，如”9999“
     */
    public static String getRepeatString(String str, Integer n) {
        if (n < 1 || str == null) {
            throw new AiurtBootException("获取重复字符串出错");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

}
