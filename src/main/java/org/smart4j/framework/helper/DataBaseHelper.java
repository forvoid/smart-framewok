package org.smart4j.framework.helper;

/**
 * Created by forvoid on 3/17/2017.
 */

import org.apache.commons.dbcp2.BasicDataSource;
import com.mysql.jdbc.Driver;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.PropsUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库操作助手
 * */
public class DataBaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseHelper.class);
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;
    private static final QueryRunner QUERY_RUNNER;
    private static final BasicDataSource DATA_SOURCE;

    static {
        CONNECTION_HOLDER = new ThreadLocal<>();

        QUERY_RUNNER = new QueryRunner();

         String driver = ConfigHelper.getJdbcDriver();
         String url = ConfigHelper.getJdbcUrl();
         String username = ConfigHelper.getJdbcUsername();
        String password = ConfigHelper.getJdbcPassword();

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
        DATA_SOURCE.setMaxWaitMillis(500);
        DATA_SOURCE.setRemoveAbandonedOnMaintenance(false);
    }

    /**
     * 获取数据库连接
     * */
    public static Connection getConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        try{
            conn = DATA_SOURCE.getConnection();
        }catch (Exception e) {
            LOGGER.error("get connection failure", e);
        } finally {
            CONNECTION_HOLDER.set(conn);
        }
        return conn;
    }
    /**
     * 查询实体列表
     * */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> entityList;
        try {
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        }
        return entityList;
    }

    /**
     * 查询实体
     * */
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T entity;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        }
//        finally {
//            closeConnection();
//        }
        return entity;
    }

    /**
     * 执行查询
     * */
    public static List<Map<String, Object>> executeQuery(String sql,Object... params) {
        List<Map<String, Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (Exception e) {
            LOGGER.error("execute query failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 插入实体
     * */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> filedMap) {
        if (CollectionUtil.isEmpty(filedMap)) {
            LOGGER.error("can not insert entity : fildMap is empty");
            return false;
        }

        String sql = "INSERT INTO " + getTable(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String filedName : filedMap.keySet()) {
            columns.append(filedName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(), ")");
        values.replace(values.lastIndexOf(", "),values.length(), ")");
        sql += columns + " VALUES" + values;

        Object[] params = filedMap.values().toArray();
        return executeUpdate(sql, params) == 1;
    }

    /**
     * 更新实体
     * */
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> filedMap) {
        if (CollectionUtil.isEmpty(filedMap)) {
            LOGGER.error("can not update entity : filedMap is empty");
            return false;
        }

        String sql = "UPDATE " + getTable(entityClass).toLowerCase() + " SET ";
        StringBuilder columns = new StringBuilder();
        for (String filedName : filedMap.keySet()) {
            columns.append(filedName).append(" = ?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id = ?";

        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(filedMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();
        return executeUpdate(sql, params) == 1;
    }

    /**
     * 删除实体
     * */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "delete FROM " + getTable(entityClass).toLowerCase() + " WHERE id = ?";
        return executeUpdate(sql, id) == 1;
    }
    /**
     * 执行更新语句（包括update\insert\delete）
     * */
    public static int executeUpdate(String sql, Object... params) {
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        }
//        finally {
//            closeConnection();
//        }
        return rows;
    }


    public static <T> String getTable(Class<T> Class) {
        return Class.getSimpleName();
    }

    /**
     * 执行sql语句
     * */
    public static void executeSqlFile(String filePath) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String sql;
            while ((sql = reader.readLine()) != null) {
                executeUpdate(sql);
            }
        } catch (IOException e) {
            LOGGER.error("execute sql file failure", e);
            throw new RuntimeException(e);
        }
    }
}
