package cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * LZ4压缩解压缩工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 00:16:52 - 19 Feb 2017
 * @detail
 */
public class LZ4Utils {

	/**
	 * 压缩
	 * @param srcBytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] srcBytes) throws IOException {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		LZ4Compressor compressor = factory.fastCompressor();
		LZ4BlockOutputStream blockOutputStream = 
				new LZ4BlockOutputStream(byteArrayOutputStream, 2048, compressor);
		blockOutputStream.write(srcBytes);
		blockOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}
	
	/**
	 * 解压缩
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] bytes) throws IOException {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		LZ4FastDecompressor decompressor = factory.fastDecompressor();
		LZ4BlockInputStream blockInputStream = 
				new LZ4BlockInputStream(new ByteArrayInputStream(bytes), decompressor);
		int count;
		byte[] buffer = new byte[2048];
		while((count = blockInputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, count);
		}
		blockInputStream.close();
		return byteArrayOutputStream.toByteArray();
	}
}
