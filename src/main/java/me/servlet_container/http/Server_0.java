package me.servlet_container.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_0 {

    private ServerSocket serverSocket;

    private void boot() throws IOException {
        serverSocket = new ServerSocket(8000); // http://localhost:8000
        Socket socket = serverSocket.accept();
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        int oneInt = -1;
        while(-1 != (oneInt = in.read())) {
            System.out.print((char)oneInt);
        }
        out.close();
        in.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        Server_0 server_0 = new Server_0();
        server_0.boot();
    }

    /**
     * 실행 후 브라우저에서 http://localhost:8000 로 접속.
     *
     * 출력 결과:
     *
     *      GET / HTTP/1.1
     *      Host: localhost:8000
     *      Upgrade-Insecure-Requests: 1
     *      Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*⁄*;q=0.8
     *      User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15
     *      Accept-Language: ko-kr
     *      Accept-Encoding: gzip, deflate
     *      Connection: keep-alive
     */

}
