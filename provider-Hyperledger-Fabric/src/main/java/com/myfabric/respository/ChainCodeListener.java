package com.myfabric.respository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import com.myfabric.myutil.ClientHelper;
import com.myfabric.myutil.ClientHelper1;
import com.myfabric.myutil.Config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class ChainCodeListener {

	private static final ClientHelper clientHelper = new ClientHelper();
	private static final Config clientConfig = Config.getConfig();
	private static final Log logger = LogFactory
			.getLog(ChainCodeListener.class);
	private String[] args;
	private HFClient client;
	private Channel channel;
	private ChaincodeID chaincodeID;

	public ChainCodeListener(String[] args) throws CryptoException,
			InvalidArgumentException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException,
			TransactionException, IOException {
		// this.args = args;
		// this.client = clientHelper.getHFClient();
		// clientHelper.getChannelx();
		ClientHelper1 th2 = new ClientHelper1();
		th2.run();

	}

}
