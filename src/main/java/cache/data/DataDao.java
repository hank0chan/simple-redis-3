package cache.data;

import java.util.List;

import org.springframework.stereotype.Repository;

import cache.annotation.SwiftCache;

@Repository
public class DataDao {

	/**
	 * 功能需求：根据rules情况设定缓存时效
	 * @param rules
	 * @param pram
	 * @return
	 */
	@SwiftCache(index=0,isIndex=true)
	public List<String> lists(String rules, String pram) {
		
		return null;
	}
	
	@SwiftCache(expire=600)
	public float[][] multi(int key, int row, int col) {
		float[][] datas = redisCreateArrays(key, row, col);
		return datas;
	}
	
	@SwiftCache(expire=1200)
	public float[][] redisKey(int key, int row, int col) {
		float[][] datas = redisCreateArrays(key, row, col);
		return datas;
	}
	
	@SwiftCache(expire=60)
	public float[][] redis(int row, int col) {
		float[][] datas = createArrays(row, col);
		return datas;
	}
	
	/**
	 *  创建二维数据，模拟从数据库取数据的过程
	 */
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
	
	/**
	 *  创建二维数据，模拟从数据库取数据的过程
	 */
	public static float[][] createArrays(int row, int col) {
		float[][] result = new float[row][col];
		int temp = 0;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				temp++;
				result[i][j] = temp;
			}
		}
		return result;
	}
}
