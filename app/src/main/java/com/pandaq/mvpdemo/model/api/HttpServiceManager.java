package com.pandaq.mvpdemo.model.api;

import com.pandaq.mvpdemo.GlobalConfig;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpServiceManager {

    private static String base_url = GlobalConfig.baseUrl;

    public static String httpPost(String url, String param) throws Exception {
        OkHttpClient client = getTLSHttpClient();
        String result = "{errcode:" + -100 + ",'errmsg': '创建网络请求失败'}";
        if (null == client) {
            return result;
        }
        String httpPost = base_url + url;
        MediaType type = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(type, param);
        Request request = new Request.Builder().url(httpPost).post(body).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
        return result;
    }

    public static String httpGet(String url) throws Exception {
        OkHttpClient client = getTLSHttpClient();
        String result = "{errcode:" + -100 + ",'errmsg': '创建网络请求失败'}";
        if (null == client) {
            return result;
        }
        String httpGet = base_url + url;
        Request request = new Request.Builder().url(httpGet).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        } else {
            result = "{errcode:" + response.code() + ",'errmsg': '" + response.message() + "'}";
        }
        return result;
    }

    /**
     * okhttp做的兼容https请求地址
     * demo中用不到留着给大家参考
     *
     * @return
     */
    private static OkHttpClient getTLSHttpClient() {
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub

            }
        };
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                return true;
            }
        };
        OkHttpClient client = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(10, TimeUnit.SECONDS);
            builder.writeTimeout(10, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslContext.getSocketFactory());
            builder.hostnameVerifier(DO_NOT_VERIFY);
            builder.retryOnConnectionFailure(false);//失败后不自动重连，默认true
            client = builder.build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }
}
