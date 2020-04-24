import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class DNS {


    public static void GetByName(String host) throws UnknownHostException {

        InetAddress[] inetAddressArray = InetAddress.getAllByName(host);

        System.out.println("解析到"+inetAddressArray.length+"个地址");
        for (InetAddress inetAddress:
             inetAddressArray) {
            if (inetAddress instanceof Inet4Address){
                System.out.println("读取到一个Ipv4地址");

                System.out.println("解析到的ip地址为:"+ inetAddress.getHostAddress());

                System.out.println("解析到的主机名为:"+inetAddress.getHostName());
            }
            else if(inetAddress instanceof Inet6Address){
                System.out.println("读取到一个Ipv6地址");

                System.out.println("解析到的ip地址为:"+ inetAddress.getHostAddress());

                System.out.println("解析到的主机名为:"+inetAddress.getHostName());
            }
        }


    }

    public static void main(String[] args)  {

        Scanner sc  = new Scanner(System.in);

        while (true){
            System.out.println("请输入一条域名信息,输入exit以退出");
            String host = sc.nextLine();
            if("exit".equalsIgnoreCase(host))
                break;;
            try {
                DNS.GetByName(host);
            }catch (UnknownHostException e){
                System.out.println("接收到一个可能不存在的域名,请重新输入");

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
