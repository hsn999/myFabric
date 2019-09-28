package com.dinpay.fabric;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

//@Service
public class FabricThreadPool  implements InitializingBean,DisposableBean{  
    
    //volatile  
    //volatile private static EthPool instance = null;  
	//public volatile static int count = 0;  
    private FabricThreadPool(){
    	System.out.println("start..FabricThreadPool.............................");
    }  
    
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
       
    public static ExecutorService getInstance() {  
        try {    
            if(threadPool != null){//   
            	System.out.println(" FabricThreadPool not null.............................");
            }else{  
                //   
                Thread.sleep(300);  
                synchronized (FabricThreadPool.class) {  
                    if(threadPool == null){//  
                    	threadPool = Executors.newFixedThreadPool(10);
                    }  
                }  
            }   
        } catch (InterruptedException e) {   
            e.printStackTrace();  
        }  
        return threadPool;  
    }

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		if(threadPool != null){//   
        	System.out.println("kill FabricThreadPool.............................");
        	threadPool.shutdown();
        }
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("start.afterPropertiesSet.FabricThreadPool.............................");
	}  
}  