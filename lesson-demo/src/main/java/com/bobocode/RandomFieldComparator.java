package com.bobocode;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A generic comparator that is comparing a random field of the given class. The field is either primitive or
 * {@link Comparable}. It is chosen during comparator instance creation and is used for all comparisons.
 * <p>
 * By default it compares only accessible fields, but this can be configured via a constructor property. If no field is
 * available to compare, the constructor throws {@link IllegalArgumentException}
 *
 * @param <T> the type of the objects that may be compared by this comparator
 */
public class RandomFieldComparator<T> implements Comparator<T> {

    private final Class<T> targetType;
    private final Field field;

    public RandomFieldComparator(Class<T> targetType) {
        this(targetType, true);
    }

    /**
     * A constructor that accepts a class and a property indicating which fields can be used for comparison. If property
     * value is true, then only public fields or fields with public getters can be used.
     *
     * @param targetType                  a type of objects that may be compared
     * @param compareOnlyAccessibleFields config property indicating if only publicly accessible fields can be used
     */
    public RandomFieldComparator(Class<T> targetType, boolean compareOnlyAccessibleFields) {
        Objects.requireNonNull(targetType);
        this.field = getRandomField(targetType, compareOnlyAccessibleFields);
        this.field.setAccessible(true);
        this.targetType = targetType;
    }

    private static Field[] getFields(Class<?> targetType, boolean compareOnlyAccessibleFields) {
        var fieldsStream = Arrays.stream(targetType.getDeclaredFields())
                                 .filter(RandomFieldComparator::isComparableOrPrimitive);

        if (compareOnlyAccessibleFields) {
            fieldsStream = fieldsStream.filter(field -> RandomFieldComparator.isAccessible(targetType, field));
        }

        return fieldsStream.toArray(Field[]::new);
    }


    private static Field getRandomField(Class<?> targetType, boolean compareOnlyAccessibleFields) {
        var fields = getFields(targetType, compareOnlyAccessibleFields);
        validateFieldsArePresent(fields, targetType, compareOnlyAccessibleFields);
        int randomIndex = ThreadLocalRandom.current().nextInt(0, fields.length - 1);
        return fields[randomIndex];
    }

    private static void validateFieldsArePresent(Field[] fields, Class<?> targetType, boolean compareOnlyAccessibleFields) {
        if (fields.length == 0) {
            String message = "%s doesn't have any comparable or primitive properties".formatted(targetType.getName());
            message += compareOnlyAccessibleFields ? " with public access or public getter" : "";
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean isComparableOrPrimitive(Field field) {
        return Comparable.class.isAssignableFrom(field.getType())
                || field.getType().isPrimitive();
    }

    private static boolean isAccessible(Class<?> targetType, Field field) {
        return isPublic(field) || hasPublicGetter(targetType, field);
    }

    private static boolean isPublic(Field field) {
        return (field.getModifiers() & Modifier.PUBLIC) > 0;
    }

    private static boolean hasPublicGetter(Class<?> targetType, Field field) {
        return Arrays.stream(targetType.getDeclaredMethods())
                     .anyMatch(method -> isGetterForField(method, field));
    }

    private static boolean isGetterForField(Method method, Field field) {
        return method.getName().equalsIgnoreCase("get" + field.getName());
    }

    /**
     * Compares two objects of the class T by the value of the field that was randomly chosen. It allows null values
     * for the fields, and it treats null value grater than a non-null value (nulls last).
     */
    @Override
    @SneakyThrows
    public int compare(T o1, T o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);

        var field1 = (Comparable) field.get(o1);
        var field2 = (Comparable) field.get(o2);

        return Comparator.<Comparable>nullsFirst(Comparator.naturalOrder()).compare(field1, field2);
    }

    /**
     * Returns a statement "Random field comparator of class '%s' is comparing '%s'" where the first param is the name
     * of the type T, and the second parameter is the comparing field name.
     *
     * @return a predefined statement
     */
    @Override
    public String toString() {
        return "Random field comparator of class '%s' is comparing '%s'".formatted(targetType, field.getName());
    }
}
