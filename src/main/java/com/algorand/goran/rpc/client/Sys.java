package com.algorand.goran.rpc.client;

public interface Sys {
    SystemShutdownReply shutdown(Integer secondsDelay) throws Throwable;
    SystemHealthReply health() throws Throwable;
}
