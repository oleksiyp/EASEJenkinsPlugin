package com.apperian.eas;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import com.apperian.eas.publishing.UploadResult;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jenkinsci.plugins.ease.Utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EASEEndpoint implements Closeable {
    private CloseableHttpClient httpClient =
            Utils.configureProxy(HttpClients.custom())
                 .build();

    private ObjectMapper mapper = new ObjectMapper();
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public final String url;

    public EASEEndpoint(String url) {
        this.url = url;
    }

    <T extends EASEResponse> T doJsonRpc(EASERequest request,
                                         Class<T> responseClass) throws IOException {

        HttpPost post = buildJsonRpcPost(request);
        try (CloseableHttpResponse response = httpClient.execute(post)) {
            return buildResponseObject(responseClass, response);
        }
    }

    public UploadResult uploadFile(String uploadUrl, File file) throws IOException {
        HttpPost post = new HttpPost(uploadUrl);

        FileBody appFileBody = new FileBody(file);

        HttpEntity multipartEntity = MultipartEntityBuilder.create()
                .addPart("LUuploadFile", appFileBody)
                .build();


        post.setEntity(multipartEntity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            String body = EntityUtils.toString(response.getEntity());
            UploadResult result;
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                result = new UploadResult();
                result.errorMessage = body;
            } else {
                result = mapper.readValue(body, UploadResult.class);
            }
            return result;
        }
    }

    private <T extends EASEResponse> T buildResponseObject(Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return mapper.readValue(responseString, responseClass);
    }

    private HttpPost buildJsonRpcPost(EASERequest request) {
        HttpPost post = new HttpPost(url);
        try {
            String requestStr = mapper.writeValueAsString(request);
            post.setEntity(new StringEntity(requestStr, APIConstants.REQUEST_CHARSET));
        } catch(Exception ex) {
            throw new RuntimeException("Request marshaling error", ex);
        }
        return post;
    }

    public void close() throws IOException {
        httpClient.close();
    }
}