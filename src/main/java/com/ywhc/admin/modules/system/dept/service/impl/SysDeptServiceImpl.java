package com.ywhc.admin.modules.system.dept.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.common.util.SecurityUtils;
import com.ywhc.admin.modules.system.dept.dto.DeptQueryDTO;
import com.ywhc.admin.modules.system.dept.dto.DeptSaveDTO;
import com.ywhc.admin.modules.system.dept.entity.SysDept;
import com.ywhc.admin.modules.system.dept.mapper.SysDeptMapper;
import com.ywhc.admin.modules.system.dept.service.SysDeptService;
import com.ywhc.admin.modules.system.dept.vo.DeptTreeVO;
import com.ywhc.admin.modules.system.role.entity.SysRole;
import com.ywhc.admin.modules.system.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统部门服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private final SysDeptMapper baseMapper;
    private final RoleService roleService;
    private final SecurityUtils securityUtils;
    @Override
    public List<DeptTreeVO> getDeptTree(DeptQueryDTO queryDTO) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getDeptName()), SysDept::getDeptName, queryDTO.getDeptName())
                .eq(queryDTO.getStatus() != null, SysDept::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getDeptType() != null, SysDept::getDeptType, queryDTO.getDeptType())
                .like(StringUtils.hasText(queryDTO.getLeaderName()), SysDept::getLeaderName, queryDTO.getLeaderName())
                .orderByAsc(SysDept::getSortOrder);

        List<SysDept> deptList = list(wrapper);
        List<DeptTreeVO> deptVOList = deptList.stream().map(this::convertToVO).collect(Collectors.toList());

        return buildDeptTree(deptVOList, 0L);
    }

    @Override
    public DeptTreeVO getDeptById(Long deptId) {
        SysDept dept = getById(deptId);
        return dept != null ? convertToVO(dept) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDept(DeptSaveDTO saveDTO) {
        // 校验部门名称和编码唯一性
        if (!checkDeptNameUnique(saveDTO)) {
            throw new RuntimeException("部门名称已存在");
        }
        if (!checkDeptCodeUnique(saveDTO)) {
            throw new RuntimeException("部门编码已存在");
        }

        SysDept dept = new SysDept();
        BeanUtils.copyProperties(saveDTO, dept);

        // 设置祖级列表
        if (saveDTO.getParentId() != null && saveDTO.getParentId() != 0) {
            SysDept parentDept = getById(saveDTO.getParentId());
            if (parentDept != null) {
                dept.setAncestors(parentDept.getAncestors() + "," + saveDTO.getParentId());
            }
        } else {
            dept.setParentId(0L);
            dept.setAncestors("0");
        }

        return save(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(DeptSaveDTO saveDTO) {
        // 校验部门名称和编码唯一性
        if (!checkDeptNameUnique(saveDTO)) {
            throw new RuntimeException("部门名称已存在");
        }
        if (!checkDeptCodeUnique(saveDTO)) {
            throw new RuntimeException("部门编码已存在");
        }

        SysDept oldDept = getById(saveDTO.getId());
        if (oldDept == null) {
            throw new RuntimeException("部门不存在");
        }

        SysDept dept = new SysDept();
        BeanUtils.copyProperties(saveDTO, dept);

        // 如果父部门发生变化，需要更新祖级列表
        if (!Objects.equals(oldDept.getParentId(), saveDTO.getParentId())) {
            updateDeptChildren(dept, oldDept);
        }

        return updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDept(Long deptId) {
        // 检查是否存在子部门
        long childCount = count(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, deptId));
        if (childCount > 0) {
            throw new RuntimeException("存在子部门，不允许删除");
        }

        // 检查是否存在用户
        // 这里需要在SysUser实体中添加deptId字段
        // long userCount = userService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, deptId));
        // if (userCount > 0) {
        //     throw new RuntimeException("部门存在用户，不允许删除");
        // }

        return removeById(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDeptByIds(Long[] deptIds) {
        for (Long deptId : deptIds) {
            deleteDept(deptId);
        }
        return true;
    }

    @Override
    public boolean checkDeptNameUnique(DeptSaveDTO saveDTO) {
        Long deptId = saveDTO.getId() == null ? -1L : saveDTO.getId();
        Long parentId = saveDTO.getParentId() == null ? 0L : saveDTO.getParentId();
        int count = baseMapper.checkDeptNameUnique(saveDTO.getDeptName(), parentId, deptId);
        return count == 0;
    }

    @Override
    public boolean checkDeptCodeUnique(DeptSaveDTO saveDTO) {
        Long deptId = saveDTO.getId() == null ? -1L : saveDTO.getId();
        int count = baseMapper.checkDeptCodeUnique(saveDTO.getDeptCode(), deptId);
        return count == 0;
    }

    @Override
    public Set<Long> getDataScope(Long userId) {
        // 获取用户角色
        List<SysRole> roles = roleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> deptIds = new HashSet<>();

        for (SysRole role : roles) {
            Integer dataScope = role.getDataScope();
            if (dataScope == null) {
                continue;
            }

            switch (dataScope) {
                case 1: // 全部数据权限
                    return new HashSet<>(getAllDeptIds());
                case 2: // 自定部门数据权限：根据角色关联的部门
                    List<Long> roleDeptIds = getDeptIdsByRoleId(role.getId());
                    deptIds.addAll(roleDeptIds);
                    break;
                case 3: // 本部门数据权限
                    Long userDeptId = getUserDeptId(userId);
                    if (userDeptId != null) {
                        deptIds.add(userDeptId);
                    }
                    break;
                case 4: // 本部门及以下数据权限
                    Long userDeptId2 = getUserDeptId(userId);
                    if (userDeptId2 != null) {
                        List<Long> childrenIds = getChildrenDeptIds(userDeptId2);
                        deptIds.addAll(childrenIds);
                    }
                    break;
                case 5: // 仅本人数据权限
                    // 返回空列表，表示只能看到自己的数据
                    return new HashSet<>();
            }
        }

        return deptIds;
    }

    @Override
    public List<Long> getDeptIdsByRoleId(Long roleId) {
        return baseMapper.selectDeptListByRoleId(roleId);
    }

    @Override
    public List<DeptTreeVO> getDeptTreeSelect() {
        List<SysDept> deptList = list(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getStatus, 1)
                .orderByAsc(SysDept::getSortOrder));

        List<DeptTreeVO> deptVOList = deptList.stream().map(this::convertToVO).collect(Collectors.toList());
        return buildDeptTree(deptVOList, 0L);
    }

    @Override
    public List<Long> getChildrenDeptIds(Long deptId) {
        List<Long> childrenIds = baseMapper.selectChildrenDeptById(deptId);
        childrenIds.add(deptId); // 包含自身
        return childrenIds;
    }

    @Override
    public List<Long> getAncestorsDeptIds(Long deptId) {
        List<Long> ancestorsIds = baseMapper.selectAncestorsDeptById(deptId);
        ancestorsIds.add(deptId); // 包含自身
        return ancestorsIds;
    }

    /**
     * 构建部门树
     */
    private List<DeptTreeVO> buildDeptTree(List<DeptTreeVO> deptList, Long parentId) {
        List<DeptTreeVO> tree = new ArrayList<>();

        for (DeptTreeVO dept : deptList) {
            if (Objects.equals(dept.getParentId(), parentId)) {
                List<DeptTreeVO> children = buildDeptTree(deptList, dept.getId());
                dept.setChildren(children);
                dept.setHasChildren(!children.isEmpty());
                tree.add(dept);
            }
        }

        return tree;
    }

    /**
     * 转换为VO对象
     */
    private DeptTreeVO convertToVO(SysDept dept) {
        DeptTreeVO vo = new DeptTreeVO();
        BeanUtils.copyProperties(dept, vo);

        // 设置部门类型名称
        if (dept.getDeptType() != null) {
            switch (dept.getDeptType()) {
                case 1:
                    vo.setDeptTypeName("公司");
                    break;
                case 2:
                    vo.setDeptTypeName("部门");
                    break;
                case 3:
                    vo.setDeptTypeName("小组");
                    break;
            }
        }

        // 设置状态名称
        if (dept.getStatus() != null) {
            vo.setStatusName(dept.getStatus() == 1 ? "正常" : "停用");
        }

        return vo;
    }

    /**
     * 修改子元素关系
     */
    private void updateDeptChildren(SysDept dept, SysDept oldDept) {
        String newAncestors = getAncestors(dept.getParentId());
        String oldAncestors = oldDept.getAncestors();
        dept.setAncestors(newAncestors);

        List<SysDept> children = list(new LambdaQueryWrapper<SysDept>()
                .like(SysDept::getAncestors, oldDept.getId()));

        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }

        if (!children.isEmpty()) {
            updateBatchById(children);
        }
    }

    /**
     * 获取祖级列表
     */
    private String getAncestors(Long parentId) {
        if (parentId == null || parentId == 0) {
            return "0";
        }

        SysDept parent = getById(parentId);
        if (parent != null) {
            return parent.getAncestors() + "," + parentId;
        }

        return "0";
    }

    /**
     * 获取所有部门ID
     */
    private List<Long> getAllDeptIds() {
        List<SysDept> allDepts = list();
        return allDepts.stream().map(SysDept::getId).collect(Collectors.toList());
    }

    /**
     * 获取用户部门ID
     */
    private Long getUserDeptId(Long userId) {
        return securityUtils.getCurrentUserDeptId();
    }
}
