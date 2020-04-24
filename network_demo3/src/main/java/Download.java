import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;

public class Download {




    public static void main(String[] args)  {

        Scanner sc  = new Scanner(System.in);

        System.out.println("请输入要下载的网页或者图片的url地址");

        String host = sc.nextLine();

        System.out.println("请输入要保存的文件的路径以及名字");

        String fileName = sc.nextLine();

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            //构造URl类
            URL url = new URL(host);
            //获取资源输入流
            inputStream = url.openStream();
            //构造文件
            File file = new File(fileName);
            //获取文件输出流
            fileOutputStream = new FileOutputStream(file);

            //输入输出流传输
            byte []buffer = new byte[1024];
            while (inputStream.read(buffer)!=-1){
                fileOutputStream.write(buffer);
            }
            System.out.println("下载完成");

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(fileOutputStream!=null){
                    fileOutputStream.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }



        }




    }

}
