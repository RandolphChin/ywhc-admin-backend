package com.ywhc.admin.common.interceptor;

import com.ywhc.admin.common.context.DataScopeContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 数据权限拦截器
 * 自动为查询SQL添加数据权限条件
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class DataPermissionInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        // Note: rowBounds and resultHandler are part of the method signature but not used in this interceptor
        
        // 获取数据权限SQL片段
        String dataScope = DataScopeContextHolder.getDataScope();
        
        // 如果没有数据权限条件，直接执行原查询
        if (dataScope == null || dataScope.trim().isEmpty()) {
            return invocation.proceed();
        }
        
        try {
            // 获取原始SQL
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String originalSql = boundSql.getSql();
            
            // 解析并修改SQL
            String modifiedSql = addDataScopeToSql(originalSql, dataScope);
            
            if (!originalSql.equals(modifiedSql)) {
                log.debug("Original SQL: {}", originalSql);
                log.debug("Modified SQL: {}", modifiedSql);
                log.debug("Data Scope: {}", dataScope);
                
                // 创建新的BoundSql
                BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), modifiedSql, 
                    boundSql.getParameterMappings(), parameter);
                
                // 复制动态参数
                for (String key : boundSql.getAdditionalParameters().keySet()) {
                    newBoundSql.setAdditionalParameter(key, boundSql.getAdditionalParameter(key));
                }
                
                // 创建新的MappedStatement
                MappedStatement newMappedStatement = copyMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
                args[0] = newMappedStatement;
            }
        } catch (Exception e) {
            log.warn("Failed to apply data permission to SQL, executing original query. Error: {}", e.getMessage());
        }
        
        return invocation.proceed();
    }
    
    /**
     * 为SQL添加数据权限条件
     */
    private String addDataScopeToSql(String originalSql, String dataScope) {
        try {
            Statement statement = CCJSqlParserUtil.parse(originalSql);
            
            if (statement instanceof Select) {
                Select select = (Select) statement;
                PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
                
                // 解析数据权限条件
                Expression dataScopeExpression = CCJSqlParserUtil.parseCondExpression(dataScope);
                
                // 获取原有的WHERE条件
                Expression whereExpression = plainSelect.getWhere();
                
                if (whereExpression != null) {
                    // 如果已有WHERE条件，使用AND连接
                    AndExpression andExpression = new AndExpression(whereExpression, new Parenthesis(dataScopeExpression));
                    plainSelect.setWhere(andExpression);
                } else {
                    // 如果没有WHERE条件，直接设置
                    plainSelect.setWhere(dataScopeExpression);
                }
                
                return select.toString();
            }
        } catch (JSQLParserException e) {
            log.warn("Failed to parse SQL for data permission: {}", e.getMessage());
        }
        
        return originalSql;
    }
    
    /**
     * 复制MappedStatement
     */
    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(String.join(",", ms.getKeyProperties()));
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
        // 可以在这里设置一些配置属性
    }
    
    /**
     * 自定义SqlSource
     */
    public static class BoundSqlSqlSource implements SqlSource {
        private final BoundSql boundSql;
        
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        
        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
