package me.servlet_container;

import java.io.*;

/**
 * 성능 개선 1 - 버퍼의 사용.
 * 외부 자원에 접근할 때 한번에 얻을 수 있는 양은
 * 전체 접근 횟수를 결정하므로 성능에 큰 영향을 미친다.
 *
 * 다음은 로컬 디스크에서 파일을 한 바이트씩 복사하는 것과
 * 버퍼를 사용하는 것에 대한 간단한 벤치 마크이다.
 */
public class Copy {

    private String fileName;
    private String targetFileName;

    private Copy(String fileName){
        this.fileName = fileName;
    }

    private void noBufferAction() throws IOException{
        targetFileName = fileName.concat("-nob");
        InputStream in = new FileInputStream(fileName);
        OutputStream out = new FileOutputStream(targetFileName);
        int oneInt = -1;
        while(-1 != (oneInt = in.read())){
            out.write(oneInt);
        }
        in.close();
        out.close();
    }

    private void bufferAction() throws IOException{
        targetFileName = fileName.concat("-useb");
        InputStream in = new FileInputStream(fileName);
        OutputStream out = new FileOutputStream(targetFileName);
        byte[] buffer = new byte[1024];
        int readSize = 0;
        while(0<(readSize = in.read(buffer))){
            out.write(buffer, 0, readSize);
        }
        in.close();
        out.close();
    }

    public static void main(String[] args) throws IOException {
        if(args == null || args.length < 1){
            System.out.println("파일이름 지정과 버퍼 사용 여부가 필요합니다.");
            System.exit(0);
        }
        Copy c = new Copy(args[0]);
        long before = System.currentTimeMillis();

        if(args.length > 1 && "no".equals(args[1])){
            c.noBufferAction();
        }else{
            c.bufferAction();
        }
        long after = System.currentTimeMillis();
        System.out.printf("%.3f\n", (float)((after-before)/1000f));
    }

}
