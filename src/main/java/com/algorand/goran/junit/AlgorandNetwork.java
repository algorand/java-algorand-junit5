package com.algorand.goran.junit;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.goran.rpc.client.NetworkStartReply;
import com.algorand.goran.rpc.client.RpcClient;
import com.algorand.goran.rpc.RpcServer;

public class AlgorandNetwork {
    public String data;
    public RpcServer server;
    public RpcClient rpc;
    public AlgodClient client;
    public NetworkStartReply networkMetadata;
}
