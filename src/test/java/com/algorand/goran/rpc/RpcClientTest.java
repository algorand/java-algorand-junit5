package com.algorand.goran.rpc;

import com.algorand.goran.rpc.client.RpcClient;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RpcClientTest {
    @Test
    public void testRpcClientFullPath() throws Throwable {
        var port = 2222;
        var server = new RpcServer(
                Path.of("/home/will/go/bin/goran"),
                Path.of("/tmp/goranTmpDir1"),
                Path.of("/home/will/go/bin"));
        server.start(port, RpcServer.DEFAULT_STARTUP_WAIT_TIME_SECONDS, RpcServer.DEFAULT_IDLE_TIMEOUT_SECONDS);

        var name = "testnet";
        var client = new RpcClient(new URL("http://localhost:"+port+"/rpc"));
        var startReply = client.start(name, false);
        assertThat(startReply).isNotNull();
        var statusReply = client.status(name);
        assertThat(statusReply).isNotNull();
        var listReply = client.list();
        assertThat(listReply).isNotNull();
        var destroyReply = client.destroy(name);
        assertThat(destroyReply).isNotNull();
    }

    @Test
    public void testRpcClientNoPath() throws Throwable {
        var port = 3333;
        var server = new RpcServer(
                null,
                Path.of("/tmp/goranTmpDir2"),
                null);
        server.start(port, RpcServer.DEFAULT_STARTUP_WAIT_TIME_SECONDS, RpcServer.DEFAULT_IDLE_TIMEOUT_SECONDS);

        var name = "testnet";
        var client = new RpcClient(new URL("http://localhost:"+port+"/rpc"));
        var startReply = client.start(name, false);
        assertThat(startReply).isNotNull();
        var statusReply = client.status(name);
        assertThat(statusReply).isNotNull();
        var listReply = client.list();
        assertThat(listReply).isNotNull();
        var destroyReply = client.destroy(name);
        assertThat(destroyReply).isNotNull();
    }
}