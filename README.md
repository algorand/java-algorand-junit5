# Algorand JUnit Plugin

This plugin aims to simplify integration testing for the Algorand blockchain by providing private networks via standard JUnit5 annotations.

## Dependencies
This plugin depends on a number of programs which must all be available on the search path:
* goran
* goal
* algod
* kmd

**TODO: provide these binaries in a convenient installation package.**

## Installation

**TODO: publish to Maven Central**


## Usage

Several annotations are provided for managing private networks.

### @AlgorandPrivateNetwork

This is a **test level** annotation used to get access to the `AlgorandNetwork` meta object. The meta object contains a number of useful constructs including an initialized `AlgodClient` object configured with the private network, and an array of Accounts and their private keys for immediate use.

The network can be configured with several annotation arguments. See the examples for how to turn on **devMode**. Inspect the object for other options.
#### Example

```java
public class TestAlgorandExtension {
    @Test
    @AlgorandPrivateNetwork
    public void simpleStatusRequest(AlgorandNetwork network) {
        var status = network.client.GetStatus().execute();
        assertThat(status.isSuccessful()).isTrue();
    }

    @Test
    @AlgorandPrivateNetwork(devMode = true)
    public void devModeAndTransactionTest(AlgorandNetwork network) {
        // Initialize account.
        var mnemonic = network.networkMetadata.Accounts[0].Mnemonic;
        var acct = new Account(mnemonic);
        var newAcct = new Account();

        // Create transaction.
        long xferAmount = 100000;
        var txn = Transaction.PaymentTransactionBuilder()
                    .lookupParams(network.client)
                    .sender(acct.getAddress())
                    .receiver(newAcct.getAddress())
                    .amount(xferAmount)
                    .build();

        // Sign and submit transaction.
        var stxn = acct.signTransaction(txn);
        var stxnPayload = Encoder.encodeToMsgPack(stxn);
        var submit = network.client.RawTransaction()
                    .rawtxn(Encoder.encodeToMsgPack(stxn))
                    .execute();
        assertThat(submit.isSuccessful()).isTrue();

        // Confirm reciept.
        assertThatCode(() -> {
                Utils.waitForConfirmation(network.client, submit.body().txId, 5);
        }).doesNotThrowAnyException();
        var acctInfo = network.client.AccountInformation(newAcct.getAddress()).execute();
        assertThat(acctInfo.isSuccessful()).isTrue();
        assertThat(acctInfo.body().amount).isEqualTo(xferAmount);
    }
}
```

### @AlgorandDevServer
To optimize network access, a **class level** annotation is also provided. This allows configuring resources to share across multiple tests. This can be convenient when there are many tests because it allows you to avoid extra setup and teardown operations and speed up the test.

It can be configured with several annotation arguments, inspect the object for details.
# Example
```java
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
```
