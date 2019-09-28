package com.myfabric.myutil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.myfabric.respository.InvokeChainCode;

@Service
@Order(value=100)
public class StartServer implements InitializingBean,DisposableBean{
	@Autowired
	static MyWebSocket myWebSocket;
	
	public ClientHelper clientHelper ;
	public static final Config clientConfig = Config.getConfig();
	public static final Log logger = LogFactory.getLog(InvokeChainCode.class);
	public static String[] args;
	public static HFClient client;
	public static Channel channel;
	public static ChaincodeID chaincodeID;
	public static SampleOrg sampleOrg;
	
	//private static Logger logger1 = Logger.getLogger(RestDemo.class);

	public void InvokeChainCode() throws CryptoException,
			InvalidArgumentException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException,
			TransactionException, IOException {
		//this.args = args;
		StartServer.client = clientHelper.getHFClient();
		StartServer.channel = clientHelper.getChannel();
		StartServer.chaincodeID = clientHelper.getChaincodeID();
		StartServer.sampleOrg = clientHelper.getSamleOrg();
		// this.client.setUserContext(sampleOrg.getUser(TESTUSER_1_NAME));
		StartServer.client.setUserContext(sampleOrg.getPeerAdmin()); // Maybe a bug of
																// 1.0.0beta,
																// only peer
																// admin can
																// call
																// chaincode?
	}

	
	public StartServer(){
		
		System.out.println("StartServer ----------------------------"); 
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		//myWebSocket.
		if(myWebSocket == null){
			myWebSocket = new MyWebSocket();
		}
		this.clientHelper = new ClientHelper(myWebSocket);
		InvokeChainCode();
	}

}
