package com.algorand.goran.junit;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static com.algorand.goran.junit.AlgorandDevServerExtension.createServer;
import static com.algorand.goran.junit.AlgorandDevServerExtension.getSharedServer;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class AlgorandTestExtension extends TypeBasedParameterResolver<AlgorandNetwork> implements AfterEachCallback {
    public AlgorandNetwork initializeNetwork(String name, AlgorandPrivateNetwork params, ExtensionContext context) throws Throwable {
        var network = new AlgorandNetwork();

        // Create a new server if required.
        var server = getSharedServer(context);
        if (server == null) {
            server = createServer();
            // any server stored here is shutdown when the test ends.
            network.server = server;
        }

        network.data = "hooked";
        network.rpc = server.getClient();
        network.networkMetadata = network.rpc.start(name, params.binDirOverride(), params.tmpDirOverride(), params.networkTemplateOverride(), params.devMode());

        var networkParts = network.networkMetadata.url.split(":");
        if (networkParts.length != 2) {
            throw new RuntimeException("Unexpected url, require 'address:port' received: " + network.networkMetadata.url);
        }
        network.client = new AlgodClient(networkParts[0], Integer.parseInt(networkParts[1]), network.networkMetadata.adminToken);
        return network;
    }

    @Override
    public AlgorandNetwork resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        var params = findAnnotation(extensionContext.getRequiredTestMethod(), AlgorandPrivateNetwork.class)
                .orElseThrow(() -> new RuntimeException("Unable to find AlgorandPrivateNetwork annotation."));
        try {
            String name = extensionContext.getDisplayName();
            var network = initializeNetwork(name, params, extensionContext);
            getStore(extensionContext).put(name, network);
            return network;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var network = getStore(context).get(context.getDisplayName(), AlgorandNetwork.class);
        if (network == null) {
            return;
        }

        try {
            network.rpc.destroy(context.getDisplayName());
            if (network.server != null) {
                network.server.stop();
            }
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
