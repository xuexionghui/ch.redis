package com.bee.sample.ch12.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/strkeyredis") 
public class StringRedisTemplateCrontroller {

	@Autowired
	private StringRedisTemplate redisClient;   //spring boot默认的redis操作接口，适合key和value都是String类型的情况
	/*
	 *相当于在命令行
	 *set  testenv 123
	 *get  testenv 
	 *"123"
	 */
	@RequestMapping("/setget.html") 
	public @ResponseBody String env(@RequestParam(defaultValue="123456xxh") String para  ) throws Exception{
		redisClient.opsForValue().set("testenv", para);
		String str = redisClient.opsForValue().get("testenv");
		String string = redisClient.opsForValue().get("testenv");
		System.out.println(string);
		return str;
	}
	
	@RequestMapping("/addmessage.html") 
	public @ResponseBody String addMessage() throws Exception{
		redisClient.opsForList().leftPush("platform:message","hello,xiandafu");
		redisClient.opsForList().leftPush("platform:message","hello,spring boot");
		List<String> list = redisClient.opsForList().range("platform:message", 0, 1);
		Long size = redisClient.opsForList().size("platform:message");
		System.out.println(size);
		if (list!=null&&list.size()!=0) {
			for (String string : list) {
				System.out.println(string);
			}
		}
		return "success";
	}
	
	@RequestMapping("/readmessage.html") 
	public @ResponseBody String readMessage() throws Exception{
		String str = redisClient.opsForList().leftPop("platform:message");
		System.out.println(str);
		return str;
	}
	
	
	@RequestMapping("/addcache.html") 
	public @ResponseBody String addMessage(String key,String value) throws Exception{
		redisClient.opsForHash().put("cache", key, value);
		Object object = redisClient.opsForHash().get("cache", key);
		System.out.println(object);
		return "success";
	}
	
	@RequestMapping("/getcache.html") 
	public @ResponseBody String addMessage(String key) throws Exception{
		String str = (String)redisClient.opsForHash().get("cache", key);
		return str;
	}
	
	@RequestMapping("/boundvalue.html") 
	public @ResponseBody String boundValue (String key) throws Exception{
		BoundListOperations operations = redisClient.boundListOps(key);
		operations.leftPush("a");
		operations.leftPush("b");
		return String.valueOf(operations.size());		
		
	}
	
	/*
	 * 利用StringRedisTemplate往数据库存值
	 */
	@RequestMapping("/connectionset.html") 
	public @ResponseBody String connectionSet (final String key,final String value) throws Exception{
		redisClient.execute(new RedisCallback(){

			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					connection.set(key.getBytes("UTF-8"), value.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				return null;
			}
			
		});
		
		return "success";
		
	}
	/*
	 * 利用StringRedisTemplate在数据库取值
	 */
	
	@RequestMapping(value="test1")
	public void  getValueByredisTemplte() {
		redisClient.execute(new RedisCallback() {

			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				
				byte[] bs = null;
				try {
					bs = connection.get("xxh1".getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            String string = new String(bs);
				System.out.println(string );
				return null;
			}
		});
	}
	
	
}
