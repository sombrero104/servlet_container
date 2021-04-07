package me.servlet_container;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server_2 {

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
        Map<String, String> headerMap = new HashMap<String, String>();
        while(-1 != (oneInt = in.read())) {
            byte thisByte = (byte)oneInt;
            if(bodyFlag) {
                bodyRead++;
                bodyByteList.add(thisByte);
                if(bodyRead >= contentLength) {
                    break;
                }
            } else {
                if(thisByte == Server_2.LF && oldByte == Server_2.CR) {
                    String oneLine = sb.substring(0, sb.length()-1);
                    lineNumber++;
                    if(lineNumber == 1) {
                        // 요청의 첫 행, HTTP 메서드, 요청 URL, 버전을 알아낸다.
                        int firstBlank = oneLine.indexOf(" ");
                        int secondBlank = oneLine.lastIndexOf(" ");
                        method = oneLine.substring(0, firstBlank);
                        requestUrl = oneLine.substring(firstBlank+1, secondBlank);
                        httpVersion = oneLine.substring(secondBlank+1);
                    } else {
                        if(oneLine.length() <= 0) {
                            bodyFlag = true;
                            // 헤더가 끝났다.
                            if ("GET".equals(method)) {
                                // GET 방식이면 메시지 바디가 없다.
                                break;
                            }
                            String contentLengthValue = headerMap.get("Content-Length");
                            if(contentLengthValue != null) {
                                contentLength = Integer.parseInt(contentLengthValue.trim());
                                bodyFlag = true;
                                bodyByteList = new ArrayList<Byte>();
                            }
                            continue;
                        }
                        int indexOfColon = oneLine.indexOf(":");
                        String headerName = oneLine.substring(0, indexOfColon);
                        String headerValue = oneLine.substring(indexOfColon+1);
                        headerMap.put(headerName, headerValue);
                    }
                    sb.setLength(0);
                } else {
                    sb.append((char)thisByte);
                }
            }
            oldByte = (byte)oneInt;
        }
        in.close();
        socket.close();
        System.out.printf("METHOD: %s REQ: %s HTTP VER. %s\n", method, requestUrl, httpVersion);
        System.out.println("Header list");
        Set<String> keySet = headerMap.keySet();
        Iterator<String> keyIter = keySet.iterator();
        while(keyIter.hasNext()) {
            String headerName = keyIter.next();
            System.out.printf("   Key: %s Value: %s\n", headerName, headerMap.get(headerName));
        }
        if(bodyByteList != null) {
            System.out.print("Message Body -->");
            for(byte oneByte : bodyByteList) {
                System.out.print(oneByte);
            }
            System.out.println("<--");
        }
        System.out.println("End of HTTP Message.");
    }

    public static void main(String[] args) throws IOException {
        Server_2 server_2 = new Server_2();
        server_2.boot();
    }

    /**
     * 실행 후 브라우저에서 http://localhost:8000 로 접속.
     *
     * 출력 결과:
     *
     * 1. GET 요청일 경우.
     *      METHOD: GET REQ: / HTTP VER. HTTP/1.1
     *      Header list
     *          Key: Accept Value:  text/html,application/xhtml+xml,application/xml;q=0.9,*⁄*;q=0.8
     *          Key: Upgrade-Insecure-Requests Value:  1
     *          Key: User-Agent Value:  Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15
     *          Key: Connection Value:  keep-alive
     *          Key: Host Value:  localhost:8000
     *          Key: Accept-Language Value:  ko-kr
     *          Key: Accept-Encoding Value:  gzip, deflate
     *      End of HTTP Message.
     *
     * 2. POST 요청일 경우.
     * (1) 사파리 브라우저에서 요청한 경우.
     *      METHOD: POST REQ: / HTTP VER. HTTP/1.1
     *      Header list
     *          Key: Origin Value:  http://localhost:63342
     *          Key: Cookie Value:  Idea-3ef64c5c=faeb596a-446d-465a-98aa-aa388ba67073
     *          Key: Accept Value:  text/html,application/xhtml+xml,application/xml;q=0.9,*⁄*;q=0.8
     *          Key: Upgrade-Insecure-Requests Value:  1
     *          Key: User-Agent Value:  Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15
     *          Key: Connection Value:  keep-alive
     *          Key: Referer Value:  http://localhost:63342/servlet_container/static/post.html?_ijt=pv6bp7mv5b8d4hf2r7ov1uqqla
     *          Key: Host Value:  localhost:8000
     *          Key: Accept-Encoding Value:  gzip, deflate
     *          Key: Accept-Language Value:  ko-kr
     *          Key: Content-Length Value:  34
     *          Key: Content-Type Value:  application/x-www-form-urlencoded
     *      Message Body -->10511011211711611097109101611051101121171161189710811710138115117981091051166111511798109105116<--
     *      End of HTTP Message.
     *
     * (2) 크롬 브라우저에서 요청한 경우.
     *      METHOD: POST REQ: / HTTP VER. HTTP/1.1
     *      Header list
     *          Key: Origin Value:  http://localhost:63342
     *          Key: Cookie Value:  Idea-3ef64c5c=faeb596a-446d-465a-98aa-aa388ba67073
     *          Key: Accept Value:  text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*⁄*;q=0.8,application/signed-exchange;v=b3;q=0.9
     *          Key: Connection Value:  keep-alive
     *          Key: User-Agent Value:  Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36
     *          Key: Referer Value:  http://localhost:63342/
     *          Key: Sec-Fetch-Site Value:  same-site
     *          Key: Sec-Fetch-Dest Value:  document
     *          Key: Host Value:  localhost:8000
     *          Key: Accept-Encoding Value:  gzip, deflate, br
     *          Key: Sec-Fetch-Mode Value:  navigate
     *          Key: sec-ch-ua Value:  "Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99"
     *          Key: sec-ch-ua-mobile Value:  ?0
     *          Key: Cache-Control Value:  max-age=0
     *          Key: Upgrade-Insecure-Requests Value:  1
     *          Key: Sec-Fetch-User Value:  ?1
     *          Key: Accept-Language Value:  ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
     *          Key: Content-Length Value:  34
     *          Key: Content-Type Value:  application/x-www-form-urlencoded
     *      Message Body -->10511011211711611097109101611051101121171161189710811710138115117981091051166111511798109105116<--
     *      End of HTTP Message.
     *
     */

}
