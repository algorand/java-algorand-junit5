package com.algorand.goran.rpc.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemShutdownReply {
    @JsonProperty("Received")
    public boolean received;
}
