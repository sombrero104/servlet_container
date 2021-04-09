package me.servlet_container.servlet.custom;

import me.servlet_container.servlet.Coffee;

public class CoffeeImpl implements Coffee {

    private String name = "DECAF HOUSE BLEND";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
