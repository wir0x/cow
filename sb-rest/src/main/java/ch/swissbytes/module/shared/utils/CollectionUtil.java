package ch.swissbytes.module.shared.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil {

    /**
     * @param collection
     * @param predicate
     * @param <T>
     * @return True if there is any value with the condition
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<? super T> predicate) {
        return collection.stream().anyMatch(predicate);
    }

    public static <T, R> Stream<R> map(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        return collection.stream().map(mapper);
    }

    public static <T, R> List<R> toList(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        return toList(map(collection, mapper));
    }

    private static <R> List<R> toList(Stream<R> stream) {
        return stream.collect(Collectors.toList());
    }

}