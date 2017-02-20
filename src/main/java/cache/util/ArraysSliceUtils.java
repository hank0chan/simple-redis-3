package cache.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据分片/合并工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 09:52:43 - 20 Feb 2017
 * @detail
 */
public class ArraysSliceUtils {

	/**
	 * 设定唯一key值
	 * @param targe 目标数组
	 * @param flag 向目标数组中添加的最后一位值，<h1>请注意是int类型强转为byte类型。
	 * @return
	 */
	public static byte[] setArraysLastFlag(byte[] targe, int flag) {
		byte[] result = new byte[targe.length+1];
		System.arraycopy(targe, 0, result, 0, targe.length);
		result[result.length-1] = (byte) flag;
		return result;
	}
	
	/**
	 * 合并
	 * @param slices 多个byte[]分片
	 * @return
	 */
	public static byte[] fastMerge(byte[] ... slices) {
		int resultLen = 0;
		// 计算合并后的数组的长度
		for(byte[] slice : slices) {
			resultLen += slice.length;
		}
		byte[] result = new byte[resultLen];
		int position = 0;
		for(byte[] slice : slices) {
			System.arraycopy(slice, 0, result, position, slice.length);
			position += slice.length;
		}
		return result;
	}
	
	/**
	 * 切分为多个byte[]数组
	 * @param src
	 * @param num 分片数
	 * @return
	 */
	public static List<byte[]> fastSlice(byte[] values, int num) {
		List<byte[]> result = new ArrayList<>();
		int sliceLen = values.length / num;
		// 当到了最后一个分片的时候，其分片长度应该是前面切分后剩下的全部。比如，101分为4片，分别是25,25,25,26
		int position = 0;
		// 对除了最后一个分片外的分片赋值
		for(int i = 0; i < (num - 1); i++) {
			byte[] value = new byte[sliceLen];
			System.arraycopy(values, position, value, 0, value.length);
			position += value.length;
			result.add(value);
		}
		// 对最后一块分片赋值
		byte[] lastValue = new byte[values.length - sliceLen * (num - 1)];
		System.arraycopy(values, position, lastValue, 0, lastValue.length);
		result.add(lastValue);
		return result;
	}
}
