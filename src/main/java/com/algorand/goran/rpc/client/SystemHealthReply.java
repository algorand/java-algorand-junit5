package com.algorand.goran.rpc.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemHealthReply {
    @JsonProperty("Running")
    public boolean running;
}
