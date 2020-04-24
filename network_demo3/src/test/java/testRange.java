import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class testRange {
    public static void main(String[] args) throws IOException {
        HttpURLConnection httpURLConnection = null;
        String host = "http://wppkg.baidupcs.com/issue/netdisk/MACguanjia/BaiduNetdisk_mac_3.1.0.dmg";
        URL url = new URL(host);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setReadTimeout(3000);
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Range", "bytes=20-");
        int code = httpURLConnection.getResponseCode();
        if(code==200){
            System.out.println("设置了Range之后状态码为"+code+"不支持分段传输");
        }else if(code==206){
            System.out.println("设置了Range之后状态码为"+code+"支持分段传输");
        }

    }
}
