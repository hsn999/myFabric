package com.myfabric.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

@FeignClient(name="PROVIDER-REDIS",fallback=HystrixClientFallback.class)
public interface RedisService {
	@RequestMapping("/addToQueue")
	void addToQueue(@RequestParam("key") String key, @RequestParam("map") String map);

	@RequestMapping("/getFromQueue")
	Map<String, String> getFromQueue(@RequestParam("key") String key);

	@RequestMapping("/getMapByKey")
	Map<String, String> getMapByKey(@RequestParam("key") String key);

    @RequestMapping("/setPointMapByKey")
    void setPointMapByKey(@RequestParam("key") String key, @RequestParam("hash") String hash) throws Exception ;

	@RequestMapping("/checkExists")
	Boolean checkExists(@RequestParam("key") String key) throws Exception;

	@RequestMapping("/checkListMember")
	Boolean checkListMember(@RequestParam("key") String key, @RequestParam("value") String value) throws Exception;

	@RequestMapping("/getKeys")
	Set<String> getKeys(@RequestParam("key") String key);

	@RequestMapping("/getSetByKey")
	Set<String> getSetByKey(@RequestParam("key") String key) throws Exception;

	@RequestMapping("/hset")
	void hset(@RequestParam("key") String key, @RequestParam("field") String field, @RequestParam("value") String value) ;

	@RequestMapping("/removeFromSet")
	void removeFromSet(@RequestParam("key") String key, @RequestParam("value") String value) ;

	@RequestMapping("/setMapByKey")
	void setMapByKey(@RequestParam("key") String key, @RequestParam("hash") String hash) throws Exception ;

	@RequestMapping("/setSetByKey")
	void setSetByKey(@RequestParam("key") String key, @RequestParam("value") String value);

	@RequestMapping("/getStringByKey")
	String getStringByKey(@RequestParam("key") String key) ;

	@RequestMapping("/setStringByKey")
	void setStringByKey(@RequestParam("key") String key, @RequestParam("value") String value) ;

	@RequestMapping("/delKey")
	void delKey(@RequestParam("key") String key);
}