package org.bm.modules.core.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class Arrays {
   public static <F, T> Collection<T> filter(boolean keepNulls, Collection<F> fromList, Function<F, T> function) {
      Collection<T> retour = new ArrayList<T>();

      for (F from : fromList) {
         T ret = function.apply(from);
         if (keepNulls || null != ret) {
            retour.add(ret);
         }
      }

      return retour;
   }

   public static <I> Collection<I> sort(Collection<I> fromList, Comparator<I> comparator) {
      if (null == fromList || fromList.size() == 0) {
         return fromList;
      }
      @SuppressWarnings("unchecked")
      I[] values = (I[]) fromList.toArray();
      quicksort(values, comparator, 0, values.length - 1);
      return java.util.Arrays.asList(values);
   }

   public static <I> void quicksort(I[] fromList, Comparator<I> comparator, int low, int high) {
      int i = low, j = high;
      // Get the pivot element from the middle of the list
      I pivot = fromList[low + (high - low) / 2];

      // Divide into two lists
      while (i <= j) {
         // If the current value from the left list is smaller then the pivot
         // element then get the next element from the left list
         while (comparator.compare(fromList[i], pivot) < 0) {
            i++;
         }
         // If the current value from the right list is larger then the pivot
         // element then get the next element from the right list
         while (comparator.compare(fromList[j], pivot) > 0) {
            j--;
         }

         // If we have found a values in the left list which is larger then
         // the pivot element and if we have found a value in the right list
         // which is smaller then the pivot element then we exchange the
         // values.
         // As we are done we can increase i and j
         if (i <= j) {
            exchange(fromList, i, j);
            i++;
            j--;
         }
      }
      // Recursion
      if (low < j) {
         quicksort(fromList, comparator, low, j);
      }
      if (i < high) {
         quicksort(fromList, comparator, i, high);
      }
   }

   private static <I> void exchange(I[] fromList, int i, int j) {
      I temp = fromList[i];
      fromList[i] = fromList[j];
      fromList[j] = temp;
   }
}
