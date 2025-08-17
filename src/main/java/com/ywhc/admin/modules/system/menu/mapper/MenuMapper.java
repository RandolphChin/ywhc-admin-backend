package com.ywhc.admin.modules.system.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.system.menu.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单Mapper接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询菜单列表
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单树
     */
    List<SysMenu> selectMenuTree();

    /**
     * 根据父ID查询子菜单
     */
    List<SysMenu> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 检查菜单下是否有子菜单
     */
    boolean hasChildren(@Param("menuId") Long menuId);

    /**
     * 检查菜单是否被角色使用
     */
    boolean isUsedByRole(@Param("menuId") Long menuId);

    /**
     * 根据权限标识查询菜单
     */
    SysMenu selectByPermission(@Param("permission") String permission);
}
