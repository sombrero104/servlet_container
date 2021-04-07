package me.servlet_container;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.*;

public class Server_3 {

    public static final byte CR = '\r';
    public static final byte LF = '\n';

    private ServerSocket serverSocket;

    private void boot() throws IOException {
        serverSocket = new ServerSocket(8000);
        Socket socket = serverSocket.accept();
        InputStream in = socket.getInputStream();
        int oneInt = -1;
        byte oldByte = (byte)-1;
        StringBuilder sb = new StringBuilder();
        int lineNumber = 0;
        boolean bodyFlag = false;
        String method = null;
        String requestUrl = null;
        String httpVersion = null;
        int contentLength = -1;
        int bodyRead = 0;
        List<Byte> bodyByteList = null;
        Map<String, String> headerMap = new HashMap<>();
        while(-1 != (oneInt = in.read())){
            byte thisByte = (byte)oneInt;
            if(bodyFlag){
                bodyRead ++;
                bodyByteList.add(thisByte);
                if(bodyRead >= contentLength){
                    break;
                }
            }else{
                if(thisByte == Server_3.LF && oldByte == Server_3.CR){
                    String oneLine = sb.substring(0, sb.length()-1);
                    lineNumber ++;
                    if(lineNumber == 1){
                        // 요청의 첫 행, HTTP 메서드, 요청 URL, 버전을 알아낸다.
                        int firstBlank = oneLine.indexOf(" ");
                        int secondBlank = oneLine.lastIndexOf(" ");
                        method = oneLine.substring(0, firstBlank);
                        requestUrl = oneLine.substring(firstBlank+1, secondBlank);
                        httpVersion = oneLine.substring(secondBlank+1);
                    }else{
                        if(oneLine.length()<=0){
                            bodyFlag = true;
                            // 헤더가 끝났다.
                            if("GET".equals(method)){
                                // GET 방식이면 메시지 바디가 없다.
                                break;
                            }
                            String contentLengthValue = headerMap.get("Content-Length");
                            if(contentLengthValue != null){
                                contentLength = Integer.parseInt(contentLengthValue.trim());
                                bodyFlag = true;
                                bodyByteList = new ArrayList<>();
                            }
                            continue;
                        }
                        int indexOfColon = oneLine.indexOf(":");
                        String headerName = oneLine.substring(0, indexOfColon);
                        String headerValue = oneLine.substring(indexOfColon+1);
                        headerMap.put(headerName, headerValue);
                    }
                    sb.setLength(0);
                }else{
                    sb.append((char)thisByte);
                }
            }
            oldByte = (byte)oneInt;
        }
        in.close();
        socket.close();

        System.out.printf("METHOD: %s REQ: %s HTTP VER. %s\n", method, requestUrl, httpVersion);
        Map<String, String> paramMap = new HashMap<String, String>();
        int indexOfQuotation = requestUrl.indexOf("?");
        if(indexOfQuotation > 0){
            StringTokenizer st = new StringTokenizer(requestUrl.substring(indexOfQuotation+1), "&");
            while(st.hasMoreTokens()){
                String params = st.nextToken();
                paramMap.put(params.substring(0, params.indexOf("=")), params.substring(params.indexOf("=")+1));
            }
        }
        System.out.println("Header list");
        Set<String> keySet = headerMap.keySet();
        Iterator<String> keyIter = keySet.iterator();
        while(keyIter.hasNext()){
            String headerName = keyIter.next();
            System.out.printf("  Key: %s Value: %s\n", headerName, headerMap.get(headerName));
        }
        if(bodyByteList != null){
            if("application/x-www-form-urlencoded".equals(headerMap.get("Content-Type").trim())){
                int startIndex = 0;
                byte[] srcBytes = new byte[bodyByteList.size()];
                String currentName = null;
                for(int i=0; i<bodyByteList.size(); i++){
                    byte oneByte = bodyByteList.get(i);
                    srcBytes[i] = oneByte;
                    if('=' == oneByte){
                        byte[] one = new byte[i-startIndex];
                        System.arraycopy(srcBytes, startIndex, one, 0, i-startIndex);
                        currentName = URLDecoder.decode(new String(one), "CP949");
                        startIndex = i+1;
                    }else if('&' == oneByte){
                        byte[] one = new byte[i-startIndex];
                        System.arraycopy(srcBytes, startIndex, one, 0, i-startIndex);
                        paramMap.put(currentName, URLDecoder.decode(new String(one), "CP949"));
                        startIndex = i+1;
                    }else if(i == bodyByteList.size()-1){
                        byte[] one = new byte[i-startIndex+1];
                        System.arraycopy(srcBytes, startIndex, one, 0, i-startIndex+1);
                        paramMap.put(currentName, URLDecoder.decode(new String(one), "CP949"));
                        startIndex = i+1;
                    }
                }
            }else{
                System.out.print("Message Body-->");
                for(byte oneByte : bodyByteList){
                    System.out.print(oneByte);
                }
                System.out.println("<--");
            }
        }
        Set<String>paramKeySet = paramMap.keySet();
        Iterator<String> paramKeyIter = paramKeySet.iterator();
        while(paramKeyIter.hasNext()){
            String paramName = paramKeyIter.next();
            System.out.printf("paramName: %s paramValue: %s\n", paramName, paramMap.get(paramName));
        }
        System.out.println("End of HTTP Message.");
    }

