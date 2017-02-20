package concurrent.lettuce;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Preconditions;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.codec.ByteArrayCodec;

@Configuration
public class LettuceBeanConfiguration {

	private static String host = "10.148.16.74";
//	private static String host = "127.0.0.1";
	private static int port = 6379;
	
	/**
	 * 用于存取byte[]类型数据的Connection的Bean
	 * @param redisClient
	 * @return
	 */
	@Bean(destroyMethod="close")
	public StatefulRedisConnection<byte[], byte[]> statefulRedisConnection(RedisClient redisClient) {
		return redisClient.connect(new ByteArrayCodec());
	}
	
	/**
	 * Redis客户端连接的Bean
	 * @return
	 */
	@Bean(destroyMethod="shutdown")
	public RedisClient redisClient() {
		RedisClient redisClient = RedisClient.create(new RedisURI(host, port, 60, TimeUnit.SECONDS));
		Preconditions.checkNotNull(redisClient, "Redis Client Create Failure!");
		return redisClient;
	}
	
	/*@Bean(destroyMethod="close")
	public StatefulRedisConnection<String, String> strConnection(RedisClient redisClient) {
		return redisClient.connect();
	}*/
}
