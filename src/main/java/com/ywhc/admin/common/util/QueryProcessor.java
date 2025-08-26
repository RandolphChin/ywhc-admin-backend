package com.ywhc.admin.common.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.DateRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * 查询处理器
 * 根据注解自动构建MyBatis-Plus查询条件
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
public class QueryProcessor {
    
    /**
     * 构建查询条件
     *
     * @param queryWrapper 查询包装器
     * @param queryDto     查询DTO对象
     * @param <T>          实体类型
     */
    public static <T> void buildQuery(QueryWrapper<T> queryWrapper, Object queryDto) {
        buildQueryWithSort(queryWrapper, queryDto, false);
    }
    
    /**
     * 构建查询条件并添加排序
     * 一站式处理查询条件构建和排序，减少业务代码臃肿
     *
     * @param queryWrapper 查询包装器
     * @param queryDto     查询DTO对象
     * @param <T>          实体类型
     * @return 返回传入的queryWrapper，支持链式调用
     */
    public static <T> QueryWrapper<T> buildQueryWithSort(QueryWrapper<T> queryWrapper, Object queryDto) {
        return buildQueryWithSort(queryWrapper, queryDto, true);
    }
    
    /**
     * 构建查询条件，可选择是否添加排序
     *
     * @param queryWrapper 查询包装器
     * @param queryDto     查询DTO对象
     * @param withSort     是否添加排序
     * @param <T>          实体类型
     * @return 返回传入的queryWrapper，支持链式调用
     */
    private static <T> QueryWrapper<T> buildQueryWithSort(QueryWrapper<T> queryWrapper, Object queryDto, boolean withSort) {
        if (queryDto == null) {
            return queryWrapper;
        }
        
        Class<?> clazz = queryDto.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        // 构建查询条件
        for (Field field : fields) {
            QueryField queryField = field.getAnnotation(QueryField.class);
            if (queryField == null) {
                continue;
            }
            
            field.setAccessible(true);
            try {
                Object value = field.get(queryDto);
                
                // 忽略空值检查
                if (queryField.ignoreEmpty() && isEmpty(value)) {
                    continue;
                }
                
                String column = getColumnName(field, queryField);
                buildCondition(queryWrapper, queryField.type(), column, value, queryField);
                
            } catch (IllegalAccessException e) {
                log.error("构建查询条件失败，字段: {}", field.getName(), e);
            }
        }
        
        // 添加排序（如果需要且DTO继承了BaseQueryDTO）
        if (withSort) {
            addSorting(queryWrapper, queryDto);
        }
        
        return queryWrapper;
    }
    
    /**
     * 添加排序条件
     * 支持BaseQueryDTO的排序功能，如果DTO没有继承BaseQueryDTO则跳过
     *
     * @param queryWrapper 查询包装器
     * @param queryDto     查询DTO对象
     * @param <T>          实体类型
     */
    private static <T> void addSorting(QueryWrapper<T> queryWrapper, Object queryDto) {
        try {
            // 通过反射获取排序相关方法
            Class<?> clazz = queryDto.getClass();
            
            // 尝试获取getOrderBy方法
            java.lang.reflect.Method getOrderByMethod = null;
            java.lang.reflect.Method getOrderDirectionMethod = null;
            java.lang.reflect.Method getOrderSqlMethod = null;
            
            try {
                getOrderByMethod = clazz.getMethod("getOrderBy");
                getOrderDirectionMethod = clazz.getMethod("getOrderDirection");
                getOrderSqlMethod = clazz.getMethod("getOrderSql");
            } catch (NoSuchMethodException e) {
                // 如果没有这些方法，说明不是BaseQueryDTO的子类，使用默认排序
                queryWrapper.orderByDesc("create_time");
                return;
            }
            
            String orderBy = (String) getOrderByMethod.invoke(queryDto);
            String orderDirection = (String) getOrderDirectionMethod.invoke(queryDto);
            String orderSql = (String) getOrderSqlMethod.invoke(queryDto);
            
            if (orderSql != null && !orderSql.trim().isEmpty()) {
                // 使用getOrderSql()生成的排序SQL
                boolean isAsc = !"desc".equalsIgnoreCase(orderDirection);
                String column = orderBy != null ? camelToUnderscore(orderBy) : "create_time";
                queryWrapper.orderBy(true, isAsc, column);
            } else {
                // 默认按创建时间倒序排序
                queryWrapper.orderByDesc("create_time");
            }
            
        } catch (Exception e) {
            log.warn("添加排序条件失败，使用默认排序: {}", e.getMessage());
            queryWrapper.orderByDesc("create_time");
        }
    }
    
