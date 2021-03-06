package com.github.zuihou.database.parsers;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.github.zuihou.context.BaseContextHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Map;


/**
 * 动态表名解析
 *
 * @author zuihou
 * @date 2019/08/20
 */
@Data
@Accessors(chain = true)
public class DynamicTableNameParser implements ISqlParser {

    private Map<String, ITableNameHandler> tableNameHandlerMap;

    private ITableNameHandler defaultTableNameHandler = (metaObject, sql, tableName) -> {
        String tenantCode = BaseContextHandler.getTenant();
        if (StrUtil.isEmpty(tenantCode)) {
            return tableName;
        }
        return BaseContextHandler.getDatabase(tenantCode) + StrUtil.DOT + tableName;
    };

    @Override
    public SqlInfo parser(MetaObject metaObject, String sql) {
        if (allowProcess(metaObject)) {
            //Collection<String> tables = new TableNameParser(sql).tables();
            //if (CollectionUtils.isNotEmpty(tables)) {
            //    boolean sqlParsed = false;
            //    String parsedSql = sql;
            //    for (final String table : tables) {
            //        ITableNameHandler tableNameHandler = defaultTableNameHandler;
            //        if (CollectionUtils.isNotEmpty(tableNameHandlerMap)) {
            //            tableNameHandler = tableNameHandlerMap.get(table);
            //        }
            //        if (tableNameHandler != null) {
            //            parsedSql = tableNameHandler.process(metaObject, parsedSql, table);
            //            sqlParsed = true;
            //        }
            //    }
            //    if (sqlParsed) {
            //        return SqlInfo.newInstance().setSql(parsedSql);
            //    }
            //}

            String tenantCode = BaseContextHandler.getTenant();
            if (StrUtil.isEmpty(tenantCode)) {
                return null;
            }

            MultiTenantInterceptor multiTenantInterceptor = new MultiTenantInterceptor();
            multiTenantInterceptor.setSchemaName(BaseContextHandler.getDatabase(tenantCode));
            String parsedSql = multiTenantInterceptor.processSqlByInterceptor(sql);
            return SqlInfo.newInstance().setSql(parsedSql);
        }
        return null;
    }


    /**
     * 判断是否允许执行
     * <p>例如：逻辑删除只解析 delete , update 操作</p>
     *
     * @param metaObject 元对象
     * @return true
     */
    private boolean allowProcess(MetaObject metaObject) {
        return true;
    }
}
