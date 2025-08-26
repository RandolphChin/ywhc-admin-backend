# 注解式查询系统使用指南

## 概述

本系统提供了一套基于注解的查询框架，支持多种查询类型，包括精确查询、模糊查询、范围查询、IN查询等。通过在DTO字段上添加`@QueryField`注解，系统会自动构建MyBatis-Plus查询条件。

## 支持的查询类型

### 基础查询类型

| 查询类型 | 说明 | 示例 |
|---------|------|------|
| `EQUAL` | 等于查询 (=) | `username = 'admin'` |
| `NOT_EQUAL` | 不等于查询 (!=) | `status != 0` |
| `LIKE` | 模糊查询 (LIKE %value%) | `name LIKE '%admin%'` |
| `LEFT_LIKE` | 左模糊查询 (LIKE %value) | `name LIKE '%admin'` |
| `RIGHT_LIKE` | 右模糊查询 (LIKE value%) | `name LIKE 'admin%'` |

### 比较查询类型

| 查询类型 | 说明 | 示例 |
|---------|------|------|
| `GREATER_THAN` | 大于查询 (>) | `age > 18` |
| `GREATER_EQUAL` | 大于等于查询 (>=) | `score >= 60` |
| `LESS_THAN` | 小于查询 (<) | `price < 100` |
| `LESS_EQUAL` | 小于等于查询 (<=) | `count <= 10` |

### 集合查询类型

| 查询类型 | 说明 | 示例 |
|---------|------|------|
| `IN` | IN查询 | `status IN (1, 2, 3)` |
| `NOT_IN` | NOT IN查询 | `id NOT IN (1, 2, 3)` |
| `BETWEEN` | 范围查询 | `age BETWEEN 18 AND 65` |

### 特殊查询类型

| 查询类型 | 说明 | 示例 |
|---------|------|------|
| `IS_NULL` | 为空查询 | `deleted_at IS NULL` |
| `IS_NOT_NULL` | 不为空查询 | `email IS NOT NULL` |
| `DATE_RANGE` | 日期范围查询 | `create_time BETWEEN '2024-01-01' AND '2024-12-31'` |

## 使用示例

### 1. 基础DTO定义

```java
@Data
@Schema(description = "用户查询条件")
public class UserQueryDTO extends BaseQueryDTO {
    
    @Schema(description = "用户名 - 精确匹配")
    @QueryField(column = "username", type = QueryType.EQUAL)
    private String username;
    
    @Schema(description = "用户名 - 模糊查询")
    @QueryField(column = "username", type = QueryType.LIKE)
    private String usernameLike;
    
    @Schema(description = "邮箱 - 精确匹配")
    @QueryField(column = "email", type = QueryType.EQUAL)
    private String email;
    
    @Schema(description = "状态 - IN查询")
    @QueryField(column = "status", type = QueryType.IN)
    private List<Integer> statusList;
    
    @Schema(description = "年龄范围")
    @QueryField(column = "age", type = QueryType.BETWEEN)
    private List<Integer> ageRange;
    
    @Schema(description = "创建时间范围")
    @QueryField(column = "create_time", type = QueryType.DATE_RANGE)
    private DateRange createTimeRange;
}
```

