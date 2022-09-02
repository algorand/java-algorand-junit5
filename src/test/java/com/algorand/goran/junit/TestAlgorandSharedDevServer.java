package com.algorand.goran.junit;

import org.junit.jupiter.api.Test;

import static com.algorand.goran.junit.AlgorandNetworkValidator.validate;

/**
 * The servers in these tests are null because they are shared across all
 * tests and should not be tampered with.
 */
@AlgorandDevServer(idleTimeoutSeconds = 60)
public class TestAlgorandSharedDevServer {
    @Test
    @AlgorandPrivateNetwork
    public void test(AlgorandNetwork network) {
        validate(network);
    }

    @Test
    @AlgorandPrivateNetwork
    public void test2(AlgorandNetwork network) {
        validate(network);
    }
}
