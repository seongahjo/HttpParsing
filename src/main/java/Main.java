import com.skel.http.HttpConnectThread;

/**
 * Created by seongahjo on 2016. 4. 7..
 */
public class Main {

    public static void main(String[] args) {
        HttpConnectThread hc = new HttpConnectThread("URL",
                "loginURL");
        hc.setUser("null", "null");
        hc.run();
    }
}

