package com.algorand.goran.rpc.client;

import com.algorand.algosdk.v2.client.model.NodeStatusResponse;
import com.algorand.goran.rpc.client.jsonrpc4j.RpcServerOkHttpClient;
import com.google.common.collect.ImmutableMap;

import java.net.URL;

public class RpcClient implements Network, Sys {
    private final RpcServerOkHttpClient client;
    public RpcClient(URL url) {
        this.client = new RpcServerOkHttpClient(url);
    }

    public NetworkStartReply start(String name, boolean devMode) throws Throwable {
        var args = ImmutableMap.of(
                "Name", name,
                "DevMode", devMode
        );
        return client.invoke("Network.Start", args, NetworkStartReply.class);
    }

    public NetworkStartReply start(String name, String binDirOverride, String tmpDirOverride, String templateOverride, boolean devMode) throws Throwable {
        var args = ImmutableMap.of(
                "Name", name,
                "DevMode", devMode,
                "BinDir", binDirOverride,
                "TmpDir", tmpDirOverride,
                "TemplateFile", templateOverride
        );
        return client.invoke("Network.Start", args, NetworkStartReply.class);
    }

    public NodeStatusResponse status(String name) throws Throwable {
        var args = ImmutableMap.of(
                "Name", name
        );
        return client.invoke("Network.Status", args, NodeStatusResponse.class);
    }

    public NetworkDestroyReply destroy(String name) throws Throwable {
        var args = ImmutableMap.of(
                "Name", name
        );
        return client.invoke("Network.Destroy", args, NetworkDestroyReply.class);
    }

    public NetworkListReply list() throws Throwable {
        var args = ImmutableMap.of();
        return client.invoke("Network.List", args, NetworkListReply.class);
    }

    @Override
    public SystemShutdownReply shutdown(Integer secondsDelay) throws Throwable {
        var args = ImmutableMap.of(
                "SecondsDelay", secondsDelay
        );
        return client.invoke("System.Shutdown", args, SystemShutdownReply.class);
    }

    @Override
    public SystemHealthReply health() throws Throwable {
        return client.invoke("System.Health", ImmutableMap.of(), SystemHealthReply.class);
    }
}
