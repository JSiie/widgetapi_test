package com.widget.apitest;

import java.util.Comparator;
import java.util.Map;
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
       
    private void setX(int x) {
        this.x = x;
        this.lastModified = Instant.now();
    }
    
    private void setY(int y) {
        this.y = y;
        this.lastModified = Instant.now();
    }
    
    private void setWidth(int width) {
        this.width = width;
        this.lastModified = Instant.now();
    }
    
    private void setHeight(int height) {
        this.height = height;
        this.lastModified = Instant.now();
    }
    
    private void setZIndex(int zindex) {
        this.zindex = zindex;
        this.lastModified = Instant.now();
    }
    
    public void set(Map<String, Integer> args) {
        synchronized(this) {
            if(args.containsKey("x")) {
                this.setX(args.get("x"));
            }
            if(args.containsKey("y")) {
                this.setY(args.get("y"));
            }
            if(args.containsKey("width")) {
                this.setWidth(args.get("width"));
            }
            if(args.containsKey("height")) {
                this.setHeight(args.get("height"));
            }
            if(args.containsKey("zindex")) {
                this.setZIndex(args.get("zindex"));
            }
        }
        return;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public int getX() {
        synchronized(this) {
            return this.x;
        }
    }
    
    public int getY() {
        synchronized(this) {
            return this.y;
        }
    }
    
    public int getWidth() {
        synchronized(this) {
            return this.width;
        }
    }
    
    public int getHeight() {
        synchronized(this) {
            return this.height;
        }
    }
    
    public int getZIndex() {
        synchronized(this) {
            return this.zindex;
        }
    }
    
    public ZonedDateTime getLastModified() {
        return ZonedDateTime.ofInstant(this.lastModified,  ZoneId.of("Europe/Amsterdam"));
    }
    
    //sort by name
    public static Comparator<Widget> sortByZIndex = new Comparator<Widget>() {
        @Override
        public int compare(Widget wgt1, Widget wgt2) {       
            //sort in ascending order
            if(wgt1.zindex < wgt2.zindex)
                return -1;
            else if (wgt1.zindex > wgt2.zindex)
                return 1;
            else 
                return 0;
        }
    }; 

}
