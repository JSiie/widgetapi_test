package com.widget.apitest;

import java.util.HashMap;

public class WidgetBuilder {
    
    private int zindex, x, y, width, height;
    
    WidgetBuilder withX(int x) {
        this.x = x;
        return this;
    }
    
    WidgetBuilder withY(int y) {
        this.y = y;
        return this;
    }
    
    WidgetBuilder withHeight(int height) {
        this.height = height;
        return this;
    }
    
    WidgetBuilder withWidth(int width) {
        this.width = width;
        return this;
    }
    
    WidgetBuilder withZIndex(int zindex) {
        this.zindex = zindex;
        return this;
    }
    
    Widget build(){
        HashMap<String, Integer> args = new HashMap<String, Integer>();
        args.put("x", this.x);
        args.put("y", this.y);
        args.put("width", this.width);
        args.put("height", this.height);
        args.put("zindex", this.zindex);
        Widget widget = new Widget();
        widget.set(args);
        return widget;
    }

}
