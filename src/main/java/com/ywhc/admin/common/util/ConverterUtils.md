# 转换工具类使用指南

本文档介绍三个核心转换工具类：`PageConverter`、`ListConverter` 和 `ObjectConverter`，它们提供了优雅的数据转换解决方案。

## 1. PageConverter - 分页数据转换

### 基本用法

```java
// 原始代码
IPage<SysUser> userPage = this.page(page, wrapper);
List<UserVO> userVOList = userPage.getRecords().stream()
        .map(this::convertToVO)
        .collect(Collectors.toList());
Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
voPage.setRecords(userVOList);
return voPage;

// 使用 PageConverter
IPage<SysUser> userPage = this.page(page, wrapper);
return PageConverter.convert(userPage, this::convertToVO);
```

### 批量转换优化

```java
// 适用于需要批量查询关联数据的场景
return PageConverter.convertBatch(userPage, users -> {
    Map<Long, List<String>> userRoleMap = getUserRolesBatch(users);
    return users.stream().map(user -> {
        UserVO vo = convertToVO(user);
        vo.setRoles(userRoleMap.get(user.getId()));
        return vo;
    }).collect(Collectors.toList());
});
```

## 2. ListConverter - 列表数据转换

### 基本转换

```java
// 原始代码
List<SysRole> roles = this.list(wrapper);
return roles.stream()
        .map(this::convertToVO)
        .collect(Collectors.toList());

// 使用 ListConverter
List<SysRole> roles = this.list(wrapper);
return ListConverter.convert(roles, this::convertToVO);
```

### 转换为 Map

```java
// 将用户列表转换为以 ID 为 key 的 Map
Map<Long, UserVO> userMap = ListConverter.convertToMap(
    users, 
    SysUser::getId, 
    this::convertToVO
);

// 按部门分组
Map<Long, List<UserVO>> usersByDept = ListConverter.convertToGroupMap(
    users,
    SysUser::getDeptId,
    this::convertToVO
);
```

### 过滤并转换

```java
// 只转换状态为正常的用户
List<UserVO> activeUsers = ListConverter.filterAndConvert(
    users,
    user -> user.getStatus() == 1,
    this::convertToVO
);
```

### 并行转换（大数据量）

```java
// 适用于大数据量或复杂转换逻辑
List<UserVO> userVOs = ListConverter.convertParallel(users, this::convertToVO);
```

## 3. ObjectConverter - 单对象转换

### 基本转换

```java
// 使用自定义转换函数
UserVO userVO = ObjectConverter.convert(user, this::convertToVO);

// 使用 BeanUtils 自动转换
UserVO userVO = ObjectConverter.convert(user, UserVO.class);
```

### 带自定义处理的转换

```java
// 基础转换 + 自定义处理
UserVO userVO = ObjectConverter.convert(user, UserVO.class, vo -> {
    vo.setGenderDesc(getGenderDesc(user.getGender()));
    vo.setStatusDesc(getStatusDesc(user.getStatus()));
});

// 基于源对象和目标对象的自定义处理
UserVO userVO = ObjectConverter.convert(user, UserVO.class, (source, target) -> {
    target.setGenderDesc(getGenderDesc(source.getGender()));
    target.setStatusDesc(getStatusDesc(source.getStatus()));
    target.setRoles(getUserRoles(source.getId()));
});
```

### 条件转换

```java
// 只有当用户状态为正常时才转换
UserVO userVO = ObjectConverter.convertIf(
    user,
    u -> u.getStatus() == 1,
    this::convertToVO
);
```

### 安全转换

```java
// 转换失败时返回默认值
UserVO userVO = ObjectConverter.convertSafely(
    user,
    this::convertToVO,
    new UserVO() // 默认值
);

// 使用供应商提供默认值
UserVO userVO = ObjectConverter.convertSafely(
    user,
    this::convertToVO,
    () -> createDefaultUserVO()
);
```

### 链式转换

```java
// 多步转换
UserDetailVO detailVO = ObjectConverter.convertChain(
    user,
    this::convertToVO,        // SysUser -> UserVO
    this::convertToDetailVO   // UserVO -> UserDetailVO
);
```

### 构建器模式

```java
// 使用构建器创建复杂转换逻辑
Function<SysUser, UserVO> converter = ObjectConverter.builder(UserVO.class)
    .customize((source, target) -> {
        target.setGenderDesc(getGenderDesc(source.getGender()));
        target.setStatusDesc(getStatusDesc(source.getStatus()));
    })
    .customize((source, target) -> {
        target.setRoles(getUserRoles(source.getId()));
    })
    .build();

UserVO userVO = converter.apply(user);
```

## 实际应用示例

### UserServiceImpl 重构

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    @Override
    public IPage<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<SysUser> wrapper = buildQueryWrapper(queryDTO);
        IPage<SysUser> userPage = this.page(page, wrapper);
        
        // 一行代码完成分页转换
        return PageConverter.convert(userPage, this::convertToVO);
    }

    @Override
    public List<UserVO> getAllUsers() {
        List<SysUser> users = this.list();
        
        // 一行代码完成列表转换
        return ListConverter.convert(users, this::convertToVO);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = this.getById(id);
        
        // 一行代码完成对象转换
        return ObjectConverter.convert(user, this::convertToVO);
    }

    // 使用构建器模式创建复杂转换器
    private final Function<SysUser, UserVO> userConverter = 
        ObjectConverter.builder(UserVO.class)
            .customize((source, target) -> {
                target.setGenderDesc(getGenderDesc(source.getGender()));
                target.setStatusDesc(getStatusDesc(source.getStatus()));
            })
            .build();

    private UserVO convertToVO(SysUser user) {
        return userConverter.apply(user);
    }
}
```

## 性能优化建议

1. **大数据量场景**：使用 `ListConverter.convertParallel()` 进行并行转换
2. **关联查询优化**：使用 `PageConverter.convertBatch()` 或 `ListConverter.convertBatch()` 避免 N+1 查询
3. **缓存转换器**：将复杂的转换器定义为类成员变量，避免重复创建
4. **条件转换**：使用 `ObjectConverter.convertIf()` 避免不必要的转换

## 最佳实践

1. **统一转换逻辑**：在 Service 层统一使用这些工具类
2. **类型安全**：充分利用泛型确保编译时类型检查
3. **异常处理**：使用 `ObjectConverter.convertSafely()` 处理可能失败的转换
4. **代码复用**：将通用转换逻辑抽取为工具方法或转换器
