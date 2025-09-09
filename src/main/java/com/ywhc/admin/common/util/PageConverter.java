package com.ywhc.admin.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页数据转换工具类
 * 用于将 IPage<Entity> 转换为 Page<VO>
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public class PageConverter {

    /**
     * 将 IPage<T> 转换为 Page<R>
     *
     * @param sourcePage 源分页对象
     * @param converter  转换函数，将 T 类型转换为 R 类型
     * @param <T>        源数据类型
     * @param <R>        目标数据类型
     * @return 转换后的分页对象
     */
    public static <T, R> Page<R> convert(IPage<T> sourcePage, Function<T, R> converter) {
        if (sourcePage == null) {
            return new Page<>();
        }

        // 转换记录
        List<R> convertedRecords = sourcePage.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());

        // 创建新的分页对象
        Page<R> targetPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        targetPage.setRecords(convertedRecords);

        return targetPage;
    }

    /**
     * 将 IPage<T> 转换为 Page<R>，支持批量转换优化
     * 当需要对整个列表进行额外处理时使用此方法
     *
     * @param sourcePage      源分页对象
     * @param batchConverter  批量转换函数，将 List<T> 转换为 List<R>
     * @param <T>             源数据类型
     * @param <R>             目标数据类型
     * @return 转换后的分页对象
     */
    public static <T, R> Page<R> convertBatch(IPage<T> sourcePage, Function<List<T>, List<R>> batchConverter) {
        if (sourcePage == null) {
            return new Page<>();
        }

        // 批量转换记录
        List<R> convertedRecords = batchConverter.apply(sourcePage.getRecords());

        // 创建新的分页对象
        Page<R> targetPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        targetPage.setRecords(convertedRecords);

        return targetPage;
    }

    /**
     * 创建空的分页对象
     *
     * @param current 当前页
     * @param size    每页大小
     * @param <R>     数据类型
     * @return 空的分页对象
     */
    public static <R> Page<R> empty(long current, long size) {
        return new Page<>(current, size, 0);
    }

    /**
     * 复制分页信息，不包含记录数据
     *
     * @param sourcePage 源分页对象
     * @param <R>        目标数据类型
     * @return 只包含分页信息的新对象
     */
    public static <R> Page<R> copyPageInfo(IPage<?> sourcePage) {
        if (sourcePage == null) {
            return new Page<>();
        }
        return new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
    }
}
