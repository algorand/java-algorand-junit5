package com.algorand.goran.rpc;

import com.algorand.goran.rpc.client.RpcClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RpcServer {
    public static final int DEFAULT_STARTUP_WAIT_TIME_SECONDS = 5;
    public static final int DEFAULT_IDLE_TIMEOUT_SECONDS = 120;

    private final Path goranBinary;
    private final Path tempDir;
    private final Path binDir;
    private Process proc;
    private Integer port;

    /**
     *
     * @param goranBinary path to the goran binary, or null to use the path.
     * @param tempDir directory to store networks.
     * @param binDir directory where algod, goal, kmd, etc are located, or null to use the path.
     */
    public RpcServer(Path goranBinary, Path tempDir, Path binDir) {
        this.goranBinary = goranBinary;
        this.tempDir = tempDir;
        this.binDir = binDir;
    }

    public RpcClient getClient() {
        try {
            return new RpcClient(new URL("http://localhost:" + port + "/rpc"));
        } catch (MalformedURLException e) {
            // this shouldn't happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Wait until the server is able to accept requests.
     * @param durationNanos duration in nanoseconds to wait until server is ready.
     */
    public boolean waitUntilReady(long durationNanos) {
        var rpc = getClient();

        long start = System.nanoTime();
        while ((System.nanoTime() - start) < durationNanos) {
            try {
                var reply = rpc.health();
                if (reply != null && reply.running) {
                    return true;
                }
            } catch (Throwable e) {
                // Ignore.
            }
        }

        return false;
    }

    /**
     * Call the 'goran' command based on what parameters are available from the constructor.
     * If no goran binary was provided, 'goran' must be on the PATH.
     * If no binary directory was provided, tools like 'goal', 'algod' and 'kmd' must be on the PATH.
     * After the server is started, a health check loop lasting up to 5 seconds begins. If the server
     * does not respond after this time a RuntimeException is thrown.
     * @param port the port to start the server on, use 0 for random port.
     * @param startupWaitTimeSeconds duration in seconds to wait for rpc-server to begin handling requests.
     * @param idleTimeoutSeconds duration in seconds to allow rpc-server to sit idle before it is shutdown,
     *                           set to 0 for no idle timeout.
     * @throws IOException from call to process start.
     */
    public void start(int port, int startupWaitTimeSeconds, int idleTimeoutSeconds) throws IOException {
        var commands = new ArrayList<String>();
        if (port != 0) {
            this.port = port;
        } else {
            this.port = 1234 + Double.valueOf(Math.random() * 10000).intValue();
        }

        if (goranBinary == null) {
            commands.add("goran");
        } else {
            commands.add(goranBinary.toFile().getAbsolutePath());
        }

        commands.add("rpc-server");
        commands.add("--tmpDir");
        commands.add(tempDir.toFile().getAbsolutePath());
        commands.add("--port");
        commands.add(Integer.toString(this.port));

        if (idleTimeoutSeconds != 0) {
            commands.add("--idleShutdown");
            commands.add(idleTimeoutSeconds + "s");
        }

        if (binDir != null) {
            commands.add("--binDir");
            commands.add(binDir.toFile().getAbsolutePath());
        }

        var builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);

        proc = builder.start();
        if (!waitUntilReady(TimeUnit.SECONDS.toNanos(startupWaitTimeSeconds))) {
            if (!proc.isAlive()) {
                throw new RuntimeException("Failed to start server: " + getLogLines(1));
            }
            throw new RuntimeException("Failed to start server. Unresponsive after 5 seconds.");
        }
    }

    public boolean isRunning() {
        return proc != null && proc.isAlive();
    }

    public void stop() throws InterruptedException {
        if (!isRunning()) {
            // nothing to do.
            return;
        }

        proc.destroy();
        proc.waitFor();
    }

    public ArrayList<String> getLogLines(int max) throws IOException {
        var result = new ArrayList<String>();
        try (
                var input = new InputStreamReader(proc.getInputStream());
                var reader = new BufferedReader(input)
        ) {
            String line;
            while (max > 0 && (line = reader.readLine()) != null) {
                max --;
                result.add(line);
            }
        }
        return result;
    }
}
