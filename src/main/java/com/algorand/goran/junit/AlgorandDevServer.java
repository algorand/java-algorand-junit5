package com.algorand.goran.junit;

import com.algorand.goran.rpc.RpcServer;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * Use to create a single RPC Server to share across all tests.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(AlgorandDevServerExtension.class)
public @interface AlgorandDevServer {
    int port() default 0;
    int startupWaitTimeSeconds() default RpcServer.DEFAULT_STARTUP_WAIT_TIME_SECONDS;
    int idleTimeoutSeconds() default RpcServer.DEFAULT_IDLE_TIMEOUT_SECONDS;
}