    public static void main(String[] args) throws IOException {
        Server_3 server = new Server_3();
        server.boot();
    }

    /**
     * 실행 후 브라우저에서 http://localhost:8000 로 접속.
     *
     * 출력 결과:
     *
     * 1. GET 요청일 경우.
     * METHOD: GET REQ: /?getinputname=getinputvalue&submit=submit HTTP VER. HTTP/1.1
     * Header list
     *   Key: Cookie Value:  Idea-3ef64c5c=faeb596a-446d-465a-98aa-aa388ba67073
     *   Key: Accept Value:  text/html,application/xhtml+xml,application/xml;q=0.9,*⁄*;q=0.8
     *   Key: Upgrade-Insecure-Requests Value:  1
     *   Key: Connection Value:  keep-alive
     *   Key: User-Agent Value:  Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15
     *   Key: Referer Value:  http://localhost:63342/servlet_container/static/param.html?_ijt=5kl9vvcj3maro0qhpbm9ikm751
     *   Key: Host Value:  localhost:8000
     *   Key: Accept-Language Value:  ko-kr
     *   Key: Accept-Encoding Value:  gzip, deflate
     * paramName: submit paramValue: submit
     * paramName: getinputname paramValue: getinputvalue
     * End of HTTP Message.
     *
     * 2. POST 요청일 경우.
     * METHOD: POST REQ: / HTTP VER. HTTP/1.1
     * Header list
     *   Key: Origin Value:  http://localhost:63342
     *   Key: Cookie Value:  Idea-3ef64c5c=faeb596a-446d-465a-98aa-aa388ba67073
     *   Key: Accept Value:  text/html,application/xhtml+xml,application/xml;q=0.9,*⁄*;q=0.8
     *   Key: Upgrade-Insecure-Requests Value:  1
     *   Key: User-Agent Value:  Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15
     *   Key: Connection Value:  keep-alive
     *   Key: Referer Value:  http://localhost:63342/servlet_container/static/param.html?_ijt=5kl9vvcj3maro0qhpbm9ikm751
     *   Key: Host Value:  localhost:8000
     *   Key: Accept-Encoding Value:  gzip, deflate
     *   Key: Accept-Language Value:  ko-kr
     *   Key: Content-Length Value:  42
     *   Key: Content-Type Value:  application/x-www-form-urlencoded
     * paramName: postinputname paramValue: postinputvalue
     * paramName: submit paramValue: submit
     * End of HTTP Message.
     *
     */

}
