package com.example.maks.filesstatisticapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by maks on 06.03.2015.
 */
public class SortingInformation {

    SortingInformation(){}


    protected static void insertionSort(File[] arr) {
        for(int i = 1; i < arr.length; i++){
            long currElem = arr[i].length();
            int prevKey = i - 1;
            File temp;
            while(prevKey >= 0 && arr[prevKey].length() > currElem){
                temp = arr[prevKey+1];
                arr[prevKey+1] = arr[prevKey];
                arr[prevKey] = temp;
                prevKey--;
            }
        }
    }


    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =  new Comparator<K>() {
            public int compare(K k1, K k2) {
                int compare = map.get(k2).compareTo(map.get(k1));
                if (compare == 0) return 1;
                else return compare;
            }
        };
        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

//    public static sortingArray(final ArrayList<File> fileList){
//        Comparator<File> sizeComparator = (s1, s2) ->{
//            int compare fileList.get(s1).length().compareTo(fileList.get(s2).length());
//        }
//        return fileList;
//    }
}
