package cache.concurrent.redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cache.redis.JedisSnappyUtils;
import cache.util.JdkSerializableUtils;
import cache.util.SnappyUtils;
import redis.clients.jedis.Jedis;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class ByteArraySliceTest {
	
	@Test
	public void testSystemArrayCopy() {
		// 设定唯一key值
		int[] src = new int[10];
		for(int i = 0; i < src.length; i++) {
			src[i] = i+1;
		}
		System.out.println(Arrays.toString(src));
		int[] target = new int[11];
		System.arraycopy(src, 0, target, 0, src.length);
		target[target.length-1] = 1;
		System.out.println(Arrays.toString(target));
		System.out.println("开始分片。。########################");
		// 分片
		int[] value = new int[200];
		for(int i = 0; i < value.length; i++) {
			value[i] = i+1;
		}
		int mid = value.length / 2;
		int[] first = new int[mid];
		int[] second = new int[value.length - mid];
		System.arraycopy(value, 0, first, 0, first.length);
		System.arraycopy(value, mid, second, 0, second.length);
		System.out.println("value:" + Arrays.toString(value));
		System.out.println(Arrays.toString(first));
		System.out.println(Arrays.toString(second));
		// 合并分片
		int[] merge = new int[first.length+second.length];
		System.arraycopy(first, 0, merge, 0, first.length);
		System.out.println("merge: "+ Arrays.toString(merge));
		System.arraycopy(second, 0, merge, first.length, second.length);
		System.out.println("merge:" + Arrays.toString(merge));
	}
	
	@Test
	public void testFast() {
		int[] value = new int[4000000];
		for(int i = 0; i < value.length; i++) {
			value[i] = i+1;
		}
//		System.out.println("原始数据: " + Arrays.toString(value));
		long startTime = System.currentTimeMillis();
		List<int[]> result = fastSlice(value);
		int[] first = result.get(0);
		int[] second = result.get(1);
		long midTime = System.currentTimeMillis();
//		System.out.println("切分后的第一段：" + Arrays.toString(first));
//		System.out.println("切分后的第二段：" + Arrays.toString(second));
//		System.out.println("开始合并。。。。。。");
		int[] merge = fastMerge(first, second);
//		System.out.println("合并数据：" + Arrays.toString(merge));
//		System.out.println("开始设置key值。。。。。。。。。。");
		// 设定key
		int[] firstKey = new int[10];
		for(int i = 0; i < firstKey.length; i++) {
			firstKey[i] = i+1;
		}
		int[] firstKeyFlag = fastAddKeyFlag(firstKey, 1);
//		System.out.println("before key:" + Arrays.toString(firstKey));
//		System.out.println("after key:" + Arrays.toString(firstKeyFlag));
		
		System.out.println("分片耗时：" + (midTime - startTime) + "ms");
	}
	/**
	 * 设定唯一key值
	 * @param targe
	 * @param flag
	 * @return
	 */
	public int[] fastAddKeyFlag(int[] targe, int flag) {
		int[] result = new int[targe.length+1];
		System.arraycopy(targe, 0, result, 0, targe.length);
		result[result.length-1] = flag;
		return result;
	}
	/**
	 * 分片
	 * @param value
	 * @return
	 */
	public List<int[]> fastSlice(int[] value) {
		List<int[]> result = new ArrayList<>();
		int mid = value.length / 2;
		int[] first = new int[mid];
		int[] second = new int[value.length - mid];
		System.arraycopy(value, 0, first, 0, first.length);
		System.arraycopy(value, mid, second, 0, second.length);
		result.add(first);
		result.add(second);
		return result;
	}
	/**
	 * 合并
	 * @return
	 */
	public int[] fastMerge(int[] first, int[] second) {
		int[] result = new int[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	// 数据分片测试
	@SuppressWarnings("unused")
	@Test
	public void testStringArraySubTest() {
		int[] strs = new int[101];
		for(int i = 0; i < strs.length; i++) {
			strs[i] = i + 1;
		}
		System.out.println("长度：" + strs.length);
		// 开始分片
		int begin = 0;
		int mid = strs.length / 2;
		int end = strs.length;
		int[] first = new int[mid - begin];
		int[] second = new int[end - mid];
		first = slice(strs, begin, mid);
		second = slice(strs, mid, end);
		// 重新组装分片
		int[] result = new int[first.length + second.length];
		result = merge(first, second);
		System.out.println("#####################执行key的设置标识");
		int[] key = new int[10];
		for(int i = 0; i < key.length; i++) {
			key[i] = 0;
		}
		List<int[]> keys = addKeyFlag(key);
		int[] firstKey = keys.get(0);
		int[] secondKey = keys.get(1);
		System.out.println("原来的Key：" + key.length);
		System.out.println("现在的第一个Key：" + firstKey.length);
		System.out.println("现在的第二个Key：" + secondKey.length);
	}
	
	/**
	 * 为分片创建唯一key，是对缓存键添加多一个标识位。通过在结尾加1和2，区别两个键
	 * @param strs
	 * @param begin
	 * @param end
	 * @return
	 */
	public List<byte[]> addKeyFlag(byte[] strs) {
		List<byte[]> result = new ArrayList<>();
		byte[] firstKey = new byte[strs.length+1];
		byte[] secondKey = new byte[strs.length+1];
		for(int i = 0; i < strs.length+1; i++) {
			if(i < strs.length) {
				firstKey[i] = strs[i];
				secondKey[i] = strs[i];
			} else {
				firstKey[i] = 1;
				secondKey[i] = 2;
			}
		}
		result.add(firstKey);
		result.add(secondKey);
		return result;
	}
	
	/**
	 * 分片，是对缓存值进行分片
	 * @param strs
	 * @param begin
	 * @param end
	 * @return
	 */
	public byte[] sliceValue(byte[] strs, int begin, int end) {
		byte[] result = new byte[end - begin];
		for(int i = 0; i < result.length; i++) {
			result[i] = strs[i+begin];
		}
		return result;
	}
	/**
	 * 合并，是对缓存值进行合并
	 * @param first
	 * @param second
	 * @return
	 */
	public byte[] mergeValue(byte[] first, byte[] second) {
		int resultBegin = 0;
		int resultEnd = first.length + second.length;
		int resultMid = (resultBegin + resultEnd) / 2;
		byte[] result = new byte[resultEnd];
		for(int i = 0; i < resultMid; i++) {
			result[i] = first[i];
		}
		for(int i = 0; i < resultEnd-resultMid; i++) {
			result[i+resultMid] = second[i];
		}
		return result;
	}
	// 加标识
	public List<int[]> addKeyFlag(int[] strs) {
		List<int[]> result = new ArrayList<>();
		int[] firstKey = new int[strs.length+1];
		int[] secondKey = new int[strs.length+1];
		for(int i = 0; i < strs.length+1; i++) {
			if(i < strs.length) {
				firstKey[i] = strs[i];
				secondKey[i] = strs[i];
			} else {
				firstKey[i] = 1;
				secondKey[i] = 2;
			}
		}
		result.add(firstKey);
		result.add(secondKey);
		return result;
	}
	// 合并
	public int[] merge(int[] first, int[] second) {
		int resultBegin = 0;
		int resultEnd = first.length + second.length;
		int resultMid = (resultBegin + resultEnd) / 2;
		int[] result = new int[resultEnd];
		for(int i = 0; i < resultMid; i++) {
			result[i] = first[i];
		}
		for(int i = 0; i < resultEnd-resultMid; i++) {
			result[i+resultMid] = second[i];
		}
		return result;
	}
	// 分片
	public int[] slice(int[] strs, int begin, int end) {
		int[] result = new int[end - begin];
		for(int i = 0; i < result.length; i++) {
			result[i] = strs[i+begin];
			System.out.println(result[i]);
		}
		return result;
	}
	
	@Test
	public void test() throws IOException {
		long startTime = System.currentTimeMillis();
		float[][] datas = redisCreateArrays(9, 2000, 2000);
		String keys = "cache.data.DataDao.multi(int,int,int).9,2000,2000";
		// 执行序列化
		byte[] datasSerialBytes = JdkSerializableUtils.serialize(datas);
		byte[] keysSerialBytes = JdkSerializableUtils.serialize(keys);
		// 执行压缩
		byte[] datasCompressBytes = SnappyUtils.compress(datasSerialBytes);
		byte[] keysCompressBytes = SnappyUtils.compress(keysSerialBytes);
		// 对数据分片
		int begin = 0, mid = datasCompressBytes.length/2, end = datasCompressBytes.length;
		byte[] firstDatasCompressBytes = sliceValue(datasCompressBytes, begin, mid);
		byte[] secondDatasCompressBytes = sliceValue(datasCompressBytes, mid, end);
		// 为分片设置唯一的key
		List<byte[]> keyFlags = addKeyFlag(keysCompressBytes);
		byte[] firstKeyCompressBytes = keyFlags.get(0);
		byte[] secondKeyCompressBytes = keyFlags.get(1);
		// 分别存到Redis
		Jedis jedis = JedisSnappyUtils.getJedis();
		jedis.set(firstKeyCompressBytes, firstDatasCompressBytes);
		jedis.expire(firstKeyCompressBytes, 600);
		jedis.set(secondKeyCompressBytes, secondDatasCompressBytes);
		jedis.expire(secondKeyCompressBytes, 600);
		jedis.close();
		long middleTime = System.currentTimeMillis();
		// 从redis取数据
		jedis = JedisSnappyUtils.getJedis();
		byte[] resultFirst = jedis.get(firstKeyCompressBytes);
		byte[] resultSecond = jedis.get(secondKeyCompressBytes);
		byte[] resultMerge = mergeValue(resultFirst, resultSecond);
		jedis.close();
		long endTime = System.currentTimeMillis();
		System.out.println("存数据耗时: " + (middleTime - startTime));
		System.out.println("取数据耗时：" + (endTime - middleTime));
		/***************************************************/
		/*// 存到Redis
		Jedis jedis = JedisSnappyUtils.getJedis();
		jedis.set(keysCompressBytes, datasCompressBytes);
		jedis.expire(keysCompressBytes, 600);
		jedis.close();
		long middle = System.currentTimeMillis();
		// 从Redis取数据
		jedis = JedisSnappyUtils.getJedis();
		jedis.get(keysCompressBytes);
		jedis.close();
		long end = System.currentTimeMillis();
		System.out.println("存数据耗时: " + (middle - start));
		System.out.println("取数据耗时：" + (end - middle));*/
		/*byte[] bs = new byte[key.length+1];
		for(int i = 0; i < key.length; i++) {
			bs[i] = key[i];
			byte a = 1;
			byte b = 2;
			byte c = 3;
			byte d = 4;
		}*/
	}
	
	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		for(int i = begin; i < begin + count; i++) {
			bs[i-begin] = src[i];
		}
		return bs;
	}
	
	// 创建数组
	public static float[][] redisCreateArrays(int key, int row, int col) {
		float[][] result = new float[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				key++;
				result[i][j] = key;
			}
		}
		return result;
	}
}
