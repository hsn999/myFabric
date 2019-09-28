package com.dinpay.fabric;

import com.myfabric.respository.InvokeChainCodeLocal;

public class TestGetTx {

    public static void main(String args[]) throws Exception {

        String[] args1 = new String[]{"a"};
        InvokeChainCodeLocal invoke = new InvokeChainCodeLocal(args);

        invoke.getx("0f1a17b7c588f9e283bc22b85e17a1906c5082214bcd7fe933f2c72e9fe2c596");

    }

}
