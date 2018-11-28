package com.hryj.utils;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 王光银
 * @className: UtilMisc
 * @description:
 * @create 2018/6/30 0030 14:38
 **/
public class UtilMisc {

    public static final String module = UtilMisc.class.getName();

    private static final BigDecimal ZERO_BD = BigDecimal.ZERO;

    private UtilMisc () {}

    public static <T extends Throwable> T initCause(T throwable, Throwable cause) {
        throwable.initCause(cause);
        return throwable;
    }

    public static <T> int compare(Comparable<T> obj1, T obj2) {
        if (obj1 == null) {
            if (obj2 == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return obj1.compareTo(obj2);
        }
    }

    public static <E> int compare(List<E> obj1, List<E> obj2) {
        if (obj1 == obj2) {
            return 0;
        }
        try {
            if (obj1.size() == obj2.size() && obj1.containsAll(obj2) && obj2.containsAll(obj1)) {
                return 0;
            }

        } catch (Exception e) {}
        return 1;
    }

    /**
     * Get an iterator from a collection, returning null if collection is null
     * @param col The collection to be turned in to an iterator
     * @return The resulting Iterator
     */
    public static <T> Iterator<T> toIterator(Collection<T> col) {
        if (col == null) {
            return null;
        } else {
            return col.iterator();
        }
    }

    /**
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static <V, V1 extends V> Map<String, V> toMap(String name1, V1 value1) {
        return populateMap(new HashMap<String, V>(), name1, value1);
    }

    /**
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static <V, V1 extends V, V2 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2) {
        return populateMap(new HashMap<String, V>(), name1, value1, name2, value2);
    }

    /**
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static <V, V1 extends V, V2 extends V, V3 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3) {
        return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3);
    }

    /**
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static <V, V1 extends V, V2 extends V, V3 extends V, V4 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3, String name4, V4 value4) {
        return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3, name4, value4);
    }

    /**
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static <V, V1 extends V, V2 extends V, V3 extends V, V4 extends V, V5 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3, String name4, V4 value4, String name5, V5 value5) {
        return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3, name4, value4, name5, value5);
    }

    /**
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static <V, V1 extends V, V2 extends V, V3 extends V, V4 extends V, V5 extends V, V6 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3, String name4, V4 value4, String name5, V5 value5, String name6, V6 value6) {
        return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3, name4, value4, name5, value5, name6, value6);
    }

    /**
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<String, V> toMap(Object... data) {
        if (data.length % 2 == 1) {
            IllegalArgumentException e = new IllegalArgumentException("You must pass an even sized array to the toMap method (size = " + data.length + ")");
            throw e;
        }
        Map<String, V> map = new HashMap<String, V>();
        for (int i = 0; i < data.length;) {
            map.put((String) data[i++], (V) data[i++]);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<String, V> populateMap(Map<String, V> map, Object... data) {
        for (int i = 0; i < data.length;) {
            map.put((String) data[i++], (V) data[i++]);
        }
        return map;
    }

    public static <K, V> String printMap(Map<? extends K, ? extends V> theMap) {
        StringBuilder theBuf = new StringBuilder();
        for (Map.Entry<? extends K, ? extends V> entry: theMap.entrySet()) {
            theBuf.append(entry.getKey());
            theBuf.append(" --> ");
            theBuf.append(entry.getValue());
            theBuf.append(System.getProperty("line.separator"));
        }
        return theBuf.toString();
    }

    public static <T> List<T> makeListWritable(Collection<? extends T> col) {
        List<T> result = new LinkedList<T>();
        if (col != null) {
            result.addAll(col);
        }
        return result;
    }

    public static <K, V> Map<K, V> makeMapWritable(Map<K, ? extends V> map) {
        if (map == null) {
            return new HashMap<K, V>();
        }
        Map<K, V> result = new HashMap<K, V>(map.size());
        result.putAll(map);
        return result;
    }

    public static <T> Set<T> makeSetWritable(Collection<? extends T> col) {
        Set<T> result = new LinkedHashSet<T>();
        if (col != null) {
            result.addAll(col);
        }
        return result;
    }

    /**
     * This change a Map to be Serializable by removing all entries with values that are not Serializable.
     *
     * @param <V>
     * @param map
     */
    public static <V> void makeMapSerializable(Map<String, V> map) {
        // now filter out all non-serializable values
        Set<String> keysToRemove = new LinkedHashSet<String>();
        for (Map.Entry<String, V> mapEntry: map.entrySet()) {
            Object entryValue = mapEntry.getValue();
            if (entryValue != null && !(entryValue instanceof Serializable)) {
                keysToRemove.add(mapEntry.getKey());
                //Debug.logInfo("Found Map value that is not Serializable: " + mapEntry.getKey() + "=" + mapEntry.getValue(), module);
            }
        }
        for (String keyToRemove: keysToRemove) { map.remove(keyToRemove); }
    }

    /**
     * Assuming theMap not null; if null will throw a NullPointerException
     */
    public static <K> BigDecimal addToBigDecimalInMap(Map<K, Object> theMap, K mapKey, BigDecimal addNumber) {
        Object currentNumberObj = theMap.get(mapKey);
        BigDecimal currentNumber = null;
        if (currentNumberObj == null) {
            currentNumber = ZERO_BD;
        } else if (currentNumberObj instanceof BigDecimal) {
            currentNumber = (BigDecimal) currentNumberObj;
        } else if (currentNumberObj instanceof Double) {
            currentNumber = new BigDecimal(((Double) currentNumberObj).doubleValue());
        } else if (currentNumberObj instanceof Long) {
            currentNumber = new BigDecimal(((Long) currentNumberObj).longValue());
        } else {
            throw new IllegalArgumentException("In addToBigDecimalInMap found a Map value of a type not supported: " + currentNumberObj.getClass().getName());
        }

        if (addNumber == null || ZERO_BD.compareTo(addNumber) == 0) {
            return currentNumber;
        }
        currentNumber = currentNumber.add(addNumber);
        theMap.put(mapKey, currentNumber);
        return currentNumber;
    }

    public static <T> T removeFirst(List<T> lst) {
        return lst.remove(0);
    }

    public static <T> Set<T> collectionToSet(Collection<T> c) {
        if (c == null) {
            return null;
        }
        Set<T> theSet = null;
        if (c instanceof Set<?>) {
            theSet = (Set<T>) c;
        } else {
            theSet = new LinkedHashSet<T>();
            c.remove(null);
            theSet.addAll(c);
        }
        return theSet;
    }

    /**
     * Create a Set from passed objX parameters
     * @return The resulting Set
     */
    public static <T> Set<T> toSet(T obj1) {
        Set<T> theSet = new LinkedHashSet<T>();
        theSet.add(obj1);
        return theSet;
    }

    /**
     * Create a Set from passed objX parameters
     * @return The resulting Set
     */
    public static <T> Set<T> toSet(T obj1, T obj2) {
        Set<T> theSet = new LinkedHashSet<T>();
        theSet.add(obj1);
        theSet.add(obj2);
        return theSet;
    }

    /**
     * Create a Set from passed objX parameters
     * @return The resulting Set
     */
    public static <T> Set<T> toSet(T obj1, T obj2, T obj3) {
        Set<T> theSet = new LinkedHashSet<T>();
        theSet.add(obj1);
        theSet.add(obj2);
        theSet.add(obj3);
        return theSet;
    }

    /**
     * Create a Set from passed objX parameters
     * @return The resulting Set
     */
    public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4) {
        Set<T> theSet = new LinkedHashSet<T>();
        theSet.add(obj1);
        theSet.add(obj2);
        theSet.add(obj3);
        theSet.add(obj4);
        return theSet;
    }

