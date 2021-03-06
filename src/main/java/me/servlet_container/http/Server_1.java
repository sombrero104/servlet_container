package me.servlet_container.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_1 {

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
        while(-1 != (oneInt = in.read())) {
            byte thisByte = (byte)oneInt;
            if(thisByte == Server_1.LF && oldByte == Server_1.CR) {
                // CRLF가 완성되었다. 따라서 직전 CRLF부터 여기까지가 한 행이다.
                // -2가 아니라 -1을 하는 이유는 아직 LF가 버퍼에 들어가기 전이기 때문이다.
                String oneLine = sb.substring(0, sb.length()-1);
                lineNumber++;
                System.out.printf("%d: %s\n", lineNumber, oneLine);
                if(oneLine.length() <= 0) {
                    // 내용이 없는 행.
                    // 따라서 메시지 헤더의 마지막일 경우다.
                    System.out.println("##### 내용이 없는 헤더, 즉 메시지 헤더의 끝.");
                    // 현 상황에서는 메시지 바디는 처리하지 말기로 한다.
                    break;
                }
                sb.setLength(0);
            } else {
                sb.append((char)thisByte);
            }
            oldByte = (byte)oneInt;
        }
        in.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        Server_1 server_1 = new Server_1();
        server_1.boot();
    }

    /**
     * 실행 후 브라우저에서 http://localhost:8000 로 접속.
     *
     * 출력 결과:
     *
     *      1: GET / HTTP/1.1
     *      2: Host: localhost:8000
     *      3: Upgrade-Insecure-Requests: 1
     *      4: Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*⁄*;q=0.8
     *      5: User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15
     *      6: Accept-Language: ko-kr
     *      7: Accept-Encoding: gzip, deflate
     *      8: Connection: keep-alive
     *      9:
     *      ##### 내용이 없는 헤더, 즉 메시지 헤더의 끝.
     *
     */

}
