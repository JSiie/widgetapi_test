package com.widget.apitest;


import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Please note that if we multithread this program, TreeMap and HashMap are not thread safe. Therefore we should select ConcurrentSkipListMap or ConcurrentHashMap. We will use the latter
     */
    
    public WidgetManager() {
        this.widgetList = new ConcurrentSkipListMap<Integer, Widget>();
    }
    
    private ConcurrentSkipListMap<Integer, Widget> widgetList;
    
   
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
    
    public List<Widget> getWidgetPartial(int limit, Optional<Integer> offset){
        if(this.widgetList.size() == 0) {
            return null;
        }
        List<Widget> tmp = new ArrayList<Widget>(this.widgetList.values());
        if(offset.isPresent() && offset.get() >= this.widgetList.size())
            return null;
        if(offset.isPresent())
            return tmp.subList(Math.max(0, offset.get()), Math.min(Math.max(offset.get(), 0) + limit, this.widgetList.size()));
        return tmp.subList(0, Math.min(limit, this.widgetList.size()));
    }
      
    private void moveZIndex(int zindex) {
        if (this.widgetList.get(zindex) != null) {
            //then we have to change the object and see if there is a cascade of changes to do
            Widget changedwidget = this.widgetList.remove(zindex);
            HashMap<String, Integer> args = new HashMap<String, Integer>();
            args.put("zindex", zindex + 1);
            changedwidget.set(args);
            this.moveZIndex(zindex + 1);
            this.widgetList.put(zindex + 1, changedwidget);
        }
    }    
   
    public Widget createWidget(Map<String, Integer> args) {
        if(!args.containsKey("x") || !args.containsKey("y") || !args.containsKey("width") || !args.containsKey("height"))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Missing widget parameters");
        int zindex = 0;
        if(!args.containsKey("zindex")) {
            if (!this.widgetList.isEmpty())
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
        if(args.containsKey("zindex")) {
            this.widgetList.remove(changedwidget.getZIndex());
            this.moveZIndex(args.get("zindex"));
            changedwidget.set(args);;
            this.widgetList.put(args.get("zindex"), changedwidget);
        }
        else {
            changedwidget.set(args);
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
