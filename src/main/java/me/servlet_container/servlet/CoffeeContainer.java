package me.servlet_container.servlet;

import java.io.File;

public class CoffeeContainer {

    private void init() {
        String contextPath = "/Users/sombrero104/IdeaProjects/"
                + "servlet_container/src/main/java/"
                + "me/servlet_container/servlet/custom";
        String classPath = contextPath.concat(File.separator)
                .concat("WEB-INF").concat(File.separator)
                .concat("classes");
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
