package cache.util;

import java.io.IOException;

import org.xerial.snappy.Snappy;

/**
 * Snappy压缩工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 22:43:22 - 17 Feb 2017
 * @detail
 */
public class SnappyUtils {

	/**
	 * 压缩
	 * @param srcBytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] srcBytes) throws IOException {
		return Snappy.compress(srcBytes);
	}
	
	/**
	 * 解压缩
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] bytes) throws IOException {
		if(bytes == null) {
			return null;
		}
		return Snappy.uncompress(bytes);
	}
}
