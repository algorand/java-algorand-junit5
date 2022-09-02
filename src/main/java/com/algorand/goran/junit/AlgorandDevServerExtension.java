package com.algorand.goran.junit;

import com.algorand.goran.rpc.RpcServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.AnnotationUtils;

import java.io.IOException;
import java.nio.file.Files;

public class AlgorandDevServerExtension implements BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        var params = AnnotationUtils.findAnnotation(context.getRequiredTestClass(), AlgorandDevServer.class)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find AlgorandDevServer annotation."));
        var instance = createServer(params.port(), params.startupWaitTimeSeconds(), params.idleTimeoutSeconds());
        setSharedServer(context, instance);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        var server = getSharedServer(context);
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Set a shared RpcServer object on a given extension context.
     * @param context where a shared server should be set.
     * @param server the server to set on the context.
     */
    private static void setSharedServer(ExtensionContext context, RpcServer server) {
        var store = context.getStore(ExtensionContext.Namespace.create(AlgorandDevServerExtension.class, context.getRequiredTestClass()));
        store.put("singleton-server", server);
    }

    /**
     * Get a shared RpcServer object, if any, from a given extension context.
     * @param context to check for a shared server should be set.
     * @return an optional RpcServer object.
     */
    public static RpcServer getSharedServer(ExtensionContext context) {
        var store = context.getStore(ExtensionContext.Namespace.create(AlgorandDevServerExtension.class, context.getRequiredTestClass()));
        return store.get("singleton-server", RpcServer.class);
    }

    /**
     * Create a new RpcServer on a random port and wait for it to start, use
     * default values for startup wait time and idle timeout.
     * @return the new RpcServer.
     * @throws IOException if process fails to start.
     */
    public static RpcServer createServer() throws IOException {
        return createServer(0, RpcServer.DEFAULT_STARTUP_WAIT_TIME_SECONDS, RpcServer.DEFAULT_IDLE_TIMEOUT_SECONDS);
    }

    /**
     * Create a new RpcServer on a specified port and wait for it to start.
     * @return the new RpcServer.
     * @throws IOException if process fails to start.
     */
    public static RpcServer createServer(Integer port, Integer startupWaitTimeSeconds, Integer idleTimeoutSeconds) throws IOException {
        // create a new server and start it.
        var tempDir = Files.createTempDirectory("goran-junit");
        var instance = new RpcServer(
                null,
                tempDir,
                null);
        instance.start(port, startupWaitTimeSeconds, idleTimeoutSeconds);
        return instance;
    }
}
