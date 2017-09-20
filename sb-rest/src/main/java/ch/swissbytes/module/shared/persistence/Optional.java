package ch.swissbytes.module.shared.persistence;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: timoteo
 * Date: 3/19/14
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */

import java.util.Collection;

/**
 * Optional component defined to avoid NULL-elements usage, it contains a target
 * element that can be with or without a value assigned to it. It's main purpose
 * is to avoid null-error-prone code.
 *
 * @param <T>
 * @author Timoteo Ponce
 */
public class Optional<T> {

    /**
     * Default element that is returned for null-elements.
     */
    public static final Optional ABSENT = new Optional(null);
    private final T value;

    public Optional(final T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static final <T> Optional<T> absent() {
        return ABSENT;
    }

    /**
     * Returns an Optional instance holding the given value.
     *
     * @param value Optional target element
     * @return Optional object containing the given target element
     */
    public static <T> Optional<T> of(final T value) {
        if (value == null) {
            return ABSENT;
        }
        return new Optional<T>(value);
    }

    /**
     * It creates an Optional component for the first element of the given
     * collection, it returns {@link Optional#ABSENT} if the given collection is
     * empty.
     *
     * @param list collection of elements to get the data from
     * @return Optional component for the first element of the given collection,
     * it returns {@link Optional#ABSENT} if the given collection is
     * empty.
     */
    public static <T> Optional<T> firstElement(final Collection<T> list) {
        return list.isEmpty() ? ABSENT : of(list.iterator().next());
    }

    /**
     * Returns the target element held within this Optional component, it throws
     * an IllegalStateException if the target element is null.
     *
     * @return not null target element
     * @throws IllegalStateException if the target element is null.
     */
    public T get() {
        if (value == null) {
            throw new IllegalStateException("Null value");
        }
        return value;
    }

    /**
     * Returns TRUE if the target element is not null.
     *
     * @return TRUE if the target element is not null
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Returns TRUE if the target element is null.
     *
     * @return TRUE if the target element is null
     */
    public boolean isAbsent() {
        return !isPresent();
    }

}
