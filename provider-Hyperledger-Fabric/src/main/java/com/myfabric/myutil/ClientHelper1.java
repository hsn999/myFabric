package com.myfabric.myutil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import static java.lang.String.format;

public class ClientHelper1 extends Thread{
	private static final Config clientConfig = Config.getConfig();
	private static final String TEST_ADMIN_NAME = clientConfig.ADMIN_NAME;
	private static final String TESTUSER_1_NAME = clientConfig.TESTUSER_1_NAME;
	private static final String FOO_CHANNEL_NAME = clientConfig.FOO_CHANNEL_NAME;
	private static final String CHAIN_CODE_NAME = clientConfig.CHAIN_CODE_NAME;
	private static final String CHAIN_CODE_PATH = clientConfig.CHAIN_CODE_PATH;
	private static final String CHAIN_CODE_VERSION = clientConfig.CHAIN_CODE_VERSION;

	private static final Log logger = LogFactory.getLog(ClientHelper1.class);
	private static final Log log = LogFactory.getLog(ClientHelper1.class);

	public SampleOrg getSamleOrg() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException, IOException {

		// Get SampleStore
		File sampleStoreFile = new File(System.getProperty("java.io.tmpdir")
				+ "/HFCSampletest.properties");
		SampleStore sampleStore = new SampleStore(sampleStoreFile);

		// Get Org1 from configuration
		SampleOrg sampleOrg = clientConfig
				.getIntegrationTestsSampleOrg("peerOrg1");
		logger.info("Get peerOrg1 SampleOrg");

		// Set up HFCA for Org1
		sampleOrg.setCAClient(HFCAClient.createNewInstance(
				sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
		logger.info("Set CA Client of peerOrg1 SampleOrg");

		sampleOrg.setAdmin(sampleStore.getMember(TEST_ADMIN_NAME,
				sampleOrg.getName())); // The
										// admin
										// of
										// this
										// org
		sampleOrg.addUser(sampleStore.getMember(TESTUSER_1_NAME,
				sampleOrg.getName())); // The
										// user
										// of
										// this
										// org

		return setPeerAdmin(sampleStore, sampleOrg);
	}

	public SampleOrg setPeerAdmin(SampleStore sampleStore, SampleOrg sampleOrg)
			throws IOException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException {
		final String sampleOrgName = sampleOrg.getName();
		final String sampleOrgDomainName = sampleOrg.getDomainName();
		String str1 = clientConfig.getChannelPath()
				+ "/crypto-config/peerOrganizations/" + sampleOrgDomainName
				+ format("/users/Admin@%s/msp/keystore", sampleOrgDomainName);
		String str2 = clientConfig.getChannelPath()
				+ "/crypto-config/peerOrganizations/"
				+ sampleOrgDomainName
				+ format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem",
						sampleOrgDomainName, sampleOrgDomainName);
		File file = new File(str1);
		File file2 = new File(str2);

		System.out.println("file :" + file + " " + file.exists());
		System.out.println("file2 :" + file2 + " " + file2.exists());
		System.out.println("file :" + str1);
		System.out.println("file2 :" + str2);
		SampleUser peerOrgAdmin = sampleStore.getMember(
				sampleOrgName + "Admin", sampleOrgName, sampleOrg.getMSPID(),
				Util.findFileSk(file), file2);
		sampleOrg.setPeerAdmin(peerOrgAdmin); // A special user that can crate
												// channels, join peers and
												// install chain code
												// and jump tall blockchains in
												// a single leap!
		return sampleOrg;
	}

	public HFClient getHFClient() throws CryptoException,
			InvalidArgumentException {

		// Create instance of client.
		HFClient client = HFClient.createNewInstance();
		logger.info("Create instance of HFClient");

		try {
			client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		} catch (IllegalAccessException | InstantiationException
				| ClassNotFoundException | NoSuchMethodException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Set Crypto Suite of HFClient");

		return client;
	}

	static File findFile_sk(File directory) {

		File[] matches = directory.listFiles((dir, name) -> name
				.endsWith("_sk"));

		if (null == matches) {
			throw new RuntimeException(format(
					"Matches returned null does %s directory exist?", directory
							.getAbsoluteFile().getName()));
		}

		if (matches.length != 1) {
			throw new RuntimeException(format(
					"Expected in %s only 1 sk file but found %d", directory
							.getAbsoluteFile().getName(), matches.length));
		}

		return matches[0];

	}

	public Channel getChannel() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException, IOException,
			CryptoException, InvalidArgumentException, TransactionException {
		SampleOrg sampleOrg = this.getSamleOrg();
		HFClient client = this.getHFClient();

		client.setUserContext(sampleOrg.getPeerAdmin());
		return getChannel(sampleOrg, client);

	}

	// public Channel getChannelWithUser() throws NoSuchAlgorithmException,
	// NoSuchProviderException, InvalidKeySpecException,
	// IOException, CryptoException, InvalidArgumentException,
	// TransactionException {
	// SampleOrg sampleOrg = this.getSamleOrg();
	// HFClient client = this.getHFClient();
	//
	// client.setUserContext(sampleOrg.getUser(TESTUSER_1_NAME));
	// return getChannel(sampleOrg, client);
	//
	// }

	private Channel getChannel(SampleOrg sampleOrg, HFClient client)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeySpecException, IOException, CryptoException,
			InvalidArgumentException, TransactionException {

		Channel channel = client.newChannel(FOO_CHANNEL_NAME);
		logger.info("Get Chain " + FOO_CHANNEL_NAME);

		// channel.setTransactionWaitTime(clientConfig.getTransactionWaitTime());
		// channel.setDeployWaitTime(clientConfig.getDeployWaitTime());

		// Collection<Peer> channelPeers = new LinkedList<>();
		for (String peerName : sampleOrg.getPeerNames()) {
			String peerLocation = sampleOrg.getPeerLocation(peerName);

			Properties peerProperties = clientConfig
					.getPeerProperties(peerName);
			if (peerProperties == null) {
				peerProperties = new Properties();
			}
			// Example of setting specific options on grpc's
			// ManagedChannelBuilder
			peerProperties.put(
					"grpc.ManagedChannelBuilderOption.maxInboundMessageSize",
					9000000);
			// channelPeers.add(client.newPeer(peerName, peerLocation,
			// peerProperties));
			channel.addPeer(client.newPeer(peerName, peerLocation,
					peerProperties));
		}

		Collection<Orderer> orderers = new LinkedList<>();

		for (String orderName : sampleOrg.getOrdererNames()) {
			orderers.add(client.newOrderer(orderName,
					sampleOrg.getOrdererLocation(orderName),
					clientConfig.getOrdererProperties(orderName)));
		}

		// Just pick the first orderer in the list to create the chain.
		Orderer anOrderer = orderers.iterator().next();
		channel.addOrderer(anOrderer);

		for (String eventHubName : sampleOrg.getEventHubNames()) {
			EventHub eventHub = client.newEventHub(eventHubName,
					sampleOrg.getEventHubLocation(eventHubName),
					clientConfig.getEventHubProperties(eventHubName));
			channel.addEventHub(eventHub);
		}

		if (!channel.isInitialized()) {
			channel.initialize();
		}

		// add by hsn

		// while(true){
		channel.registerBlockListener(new BlockListener() {

			@Override
			public void received(BlockEvent event) {
				// TODO
				log.debug("========================Event========================");
				System.out
						.println("========================Event========================");
				try {
					log.debug("event.getChannelId() = " + event.getChannelId());
					// log.debug("event.getEvent().getChaincodeEvent().getPayload().toStringUtf8() = "
					// + event.getChaincodeEvent().getPayload().toStringUtf8());
					log.debug("event.getBlock().getData().getDataList().size() = "
							+ event.getBlock().getData().getDataList().size());
					ByteString byteString = event.getBlock().getData()
							.getData(0);
					String result = byteString.toStringUtf8();
					log.debug("byteString.toStringUtf8() = " + result);
					System.out.println(result);

					String r1[] = result.split("END CERTIFICATE");
					String rr = r1[2];
					log.debug("rr = " + rr);
					Iterable<TransactionEvent> kk1 = event
							.getTransactionEvents();
					for (TransactionEvent en : kk1) {
						System.out.println(en.getTransactionID());
						System.out.println(en.isValid());
					}

				} catch (InvalidProtocolBufferException e) {
					// TODO
					e.printStackTrace();
				}
				log.debug("========================Event========================");
			}
		});

		// }

		/*
		 * channel.registerBlockListener(new BlockListener() {
		 * 
		 * @Override public void received(BlockEvent event) { // TODO
		 * log.debug("========================Event========================");
		 * System
		 * .out.println("========================Event========================"
		 * ); try { log.debug("event.getChannelId() = " + event.getChannelId());
		 * //log.debug(
		 * "event.getEvent().getChaincodeEvent().getPayload().toStringUtf8() = "
		 * +
		 * event.getEventHub().getChaincodeEvent().getPayload().toStringUtf8());
		 * log.debug("event.getBlock().getData().getDataList().size() = " +
		 * event.getBlock().getData().getDataList().size()); ByteString
		 * byteString = event.getBlock().getData().getData(0); String result =
		 * byteString.toStringUtf8(); log.debug("byteString.toStringUtf8() = " +
		 * result); System.out.println(result);
		 * 
		 * String r1[] = result.split("END CERTIFICATE"); String rr = r1[2];
		 * log.debug("rr = " + rr); } catch (InvalidProtocolBufferException e) {
		 * // TODO e.printStackTrace(); }
		 * log.debug("========================Event========================"); }
		 * });
		 */

		return channel;
	}

	public ChaincodeID getChaincodeID() {
		return ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
				.setVersion(CHAIN_CODE_VERSION).setPath(CHAIN_CODE_PATH)
				.build();
	}

	private void getChannelx(SampleOrg sampleOrg, HFClient client)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeySpecException, IOException, CryptoException,
			InvalidArgumentException, TransactionException, InterruptedException {

		Channel channel = client.newChannel(FOO_CHANNEL_NAME);
		logger.info("Get Chain " + FOO_CHANNEL_NAME);

		// channel.setTransactionWaitTime(clientConfig.getTransactionWaitTime());
		// channel.setDeployWaitTime(clientConfig.getDeployWaitTime());

		// Collection<Peer> channelPeers = new LinkedList<>();
		for (String peerName : sampleOrg.getPeerNames()) {
			String peerLocation = sampleOrg.getPeerLocation(peerName);

			Properties peerProperties = clientConfig
					.getPeerProperties(peerName);
			if (peerProperties == null) {
				peerProperties = new Properties();
			}
			// Example of setting specific options on grpc's
			// ManagedChannelBuilder
			peerProperties.put(
					"grpc.ManagedChannelBuilderOption.maxInboundMessageSize",
					9000000);
			// channelPeers.add(client.newPeer(peerName, peerLocation,
			// peerProperties));
			channel.addPeer(client.newPeer(peerName, peerLocation,
					peerProperties));
		}

		Collection<Orderer> orderers = new LinkedList<>();

		for (String orderName : sampleOrg.getOrdererNames()) {
			orderers.add(client.newOrderer(orderName,
					sampleOrg.getOrdererLocation(orderName),
					clientConfig.getOrdererProperties(orderName)));
		}

		// Just pick the first orderer in the list to create the chain.
		Orderer anOrderer = orderers.iterator().next();
		channel.addOrderer(anOrderer);

		for (String eventHubName : sampleOrg.getEventHubNames()) {
			EventHub eventHub = client.newEventHub(eventHubName,
					sampleOrg.getEventHubLocation(eventHubName),
					clientConfig.getEventHubProperties(eventHubName));
			channel.addEventHub(eventHub);
		}
		

		if (!channel.isInitialized()) {
			channel.initialize();
		}

		// add by hsn

		// while(true){
		//channel.unregisterBlockListener(false);
	
		String a = channel.registerBlockListener(new BlockListener() {

			@Override
			public void received(BlockEvent event) {
				// TODO
				log.debug("========================Event========================");
				System.out
						.println("========================Event========================");
				try {
					log.debug("event.getChannelId() = " + event.getChannelId());
					// log.debug("event.getEvent().getChaincodeEvent().getPayload().toStringUtf8() = "
					// + event.getChaincodeEvent().getPayload().toStringUtf8());
					log.debug("event.getBlock().getData().getDataList().size() = "
							+ event.getBlock().getData().getDataList().size());
					ByteString byteString = event.getBlock().getData()
							.getData(0);
					String result = byteString.toStringUtf8();
					log.debug("byteString.toStringUtf8() = " + result);
					//System.out.println(result);

					String r1[] = result.split("END CERTIFICATE");
					String rr = r1[2];
					log.debug("rr = " + rr);
					Iterable<TransactionEvent> kk1 = event
							.getTransactionEvents();
					for (TransactionEvent en : kk1) {
						System.out.println(en.getTransactionID());
						System.out.println(en.isValid());
					}

				} catch (InvalidProtocolBufferException e) {
					// TODO
					e.printStackTrace();
				}
				log.debug("========================Event========================");
			}
		});
		
		
		//channel.unregisterBlockListener(a);
		//channel.wait();	

		// }
/*		final Map<Long, BlockEvent> blockEvents = Collections.synchronizedMap(new HashMap<>(100));
		System.out.println("------------------111----------------------------");
	    final String blockListenerHandle = channel.registerBlockListener(blockEvent -> { // register a block listener
	    	
	    	System.out.println("----------------------------------------------");
	    	BlockEvent event = blockEvent;

	        try {
	            final long blockNumber = blockEvent.getBlockNumber();
	            System.out.println("-------------------blockNumber---------------------------"+blockNumber);
	            BlockEvent seen = blockEvents.put(blockNumber, blockEvent);

	        } catch (AssertionError | Exception e) {
	            e.printStackTrace();

	        }
	        
	        try {
				log.debug("event.getChannelId() = " + event.getChannelId());
				// log.debug("event.getEvent().getChaincodeEvent().getPayload().toStringUtf8() = "
				// + event.getChaincodeEvent().getPayload().toStringUtf8());
				log.debug("event.getBlock().getData().getDataList().size() = "
						+ event.getBlock().getData().getDataList().size());
				ByteString byteString = event.getBlock().getData()
						.getData(0);
				String result = byteString.toStringUtf8();
				log.debug("byteString.toStringUtf8() = " + result);
				System.out.println(result);

				String r1[] = result.split("END CERTIFICATE");
				String rr = r1[2];
				log.debug("rr = " + rr);
				Iterable<TransactionEvent> kk1 = event
						.getTransactionEvents();
				for (TransactionEvent en : kk1) {
					System.out.println(en.getTransactionID());
					System.out.println(en.isValid());
				}

			} catch (InvalidProtocolBufferException e) {
				// TODO
				e.printStackTrace();
			}

	    });*/
	    
	    //channel.isShutdown();

	}
	
	SampleOrg sampleOrg = null;
	HFClient client = null;
	public void run() {
		System.out.println("run in new Thread....................");
		//SampleOrg sampleOrg = null;
		try {
			sampleOrg = this.getSamleOrg();
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| InvalidKeySpecException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//HFClient client = null;
		try {
			client = this.getHFClient();
		} catch (CryptoException | InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			client.setUserContext(sampleOrg.getPeerAdmin());
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			getChannelx(sampleOrg, client);
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| InvalidKeySpecException | CryptoException
				| InvalidArgumentException | TransactionException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	
}
