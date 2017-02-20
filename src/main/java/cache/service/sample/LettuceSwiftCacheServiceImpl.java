package cache.service.sample;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import cache.service.SwiftCacheService;
import concurrent.lettuce.LettuceUtils;

/**
 * 尝试使用Lettuce方式实现缓存
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 17:32:42 - 15 Feb 2017
 * @detail
 */
//@Service
public class LettuceSwiftCacheServiceImpl implements SwiftCacheService {

	@Override
	public Serializable get(String cacheName, String cacheKey) {
		return LettuceUtils.get(cacheKey);
	}

	@Override
	public void put(String cacheName, int expire, String cacheKey, Serializable result) {
		LettuceUtils.put(cacheKey, result, expire);
	}

}
