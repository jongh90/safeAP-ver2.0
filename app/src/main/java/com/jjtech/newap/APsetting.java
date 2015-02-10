package com.jjtech.newap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by user on 2015-02-10.
 */
public class APsetting {
    private ExecutorService es;
    private Context context;
    public APsetting(Context context){
        this.context = context;
        es = Executors.newSingleThreadExecutor();
    }


    public void setDNS(int ip1, int ip2, int ip3, int ip4) {
        es.submit(new setDNSTask(getGateway(), 80, ip1, ip2, ip3, ip4));
    }

    private class setDNSTask implements Runnable{
        private String gateway;
        private int port;
        private int ip1;
        private int ip2;
        private int ip3;
        private int ip4;

        setDNSTask(String gateway, int port, int ip1, int ip2, int ip3, int ip4){
            this.gateway   = gateway;
            this.port = port;
            this.ip1 = ip1;
            this.ip2 = ip2;
            this.ip3 = ip3;
            this.ip4 = ip4;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                CredentialsProvider credProvider = new BasicCredentialsProvider();
                credProvider.setCredentials(new AuthScope(gateway, port), new UsernamePasswordCredentials("admin", "admin"));
                DefaultHttpClient http = new DefaultHttpClient();
                http.setCredentialsProvider(credProvider);
                String query = "http://"+gateway+":"+port+"/cgi-bin/timepro.cgi?tmenu=netconf&smenu=wansetup&act=save" +
                        "&wan=wan1&ifname=eth1&wan_type=dynamic&allow_private=on&dhcp_auto_restart=on&dns_dynamic_chk=on" +
                        "&fdns_dynamic1="+ip1+
                        "&fdns_dynamic2="+ip2+
                        "&fdns_dynamic3="+ip3+
                        "&fdns_dynamic4="+ip4+
                        "&sdns_dynamic1=" + getDNS2(1) +
                        "&sdns_dynamic2=" + getDNS2(2) +
                        "&sdns_dynamic3=" + getDNS2(3) +
                        "&sdns_dynamic4=" + getDNS2(4) +
                        "&userid=&passwd=&mtu.pppoe.eth1=1454&lcp_flag=1&lcp_echo_interval=30&lcp_echo_failure=10" +
                        "&ip1=192&ip2=168&ip3=10&ip4=62&sm1=255&sm2=255&sm3=255&sm4=0&gw1=192&gw2=168&gw3=10&gw4=1" +
                        "&fdns_static1=&fdns_static2=&fdns_static3=&fdns_static4=&sdns_static1=&sdns_static2=&sdns_static3=&sdns_static4=&mtu.static.eth1=1500";
                HttpGet httpget = new HttpGet(query);
                System.out.println("Executing request " + httpget.getRequestLine());
                HttpResponse  response = http.execute(httpget);
                System.out.println(response.getStatusLine().toString());
                System.out.println(response.getEntity().toString());
            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
        }
    }

    public String getDNS1(){
        WifiManager wifi;
        DhcpInfo info;
        wifi= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        info = wifi.getDhcpInfo();
        return intToIp(info.dns1);
    }

    public String getDNS2(){
        WifiManager wifi;
        DhcpInfo info;
        wifi= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        info = wifi.getDhcpInfo();
        return intToIp(info.dns2);
    }

    public String getDNS2(int i){
        String ip = "";
        WifiManager wifi;
        DhcpInfo info;
        int dns;
        wifi= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        info = wifi.getDhcpInfo();
        dns = info.dns2;
        switch (i){
            case 1:
                ip += (dns & 0xFF);
                break;
            case 2:
                ip += ((dns >> 8 ) & 0xFF);
                break;
            case 3:
                ip += ((dns >> 16 ) & 0xFF);
                break;
            case 4:
                ip += ((dns >> 24 ) & 0xFF);
                break;
        }
        return ip;
    }

    public String getGateway(){
        WifiManager wifi;
        DhcpInfo info;
        wifi= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        info = wifi.getDhcpInfo();
        return intToIp(info.gateway);
    }

    public String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 24 ) & 0xFF) ;
    }

    public boolean isWiFi(){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }
}