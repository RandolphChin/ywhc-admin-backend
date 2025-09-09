# PageConverter 工具类使用说明

## 概述

`PageConverter` 是一个通用的分页数据转换工具类，用于优雅地将 MyBatis Plus 的 `IPage<Entity>` 转换为 `Page<VO>`，避免在每个 Service 实现类中重复编写相同的转换逻辑。

## 核心功能

### 1. 基本转换 - convert()

最常用的方法，使用函数式接口进行单个对象转换：

```java
// 原始代码（重复模式）
IPage<SysUser> userPage = this.page(page, wrapper);
List<UserVO> userVOList = userPage.getRecords().stream()
        .map(this::convertToVO)
        .collect(Collectors.toList());
Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
voPage.setRecords(userVOList);
return voPage;

// 使用 PageConverter 简化后
IPage<SysUser> userPage = this.page(page, wrapper);
return PageConverter.convert(userPage, this::convertToVO);
```

### 2. 批量转换 - convertBatch()

当需要对整个列表进行额外处理时使用：

```java
// 例如：需要批量查询关联数据
return PageConverter.convertBatch(userPage, users -> {
    // 批量查询用户角色信息
    Map<Long, List<String>> userRoleMap = getUserRolesBatch(users);
    
    return users.stream().map(user -> {
        UserVO vo = convertToVO(user);
        vo.setRoles(userRoleMap.get(user.getId()));
        return vo;
    }).collect(Collectors.toList());
});
```

### 3. 辅助方法

```java
// 创建空分页对象
Page<UserVO> emptyPage = PageConverter.empty(1, 10);

// 复制分页信息
Page<UserVO> voPage = PageConverter.copyPageInfo(sourcePage);
voPage.setRecords(customConvertedList);
```

## 使用示例

### UserServiceImpl 中的应用

```java
@Override
public IPage<UserVO> pageUsers(UserQueryDTO queryDTO) {
    Page<SysUser> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
    
    LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
    // ... 构建查询条件
    
    IPage<SysUser> userPage = this.page(page, wrapper);
    
    // 一行代码完成转换
    return PageConverter.convert(userPage, this::convertToVO);
}

private UserVO convertToVO(SysUser user) {
    UserVO vo = new UserVO();
    BeanUtils.copyProperties(user, vo);
    vo.setGenderDesc(getGenderDesc(user.getGender()));
    vo.setStatusDesc(getStatusDesc(user.getStatus()));
    return vo;
}
```

### RoleServiceImpl 中的应用

```java
@Override
public IPage<RoleVO> pageRoles(Long current, Long size, String roleName, String roleKey, Integer status) {
    Page<SysRole> page = new Page<>(current, size);
    
    LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
    // ... 构建查询条件
    
    IPage<SysRole> rolePage = this.page(page, wrapper);
    
    // 一行代码完成转换
    return PageConverter.convert(rolePage, this::convertToVO);
}
```

## 优势

1. **代码简洁**：将原本 6-8 行的转换代码简化为 1 行
2. **类型安全**：使用泛型确保类型安全
3. **复用性强**：所有 Service 层都可以使用
4. **易于维护**：转换逻辑集中管理
5. **性能优化**：支持批量转换优化

## 注意事项

1. 确保转换函数 `converter` 不为 null
2. 转换函数应该是纯函数，避免副作用
3. 对于复杂的关联查询，建议使用 `convertBatch()` 方法
4. 空分页对象会被正确处理，返回空的 Page 对象

## 扩展建议

可以根据项目需要添加更多便利方法：

```java
// 可能的扩展方法
public static <T, R> Page<R> convertWithTotal(IPage<T> sourcePage, Function<T, R> converter, long customTotal);
public static <T, R> Page<R> convertAsync(IPage<T> sourcePage, Function<T, CompletableFuture<R>> asyncConverter);
```
