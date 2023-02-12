package com.agan.boot.customer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import javax.persistence.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支持主键/分片键
 */
public class BaseSelectProviderWithBusinessId extends MapperTemplate {
    public BaseSelectProviderWithBusinessId(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * [{"tableName":"users", "shardingKey":"username"}, {"tableName":"test", "shardingKey":"password"}]
     * 注: 每个继承了自定义Mapper的xxxMapper, 程序启动的时候都会来这跑一次
     *
     * @param ms
     * @return
     * @throws SQLException
     */

    // 获取表名: {tableName:shardingkey}
    private static Map<String, String> tableNameAndShardingKeyMap = new ConcurrentHashMap<>();

    public String selectByBusinessId(MappedStatement ms) throws SQLException {
        if(CollectionUtils.isEmpty(tableNameAndShardingKeyMap)){
            // 如何获取数据库的数据
            String sql1 = "SELECT * FROM users WHERE id = 1004;"; // 也可以配置到application.properties方便获取
            ResultSet resultSet = ms.getConfiguration().getEnvironment().getDataSource().getConnection().prepareStatement(sql1).executeQuery();
            while (resultSet.next()){
                String json = resultSet.getString("username");
                System.out.println(json);
                // 序列化之后存到Map里面, 抽取到工具类
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<TableAndShardingKey> tableNameAndShardingKeyList = objectMapper.readValue(json, new TypeReference<List<TableAndShardingKey>>() {});
                    if(CollectionUtils.isEmpty(tableNameAndShardingKeyList)){
                        throw new RuntimeException("配置为空");
                    }

                    for(TableAndShardingKey tableAndShardingKey : tableNameAndShardingKeyList){
                        String tableName = tableAndShardingKey.getTableName();
                        String shardingKey = tableAndShardingKey.getShardingKey();
                        tableNameAndShardingKeyMap.put(tableName, shardingKey);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Class<?> entityClass = this.getEntityClass(ms);
        // 获取实体上面的@Table里面的注解值
        String tableName = entityClass.getAnnotationsByType(Table.class)[0].name();
        System.out.println(tableName);
        if(StringUtils.isEmpty(tableName)){
            String msg = String.format("xxx实体上, 没有设置@Table注解");
            throw new RuntimeException(msg);
        }

        String shardingkey = tableNameAndShardingKeyMap.get(Optional.ofNullable(tableName).orElse(""));
        if(StringUtils.isEmpty(shardingkey)){
            String msg = String.format("表名:%s, 没有设置分片键:%s", tableName, shardingkey);
            throw new RuntimeException(msg);
        }

        this.setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, this.tableName(entityClass)));
        //sql.append(SqlHelper.wherePKColumns(entityClass));
        sql.append("<where>");
        sql.append("AND " + shardingkey + " = " + "#{" + shardingkey + "}"); // #{xx} 使用guava工具类下划线转驼峰
        sql.append("</where>");
        //SELECT id,username,password,sex,deleted,update_time,create_time  FROM users <where> AND id = #{id}</where>
        return sql.toString();
    }


    @Data
    public static class TableAndShardingKey{
        private String tableName;
        private String shardingKey;
    }

    public static void main(String[] args) {
        String msg = String.format("表名:%s, 没有设置分片键:%s", "x", "y");
        System.out.println(msg);
    }
}
