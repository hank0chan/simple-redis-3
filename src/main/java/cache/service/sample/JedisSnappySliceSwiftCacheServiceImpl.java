package cache.service.sample;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import cache.redis.JedisSnappySliceUtils;
import cache.service.SwiftCacheService;

/**
 * 尝试Redis缓存服务的Snappy压缩结合使用分片方式的实现
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 00:17:12 - 19 Feb 2017
 * @detail 性能并不会比单纯使用Snappy压缩的实现方式有优势。
 */
@Service
public class JedisSnappySliceSwiftCacheServiceImpl implements SwiftCacheService {
	
	@Override
	public Serializable get(String cacheName, String cacheKey) {
		return JedisSnappySliceUtils.get(cacheKey);
	}

	@Override
	public void put(String cacheName, int expire, String cacheKey, Serializable result) {
		JedisSnappySliceUtils.put(cacheKey, result, expire);
	}

}
