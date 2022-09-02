package com.algorand.goran.rpc.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkStartReply {
    public static class AccountData {
        @JsonProperty("Account")
        public String Account;

        @JsonProperty("Mnemonic")
        public String Mnemonic;

        @JsonProperty("PrivateKey")
        public byte[] PrivateKey;
    }

    @JsonProperty("Token")
    public String token;

    @JsonProperty("AdminToken")
    public String adminToken;

    @JsonProperty("URL")
    public String url;

    @JsonProperty("Accounts")
    public AccountData[] Accounts;
}
