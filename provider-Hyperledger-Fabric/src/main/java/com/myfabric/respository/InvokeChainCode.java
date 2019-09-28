package com.myfabric.respository;

import com.google.protobuf.InvalidProtocolBufferException;
import com.myfabric.beans.bo.BlockEnvelop;
import com.myfabric.beans.bo.BlockTxInfo;
import com.myfabric.myutil.ClientHelper;
import com.myfabric.myutil.Config;
import com.myfabric.myutil.SampleOrg;
import com.myfabric.myutil.StartServer;
import com.myfabric.service.RedisService;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class InvokeChainCode {

    private static final ClientHelper clientHelper = new ClientHelper();
    private static final Config clientConfig = Config.getConfig();
    private static final Log logger = LogFactory.getLog(InvokeChainCode.class);
    private String[] args;
    private HFClient client;
    private Channel channel;
    private ChaincodeID chaincodeID;

    @Autowired
    StartServer startServer;

    /*
     * public InvokeChainCode(String[] args) throws CryptoException,
     * InvalidArgumentException, NoSuchAlgorithmException,
     * NoSuchProviderException, InvalidKeySpecException, TransactionException,
     * IOException { this.args = args; this.client = clientHelper.getHFClient();
     * this.channel = clientHelper.getChannel(); this.chaincodeID =
     * clientHelper.getChaincodeID(); SampleOrg sampleOrg =
     * clientHelper.getSamleOrg(); //
     * this.client.setUserContext(sampleOrg.getUser(TESTUSER_1_NAME));
     * this.client.setUserContext(sampleOrg.getPeerAdmin()); // Maybe a bug of
     * // 1.0.0beta, // only peer // admin can // call // chaincode? }
     */

    public InvokeChainCode() throws CryptoException,
            InvalidArgumentException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeySpecException,
            TransactionException, IOException {
        this.args = args;
        this.client = startServer.client;
        this.channel = startServer.channel;
        this.chaincodeID = startServer.chaincodeID;
        SampleOrg sampleOrg = startServer.sampleOrg;
        // this.client.setUserContext(sampleOrg.getUser(TESTUSER_1_NAME));
        this.client.setUserContext(sampleOrg.getPeerAdmin()); // Maybe a bug of
        // 1.0.0beta,
        // only peer
        // admin can
        // call
        // chaincode?
    }

    public String invoke(String fun, String[] pa) throws InvalidArgumentException, ProposalException,
            InvalidProtocolBufferException, UnsupportedEncodingException,
            InterruptedException, ExecutionException, TimeoutException, Exception {

        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[]{"a", "b", "1"});

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

        logger.info("sending transactionProposal to all peers with arguments inspect(IMLC-000001,Chinasystem,China Ocean Shipping Company)");

        Collection<ProposalResponse> transactionPropResp = null;
        String resultAsString = null;
        try {
            ProposalResponse e = null;
            transactionPropResp = channel.sendTransactionProposal(
                    transactionProposalRequest, channel.getPeers());

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
                logger.warn("Expected only one set of consistent proposal responses but got "
                        + proposalConsistencySets.size());
            }

            if (failed.size() > 0) {
                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
                logger.warn("Not enough endorsers for inspect(IMLC-000001,Chinasystem,China Ocean Shipping Company):"
                        + failed.size()
                        + " endorser error: "
                        + firstTransactionProposalResponse.getMessage()
                        + ". Was verified: "
                        + firstTransactionProposalResponse.isVerified());
            } else {
                logger.info("Successfully received transaction proposal responses.");
            }
            ProposalResponse resp = transactionPropResp.iterator().next();
            byte[] x = resp.getChaincodeActionResponsePayload();

            String a = resp.getProposalResponse().getResponse().getPayload().toStringUtf8();
            if (a != null) {
                resultAsString = a;
            }

            logger.info("resultAsString : " + resultAsString);

            logger.info("Sending chain code transaction(inspect(IMLC-000001,Chinasystem,China Ocean Shipping Company)) to orderer.");

        } catch (Exception e1) {
            logger.error("-- fabric: sending proposal error........", e1);
            if (resultAsString != null && !resultAsString.isEmpty()) {
                return resultAsString;
            }
            return "{\"status\":\"false\", \"code\":\"0\", \"message\":\"\"}";
        }

        try {
            logger.info("-- sending transaction proposal to orderer, func " + fun + "pa " + pa);
            channel.sendTransaction(successful)
                    .thenApply(
                            transactionEvent -> {
                                if (transactionEvent.isValid()) {
                                    logger.info("Successfully send transaction proposal to orderer. Transaction ID: "
                                            + transactionEvent.getTransactionID());
                                } else {
                                    logger.error("-- send to orderer  transaction proposal error");
                                }
                                // chain.shutdown(true);
                                return transactionEvent.getTransactionID();
                            })
                    .get(clientConfig.getTransactionWaitTime(), TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("-- fabric: error while sending txs to orderer........", e);
            throw new Exception("error occurred while sending txs to orderer" + e);
        }

        return resultAsString;
    }

    private Collection<ProposalResponse> sendFabricProposal(String fun, String[] parameter) throws Exception {
        logger.info("-- sending transaction proposal to peers, func " + fun + "parameter " + parameter);

        if (StringUtils.isBlank(fun) || parameter == null || parameter.length == 0) {
            throw new Exception("parameter is invalid");
        }

        TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();
        proposalRequest.setChaincodeID(chaincodeID);
        proposalRequest.setFcn(fun);
        proposalRequest.setArgs(parameter);

        Map<String, byte[]> transientMap = new HashMap<>();
        transientMap.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        transientMap.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        transientMap.put("result", ":)".getBytes(UTF_8));
        proposalRequest.setTransientMap(transientMap);

        Collection<ProposalResponse> proposalResponses = channel.sendTransactionProposal(
                proposalRequest, channel.getPeers());

        Collection<ProposalResponse> successProposals = new LinkedList<>();
        Collection<ProposalResponse> failedProposals = new LinkedList<>();
        StringBuffer error = new StringBuffer();
        for (ProposalResponse response : proposalResponses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {

                try {
                    JSONObject jsonObject = JSONObject.fromObject(response.getProposalResponse().getResponse().getPayload().toStringUtf8());

                    if (jsonObject != null && "true".equals(jsonObject.getString("Status"))) {
                        successProposals.add(response);
                    } else {
                        failedProposals.add(response);
                        error.append(jsonObject.getString("Result")).append(":");
                    }
                } catch (Exception e) {

                    failedProposals.add(response);
                }

                continue;
            }
            failedProposals.add(response);
        }

        if (failedProposals.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failedProposals.iterator().next();
            logger.warn("Not enough endorsers for inspect(IMLC-000001,Chinasystem,China Ocean Shipping Company):"
                    + failedProposals.size()
                    + " endorser error: "
                    + firstTransactionProposalResponse.getMessage()
                    + ". Was verified: "
                    + firstTransactionProposalResponse.isVerified());
            throw new Exception("send proposal endorse error:" + error);
        }

        return successProposals;
    }

    private void sendFabricOrder(Collection<ProposalResponse> proposalResponses) throws InterruptedException, ExecutionException, TimeoutException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        logger.info("--## proposalResponse to order, " + proposalResponses.iterator().next().getProposalResponse().getResponse().getPayload().toStringUtf8());
