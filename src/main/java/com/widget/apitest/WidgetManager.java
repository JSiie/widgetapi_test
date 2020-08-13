package com.widget.apitest;


import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WidgetManager {
    
    /* now comes the point on which data collection to choose
     * 2 main candidates: HashMap and TreeMap.
     * HashMap has the advantage to make search/insert/delete with O(1) in the average and O(n) at worst using widget id as a key, but sorting through z-index is nlogn
     * TreeMap has the advantage to be sorted using z-index as a key so going through it will be n at worst to output all widgets, but insert/delete are logn operations and search will be O(n) (with key being zindex)
     * 
     * We could imagine using z-index as a key but that could give some issues when changing the z-index.
     * I guess that our basic operations will be N insert + some search/delete, and at least one full list output with zindex sorting (plus eventually some if during insert/modifying zindex given is already existing)
     * Therefore I think TreeMap could be the best option (hashmap: n op. at O(1) + 1+ op at nlogn, treemap: n op. at logn + 1+ op at n)
     * We could also do a mix of the 2: insert and remove would be logn, search would be O(1), modifyng would be 2*log(n) and full output of widgets logn The question therefore would be memory usage.
     * 
     * Please note that if we multithread this program, TreeMap and HashMap are not thread safe. Therefore we should select ConcurrentSkipListMap or HashTable.
     */
    
    public WidgetManager() {
        this.widgetList = new TreeMap<Integer, Widget>();
    }
    
    private TreeMap<Integer, Widget> widgetList;
    
   
    public Widget getWidgetbyID(String id) {
        List<Widget> result = this.widgetList.entrySet().stream()
                .filter(x -> id.equals( x.getValue().getId().toString()))
                .map(x -> x.getValue())
                .collect(Collectors.toList());
        switch(result.size()) {
            case 0:
                return null;
            case 1:
                return result.get(0);
            default:
                return null;
        }
    }
    
    public List<Widget> getWidgetAll(){
        if(this.widgetList.size() == 0) {
            return null;
        }
        return new ArrayList<Widget>(this.widgetList.values());
    }
    
    public Widget createWidget(int x, int y, int width, int height) {
        int zindex = 0;
        if (!this.widgetList.isEmpty()) 
            zindex = this.widgetList.lastEntry().getValue().getZIndex() + 1;
        Widget result = new WidgetBuilder()
                .withX(x)
                .withY(y)
                .withWidth(width)
                .withHeight(height)
                .withZIndex(zindex)
                .build();
        this.widgetList.put(zindex, result);
        return result;
    }
    
    private void moveZIndex(int zindex) {
        if (this.widgetList.get(zindex) != null) {
            //then we have to change the object and see if there is a cascade of changes to do
            Widget changedwidget = this.widgetList.remove(zindex);
            changedwidget.setZIndex(zindex + 1);
            this.moveZIndex(zindex + 1);
            this.widgetList.put(zindex + 1, changedwidget);
        }
    }
    
    public Widget createWidget(int x, int y, int width, int height, int zindex) {
        Widget result = new WidgetBuilder()
                .withX(x)
                .withY(y)
                .withWidth(width)
                .withHeight(height)
                .withZIndex(zindex)
                .build();
        this.moveZIndex(zindex);
        this.widgetList.put(zindex, result);
        return result;
    }
    
   
    public Widget createWidget(Map<String, Integer> args) {
        if(!args.containsKey("x") || !args.containsKey("y") || !args.containsKey("width") || !args.containsKey("height"))
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Missing widget parameters");
        int zindex = 0;
        if(!args.containsKey("zindex")) {
            zindex = this.widgetList.lastEntry().getValue().getZIndex() + 1;
        }
        else{
            zindex = args.get("zindex");
        }
            
        Widget result = new WidgetBuilder()
                .withX(args.get("x"))
                .withY(args.get("y"))
                .withWidth(args.get("width"))
                .withHeight(args.get("height"))
                .withZIndex(zindex)
                .build();
        this.moveZIndex(zindex);
        this.widgetList.put(zindex, result);
        return result;
    }
    
    public Widget changeWidget(String id, HashMap<String, Integer> args) {
        Widget changedwidget = this.getWidgetbyID(id);
        if(args.containsKey("x")) {
            changedwidget.setX(args.get("x"));
        }
        if(args.containsKey("y")) {
            changedwidget.setY(args.get("y"));
        }
        if(args.containsKey("width")) {
            changedwidget.setWidth(args.get("width"));
        }
        if(args.containsKey("height")) {
            changedwidget.setHeight(args.get("height"));
        }
        if(args.containsKey("zindex")) {
            this.widgetList.remove(changedwidget.getZIndex());
            this.moveZIndex(args.get("zindex"));
            changedwidget.setZIndex(args.get("zindex"));
            this.widgetList.put(args.get("zindex"), changedwidget);
        }
        return changedwidget;
    }
    
    public boolean deleteWidget(String id) {
        Widget removedwidget = this.getWidgetbyID(id);
        if(removedwidget != null) {
            this.widgetList.remove(removedwidget.getZIndex());
            removedwidget = null;
            return true;
        }
        return false;
    }
    
}