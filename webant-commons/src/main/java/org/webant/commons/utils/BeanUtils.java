package org.webant.commons.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.apache.commons.beanutils.PropertyUtils.isReadable;
import static org.apache.commons.beanutils.PropertyUtils.isWriteable;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    public static Field[] getDeclaredFields(Object bean) {
        Field[] fields = new Field[0];
        Class clazz = bean.getClass();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            Field[] superFields = clazz.getDeclaredFields();
            fields = (Field[]) ArrayUtils.addAll(fields, superFields);
        }

        return Arrays.stream(fields).filter(field -> isReadable(bean, field.getName()) && isWriteable(bean, field.getName())).toArray(Field[]::new);
    }

    public static Field[] getDeclaredFields(Class clazz) {
        Field[] fields = new Field[0];
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            Field[] superFields = clazz.getDeclaredFields();
            fields = (Field[]) ArrayUtils.addAll(fields, superFields);
        }

        return fields;
    }

    public static String[] getDeclaredFieldNames(Class clazz) {
        Field[] fields = getDeclaredFields(clazz);
        return Arrays.stream(fields).map(Field::getName).toArray(String[]::new);
    }
}