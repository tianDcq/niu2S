package com.micro.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerIpconfig
{ 
    private static ServerIpaddress instance = new ServerIpaddress();
    private ServerIpconfig(){        
    }
    
    public static ServerIpaddress getInstance(){
        if(null != instance) {
            return instance;
        }
        else {
            instance = new ServerIpaddress();
            return instance;
        }
    }    

    @Data
    public static class ServerIpaddress 
    {
        public String ipaddress = null;
        public String getHostIp(){
    		try{
    			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
    			while (allNetInterfaces.hasMoreElements()){
    				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
    				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
    				while (addresses.hasMoreElements()){
    					InetAddress ip = (InetAddress) addresses.nextElement();
    					if (ip != null 
    							&& ip instanceof Inet4Address
                        		&& !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                        		&& ip.getHostAddress().indexOf(":")==-1){
    						System.out.println("本机的IP = " + ip.getHostAddress());
    						return ip.getHostAddress();
    					} 
    				}
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		return null;
    	}
        public ServerIpaddress() {
            while(null == ipaddress) {
                try
                {
                    ipaddress = InetAddress.getLocalHost().getHostAddress().toString();
                }
                catch (UnknownHostException e)
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                log.info("获取IP地址:"  + String.valueOf(ipaddress));
            }
            ipaddress = ipaddress.replace(".", "-");
        }
    }

	public static String getHostIp() {
		return null;
	}
}