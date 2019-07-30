package com.evernym.verity.sdk.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.evernym.verity.sdk.utils.VerityUtil.retrieveVerityPublicDid;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerityUtilTest {
    @Test
    public void RetrieveVerityPublicDid_canMock() throws IOException {
        //given:
        HttpClient httpClient = mock(HttpClient.class);
        HttpGet httpGet = mock(HttpGet.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        HttpEntity entity = mock(HttpEntity.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(entity);
        when(httpClient.execute(httpGet)).thenReturn(httpResponse);

        String validContent =
                "{\"DID\":\"CV65RFpeCtPu82hNF9i61G\",\"verKey\":\"7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2\"}";

        when(entity.getContentType()).thenReturn(new BasicHeader("Content-Type", "application/json"));
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(validContent.getBytes(StandardCharsets.UTF_8)));

        Did did = retrieveVerityPublicDid(httpGet, httpClient);

        assertEquals("CV65RFpeCtPu82hNF9i61G", did.did);
        assertEquals("7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2", did.verkey);
    }

    @Test(expected = IOException.class)
    public void RetrieveVerityPublicDid_exceptionWhenNon200() throws IOException {
        //given:
        HttpClient httpClient = mock(HttpClient.class);
        HttpGet httpGet = mock(HttpGet.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        HttpEntity entity = mock(HttpEntity.class);

        when(statusLine.getStatusCode()).thenReturn(400);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(httpGet)).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(entity);

        String validContent = "{}";

        when(entity.getContentType()).thenReturn(new BasicHeader("Content-Type", "application/json"));
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(validContent.getBytes(StandardCharsets.UTF_8)));

        retrieveVerityPublicDid(httpGet, httpClient);
    }

}
