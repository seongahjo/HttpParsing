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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by seongahjo on 2016. 5. 22..
 */
public class HttpConnectThread extends Thread {
    private String toUrl; // 이후 접근하는 URL
    private String totoUrl; // 처음 접근하는 URL
    private String loginUrl; // 로그인하는 URL
    private String id; // 아이디
    private String password; // 비밀번호
    private String all; // 취득학점
    private String score; // 학점
    private CloseableHttpClient client; // client객체
    private HttpGet get; // get객체 선언
    private HttpResponse response; // response객체 선언
    private Header header; // header객체 선언

    public HttpConnectThread(String url, String totoUrl, String loginUrl) {
        this.loginUrl = loginUrl;
        this.toUrl = url;
        this.totoUrl = totoUrl;
    }

    public void setUser(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public void run() {
        try {
            login();
            request();
            go();
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void request() {
        get = new HttpGet(toUrl); // Get객체 생성
        get.addHeader(header); // 세션 추가
        try {
            response = client.execute(get); // 접속
            get.abort(); // abort
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void go() {
        get = new HttpGet(totoUrl); // Get객체 생성
        get.addHeader(header); // 세션 추가
        StringBuilder sb = new StringBuilder();
        try {
            response = client.execute(get); // 접속
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null)
                sb.append(line);
            rd.close();
            get.abort(); // abort

            /* 파싱 */
            Document doc = Jsoup.parse(sb.toString());
            Elements el = doc.select("table.adTB td");
            String temp = el.get(0).text();
            StringTokenizer st = new StringTokenizer(temp);
            st.nextToken();
            all = st.nextToken();
            st.nextToken();
            score = st.nextToken();

            System.out.println(all);
            System.out.print(score);
        } catch (IOException e) { // IOException
            e.printStackTrace();
        }
    }
}