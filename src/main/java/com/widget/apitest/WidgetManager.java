package com.widget.apitest;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

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
     * 
     * We could also do a mix of the 2: insert and remove would be logn, search would be O(1), modifyng would be 2*log(n) and full output of widgets logn The question therefore would be memory usage.
     * 
     * Please note that if we multithread this program, TreeMap and HashMap are not thread safe. Therefore we should select ConcurrentSkipListMap or ConcurrentHashMap. We will use the latter
     */
    
    private ConcurrentHashMap<String, Widget> widgetList;
    private ConcurrentSkipListMap<Integer, Widget> widgetListSortedbyZIndex;
    private WidgetLocationList widgetLocated;
    
    public WidgetManager() {
        this.widgetList = new ConcurrentHashMap<String, Widget>();
        this.widgetListSortedbyZIndex = new ConcurrentSkipListMap<Integer, Widget>();
        this.widgetLocated = new WidgetLocationList();
    }    
   
    public Widget getWidgetbyID(String id) {
        return this.widgetList.get(id);
    }
    
    public List<Widget> getWidgetAll(){
        if(this.widgetListSortedbyZIndex.size() == 0) {
            return null;
        }
        return new ArrayList<Widget>(this.widgetListSortedbyZIndex.values());
    }
    
    public List<Widget> getWidgetPartial(int limit, Optional<Integer> offset){
        if(this.widgetListSortedbyZIndex.size() == 0) {
            return null;
        }
        List<Widget> tmp = new ArrayList<Widget>(this.widgetListSortedbyZIndex.values());
        if(offset.isPresent() && offset.get() >= this.widgetListSortedbyZIndex.size())
            return null;
        if(offset.isPresent())
            return tmp.subList(Math.max(0, offset.get()), Math.min(Math.max(offset.get(), 0) + limit, this.widgetListSortedbyZIndex.size()));
        return tmp.subList(0, Math.min(limit, this.widgetListSortedbyZIndex.size()));
    }
      
    private void moveZIndex(int zindex) {
        if (this.widgetListSortedbyZIndex.get(zindex) != null) {
            //then we have to change the object and see if there is a cascade of changes to do
            Widget changedwidget = this.widgetListSortedbyZIndex.remove(zindex);
            HashMap<String, Integer> args = new HashMap<String, Integer>();
            args.put("zindex", zindex + 1);
            changedwidget.set(args);
            this.moveZIndex(zindex + 1);
            this.widgetListSortedbyZIndex.put(zindex + 1, changedwidget);
        }
    }    
   
    public Widget createWidget(Map<String, Integer> args) {
        if(!args.containsKey("x") || !args.containsKey("y") || !args.containsKey("width") || !args.containsKey("height"))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Missing widget parameters");
        int zindex = 0;
        if(!args.containsKey("zindex")) {
            if (!this.widgetListSortedbyZIndex.isEmpty())
                zindex = this.widgetListSortedbyZIndex.lastEntry().getValue().getZIndex() + 1;
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
        this.widgetList.put(result.getId().toString(), result);
        this.moveZIndex(zindex);
        this.widgetListSortedbyZIndex.put(zindex, result);
        this.widgetLocated.put(result);
        return result;
    }
    
    public Widget changeWidget(String id, HashMap<String, Integer> args) {
        Widget changedwidget = this.getWidgetbyID(id);
        if(args.containsKey("zindex")) {
            this.widgetListSortedbyZIndex.remove(changedwidget.getZIndex());
            this.moveZIndex(args.get("zindex"));
            if(args.containsKey("x"))
                this.widgetLocated.remove(changedwidget);
            changedwidget.set(args);;
            if(args.containsKey("x"))
                this.widgetLocated.put(changedwidget);
            this.widgetListSortedbyZIndex.put(args.get("zindex"), changedwidget);
        }
        else {
            if(args.containsKey("x"))
                this.widgetLocated.remove(changedwidget);
            if(args.containsKey("x"))
                this.widgetLocated.put(changedwidget);
            changedwidget.set(args);
        }
        return changedwidget;
    }
    
    public boolean deleteWidget(String id) {
        Widget removedwidget = this.getWidgetbyID(id);
        if(removedwidget != null) {
            this.widgetList.remove(id);
            this.widgetLocated.remove(removedwidget);
            this.widgetListSortedbyZIndex.remove(removedwidget.getZIndex());
            removedwidget = null;
            return true;
        }
        return false;
    }
    
    public List<Widget> getWidgetLocated(int xstart, int xend, int ystart, int yend) {
        return this.widgetLocated.searchX(xstart, xend, ystart, yend);
    }
    
}
