package com.algorand.goran.rpc.client;

import com.algorand.algosdk.v2.client.model.NodeStatusResponse;

public interface Network {
    NetworkStartReply start(String name, boolean devMode) throws Throwable;

    NetworkStartReply start(String name, String binDirOverride, String tmpDirOverride, String templateOverride, boolean devMode) throws Throwable;

    NodeStatusResponse status(String name) throws Throwable;

    NetworkDestroyReply destroy(String name) throws Throwable;

    NetworkListReply list() throws Throwable;
}
