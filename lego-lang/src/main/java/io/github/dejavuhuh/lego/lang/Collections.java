package io.github.dejavuhuh.lego.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 集合工具类
 *
 * @author wu.yue
 * @since 2023/12/30 03:28
 */
public class Collections {

    public static <E> boolean some(Iterable<E> iterable, Predicate<? super E> predicate) {
        return stream(iterable).anyMatch(predicate);
    }

    public static <E> boolean every(Iterable<E> iterable, Predicate<? super E> predicate) {
        return stream(iterable).allMatch(predicate);
    }

    public static <E> boolean none(Iterable<E> iterable, Predicate<? super E> predicate) {
        return stream(iterable).noneMatch(predicate);
    }

    public static <E> Stream<E> stream(Iterable<E> iterable) {
        Check.notNull(iterable, "Iterable<E> iterable");
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <E, K, V> Map<K, V> iterableToMap(
            Iterable<E> iterable,
            Mapper<? super E, ? extends K> keyMapper,
            Mapper<? super E, ? extends V> valueMapper) {
        Check.notNull(iterable, "Iterable<E> iterable");
        Check.notNull(keyMapper, "Mapper<? super E, ? extends K> keyMapper");
        Check.notNull(valueMapper, "Mapper<? super E, ? extends V> valueMapper");
        Map<K, V> map = new HashMap<>();
        for (E element : iterable) {
            K key = keyMapper.map(element);
            V value = valueMapper.map(element);
            map.put(key, value);
        }
        return map;
    }

    public static <E, K> Map<K, E> iterableToMap(
            Iterable<E> iterable, Mapper<? super E, ? extends K> keyMapper) {
        return iterableToMap(iterable, keyMapper, Mapper.noop());
    }
}
