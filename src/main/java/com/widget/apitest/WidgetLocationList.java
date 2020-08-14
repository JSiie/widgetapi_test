package com.widget.apitest;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class WidgetLocationList {
    
    private ConcurrentSkipListMap<Integer, ConcurrentHashMap<String, Widget>> widgetlist;
    
    public WidgetLocationList(){
        this.widgetlist = new ConcurrentSkipListMap<Integer, ConcurrentHashMap<String, Widget>>();
    }
    
    public void put(Widget widget) {
        int x = widget.getX();
        if(this.widgetlist.containsKey(x)) {
            if(this.widgetlist.get(x).containsKey(widget.getId().toString()))
                return;
            this.widgetlist.get(x).put(widget.getId().toString(), widget);
        }
        else {
            this.widgetlist.put(x, new ConcurrentHashMap<String, Widget>());
            this.widgetlist.get(x).put(widget.getId().toString(), widget);
        }
    }

    public Widget remove(Widget widget) {
        int x = widget.getX();
        if(this.widgetlist.containsKey(x)) {
            if(!this.widgetlist.get(x).containsKey(widget.getId().toString()))
                return null;
            Widget result = this.widgetlist.get(x).remove(widget.getId().toString());
            return result;
        }
        else 
            return null;
    }
    
    /*
     * Idea is there we have stored data in treemap sorted by x (with many potential values for a same x, so widget are put into a hashmap)
     * Now we do 2 binary search to find possible candidates with x coodinate into x interval of search. Binary search is O(log(n)) complex in average
     * now we have a list of K elements. We will look at candidates in these k possibilities. complexity is now O(k)
     * final complexity is about O(k+2log(n))
     */
    public ArrayList<Widget> searchX(int xstart, int xend, int ystart, int yend){
        ArrayList<Widget> result = new ArrayList<Widget>();
        ArrayList<Integer> indexes = new ArrayList<Integer>(this.widgetlist.keySet());
        int indexStart = 0;
        int indexEnd = indexes.size() - 1;
        //we will do a binary search on the lower and top boundaries, with log(n) complexity
        
        //first lower boundaries
        int indexLow = 0;
        int indexHigh = indexes.size() - 1;
        while(indexLow < indexHigh) {
            int indexTmp = Math.floorDiv(indexLow + indexHigh, 2);
            if(indexes.get(indexTmp) < xstart) {
                indexLow = indexTmp + 1;
            }
            else if(indexes.get(indexTmp) > xstart) {
                indexHigh = indexTmp - 1;
                //in case we don't have a value for xtart, we take the first one which goes after it
                if(indexes.get(indexTmp) < xend && (indexTmp < indexStart || indexStart == 0)) {
                    indexStart = indexTmp;
                }
            }
            else {
                indexStart = indexTmp;
                break;
            }    
        }
        
        //now higher boundaries
        indexLow = 0;
        indexHigh = indexes.size() - 1;
        while(indexLow < indexHigh) {
            int indexTmp = Math.floorDiv(indexLow + indexHigh, 2);
            if(indexes.get(indexTmp) < xend) {
                indexLow = indexTmp + 1;
                //in case we don't have a value for xend, we take the first one which goes before it
                if(indexes.get(indexTmp) > xstart && (indexTmp > indexEnd || indexEnd == indexes.size() - 1)) {
                    indexEnd = indexTmp;
                }
            }
            else if(indexes.get(indexTmp) > xend) {
                indexHigh = indexTmp - 1;
            }
            else {
                indexEnd = indexTmp;
                break;
            }    
        }
        
        //now we look at all candidates and search if they satisfy the conditions
        //if yes we put them in result
        for(int i=indexStart; i<=indexEnd; i++) {
            for (Widget value : this.widgetlist.get(indexes.get(i)).values()) {
                if(value.getX() >= xstart && (value.getX() + value.getWidth() <= xend) && value.getY() >= ystart && (value.getY() + value.getHeight()) <= yend) {
                    result.add(value);
                }
            }
        }
        
        //we sort by zindex
        result.sort(Widget.sortByZIndex);
        return result;
    }
    
}
