package utils;

import java.util.Arrays;
import java.util.Comparator;

import com.google.common.collect.MinMaxPriorityQueue;


public class ArrayUtils {

	/**
	 * @param args
	 */
	
	public static double max(double[] arr){
		double max = arr[0];
		for (double d: arr){
			if (d > max){
				max = d;
			}
		}
		return max;
	}
	
	public static double min(double[] arr){
		double min = arr[0];
		for (double d: arr){
			if (d < min){
				min = d;
			}
		}
		return min;
	}
		
	public static double[] minMax(Object[] array){
		double min = (double) array[0];
		double max = (double) array[0];
		for (int i=1; i < array.length; i++){
			double d = (double) array[i];
			if (d < min) min = d;
			else if (d > max) max = d;
		}
		return new double[]{min, max};
	}
	
	public static double[] minMax2(Double[] array){
		double min = array[0];
		double min2 = min;
		double max = array[0];
		double max2 = max;
		for (int i=1; i < array.length; i++){
			double d = array[i];
			if (d < min){
				min2 = min;
				min = d;
			}
			else if(d < min2){
				min2 = d;
			}
			else if(d > max){
				max2 = max;
				max = d;
			}
			else if(d > max2){
				max2 = d;
			}
		}
		return new double[]{min, min2, max2, max};
	}
	
	public static double getMaxRangeByWindowNaive(Double[] array, int windowSize){
		double range = 0;
		for (int i=0; i < array.length - windowSize; i++){
			double high = array[i];
			double low = high;
			for (int j=i; j < windowSize; j++){
				double rate = array[j];
				if (rate > high) high = rate;
				if (rate < low) low = rate;
			}
			if(high - low > range) range = high - low;
		}
		return range;
	}
	
	public static double getMaxRangeByWindow(Double[] array, int windowSize){
		int length = array.length;
		if(windowSize > length){
			throw new IllegalArgumentException("window size cannot be larger than array size");
		}
		MinMaxPriorityQueue<Double> heap = MinMaxPriorityQueue
											.maximumSize(windowSize)
											.expectedSize(windowSize)
											.create();
		for(int i=0; i < windowSize; i++){
			heap.add(array[i]);
		}
		double maxRange = 0;
		int windowStart = 0;;
		for(int j=0; j < length - windowSize + 1; j++){
			double range = heap.peekLast() - heap.peekFirst();
			
//			for(Double d: heap){
//				System.out.print(d + "  ");
//			}
//			System.out.print("range = " + range);
//			if (range > maxRange) System.out.print("*");
//			System.out.println();
			
			if (range > maxRange) maxRange = range;
				
			heap.remove(array[windowStart]);
			try{
				heap.add(array[windowStart + windowSize]);
			} catch(ArrayIndexOutOfBoundsException e){
				break;
			}
			windowStart++;
		}
		
		return maxRange;
	}
	
	
//	public static getMaxRangeByWindow(double[] array, int windowLength){
//		double[] extrema = getMinMax2(Arrays.copyOfRange(array, 0, windowLength));
//		double min = array[0];
//		double min2 = array[1];
//		double max2 = array[2];
//		double max = array[3];
//		double range = max - min;
//		int windowStart = windowLength;
//		int windowEnd = 2*windowLength - 1;
//		while(windowEnd < array.length){
//			double windowStartVal = array[windowStart];
//			if (windowStartVal == )
//		}
//		
//	}
	
	public static void main(String[] args){
		Double[] arr = new Double[28000];
		Arrays.fill(arr, 0.0);
		Logger.info(getMaxRangeByWindow(arr, 300) + "");
	}

}
