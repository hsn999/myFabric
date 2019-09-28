package com.myfabric.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class HystrixClientFallback implements RedisService {


    @Override
    public void setPointMapByKey(String key, String hash) throws Exception {

    }

    @Override
    public void addToQueue(String key, String map) {

    }

    @Override
    public void setMapByKey(String key, String hash) throws Exception {

    }

//    @Override
//	public void addToQueue(String key, Map<String, String> map) {
//		// TODO Auto-generated method stub
//		System.out.println(map);
//
//	}

	@Override
	public Map<String, String> getFromQueue(String key) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		return null;
	}

	@Override
	public Map<String, String> getMapByKey(String key) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		return null;
	}

	@Override
	public Boolean checkExists(String key) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		return null;
	}

	@Override
	public Boolean checkListMember(String key, String value) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		return null;
	}

	@Override
	public Set<String> getKeys(String key) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		return null;
	}

	@Override
	public Set<String> getSetByKey(String key) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		return null;
	}

	@Override
	public void hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		
	}

	@Override
	public void removeFromSet(String key, String value) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
	}

//	@Override
//	public void setMapByKey(String key, Map<String, String> hash) throws Exception {
//		// TODO Auto-generated method stub
//		System.out.println("HystrixClientFallback............");
//	}

	@Override
	public void setSetByKey(String key, String value) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
	}

	@Override
	public String getStringByKey(String key) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
		return null;
	}

	@Override
	public void setStringByKey(String key, String value) {
		// TODO Auto-generated method stub
		System.out.println("HystrixClientFallback............");
	}

	@Override
	public void delKey(String key) {

	}
}