### 2. Controller使用

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/page")
    public Result<IPage<User>> pageUsers(UserQueryDTO queryDTO) {
        IPage<User> page = userService.pageUsers(queryDTO);
        return Result.success(page);
    }
}
```

### 3. Service实现

```java
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public IPage<User> pageUsers(UserQueryDTO queryDTO) {
        Page<User> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        QueryProcessor.buildQuery(wrapper, queryDTO);
        
        // 添加排序
        String orderSql = queryDTO.getOrderSql();
        if (orderSql != null) {
            wrapper.orderBy(true, "desc".equalsIgnoreCase(queryDTO.getOrderDirection()), 
                          queryDTO.getOrderBy() != null ? queryDTO.getOrderBy() : "create_time");
        }
        
        return this.page(page, wrapper);
    }
}
```

## 注解参数说明

### @QueryField 参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `column` | String | "" | 数据库字段名，默认使用字段名的下划线形式 |
| `type` | QueryType | EQUAL | 查询类型 |
| `ignoreEmpty` | boolean | true | 是否忽略空值 |
| `dateFormat` | String | "yyyy-MM-dd HH:mm:ss" | 日期格式 |
| `sortOrder` | int | -1 | 排序优先级 |
| `sortable` | boolean | false | 是否为排序字段 |

## 高级用法

### 1. 自定义字段名映射

```java
@QueryField(column = "user_name", type = QueryType.LIKE)
private String name; // 字段名为name，但映射到数据库的user_name字段
```

### 2. 日期范围查询

```java
@QueryField(column = "create_time", type = QueryType.DATE_RANGE)
private DateRange createTimeRange;

// 前端传递
{
  "createTimeRange": {
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-12-31T23:59:59"
  }
}
```

### 3. 复合查询条件

```java
public class LogQueryDTO extends BaseQueryDTO {
    // 用户名精确查询
    @QueryField(column = "username", type = QueryType.EQUAL)
    private String username;
    
    // 用户名模糊查询
    @QueryField(column = "username", type = QueryType.LIKE)
    private String usernameLike;
    
    // 状态IN查询
    @QueryField(column = "status", type = QueryType.IN)
    private List<Integer> statusList;
    
    // 执行时间范围
    @QueryField(column = "execution_time", type = QueryType.BETWEEN)
    private List<Long> executionTimeRange;
}
```

## 前端集成

### 1. 查询参数构建

```javascript
const buildQueryParams = () => {
  const params = {
    current: pagination.value.page,
    size: pagination.value.rowsPerPage
  }
  
  // 根据查询类型选择对应字段
  if (queryForm.value.queryType.username === 'exact' && queryForm.value.username) {
    params.username = queryForm.value.username
  } else if (queryForm.value.queryType.username === 'fuzzy' && queryForm.value.usernameLike) {
    params.usernameLike = queryForm.value.usernameLike
  }
  
  // IN查询
  if (queryForm.value.statusList?.length > 0) {
    params.statusList = queryForm.value.statusList
  }
  
  // 日期范围查询
  if (queryForm.value.dateRange?.from && queryForm.value.dateRange?.to) {
    params.createTimeRange = {
      startTime: queryForm.value.dateRange.from + ' 00:00:00',
      endTime: queryForm.value.dateRange.to + ' 23:59:59'
    }
  }
  
  return params
}
```

### 2. 动态查询类型切换

```vue
<template>
  <q-input
    v-model="usernameQuery"
    :label="`用户名 (${queryType.username === 'exact' ? '精确' : '模糊'})`"
  >
    <template v-slot:append>
      <q-btn @click="toggleQueryType('username')">
        切换查询模式
      </q-btn>
    </template>
  </q-input>
</template>

<script>
const toggleQueryType = (field) => {
  if (field === 'username') {
    queryType.value.username = queryType.value.username === 'exact' ? 'fuzzy' : 'exact'
    // 清空相关字段
    queryForm.value.username = ''
    queryForm.value.usernameLike = ''
  }
}
</script>
```

## 最佳实践

1. **字段命名规范**：使用清晰的字段名区分不同查询类型，如`username`（精确）和`usernameLike`（模糊）

2. **查询类型选择**：根据业务需求选择合适的查询类型，避免过度使用模糊查询影响性能

3. **索引优化**：为常用的查询字段建立数据库索引

4. **参数验证**：在Controller层添加参数验证注解

5. **分页限制**：设置合理的分页大小限制，防止大数据量查询

## 扩展开发

如需添加自定义查询类型，可以：

1. 在`QueryType`枚举中添加新类型
2. 在`QueryProcessor.buildCondition`方法中添加处理逻辑
3. 更新文档说明新查询类型的使用方法
