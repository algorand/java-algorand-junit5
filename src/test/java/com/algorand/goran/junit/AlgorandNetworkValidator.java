package com.algorand.goran.junit;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;

import static org.assertj.core.api.Assertions.*;

public class AlgorandNetworkValidator {
    public static void validate(AlgorandNetwork network) {
        long xferAmount = 100000;
        assertThatCode(() -> {
            // Verify things exist
            assertThat(network).isNotNull();
            assertThat(network.client).isNotNull();
            assertThat(network.server).isNotNull();
            assertThat(network.networkMetadata).isNotNull();
            assertThat(network.networkMetadata.Accounts).hasSizeGreaterThan(0);

            // Verify the client works.
            var status = network.client.GetStatus().execute();
            assertThat(status).isNotNull();
            assertThat(status.isSuccessful()).isTrue();

            // Verify the account is valid and can be used.
            Account acct = new Account(network.networkMetadata.Accounts[0].Mnemonic);
            Account newAcct = new Account();
            var txn = Transaction.PaymentTransactionBuilder()
                    .lookupParams(network.client)
                    .sender(acct.getAddress())
                    .receiver(newAcct.getAddress())
                    .amount(xferAmount)
                    .build();

            var stxn = acct.signTransaction(txn);
            var submit = network.client.RawTransaction()
                    .rawtxn(Encoder.encodeToMsgPack(stxn))
                    .execute();
            assertThat(submit).isNotNull();
            assertThat(submit.isSuccessful()).isTrue();
            assertThatCode(() -> {
                Utils.waitForConfirmation(network.client, submit.body().txId, 5);
            }).doesNotThrowAnyException();

            // Confirm the payment is received.
            var acctInfo = network.client.AccountInformation(newAcct.getAddress()).execute();
            assertThat(acctInfo).isNotNull();
            assertThat(acctInfo.isSuccessful()).isTrue();
            assertThat(acctInfo.body().amount).isEqualTo(xferAmount);
        }).doesNotThrowAnyException();
    }
}
