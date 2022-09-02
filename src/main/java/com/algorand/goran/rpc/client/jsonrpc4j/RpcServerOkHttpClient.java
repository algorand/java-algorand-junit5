package com.algorand.goran.rpc.client.jsonrpc4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.IJsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.jsonrpc4j.JsonRpcBasicServer.ACCEPT_ENCODING;

public class RpcServerOkHttpClient extends JsonRpcClient implements IJsonRpcClient {
    private final OkHttpClient client = new OkHttpClient();
    private final MediaType mediaType = MediaType.parse("application/json");

    private static final String GZIP = "gzip";

    private final Map<String, String> headers;
    private final URL serviceUrl;
    private final boolean gzipRequests;

    /**
     * Creates the {@link RpcServerOkHttpClient} bound to the given {@code serviceUrl}.
     * The headers provided in the {@code headers} map are added to every request
     * made to the {@code serviceUrl}.
     *
     * @param serviceUrl the service end-point URL
     * @param headers    the headers
     */
    public RpcServerOkHttpClient(URL serviceUrl, Map<String, String> headers) {
        this(new ObjectMapper(), serviceUrl, headers);
    }

    /**
     * Creates the {@link RpcServerOkHttpClient} bound to the given {@code serviceUrl}.
     * The headers provided in the {@code headers} map are added to every request
     * made to the {@code serviceUrl}.
     *
     * @param mapper     the {@link ObjectMapper} to use for json&lt;-&gt;java conversion
     * @param serviceUrl the service end-point URL
     * @param headers    the headers
     */
    public RpcServerOkHttpClient(ObjectMapper mapper, URL serviceUrl, Map<String, String> headers) {
        this(mapper, serviceUrl, headers, false, false);
    }

    /**
     * Creates the {@link RpcServerOkHttpClient} bound to the given {@code serviceUrl}.
     * The headers provided in the {@code headers} map are added to every request
     * made to the {@code serviceUrl}.
     *
     * @param mapper              the {@link ObjectMapper} to use for json&lt;-&gt;java conversion
     * @param serviceUrl          the service end-point URL
     * @param headers             the headers
     * @param gzipRequests        whether gzip the request
     * @param acceptGzipResponses whether accept gzip response
     */
    public RpcServerOkHttpClient(ObjectMapper mapper, URL serviceUrl, Map<String, String> headers, boolean gzipRequests, boolean acceptGzipResponses) {
        super(mapper);
        this.serviceUrl = serviceUrl;
        this.headers = headers;
        this.gzipRequests = gzipRequests;
        if (acceptGzipResponses) {
            this.headers.put(ACCEPT_ENCODING, GZIP);
        }
    }

    /**
     * Creates the {@link RpcServerOkHttpClient} bound to the given {@code serviceUrl}.
     * The headers provided in the {@code headers} map are added to every request
     * made to the {@code serviceUrl}.
     *
     * @param serviceUrl the service end-point URL
     */
    public RpcServerOkHttpClient(URL serviceUrl) {
        this(new ObjectMapper(), serviceUrl, new HashMap<>());
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void invoke(String methodName, Object argument) throws Throwable {
        invoke(methodName, argument, null, new HashMap<>());
    }

    @Override
    public Object invoke(String methodName, Object argument, Type returnType) throws Throwable {
        return invoke(methodName, argument, returnType, new HashMap<>());
    }

    @Override
    public Object invoke(String methodName, Object argument, Type returnType, Map<String, String> extraHeaders) throws Throwable {
        var baos = new ByteArrayOutputStream();
        super.invoke(methodName, argument, baos);

        var body = RequestBody.create(mediaType, baos.toByteArray());
        extraHeaders.putAll(this.headers);
        var request = new Request.Builder()
                .url(serviceUrl)
                .post(body)
                .headers(Headers.of(extraHeaders))
                .build();

        if (gzipRequests) {
            // TODO: set something on okhttp
        }

        var call = client.newCall(request);
        var response = call.execute();

        if (! response.isSuccessful()) {
            throw new RuntimeException("Failed to execute request: " + response);
        }

        try (var responseBody = response.body()) {
            return super.readResponse(returnType, responseBody.byteStream());
        }
    }

    @Override
    public <T> T invoke(String methodName, Object argument, Class<T> clazz) throws Throwable {
        return invoke(methodName, argument, clazz, new HashMap<>());
    }

    @Override
    public <T> T invoke(String methodName, Object argument, Class<T> clazz, Map<String, String> extraHeaders) throws Throwable {
        return (T) invoke(methodName, argument, (Type) clazz, new HashMap<>());
    }
}