//        logger.info("--## proposalResponse," + ToStringBuilder.reflectionToString(proposalResponses, ToStringStyle.MULTI_LINE_STYLE));
        logger.info("--## proposalResponse," + ToStringBuilder.reflectionToString(proposalResponses.iterator().next(), ToStringStyle.MULTI_LINE_STYLE));

        channel.sendTransaction(proposalResponses)
                .thenApply(
                        transactionEvent -> {
                            if (transactionEvent.isValid()) {
                                logger.info("-- send transaction proposal to orderer success. Txid: " + transactionEvent.getTransactionID());
                            } else {
                                logger.error("--send transaction proposal to orderer  transaction proposal error");
                            }
                            return transactionEvent.getTransactionID();
                        })
                .get(clientConfig.getTransactionWaitTime(), TimeUnit.SECONDS);
    }

    private void delDealingApp(RedisService cacheUtil, String appId, String uuid, String status, String rsp) {

        if (StringUtils.isBlank(appId)) {
            logger.error("appId is null ");
            return;
        }

        if (StringUtils.isBlank(uuid)) {
            logger.error("uuid is null ");
            cacheUtil.delKey("dealing:" + appId);
            return;
        }

        if (StringUtils.isBlank(status)) {
            logger.error("status is null ");
            cacheUtil.delKey("dealing:" + appId);
            return;
        }

        cacheUtil.hset("point:" + appId + ":uuid:" + uuid, "status", status);

        cacheUtil.hset("point:" + appId + ":uuid:" + uuid, "rsp", rsp);

        if (!"ok".equals(status)) {
            cacheUtil.setSetByKey("failed:app:" + appId, uuid);//todo remove
        }

        cacheUtil.delKey("dealing:" + appId);
    }

    public String invokeUpdate(String fun, String[] parameter, RedisService cacheUtil) {

        //extract parameters: appId , uuid
        String uuid = "";
        String appId = "";
        Map<String, String> dealingApp;
        try {

            if (parameter != null && parameter.length > 0) {
                JSONObject jsonObject = JSONObject.fromObject(parameter[0]);
                appId = jsonObject.getString("appId");

                dealingApp = cacheUtil.getMapByKey("dealing:" + appId);
                if (dealingApp != null) {
                    uuid = dealingApp.get("uuid");
                }
            }
        } catch (Exception e) {
            logger.error("-- phase0: extract parameter error", e);
            delDealingApp(cacheUtil, appId, uuid, "extract para, error", e.getMessage());
            return "error";
        }

        logger.info("-- cc invoke update at " + System.currentTimeMillis() + ", param:" + parameter);

        //1. send proposal to peers
        Collection<ProposalResponse> proposalResponses = null;
        try {
            proposalResponses = sendFabricProposal(fun, parameter);
        } catch (Exception e) {
            logger.error("-- phase1: send proposal error", e);
            delDealingApp(cacheUtil, appId, uuid, "error", e.getMessage());
            return "error";
        }

        try {
            ProposalResponse proposalResponse = proposalResponses.iterator().next();
            String txid = proposalResponse.getTransactionID();
            if (StringUtils.isNotBlank(txid)) {
                cacheUtil.hset("point:" + appId + ":uuid:" + uuid, "txid", txid);
            }
        } catch (Exception e) {
            logger.error("-- get tx id ", e);
        }

        //2. send signed endorsed proposal to orderer
        try {
            sendFabricOrder(proposalResponses);
        } catch (Exception e) {
            logger.error("-- phase2: send to order error, param:" + parameter, e);
            delDealingApp(cacheUtil, appId, uuid, "error", "orderer:" + e.getMessage());
            return "error";
        }

        delDealingApp(cacheUtil, appId, uuid, "ok", "success");
        return "success";
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

    public BlockTxInfo getx(String txID) throws InvalidArgumentException, ProposalException, InvalidProtocolBufferException {
        TransactionInfo txInfo = channel.queryTransactionByID(txID);

        txInfo.getEnvelope().getPayload();

        BlockInfo blockInfo = channel.queryBlockByTransactionID(txID);
        BlockTxInfo ret = new BlockTxInfo();
        ret.setBlockNumber(Long.valueOf(blockInfo.getBlockNumber()).toString());
        ret.setChannelId(blockInfo.getChannelId());
        ret.setChannelName(blockInfo.getChannelId());
        ret.setDataHash(Hex.encodeHexString(blockInfo.getDataHash()));
        ret.setPreviousHash(Hex.encodeHexString(blockInfo.getPreviousHash()));
        ret.setEnvelopCount(String.valueOf(blockInfo.getEnvelopeCount()));


        BlockEnvelop envelop = null;
        txInfo.getEnvelope().getPayload().toStringUtf8();
        List<BlockEnvelop> envelops = new ArrayList<>();
        if (blockInfo.getEnvelopeInfos() != null) {
            for (BlockInfo.EnvelopeInfo t : blockInfo.getEnvelopeInfos()) {
                envelop = new BlockEnvelop();
//            envelop.setChaincode(t.getch);
//            envelop.setChaincodeName(t.getChaincodeName());
                envelop.setTransactionID(txID);
//            envelop.setTimestamp(t.getTimestamp());
//            envelop.setVersion(t.getVersion());
                envelops.add(envelop);
            }
            if (envelops.size() > 0) {
                ret.setEnvelops(envelops);
            }
        }


        return ret;

    }

}
