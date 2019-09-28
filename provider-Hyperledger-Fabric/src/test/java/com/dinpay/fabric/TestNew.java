package com.dinpay.fabric;


import net.sf.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.myfabric.respository.InvokeChainCodeLocal;

public class TestNew {

    public static void main(String args[]) throws Exception {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        int count = 7;
        {
            String[] args1 = new String[]{"a"};

            InvokeChainCodeLocal invoke = new InvokeChainCodeLocal(args);

            for (int i = 0; i < count; i++) {
                try {
                    Thread.sleep(20);
                    System.out.println("----------" + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final int index = i;
                fixedThreadPool.execute(new Runnable() {
                    public volatile boolean exit = false;

                    public void run() {

                        try {
                            //InvokeChainCodeLocal invoke = new InvokeChainCodeLocal(args);
                            //new ChainCodeListener(args);
                            JSONObject js = new JSONObject();
                            //\"appId\":\"xxxx001\",\"appName\":\"xxxx\",\"appType\":\"0\",\"company\":\"xxxx\",\"contact\":\"xxxx\",\"tel\":\"15115555555\",\"qq\":\"xxxxx\",
                            //\"status\":\"0\",\"desc\":\"xxxxx\",\"publicKey\":\"xxxxx\",\"privateKey\":\"xxxxx\",\"password\":\"xxxxx\",\"remark\":\"xxxxx\"}
                            js.put("appId", "abcliuyw" + index);
                            js.put("appName", "xrxz" + index);
                            js.put("appType", "0");
                            js.put("company", "abc xxx");
                            js.put("contact", "zhangxiaoko");
                            js.put("tel", "12254878");
                            js.put("qq", "4544545454547");
                            js.put("status", "0");
                            js.put("desc", "jhgsadgshad");
                            js.put("publicKey", "asdjjjc00sdssdssd0020kkdkd");
                            js.put("privateKey", "dczdcsdsdsdsdsdxcxc");
                            js.put("password", "1111dsddssdsdds11");
                            js.put("remark", "dsdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssd");

                            System.out.println(js.toString());
                            String result = null;
                            result = invoke.invoke("createAppAccount", new String[]{js.toString()});

                            //System.out.println(result);


                            //result = invoke.query("getAppById",new String[] { "app003" });

                            //getAllApp
                            //result = invoke.query("getAllApp",new String[] { });
                            //System.out.println(result);
                            //JSONObject json = JSONObject.fromObject(result);
                            //Object bb = json.get("Result");
                            //System.out.println(json.toString());


                            //InvokeChainCodeLocal invoke = new InvokeChainCodeLocal(args);
                            //invoke.getx("aa");


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });

            }


        }
    }
}
