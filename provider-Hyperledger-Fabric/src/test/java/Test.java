import net.sf.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.myfabric.respository.InvokeChainCodeLocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Test.class);


    public static void main1(String args[]) throws Exception {

        int count = 1;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

        InvokeChainCodeLocal invoke = new InvokeChainCodeLocal(args);

        for (int i = 0; i < count; i++) {
            logger.info("----------start thread to submit channel proposal, thread - " + i);
            final int index = i;
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                logger.error("error in sleep.", e);
            }

            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject js = new JSONObject();
                        js.put("appId", "app0012306");
                        js.put("appName", "app0012306Name");
                        js.put("appType", "1");
                        js.put("company", "dinpay");
                        js.put("contact", "test con");
                        js.put("tel", "13695003603");
                        js.put("qq", "2342342");
                        js.put("status", "1");
                        js.put("desc", "f4f");
                        js.put("publicKey", "fsdfsdsfs");
                        js.put("privateKey", "23424234abcddddeee");
                        js.put("password", "123456");
                        js.put("remark", "uuuuuu23132" + index);

                        logger.info("--js: "+js.toString());
                        String result = invoke.invoke("updateAppInfo", new String[]{js.toString()});

                        logger.info("------------------ update result:" + result);
                    } catch (Exception e) {
                        logger.error("----error in channel submit proposal..", e);
                    }
                }
            });


        }


    }

    public static void main(String[] args) {
        System.out.println("test main ");
        System.out.println(new String[]{});
    }
}
