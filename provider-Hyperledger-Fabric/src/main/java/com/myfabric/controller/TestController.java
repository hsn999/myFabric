package com.myfabric.controller;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfabric.myutil.UUIDUtil;
import com.myfabric.respository.InvokeChainCode;
import com.myfabric.service.RedisService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class TestController {

    private final static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    RedisService cacheUtil;


    @RequestMapping(value = "/mock", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject uploadkey(String selected, HttpServletRequest request, @RequestParam("count") int count, @RequestParam("app") String app) {
        logger.info("mock test mutilpe thread " + selected);

        String[] args1 = new String[]{"a"};

        String result = null;

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);

        if ("all".equals(app)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {

                        final int index = i;
                        fixedThreadPool.execute(new Runnable() {
                            public void run() {
                                try {
                                    String parameter = "{\"appId\":\"abcliuyw0\", \"tel\": \"13695003630\", \"point\": \"1\"" +
                                            ", \"name\":\"fkkk\",\"addr\":\"addr1\",\"email\":\"1@1.cn\",\"localUName\":\"localUName\"," +
                                            "\"localUId\":\"localUId\",\"localPoint\":\"100\",\"desc\":\"desc\",\"remark\":\"remark\"}";

                                    String result = runExtend("exchangePoint", parameter);
                                    logger.info("-- current thread result: " + result);
                                } catch (Exception e) {
                                    logger.error("test error :", e);
                                }
                            }
                        });
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {
                        final int index = i;
                        fixedThreadPool.execute(new Runnable() {
                            public void run() {
                                try {
                                    String parameter = "{\"appId\":\"abcliuyw0\", \"tel\": \"17724002684\", \"point\": \"1\"" +
                                            ", \"name\":\"fkkk\",\"addr\":\"addr1\",\"email\":\"1@1.cn\",\"localUName\":\"localUName\"," +
                                            "\"localUId\":\"localUId\",\"localPoint\":\"100\",\"desc\":\"desc\",\"remark\":\"remark\"}";
                                    String result = runExtend("exchangePoint", parameter);
                                    logger.info("-- current thread result: " + result);
                                } catch (Exception e) {
                                    logger.error("test error :", e);
                                }
                            }
                        });
                    }
                }
            }).start();
        }


        if (!"all".equals(app)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {

                        final int index = i;
                        fixedThreadPool.execute(new Runnable() {
                            public volatile boolean exit = false;

                            public void run() {
                                try {
                                    String parameter = "{\"appId\":\"" + app +"\", \"tel\": \"13695003630\", \"point\": \"1\"" +
                                            ", \"name\":\"fkkk\",\"addr\":\"addr1\",\"email\":\"1@1.cn\",\"localUName\":\"localUName\"," +
                                            "\"localUId\":\"localUId\",\"localPoint\":\"100\",\"desc\":\"desc\",\"remark\":\"remark\"}";

                                    String result = runExtend("exchangePoint", parameter);
                                    logger.info("-- current thread result: " + result);
                                } catch (Exception e) {
                                    logger.error("test error :", e);
                                }
                            }
                        });
                    }
                }
            }).start();
        }

        JSONObject ret = new JSONObject();
        ret.put("code", "ok");
        ret.put("msg", "提交成功");
        return ret;
    }


    public String runExtend(String functionName, String parameter) throws Exception {
//        logger.info("/run/invoke/update" + "---" + functionName + "----" + parameter);

        String result = null;

        //invoke chaincode
        ArrayList<String> updateFcnList = new ArrayList<>();
        updateFcnList.add("rechargeByAppId");
        updateFcnList.add("exchangePoint");
        InvokeChainCode invoke = new InvokeChainCode();

        //非账户余额类事务性交易
        if (!updateFcnList.contains(functionName)) {
            result = invoke.invoke(functionName, new String[]{parameter});
            return result;
        }

        //账户余额交易
        //1.send to redis queue
        String uuid = UUIDUtil.get();

        JSONObject tmp = JSONObject.fromObject(parameter);
        String appId = tmp.getString("appId");
        Map<String, String> queueMap = new HashMap<String, String>();
        queueMap.put("appId", appId);
        queueMap.put("uuid", uuid);
        queueMap.put("status", "pending");

        queueMap.put("txid", "-");
        queueMap.put("rsp", "-");

        queueMap.put("functionName", functionName);
        queueMap.put("parameter", parameter);

        cacheUtil.addToQueue("exchange:" + appId, JSONObject.fromObject(queueMap).toString());
        cacheUtil.setPointMapByKey("point:" + appId + ":uuid:" + uuid, JSONObject.fromObject(queueMap).toString());

        //2.sleep 200ms
//        Thread.currentThread().sleep(12000);

        //3.get result from queue
//        Map<String, String> resultMap = cacheUtil.getMapByKey("point:uuid:" + uuid);
//        String status = resultMap.get("status");
//        if ("ok".equals(status)) {
//            result = resultMap.get("rsp");
//        } else {
//            result = "status error"; //todo
//        }

        return result;
    }


}
