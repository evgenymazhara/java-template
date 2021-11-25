package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort (int array[]) {
    //Arrays.sort(array);

    quickSort(array, 0, array.length - 1);
  }

  public static void quickSort (int array[], int leftBorder, int rightBorder){
    int leftPointer = leftBorder;
    int rightPointer = rightBorder;
    int pivot = array[(leftBorder + rightBorder) / 2];

    do{
      while(array[leftPointer] < pivot)
        leftPointer++;
      while(array[rightPointer] > pivot)
        rightPointer--;

        if(leftPointer <= rightPointer) {
          if(array[leftPointer] > array[rightPointer]) {
            int tmp = array[leftPointer];
            array[leftPointer] = array[rightPointer];
            array[rightPointer] = tmp;
        }
        leftPointer++;
        rightPointer--;
      }
    }while(leftPointer <= rightPointer);

    if(leftPointer < rightBorder)
      quickSort(array, leftPointer, rightBorder);
    if(rightPointer > leftBorder)
      quickSort(array, leftBorder, rightPointer);
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