    /**
     * 构建具体的查询条件
     */
    private static <T> void buildCondition(QueryWrapper<T> queryWrapper, QueryType type, 
                                         String column, Object value, QueryField queryField) {
        switch (type) {
            case EQUAL:
                queryWrapper.eq(column, value);
                break;
            case NOT_EQUAL:
                queryWrapper.ne(column, value);
                break;
            case LIKE:
                queryWrapper.like(column, value);
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(column, value);
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(column, value);
                break;
            case GREATER_THAN:
                queryWrapper.gt(column, value);
                break;
            case GREATER_EQUAL:
                queryWrapper.ge(column, value);
                break;
            case LESS_THAN:
                queryWrapper.lt(column, value);
                break;
            case LESS_EQUAL:
                queryWrapper.le(column, value);
                break;
            case BETWEEN:
                handleBetween(queryWrapper, column, value);
                break;
            case IN:
                if (value instanceof Collection) {
                    queryWrapper.in(column, (Collection<?>) value);
                } else if (value.getClass().isArray()) {
                    queryWrapper.in(column, (Object[]) value);
                }
                break;
            case NOT_IN:
                if (value instanceof Collection) {
                    queryWrapper.notIn(column, (Collection<?>) value);
                } else if (value.getClass().isArray()) {
                    queryWrapper.notIn(column, (Object[]) value);
                }
                break;
            case IS_NULL:
                queryWrapper.isNull(column);
                break;
            case IS_NOT_NULL:
                queryWrapper.isNotNull(column);
                break;
            case DATE_RANGE:
                handleDateRange(queryWrapper, column, value, queryField);
                break;
            case CUSTOM:
                // 自定义查询需要在具体业务中处理
                log.warn("遇到自定义查询类型，字段: {}, 需要手动处理", column);
                break;
            default:
                log.warn("未知的查询类型: {}", type);
        }
    }
    
    /**
     * 处理BETWEEN查询
     */
    private static <T> void handleBetween(QueryWrapper<T> queryWrapper, String column, Object value) {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            if (collection.size() >= 2) {
                Object[] array = collection.toArray();
                queryWrapper.between(column, array[0], array[1]);
            }
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            if (array.length >= 2) {
                queryWrapper.between(column, array[0], array[1]);
            }
        } else if (value instanceof DateRange) {
            DateRange dateRange = (DateRange) value;
            if (dateRange.isValid()) {
                queryWrapper.between(column, dateRange.getStartTime(), dateRange.getEndTime());
            }
        }
    }
    
    /**
     * 处理日期范围查询
     */
    private static <T> void handleDateRange(QueryWrapper<T> queryWrapper, String column, 
                                          Object value, QueryField queryField) {
        if (value instanceof DateRange) {
            DateRange dateRange = (DateRange) value;
            if (!dateRange.isEmpty()) {
                if (dateRange.getStartTime() != null) {
                    queryWrapper.ge(column, dateRange.getStartTime());
                }
                if (dateRange.getEndTime() != null) {
                    queryWrapper.le(column, dateRange.getEndTime());
                }
            }
        } else if (value instanceof String) {
            // 处理字符串格式的日期范围，如 "2024-01-01,2024-01-31"
            String dateStr = (String) value;
            if (StringUtils.hasText(dateStr) && dateStr.contains(",")) {
                String[] dates = dateStr.split(",");
                if (dates.length == 2) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(queryField.dateFormat());
                        LocalDateTime startTime = LocalDateTime.parse(dates[0].trim(), formatter);
                        LocalDateTime endTime = LocalDateTime.parse(dates[1].trim(), formatter);
                        queryWrapper.between(column, startTime, endTime);
                    } catch (Exception e) {
                        log.error("日期范围解析失败: {}", dateStr, e);
                    }
                }
            }
        }
    }
    
    /**
     * 获取数据库字段名
     */
    private static String getColumnName(Field field, QueryField queryField) {
        String column = queryField.column();
        if (!StringUtils.hasText(column)) {
            // 将驼峰命名转换为下划线命名
            column = camelToUnderscore(field.getName());
        }
        return column;
    }
    
    /**
     * 驼峰命名转下划线命名
     */
    private static String camelToUnderscore(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * 检查值是否为空
     */
    private static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return !StringUtils.hasText((String) value);
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        if (value.getClass().isArray()) {
            return ((Object[]) value).length == 0;
        }
        if (value instanceof DateRange) {
            return ((DateRange) value).isEmpty();
        }
        return false;
    }
    
    /**
     * 创建一个新的QueryWrapper并构建查询条件和排序
     * 最简洁的使用方式，一行代码搞定查询构建
     *
     * @param queryDto 查询DTO对象
     * @param <T>      实体类型
     * @return 构建好的QueryWrapper
     */
    public static <T> QueryWrapper<T> createQueryWrapper(Object queryDto) {
        return buildQueryWithSort(new QueryWrapper<>(), queryDto);
    }
}
