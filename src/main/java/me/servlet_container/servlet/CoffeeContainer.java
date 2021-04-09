package me.servlet_container.servlet;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class CoffeeContainer {

    URLClassLoader urlClassLoader = null;

    private void init() {
        // 커스텀하게 만든 서블릿 구현체 예시(CoffeeImpl)을 파일 경로로 가져온다.
        // 원래는 target 밑에서 들고 오는데 이 예제에서는 'target_fake'를 만들어서 구조 이름만 비슷하게 만들었다.
        String contextPath = "/Users/sombrero104/IdeaProjects/servlet_container/"
                            + "target/classes/me/servlet_container/servlet/custom";
        String classPath = contextPath.concat(File.separator)
                .concat("target_fake").concat(File.separator).concat("classes"); // classes 디렉토리에서 파일을 가져온다.
        String libPath = contextPath.concat(File.separator)
                .concat("target_fake").concat(File.separator).concat("lib"); // lib 디렉토리에서 파일을 가져온다.

        File classes = new File(classPath);
        List<URL> urlList = new ArrayList<>();
        if(classes.exists()) {
            try {
                urlList.add(classes.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        File lib = new File(libPath);
        if(lib.exists()) {
            try {
                FileFilter fileFilter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if(pathname.getName().endsWith(".jar")) { // .jar 인것만 가져오도록 FileFilter 사용.
                            return true;
                        }
                        return false;
                    }
                };
                File[] jarList = lib.listFiles(fileFilter);
                for(File file : jarList) {
                    urlList.add(file.toURI().toURL());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        URL[] urls = new URL[urlList.size()];
        for (int i=0; i<urls.length; i++) {
            urls[i] = urlList.get(i);
        }

        // 현재 스레드에 클래스로더를 새로 만들어준다.
        urlClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }

    private void load() {

    }

    private void action() {
        init();
        load();
    }

    public static void main(String[] args) {
        CoffeeContainer coffeeContainer = new CoffeeContainer();
        coffeeContainer.action();
    }

}
