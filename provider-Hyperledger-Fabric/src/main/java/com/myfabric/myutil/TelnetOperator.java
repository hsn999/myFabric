package com.myfabric.myutil;

import java.io.IOException;  
import java.io.InputStream;  
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;  
  
/** 
 * Telnet,commons-net-2.2.jar 
 * @author JiangKunpeng 
 * 
 */  
public class TelnetOperator {  
      
    private String prompt = ">"; //,Windows>,Linux#  
    private char promptChar = '>';   //  
    private TelnetClient telnet;  
    private InputStream in;     // ,  
    private PrintStream out;    //    
      
    /** 
     * @param termtype  VT100VT52VT220VTNTANSI 
     * @param prompt     
     */  
    public TelnetOperator(String termtype,String prompt){  
        telnet = new TelnetClient(termtype);  
        setPrompt(prompt);  
    }  
      
    public TelnetOperator(String termtype){  
        telnet = new TelnetClient(termtype);  
    }  
      
    public TelnetOperator(){  
        telnet = new TelnetClient();  
    }  
      
    /** 
     *  
     * @param ip 
     * @param port 
     * @param username 
     * @param password 
     */  
    public void login(String ip, int port, String username, String password){  
        try {  
            telnet.connect(ip, port);  
            in = telnet.getInputStream();  
            out = new PrintStream(telnet.getOutputStream());  
            readUntil("login:");  
            write(username);  
            readUntil("password:");  
            write(password);  
            String rs = readUntil(null);  
            if(rs!=null&&rs.contains("Login Failed")){  
                throw new RuntimeException("");  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
      
    
    
    public void login1(String ip, int port, String username, String password){  
        try {  
            telnet.connect(ip, port);  
            in = telnet.getInputStream();  
            out = new PrintStream(telnet.getOutputStream());  
            telnet.disconnect();
          
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
    /** 
     *  
     *  
     * @param pattern    
     * @return 
     */  
    public String readUntil(String pattern) {  
        StringBuffer sb = new StringBuffer();  
        try {  
            char lastChar = (char)-1;  
            boolean flag = pattern!=null&&pattern.length()>0;  
            if(flag)  
                lastChar = pattern.charAt(pattern.length() - 1);  
            char ch;  
            int code = -1;  
            while ((code = in.read()) != -1) {  
                ch = (char)code;  
                sb.append(ch);  
                  
                //  
                if (flag) {  
                    if (ch == lastChar && sb.toString().endsWith(pattern)) {  
                        return sb.toString();  
                    }  
                }else{  
                    //,  
                    if(ch == promptChar)  
                        return sb.toString();  
                }  
                //  
                if(sb.toString().contains("Login Failed")){  
                    return sb.toString();  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return sb.toString();  
    }  
      
    /** 
     *  
     *  
     * @param value 
     */  
    public void write(String value) {  
        try {  
            out.println(value);  
            out.flush();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * , 
     *  
     * @param command 
     * @return 
     */  
    public String sendCommand(String command) {  
        try {  
            write(command);  
            return readUntil(prompt);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
      
    /** 
     *  
     */  
    public void distinct(){  
        try {  
            if(telnet!=null&&!telnet.isConnected())  
                telnet.disconnect();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public void setPrompt(String prompt) {  
        if(prompt!=null){  
            this.prompt = prompt;  
            this.promptChar = prompt.charAt(prompt.length()-1);  
        }  
    }  
      
    public static void main(String[] args) {  
        TelnetOperator telnet = new TelnetOperator("VT220",">"); //Windows,VT220,  
        telnet.login1("127.0.0.1", 7058, "administrator", "123456");  
        telnet.distinct();
     
         
    }  
      
}  