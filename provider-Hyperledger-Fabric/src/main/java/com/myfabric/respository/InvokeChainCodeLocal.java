package com.myfabric.respository;

import com.google.protobuf.InvalidProtocolBufferException;
import com.myfabric.myutil.ClientHelper;
import com.myfabric.myutil.Config;
import com.myfabric.myutil.SampleOrg;
import com.myfabric.myutil.StartServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class InvokeChainCodeLocal {

    private static final ClientHelper clientHelper = new ClientHelper();
    private static final Config clientConfig = Config.getConfig();
    private static final Log logger = LogFactory
            .getLog(InvokeChainCodeLocal.class);
    private String[] args;
    private HFClient client;
    private Channel channel;
    private ChaincodeID chaincodeID;

    // private static Logger logger1 = Logger.getLogger(RestDemo.class);
    @Autowired
    StartServer startServer;

    public InvokeChainCodeLocal(String[] args) throws CryptoException,
            InvalidArgumentException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeySpecException,
            TransactionException, IOException {
        this.args = args;
        this.client = clientHelper.getHFClient();
        this.channel = clientHelper.getChannel();
        this.chaincodeID = clientHelper.getChaincodeID();
        SampleOrg sampleOrg = clientHelper.getSamleOrg(); //
        // this.client.setUserContext(sampleOrg.getUser(TESTUSER_1_NAME));
        this.client.setUserContext(sampleOrg.getPeerAdmin());
        // Maybe a bug of
        // 1.0.0beta, // only peer // admin can // call // chaincode? }
    }

    /*
     * public InvokeChainCodeLocal(String[] args) throws CryptoException,
     * InvalidArgumentException, NoSuchAlgorithmException,
     * NoSuchProviderException, InvalidKeySpecException, TransactionException,
     * IOException { this.args = args; this.client = startServer.client;
     * this.channel = startServer.channel; this.chaincodeID =
     * startServer.chaincodeID; SampleOrg sampleOrg = startServer.sampleOrg; //
     * this.client.setUserContext(sampleOrg.getUser(TESTUSER_1_NAME));
     * this.client.setUserContext(sampleOrg.getPeerAdmin()); // Maybe a bug of
     * // 1.0.0beta, // only peer // admin can // call // chaincode? }
     */
    public String invoke(String fun, String[] pa) throws InvalidArgumentException, ProposalException,
            InvalidProtocolBufferException, UnsupportedEncodingException,
            InterruptedException, ExecutionException, TimeoutException {


        ///former

        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);

        if (fun != null) {
            transactionProposalRequest.setFcn(fun);
        }

        if (pa != null) {
            transactionProposalRequest.setArgs(pa);
        }

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8)); // / This should be returned
        transactionProposalRequest.setTransientMap(tm2);

        System.out.println("sending transactionProposal to all peers with arguments inspect(IMLC-000001,Chinasystem,China Ocean Shipping Company)");

        Collection<ProposalResponse> transactionPropResp = null;
        String resultAsString = null;
        try {
            ProposalResponse e = null;
            transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());


            for (ProposalResponse response : transactionPropResp) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    successful.add(response);
                    logger.info("ransactionProposal..........." + response.getTransactionID());
                } else {
                    failed.add(response);
                }
            }

            Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
                    .getProposalConsistencySets(transactionPropResp);
            if (proposalConsistencySets.size() != 1) {
                System.out.println("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size());
            }

            if (failed.size() > 0) {
                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
                System.out.println("Not enough endorsers for inspect(IMLC-000001,Chinasystem,China Ocean Shipping Company):"
                        + failed.size()
                        + " endorser error: "
                        + firstTransactionProposalResponse.getMessage()
                        + ". Was verified: "
                        + firstTransactionProposalResponse.isVerified());
            } else {
                System.out.println("Successfully received transaction proposal responses.");
            }

            ProposalResponse resp = transactionPropResp.iterator().next();
            byte[] x = resp.getChaincodeActionResponsePayload();
            String a = resp.getProposalResponse().getResponse().getPayload().toStringUtf8();
            if (a != null) {
                resultAsString = a;
            }
            System.out.println(resultAsString);

            logger.info("sending transaction proposal to orderer");
            channel.sendTransaction(successful).thenApply(
                    transactionEvent -> {
                        if (transactionEvent.isValid()) {
                            System.out.println("Successfully send transaction proposal to orderer. Transaction ID: "
                                    + transactionEvent
                                    .getTransactionID());
                        } else {
                            System.out.println("Failed to send transaction proposal to orderer");
                        }
                        // chain.shutdown(true);
                        return transactionEvent.getTransactionID();

                    })
                    .get(clientConfig.getTransactionWaitTime(),
                            TimeUnit.SECONDS);
        } catch (Exception e1) {
            logger.error("--local:m........" + e1);
            return "{\"Status\":false,\"Code\":1,\"Result\":\"fail to update AppInfo.\"}";

        }

        return resultAsString;
    }

    public String query(String fun, String[] pa) throws InvalidArgumentException, ProposalException {

        // Create instance of client.
        // HFClient client = clientHelper.getHFClient();

        // Create instance of channel.
        // Channel channel = clientHelper.getChannelWithPeerAdmin();

        // Create instance of ChaincodeID
        // ChaincodeID chaincodeID = clientHelper.getChaincodeID();

        QueryByChaincodeRequest queryByChaincodeRequest = client
                .newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(new String[]{"a"});
        queryByChaincodeRequest.setFcn("query");

        if (fun != null) {
            queryByChaincodeRequest.setFcn(fun);
        }

        if (pa != null) {
            queryByChaincodeRequest.setArgs(pa);
        }

        queryByChaincodeRequest.setChaincodeID(chaincodeID);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric",
                "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tm2);
        String payload = null;
        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(
                queryByChaincodeRequest, channel.getPeers());
        for (ProposalResponse proposalResponse : queryProposals) {
            if (!proposalResponse.isVerified()
                    || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                System.out.println("Failed query proposal from peer "
                        + proposalResponse.getPeer().getName() + " status: "
                        + proposalResponse.getStatus() + ". Messages: "
                        + proposalResponse.getMessage() + ". Was verified : "
                        + proposalResponse.isVerified());
            } else {
                payload = proposalResponse.getProposalResponse().getResponse()
                        .getPayload().toStringUtf8();
                System.out.println("Query payload of IMLC-0001 from peer: "
                        + proposalResponse.getPeer().getName());
                System.out.println("" + payload);
            }
        }
        System.out.println("payload....." + payload);
        return payload;

    }

    public TransactionInfo getx(String txID) throws InvalidArgumentException, ProposalException, InvalidProtocolBufferException {
        TransactionInfo info = channel.queryTransactionByID("3d7473b704e092ae7e292cd473780d6f34955eee1557e60e4e99dbb5c712a16d");
        Common.Envelope envelope = info.getEnvelope();
//		BlockInfo a = channel.queryBlockByTransactionID("3d7473b704e092ae7e292cd473780d6f34955eee1557e60e4e99dbb5c712a16d");
//		ByteString gg = info.getEnvelope().getSignature();
//		info.getEnvelope().parseFrom(gg.toByteArray());
//		System.out.println(a);
        return info;

    }

}
