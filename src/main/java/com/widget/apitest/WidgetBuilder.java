package com.widget.apitest;


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
        Widget widget = new Widget();
        widget.setX(this.x);
        widget.setY(this.y);
        widget.setWidth(this.width);
        widget.setHeight(this.height);
        widget.setZIndex(this.zindex);
        return widget;
    }

}
