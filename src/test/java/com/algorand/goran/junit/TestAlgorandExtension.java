package com.algorand.goran.junit;

import org.junit.jupiter.api.Test;

import static com.algorand.goran.junit.AlgorandNetworkValidator.validate;

/**
 * The servers in these tests are not null, because they are unique to each
 * individual test.
 */
public class TestAlgorandExtension {
    @Test
    @AlgorandPrivateNetwork(devMode = true)
    public void devModeTest(AlgorandNetwork network) {
        validate(network);
    }

    @Test
    @AlgorandPrivateNetwork
    public void simpleTest(AlgorandNetwork network) {
        validate(network);
    }
}
