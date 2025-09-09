package com.ywhc.admin.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 列表数据转换工具类
 * 用于将 List<Entity> 转换为 List<VO>
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public class ListConverter {

    /**
     * 将 List<T> 转换为 List<R>
     *
     * @param sourceList 源列表
     * @param converter  转换函数，将 T 类型转换为 R 类型
     * @param <T>        源数据类型
     * @param <R>        目标数据类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> convert(List<T> sourceList, Function<T, R> converter) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        return sourceList.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    /**
     * 将 List<T> 转换为 List<R>，支持批量转换优化
     * 当需要对整个列表进行额外处理时使用此方法
     *
     * @param sourceList     源列表
     * @param batchConverter 批量转换函数，将 List<T> 转换为 List<R>
     * @param <T>            源数据类型
     * @param <R>            目标数据类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> convertBatch(List<T> sourceList, Function<List<T>, List<R>> batchConverter) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        return batchConverter.apply(sourceList);
    }

    /**
     * 将 List<T> 转换为 Map<K, R>
     * 适用于需要将列表转换为以某个字段为 key 的 Map 的场景
     *
     * @param sourceList  源列表
     * @param keyMapper   key 提取函数
     * @param valueMapper value 转换函数
     * @param <T>         源数据类型
     * @param <K>         Map 的 key 类型
     * @param <R>         Map 的 value 类型
     * @return 转换后的 Map
     */
    public static <T, K, R> Map<K, R> convertToMap(List<T> sourceList, 
                                                   Function<T, K> keyMapper, 
                                                   Function<T, R> valueMapper) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyMap();
        }

        return sourceList.stream()
                .collect(Collectors.toMap(keyMapper, valueMapper, (existing, replacement) -> replacement));
    }

    /**
     * 将 List<T> 转换为 Map<K, List<R>>
     * 适用于需要按某个字段分组的场景
     *
     * @param sourceList  源列表
     * @param keyMapper   分组 key 提取函数
     * @param valueMapper value 转换函数
     * @param <T>         源数据类型
     * @param <K>         分组 key 类型
     * @param <R>         转换后的数据类型
     * @return 分组后的 Map
     */
    public static <T, K, R> Map<K, List<R>> convertToGroupMap(List<T> sourceList,
                                                              Function<T, K> keyMapper,
                                                              Function<T, R> valueMapper) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyMap();
        }

        return sourceList.stream()
                .collect(Collectors.groupingBy(
                        keyMapper,
                        Collectors.mapping(valueMapper, Collectors.toList())
                ));
    }

    /**
     * 过滤并转换列表
     *
     * @param sourceList 源列表
     * @param filter     过滤条件
     * @param converter  转换函数
     * @param <T>        源数据类型
     * @param <R>        目标数据类型
     * @return 过滤并转换后的列表
     */
    public static <T, R> List<R> filterAndConvert(List<T> sourceList,
                                                  Function<T, Boolean> filter,
                                                  Function<T, R> converter) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        return sourceList.stream()
                .filter(filter::apply)
                .map(converter)
                .collect(Collectors.toList());
    }

    /**
     * 并行转换列表（适用于大数据量或复杂转换逻辑）
     *
     * @param sourceList 源列表
     * @param converter  转换函数
     * @param <T>        源数据类型
     * @param <R>        目标数据类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> convertParallel(List<T> sourceList, Function<T, R> converter) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        return sourceList.parallelStream()
                .map(converter)
                .collect(Collectors.toList());
    }
}
