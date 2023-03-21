package com.aiurt.modules.recycle.service.impl;


import com.aiurt.modules.recycle.constant.SysRecycleConstant;
import com.aiurt.modules.recycle.entity.SysRecycle;
import com.aiurt.modules.recycle.mapper.SysRecycleMapper;
import com.aiurt.modules.recycle.service.ISysRecycleService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysRecycleImpl extends ServiceImpl<SysRecycleMapper, SysRecycle> implements ISysRecycleService {

    @Autowired
    private SysRecycleMapper sysRecycleMapper;

    @Autowired
    private DataSource dataSource;

    /**
     * 通过id还原数据
     * 查询要还原的数据、伪删除，更新del_flag=0。真删除，插入要还原的数据
     * @param id
     * @return
     */
    @Override
    public Result<String> restoreById(String id) throws SQLException {
        // 查询要还原的数据
        SysRecycle sysRecycle = sysRecycleMapper.selectById(id);
        if (sysRecycle == null) {
            return Result.error("还原失败，没有查询到要还原的数据！");
        }
        if (!SysRecycleConstant.STATE_NORMAL.equals(sysRecycle.getState())) {
            return Result.error("还原失败，该条数据不是处于可还原状态！");
        }

        Connection connection = dataSource.getConnection();  // 获取链接，直接写sql
        connection.setAutoCommit(false);

        String tableName = sysRecycle.getBillTablenm();
        Integer delSign = sysRecycle.getDelSign();
        // 伪删除，直接更新del_flag=0
        if (SysRecycleConstant.DEL_SIGN_1.equals(delSign)) {
            List<String> billIdList = JSONArray.parseArray(sysRecycle.getBillId(), String.class);
            StringBuilder idInSql = new StringBuilder();
            billIdList.forEach(billId->idInSql.append("?").append(","));
            idInSql.deleteCharAt(idInSql.length() - 1);
            String updateSql = "update " + tableName + " set del_flag = 0 where id in (" + idInSql.toString() + ")";
            // 创建PreparedStatement对象
            PreparedStatement preparedStatement = connection.prepareStatement(updateSql);
            for (int i = 0; i < billIdList.size(); i++) {
                preparedStatement.setObject(i + 1, billIdList.get(i));
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } else {
            // 真删除，插入数据
            List<JSONObject> restoreDataList = JSONArray.parseArray(sysRecycle.getBillValue(), JSONObject.class);
            if (restoreDataList.size() == 0){
                return Result.error("还原失败，没有要还原的数据！");
            }
            // 拼凑sql insert into xxx(yy,yy,yy) values(?,?,?)
            StringBuilder headSql = new StringBuilder();
            StringBuilder valueSql = new StringBuilder();
            headSql.append("insert into ");
            headSql.append(tableName);
            headSql.append("(");

            valueSql.append("values(");

            Set<String> fieldSet = restoreDataList.get(0).keySet(); // 有多少个字段
            List<String> fieldList = new ArrayList<>();  // set是无序的，使用了列表存，防止预编译参数顺序对不上
            fieldSet.forEach(field->{
                headSql.append(field).append(",");
                valueSql.append("?").append(",");
                fieldList.add(field);
            });
            headSql.deleteCharAt(headSql.length() - 1);
            valueSql.deleteCharAt(valueSql.length() - 1);
            headSql.append(") ");
            valueSql.append(")");
            String insertSql = headSql.toString() + valueSql.toString();
            // 创建PreparedStatement对象
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            restoreDataList.forEach(restoreData->{
                for (int i = 0; i < fieldList.size(); i++) {
                    String field = fieldList.get(i);
                    try {
                        preparedStatement.setObject(i+1, restoreData.get(field));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            preparedStatement.close();
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = loginUser!=null ? loginUser.getUsername(): null; // 当前登录人
        // 更新sys_recycle表, 这里自己写sql是为了保证事务，还原数据和更新sys_recycle表同成功，同失败
        String updateSysRecycleSql = "update sys_recycle set update_by = ?, update_time = ?, restore_user_id = ?, " +
                "restore_time = ?, state = ? where id = ?";
        PreparedStatement sysRecyclePreparedStatement = connection.prepareStatement(updateSysRecycleSql);
        sysRecyclePreparedStatement.setObject(1, username);
        sysRecyclePreparedStatement.setObject(2, new Date());
        sysRecyclePreparedStatement.setObject(3, username);
        sysRecyclePreparedStatement.setObject(4, new Date());
        sysRecyclePreparedStatement.setObject(5, SysRecycleConstant.STATE_RESTORE);
        sysRecyclePreparedStatement.setObject(6, sysRecycle.getId());
        sysRecyclePreparedStatement.executeUpdate();

        try{
            connection.commit();
            return Result.ok("还原数据成功！");  // try 里面有 return,finally依然会执行
        } catch (SQLException e){
            e.printStackTrace();
            connection.rollback();
            return Result.error("还原数据失败");
        }finally {
            connection.close();
        }
    }

    /**
     * 通过ids批量还原数据
     *
     * @param ids
     * @return
     */
    @Override
    public Result<String> restoreBatchByIds(List<String> ids) throws SQLException {
        List<SysRecycle> sysRecycleList = sysRecycleMapper.selectBatchIds(ids);
        // 筛选出伪删除和真删除
        Map<Integer, List<SysRecycle>> sysRecycleMap = sysRecycleList.stream().collect(Collectors.groupingBy(SysRecycle::getDelSign));
        // 获取链接，直接写sql
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = null;
        // 伪删除的，直接更新del_flag=0
        if (sysRecycleMap.containsKey(SysRecycleConstant.DEL_SIGN_1)) {
            for (SysRecycle sysRecycleFake: sysRecycleMap.get(SysRecycleConstant.DEL_SIGN_1)){
                String tableName = sysRecycleFake.getBillTablenm();
                // 伪删除
                List<String> billIdList = JSONArray.parseArray(sysRecycleFake.getBillId(), String.class);
                StringBuilder idInSql = new StringBuilder();
                billIdList.forEach(billId->idInSql.append("?").append(","));
                idInSql.deleteCharAt(idInSql.length() - 1);
                String updateSql = "update " + tableName + " set del_flag = 0 where id in (" + idInSql.toString() + ")";
                // 创建PreparedStatement对象
                preparedStatement = connection.prepareStatement(updateSql);
                for (int i = 0; i < billIdList.size(); i++) {
                    preparedStatement.setObject(i + 1, billIdList.get(i));
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        }
        if (sysRecycleMap.containsKey(SysRecycleConstant.DEL_SIGN_0)){
            // 真实删除的，插入数据
            for (SysRecycle sysRecycleReal: sysRecycleMap.get(SysRecycleConstant.DEL_SIGN_0)){
                String tableName = sysRecycleReal.getBillTablenm();

                List<JSONObject> restoreDataList = JSONArray.parseArray(sysRecycleReal.getBillValue(), JSONObject.class);
                if (restoreDataList.size() == 0){
                    continue;
                }
                // 拼凑sql insert into xxx(yy,yy,yy) values(?,?,?)
                StringBuilder headSql = new StringBuilder();
                StringBuilder valueSql = new StringBuilder();
                headSql.append("insert into ");
                headSql.append(tableName);
                headSql.append("(");

                valueSql.append("values(");

                Set<String> fieldSet = restoreDataList.get(0).keySet(); // 有多少个字段
                List<String> fieldList = new ArrayList<>();  // set是无序的，使用了列表存，防止预编译参数顺序对不上
                fieldSet.forEach(field->{
                    headSql.append(field).append(",");
                    valueSql.append("?").append(",");
                    fieldList.add(field);
                });
                headSql.deleteCharAt(headSql.length() - 1);
                valueSql.deleteCharAt(valueSql.length() - 1);
                headSql.append(") ");
                valueSql.append(")");
                String insertSql = headSql.toString() + valueSql.toString();
                // 创建PreparedStatement对象
                preparedStatement = connection.prepareStatement(insertSql);
                for (JSONObject restoreData : restoreDataList) {
                    for (int i = 0; i < fieldList.size(); i++) {
                        String field = fieldList.get(i);
                        preparedStatement.setObject(i+1, restoreData.get(field));
                    }
                    preparedStatement.executeUpdate();
                }
                preparedStatement.close();
            }
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = loginUser!=null ? loginUser.getUsername(): null; // 当前登录人
        // 更新sys_recycle表, 这里自己写sql是为了保证事务，还原数据和更新sys_recycle表同成功，同失败
        String updateSysRecycleSql = "update sys_recycle set update_by = ?, update_time = ?, restore_user_id = ?, " +
                "restore_time = ?, state = ? where id = ?";
        preparedStatement = connection.prepareStatement(updateSysRecycleSql);
        for (SysRecycle sysRecycle : sysRecycleList) {
            preparedStatement.setObject(1, username);
            preparedStatement.setObject(2, new Date());
            preparedStatement.setObject(3, username);
            preparedStatement.setObject(4, new Date());
            preparedStatement.setObject(5, SysRecycleConstant.STATE_RESTORE);
            preparedStatement.setObject(6, sysRecycle.getId());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();

        try{
            connection.commit();
            return Result.ok("还原数据成功！");  // try 里面有 return,finally依然会执行
        } catch (SQLException e){
            e.printStackTrace();
            connection.rollback();
            return Result.error("还原数据失败");
        }finally {
            connection.close();
        }
    }
}
