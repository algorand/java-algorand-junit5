package com.algorand.goran.rpc.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkListReply {
    @JsonProperty("Networks")
    public String[] Networks;
}
