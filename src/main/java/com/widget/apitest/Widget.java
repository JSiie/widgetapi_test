package com.widget.apitest;

import java.util.UUID;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class Widget {
    
    //UUID is pratically unique. id could also be an atomic counter. But in case of a distributed system it can be tricky
    private UUID id;
    private int zindex, x, y, width, height;
    private Instant lastModified;
    
    public Widget() {
        this.id = UUID.randomUUID();
        this.lastModified = Instant.now();
    }
    
    public Widget(int x, int y, int width, int height, int zindex) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zindex = zindex;
        this.lastModified = Instant.now();
    }
    
    public void setID(String id) {
        this.lastModified = Instant.now();
    }
    
    public void setX(int x) {
        this.x = x;
        this.lastModified = Instant.now();
    }
    
    public void setY(int y) {
        this.y = y;
        this.lastModified = Instant.now();
    }
    
    public void setWidth(int width) {
        this.width = width;
        this.lastModified = Instant.now();
    }
    
    public void setHeight(int height) {
        this.height = height;
        this.lastModified = Instant.now();
    }
    
    public void setZIndex(int zindex) {
        this.zindex = zindex;
        this.lastModified = Instant.now();
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getZIndex() {
        return this.zindex;
    }
    
    public ZonedDateTime getLastModified() {
        return ZonedDateTime.ofInstant(this.lastModified,  ZoneId.of("Europe/Amsterdam"));
    }

}
