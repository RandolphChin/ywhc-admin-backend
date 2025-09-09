package com.ywhc.admin.common.util;

import org.springframework.beans.BeanUtils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 对象转换工具类
 * 用于单个 Entity 到 VO 的转换
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public class ObjectConverter {

    /**
     * 基本对象转换
     *
     * @param source    源对象
     * @param converter 转换函数
     * @param <T>       源对象类型
     * @param <R>       目标对象类型
     * @return 转换后的对象，如果源对象为 null 则返回 null
     */
    public static <T, R> R convert(T source, Function<T, R> converter) {
        if (source == null) {
            return null;
        }
        return converter.apply(source);
    }

    /**
     * 使用 BeanUtils 进行基础属性复制转换
     *
     * @param source      源对象
     * @param targetClass 目标类型
     * @param <T>         源对象类型
     * @param <R>         目标对象类型
     * @return 转换后的对象
     */
    public static <T, R> R convert(T source, Class<R> targetClass) {
        if (source == null) {
            return null;
        }

        try {
            R target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 BeanUtils 进行基础属性复制，并支持额外的自定义处理
     *
     * @param source         源对象
     * @param targetClass    目标类型
     * @param customizer     自定义处理函数
     * @param <T>            源对象类型
     * @param <R>            目标对象类型
     * @return 转换后的对象
     */
    public static <T, R> R convert(T source, Class<R> targetClass, Consumer<R> customizer) {
        R target = convert(source, targetClass);
        if (target != null && customizer != null) {
            customizer.accept(target);
        }
        return target;
    }

    /**
     * 使用 BeanUtils 进行基础属性复制，并支持基于源对象的自定义处理
     *
     * @param source         源对象
     * @param targetClass    目标类型
     * @param customizer     自定义处理函数，接收源对象和目标对象
     * @param <T>            源对象类型
     * @param <R>            目标对象类型
     * @return 转换后的对象
     */
    public static <T, R> R convert(T source, Class<R> targetClass, java.util.function.BiConsumer<T, R> customizer) {
        R target = convert(source, targetClass);
        if (target != null && customizer != null) {
            customizer.accept(source, target);
        }
        return target;
    }

    /**
     * 条件转换：只有当条件满足时才进行转换
     *
     * @param source    源对象
     * @param condition 转换条件
     * @param converter 转换函数
     * @param <T>       源对象类型
     * @param <R>       目标对象类型
     * @return 转换后的对象，如果条件不满足则返回 null
     */
    public static <T, R> R convertIf(T source, Function<T, Boolean> condition, Function<T, R> converter) {
        if (source == null || !condition.apply(source)) {
            return null;
        }
        return converter.apply(source);
    }

    /**
     * 安全转换：转换失败时返回默认值
     *
     * @param source       源对象
     * @param converter    转换函数
     * @param defaultValue 默认值
     * @param <T>          源对象类型
     * @param <R>          目标对象类型
     * @return 转换后的对象，转换失败时返回默认值
     */
    public static <T, R> R convertSafely(T source, Function<T, R> converter, R defaultValue) {
        if (source == null) {
            return defaultValue;
        }

        try {
            return converter.apply(source);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 安全转换：转换失败时使用供应商提供默认值
     *
     * @param source          源对象
     * @param converter       转换函数
     * @param defaultSupplier 默认值供应商
     * @param <T>             源对象类型
     * @param <R>             目标对象类型
     * @return 转换后的对象，转换失败时返回供应商提供的默认值
     */
    public static <T, R> R convertSafely(T source, Function<T, R> converter, Supplier<R> defaultSupplier) {
        if (source == null) {
            return defaultSupplier.get();
        }

        try {
            return converter.apply(source);
        } catch (Exception e) {
            return defaultSupplier.get();
        }
    }

    /**
     * 链式转换：将多个转换函数链接起来
     *
     * @param source     源对象
     * @param converter1 第一个转换函数
     * @param converter2 第二个转换函数
     * @param <T>        源对象类型
     * @param <M>        中间对象类型
     * @param <R>        目标对象类型
     * @return 最终转换结果
     */
    public static <T, M, R> R convertChain(T source, Function<T, M> converter1, Function<M, R> converter2) {
        if (source == null) {
            return null;
        }
        M intermediate = converter1.apply(source);
        if (intermediate == null) {
            return null;
        }
        return converter2.apply(intermediate);
    }

    /**
     * 创建转换器构建器，支持流式API
     *
     * @param targetClass 目标类型
     * @param <T>         源对象类型
     * @param <R>         目标对象类型
     * @return 转换器构建器
     */
    public static <T, R> ConverterBuilder<T, R> builder(Class<R> targetClass) {
        return new ConverterBuilder<>(targetClass);
    }

    /**
     * 转换器构建器，支持流式API配置转换逻辑
     */
    public static class ConverterBuilder<T, R> {
        private final Class<R> targetClass;
        private java.util.function.BiConsumer<T, R> customizer;

        private ConverterBuilder(Class<R> targetClass) {
            this.targetClass = targetClass;
        }

        /**
         * 添加自定义处理逻辑
         */
        public ConverterBuilder<T, R> customize(java.util.function.BiConsumer<T, R> customizer) {
            if (this.customizer == null) {
                this.customizer = customizer;
            } else {
                this.customizer = this.customizer.andThen(customizer);
            }
            return this;
        }

        /**
         * 构建转换函数
         */
        public Function<T, R> build() {
            return source -> ObjectConverter.convert(source, targetClass, customizer);
        }

        /**
         * 执行转换
         */
        public R convert(T source) {
            return build().apply(source);
        }
    }
}
