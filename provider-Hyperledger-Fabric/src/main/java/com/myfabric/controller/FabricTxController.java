package com.myfabric.controller;

import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.web.bind.annotation.*;

import com.myfabric.beans.bo.BlockTxInfo;
import com.myfabric.beans.bo.ResponseJson;
import com.myfabric.respository.InvokeChainCode;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class FabricTxController {


    //通过交易id来获取交易信息
    @RequestMapping(value = "/getx", method = RequestMethod.POST)
    public ResponseJson transation(String transactionId) throws InvalidArgumentException, NoSuchAlgorithmException, IOException, TransactionException, NoSuchProviderException, CryptoException, InvalidKeySpecException, ProposalException {

        InvokeChainCode invoke = new InvokeChainCode();

        BlockTxInfo blockTxInfo = invoke.getx(transactionId);

        return new ResponseJson(blockTxInfo);
    }
}