    /**
     * Create a Set from passed objX parameters
     * @return The resulting Set
     */
    public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4, T obj5) {
        Set<T> theSet = new LinkedHashSet<T>();
        theSet.add(obj1);
        theSet.add(obj2);
        theSet.add(obj3);
        theSet.add(obj4);
        theSet.add(obj5);
        return theSet;
    }

    /**
     * Create a Set from passed objX parameters
     * @return The resulting Set
     */
    public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6) {
        Set<T> theSet = new LinkedHashSet<T>();
        theSet.add(obj1);
        theSet.add(obj2);
        theSet.add(obj3);
        theSet.add(obj4);
        theSet.add(obj5);
        theSet.add(obj6);
        return theSet;
    }

    public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6, T obj7, T obj8) {
        Set<T> theSet = new LinkedHashSet<T>();
        theSet.add(obj1);
        theSet.add(obj2);
        theSet.add(obj3);
        theSet.add(obj4);
        theSet.add(obj5);
        theSet.add(obj6);
        theSet.add(obj7);
        theSet.add(obj8);
        return theSet;
    }

    public static <T> Set<T> toSet(Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        if (collection instanceof Set<?>) {
            return (Set<T>) collection;
        } else {
            Set<T> theSet = new LinkedHashSet<T>();
            theSet.addAll(collection);
            return theSet;
        }
    }

    public static <T> Set<T> toSetArray(T[] data) {
        if (data == null) {
            return null;
        }
        Set<T> set = new LinkedHashSet<T>();
        for (T value: data) {
            set.add(value);
        }
        return set;
    }

    /**
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static <T> List<T> toList(T obj1) {
        List<T> list = new LinkedList<T>();

        list.add(obj1);
        return list;
    }

    /**
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static <T> List<T> toList(T obj1, T obj2) {
        List<T> list = new LinkedList<T>();

        list.add(obj1);
        list.add(obj2);
        return list;
    }

    /**
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static <T> List<T> toList(T obj1, T obj2, T obj3) {
        List<T> list = new LinkedList<T>();

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        return list;
    }

    /**
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static <T> List<T> toList(T obj1, T obj2, T obj3, T obj4) {
        List<T> list = new LinkedList<T>();

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        return list;
    }

    /**
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static <T> List<T> toList(T obj1, T obj2, T obj3, T obj4, T obj5) {
        List<T> list = new LinkedList<T>();

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        list.add(obj5);
        return list;
    }

    /**
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static <T> List<T> toList(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6) {
        List<T> list = new LinkedList<T>();

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        list.add(obj5);
        list.add(obj6);
        return list;
    }

    public static <T> List<T> toList(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6, T obj7, T obj8, T obj9) {
        List<T> list = new LinkedList<T>();

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        list.add(obj5);
        list.add(obj6);
        list.add(obj7);
        list.add(obj8);
        list.add(obj9);
        return list;
    }

    public static <T> List<T> toList(Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        if (collection instanceof List<?>) {
            return (List<T>) collection;
        } else {
            List<T> list = new LinkedList<T>();
            list.addAll(collection);
            return list;
        }
    }

    public static <T> List<T> toListArray(T[] data) {
        if (data == null) {
            return null;
        }
        List<T> list = new LinkedList<T>();
        for (T value: data) {
            list.add(value);
        }
        return list;
    }


    /** Converts an <code>Object</code> to a <code>double</code>. Returns
     * zero if conversion is not possible.
     * @param obj Object to convert
     * @return double value
     */
    public static double toDouble(Object obj) {
        Double result = toDoubleObject(obj);
        return result == null ? 0.0 : result.doubleValue();
    }

    /** Converts an <code>Object</code> to a <code>Double</code>. Returns
     * <code>null</code> if conversion is not possible.
     * @param obj Object to convert
     * @return Double
     */
    public static Double toDoubleObject(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Number) {
            return new Double(((Number)obj).doubleValue());
        }
        Double result = null;
        try {
            result = Double.parseDouble(obj.toString());
        } catch (Exception e) {}
        return result;
    }

    /** Converts an <code>Object</code> to an <code>int</code>. Returns
     * zero if conversion is not possible.
     * @param obj Object to convert
     * @return int value
     */
    public static int toInteger(Object obj) {
        Integer result = toIntegerObject(obj);
        return result == null ? 0 : result.intValue();
    }

    /** Converts an <code>Object</code> to an <code>Integer</code>. Returns
     * <code>null</code> if conversion is not possible.
     * @param obj Object to convert
     * @return Integer
     */
    public static Integer toIntegerObject(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof Number) {
            return ((Number)obj).intValue();
        }
        Integer result = null;
        try {
            result = Integer.parseInt(obj.toString());
        } catch (Exception e) {}
        return result;
    }

    /** Converts an <code>Object</code> to a <code>long</code>. Returns
     * zero if conversion is not possible.
     * @param obj Object to convert
     * @return long value
     */
    public static long toLong(Object obj) {
        Long result = toLongObject(obj);
        return result == null ? 0 : result.longValue();
    }

    /** Converts an <code>Object</code> to a <code>Long</code>. Returns
     * <code>null</code> if conversion is not possible.
     * @param obj Object to convert
     * @return Long
     */
    public static Long toLongObject(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Number) {
            return new Long(((Number)obj).longValue());
        }
        Long result = null;
        try {
            result = Long.parseLong(obj.toString());
        } catch (Exception e) {}
        return result;
    }

}
