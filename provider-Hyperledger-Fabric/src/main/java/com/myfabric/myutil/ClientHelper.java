package com.myfabric.myutil;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import static java.lang.String.format;


@Service
public class ClientHelper {
    private static final Config clientConfig = Config.getConfig();
    private static final String ADMIN_NAME = clientConfig.ADMIN_NAME;
    private static final String TESTUSER_1_NAME = clientConfig.TESTUSER_1_NAME;
    private static final String FOO_CHANNEL_NAME = clientConfig.FOO_CHANNEL_NAME;
    private static final String CHAIN_CODE_NAME = clientConfig.CHAIN_CODE_NAME;
    private static final String CHAIN_CODE_PATH = clientConfig.CHAIN_CODE_PATH;
    private static final String CHAIN_CODE_VERSION = clientConfig.CHAIN_CODE_VERSION;

    private final static Logger logger = LoggerFactory.getLogger(ClientHelper.class);
    private final static Logger log = LoggerFactory.getLogger(ClientHelper.class);

    MyWebSocket myWebSocket;

    public ClientHelper(MyWebSocket myWebSocket) {
        this.myWebSocket = myWebSocket;
    }

    public ClientHelper() {
        System.out.println("ClientHelper initListener ----------------------------");
    }

    public SampleOrg getSamleOrg() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeySpecException, IOException {

        // Get SampleStore
        File sampleStoreFile = new File(System.getProperty("java.io.tmpdir")
                + "/HFCSampletest.properties");
        log.info(System.getProperty("java.io.tmpdir")
                + "/HFCSampletest.properties");
        SampleStore sampleStore = new SampleStore(sampleStoreFile);

        // Get Org1 from configuration
        SampleOrg sampleOrg = clientConfig
                .getIntegrationTestsSampleOrg("peerOrg1");
        logger.info("-- Get peerOrg1 SampleOrg" + sampleOrg);

        // Set up HFCA for Org1
        logger.info("Set CA Client of peerOrg1 SampleOrg, " + sampleOrg.getCALocation());
        logger.info("Set CA Client of peerOrg1 SampleOrg, " + sampleOrg.getCAProperties());
        sampleOrg.setCAClient(HFCAClient.createNewInstance(
                sampleOrg.getCALocation(), sampleOrg.getCAProperties()));

        sampleOrg.setAdmin(sampleStore.getMember(ADMIN_NAME,
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

        //return setPeerAdmin(sampleStore, sampleOrg);
        return setPeerUser(sampleStore, sampleOrg, ADMIN_NAME);
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

    public SampleOrg setPeerUser(SampleStore sampleStore, SampleOrg sampleOrg, String userName)
            throws IOException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeySpecException {
        final String sampleOrgName = sampleOrg.getName();
        final String sampleOrgDomainName = sampleOrg.getDomainName();
        String str1 = clientConfig.getChannelPath()
                + "/crypto-config/peerOrganizations/" + sampleOrgDomainName
                + format("/users/%s@%s/msp/keystore", userName, sampleOrgDomainName);
        String str2 = clientConfig.getChannelPath()
                + "/crypto-config/peerOrganizations/"
                + sampleOrgDomainName
                + format("/users/%s@%s/msp/signcerts/%s@%s-cert.pem",
                userName, sampleOrgDomainName, userName, sampleOrgDomainName);
        File file = new File(str1);
        File file2 = new File(str2);

        System.out.println("file :" + file + " " + file.exists());
        System.out.println("file2 :" + file2 + " " + file2.exists());
        System.out.println("file :" + str1);
        System.out.println("file2 :" + str2);
        SampleUser peerOrgAdmin = sampleStore.getMember(
                sampleOrgName + userName, sampleOrgName, sampleOrg.getMSPID(),
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
        logger.info("-- Get Chain " + FOO_CHANNEL_NAME);

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

            //check peerLocation
            MyUtil myUtil = new MyUtil();
            String[] tmp = peerLocation.split(":");
            String ipAdd = tmp[1].replace("//", "");
            String port = tmp[2];
            boolean check = myUtil.checkConn(ipAdd, Integer.parseInt(port));
            if (check) {
                channel.addPeer(client.newPeer(peerName, peerLocation,
                        peerProperties));
            }
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

        //add by hsn
        //check the connection


        if (!channel.isInitialized()) {
            channel.initialize();
        }

        // add by hsn

        // while(true){ //todo block listener
		channel.registerBlockListener(new BlockListener() {

			@Override
			public void received(BlockEvent event) {
				// TODO
				log.debug("--log ========================Event========================" + event.getPeer());
				try {
					log.debug("event.getChannelId() = " + event.getChannelId());
					// log.debug("event.getEvent().getChaincodeEvent().getPayload().toStringUtf8() = "
					// + event.getChaincodeEvent().getPayload().toStringUtf8());
					log.debug("event.getBlock().getData().getDataList().size() = "
							+ event.getBlock().getData().getDataList().size());
					ByteString byteString = event.getBlock().getData()
							.getData(0);
					String result = byteString.toStringUtf8();
					//log.debug("byteString.toStringUtf8() = " + result);
					//System.out.println(result);

					String r1[] = result.split("END CERTIFICATE");
					String rr = r1[2];
					//log.debug("rr = " + rr);
					Iterable<TransactionEvent> kk1 = event
							.getTransactionEvents();

					long blockNumber = event.getBlockNumber();
					String channel = event.getChannelId();

					event.getEnvelopeInfo(0).getValidationCode();
					// --- start --

					JSONObject js = null;


					// --- end --
					for (TransactionEvent en : kk1) {
                        if (en !=null && en.getTransactionActionInfo(0) != null && en.getTransactionActionInfo(0).getEvent() != null) {
                            js = new JSONObject();
                            js.put("txid", en.getTransactionID());
                            js.put("chaincode", new String(en.getTransactionActionInfo(0).getChaincodeInputArgs(0)));
                            js.put("proposalUser", new String(en.getTransactionActionInfo(0).getEvent().getPayload()));
                            js.put("ctime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(en.getTimestamp()));
                            System.out.println("--listener event :" + js.toString());

                            if (myWebSocket != null) {
                               myWebSocket.onMessage("start"+ js.toString(), null);
                            }
                        }

						System.out.println("--block listerner :"  + en.isValid());
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

    }

    public void getChannelx() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeySpecException, IOException,
            CryptoException, InvalidArgumentException, TransactionException {
        SampleOrg sampleOrg = this.getSamleOrg();
        HFClient client = this.getHFClient();

        client.setUserContext(sampleOrg.getPeerAdmin());
        getChannelx(sampleOrg, client);

    }
}
