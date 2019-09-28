package com.myfabric;

import com.google.protobuf.InvalidProtocolBufferException;
import com.myfabric.beans.bo.BlockTxInfo;
import com.myfabric.myutil.UUIDUtil;
import com.myfabric.respository.InvokeChainCode;
import com.myfabric.service.RedisService;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@RestController
public class RestDemo {

    private final static Logger logger = LoggerFactory.getLogger(RestDemo.class);

    @Autowired
    RedisService cacheUtil;

    private static ArrayList<String> updateFcnList = new ArrayList<>();


    @RequestMapping(value = "/springcontent.htm", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getUserHtml(HttpServletRequest request) throws InterruptedException {
        // Test HTML view
        String type = request.getParameter("type");
        if ("on".equals(type)) {
            Thread.sleep(3000);
            return "example on";
        }
        return "example";
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public JSONObject uploadkey(String selected, HttpServletRequest request) {
        logger.info("" + selected);

        String[] args1 = new String[]{"a"};

        String result = null;

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        int count = 6000;
        {

            for (int i = 0; i < count; i++) {
                try {
                    Thread.sleep(2);
                    System.out.println("----------" + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final int index = i;
                fixedThreadPool.execute(new Runnable() {
                    public volatile boolean exit = false;

                    public void run() {

                        try {
                            InvokeChainCode invoke = new InvokeChainCode();
                            //new ChainCodeListener(args);
                            JSONObject js = new JSONObject();
                            js.put("appId", "aro" + index);
                            js.put("appName", "app001");
                            js.put("appType", "0");
                            js.put("company", "abc xxx");
                            js.put("contact", "zhangxiaoko");
                            js.put("tel", "12254878");
                            js.put("qq", "4544545454547");
                            js.put("status", "0");
                            js.put("desc", "jhgsadgshad");
                            js.put("publicKey", "asdjjjc000020kkdkd");
                            js.put("privateKey", "dczdcxcxc");
                            js.put("password", "111111");
                            js.put("remark", "dd");

                            System.out.println(js.toString());
                            String result = null;
                            result = invoke.invoke("createAppAccount", new String[]{js.toString()});
                            System.out.println("----------" + result);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });

            }
        }

        JSONObject ret = new JSONObject();
        ret.put("retCode", "ok");
        ret.put("path", "s");
        return ret;
    }


    static {
        updateFcnList.add("rechargeByAppId");
        updateFcnList.add("exchangePoint");
    }

    @RequestMapping(value = "/run/invoke/update", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String runAsync(@RequestParam(value = "functionName") String functionName, @RequestParam(value = "parameter") String parameter) throws Exception {
        logger.info("-- run async" + "---" + functionName + "----" + parameter);

        String result = "error";

        //.账户余额更新
        // 1.send to redis queue
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
        int count = 10;
        while (count > 0) {
            count -= 1;

            Thread.currentThread().sleep(200);

            //3.get result from queue
            Map<String, String> resultMap = cacheUtil.getMapByKey("point:" + appId + ":uuid:" + uuid);
            if (resultMap == null) {
                continue;
            }

            String status = resultMap.get("status");
            String txid = resultMap.get("txid");
            result = resultMap.get("rsp");

//            if ("error".equals(status) && StringUtils.isNotBlank(txid)) { //fabric 网络存在问题， 返回失败，但最终仍然交易是成功； 补偿机制
//                InvokeChainCode invoke = new InvokeChainCode();
//                invoke.getx(txid);
//                logger.warn("--- tx success reverse ");
//                result = "success";
//                status = "ok";
//                //remove from failed, update uuid#status, rsp
//            }

            if ("ok".equals(status)) {
                return result;
            }
        }
        return result;
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String run(@RequestParam(value = "type") String type,
                      @RequestParam(value = "functionName") String functionName,
                      @RequestParam(value = "parameter") String parameter) throws Exception {

        logger.info("-- run post: " + type + "---" + functionName + "----" + parameter);

        String result = null;
        if (type.equalsIgnoreCase("invoke")) {
            InvokeChainCode invoke = new InvokeChainCode();

            if (updateFcnList.contains(functionName)) {
                logger.info("-- start to invoke update " + parameter);
                result = invoke.invokeUpdate(functionName, new String[]{parameter}, cacheUtil);
            } else {
                result = invoke.invoke(functionName, new String[]{parameter});
            }
        }

        if (type.equalsIgnoreCase("query")) { 
            InvokeChainCode invoke = new InvokeChainCode(); 
            if(functionName.equals("getExchangeRecordByDate")) { 
               JSONObject js = JSONObject.fromObject(parameter); 
               String appId= js.getString("appId"); 
               String start = js.getString("start"); 
               String end = js.getString("end"); 
               result = invoke.query(functionName, new String[]{appId,start,end}); 
            }else { 
               result = invoke.query(functionName, new String[]{parameter}); 
            } 
        }

        return result;
    }

    @RequestMapping(value = "/getTx", method = RequestMethod.POST)
    public String getTxById(@RequestParam(value = "txid") String txid) {

        String result = "";
        try {
            InvokeChainCode invoke = new InvokeChainCode();
            BlockTxInfo txInfo = invoke.getx(txid);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView upload(HttpServletRequest request,
                               HttpServletResponse rep,
                               @RequestParam("creator") String creator,
                               @RequestParam("contractName") String contractName,
                               @RequestParam("contractNumber") String contractNumber,
                               @RequestParam("contractorA") String contractorA,
                               @RequestParam("contractorB") String contractorB,
                               @RequestParam("desc") String desc,
                               @RequestParam("remark") String remark,
                               @RequestParam("myfile1") MultipartFile file) throws Exception {

        JSONObject ret = new JSONObject();

        String attchment = null;
        String time = null;

        try {
            //File fichier = new File(request.getServletContext().getRealPath("/images/") ) ;
            String path = request.getServletContext().getRealPath("/images/");
            //
            String filename = file.getOriginalFilename();
            File fichier = new File(path, filename);
            System.out.println("filename...." + filename);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fichier));
            InputStream is = file.getInputStream();
            byte[] bytes = new byte[1024];
            int sizeRead;
            while ((sizeRead = is.read(bytes, 0, 1024)) > 0) {
                stream.write(bytes, 0, sizeRead);
            }
            stream.flush();
            stream.close();
            System.out.println("You successfully uploaded " + file.getOriginalFilename() + "!");

            IPFS ipfs = new IPFS("/ip4/192.168.37.111/tcp/5001");

            // ipfs.refs.local();

            NamedStreamable.FileWrapper ipfsFile = new NamedStreamable.FileWrapper(new File(path + File.separator + filename));
            MerkleNode addResult = ipfs.add(ipfsFile).get(0);
            System.out.println(addResult.hash);

            JSONObject js = new JSONObject();
            js.put("creator", creator);
            js.put("contractName", contractName);
            js.put("contractNumber", contractNumber);
            js.put("contractorA", contractorA);
            js.put("contractorB", contractorB);
            js.put("attachment", addResult.hash.toString());
            js.put("desc", desc);
            js.put("remark", remark);
            js.put("time", "");


            logger.debug(js.toString());

            InvokeChainCode invoke = new InvokeChainCode();
            String result = invoke.invoke("createContractRecord", new String[]{js.toString()});

            ret.put("retCode", "ok");
            ret.put("path", addResult.hash);

            rep.sendRedirect("new.html");

        } catch (Exception e) {
            logger.error("You failed to upload " + file.getOriginalFilename() + " => ", e.getMessage());
        }

        return null;
    }

    @RequestMapping(value = "/exchangeGift", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String exchangeGift(String parameter, HttpServletRequest request)
            throws ClientProtocolException, IOException {
        String uuid = UUIDUtil.get();
        String key = "exchangeGift:" + uuid;
        cacheUtil.setStringByKey(key, parameter);
        return uuid;
    }

    // 校验验证码
    @RequestMapping(value = "/checkValue", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String checkValue(String value, String uuid, HttpServletRequest request)
            throws ProposalException, InterruptedException, ExecutionException, TimeoutException, Exception {
        // String uuid = UUIDUtil.get();
        System.out.println("-----value-----: " + value);
        System.out.println("-----uuid-----: " + uuid);
        if (value.equalsIgnoreCase("1234")) {
            String key = "exchangeGift:" + uuid;
            String parameter = cacheUtil.getStringByKey(key);
            InvokeChainCode invoke = new InvokeChainCode();
            System.out.println(parameter.toString());
            String result = null;
            result = invoke.invoke("exchangeGift", new String[] { parameter });
            System.out.println("----------" + result);
            return "ok";
        }
        return "error";
    }

}
