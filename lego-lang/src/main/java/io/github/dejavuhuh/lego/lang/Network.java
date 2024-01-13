package io.github.dejavuhuh.lego.lang;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 网络工具类
 *
 * @author wu.yue
 * @since 2024/1/12 17:52
 */
public class Network {

    private static final String LOCAL_IP = doGetLocalIP();

    public static String getLocalIP() {
        return LOCAL_IP;
    }

    private static String doGetLocalIP() {
        InetAddress localIpAddress = doGetLocalIpAddress();
        return localIpAddress.getHostAddress();
    }

    private static InetAddress doGetLocalIpAddress() {
        try {
            InetAddress candidateAddress = null;

            Enumeration<NetworkInterface> networkInterfaces =
                    NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
                     inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                            return inetAddr;
                        }

                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                    else {
                    }
                }
            }

            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
