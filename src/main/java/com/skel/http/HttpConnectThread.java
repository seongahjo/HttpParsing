package com.skel.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seongahjo on 2016. 5. 22..
 */
public class HttpConnectThread extends Thread {
    private String toUrl;
    private String loginUrl;
    private String id;
    private String password;
    private CloseableHttpClient client;
    private HttpGet get;
    private HttpResponse response;
    private Header header;

    public HttpConnectThread(String url, String loginUrl) {
        toUrl = url;
        this.loginUrl = loginUrl;
    }

    public void setUser(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public void run() {
        try {
            login();
            request();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        client = HttpClientBuilder.create().build(); //client 생성

        HttpPost post = new HttpPost(loginUrl); //Post 객체 생성
        List<NameValuePair> paramList = new ArrayList<NameValuePair>(); // 변수들
        paramList.add(new BasicNameValuePair("user_id", id)); // 아이디
        paramList.add(new BasicNameValuePair("password", password)); // 비밀번호
        try {
            post.setEntity(new UrlEncodedFormEntity(paramList)); // Post객체에 저장
            response = client.execute(post); // 접속
            for (Header h : response.getHeaders("Set-Cookie"))
                if (h.getValue().contains("JSESSIONID")) //Session id를 가지고 있는 쿠키 찾기
                    header = h;
            post.abort(); // abort

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void request() {
        get = new HttpGet(toUrl); // Get객체 생성
        get.addHeader(header); // 세션 추가
        try {
            response = client.execute(get); // 접속
            /*
            파싱
             */
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null)
                System.out.println(line);
            rd.close();
            get.abort(); //abort
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
