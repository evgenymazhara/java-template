package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort (int array[]) {
    Arrays.sort(array);

    for(int i = 0; i < array.length; i++){
      for(int j = 0; j < array.length - i - 1; j++){
        if(array[i] > array[j]){
          int tmp = array[i];
          array[i] = array[j];
          array[j] = tmp;
        }
      }
    }
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
