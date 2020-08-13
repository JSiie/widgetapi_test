package com.widget.apitest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class APIController {
    
    @Autowired
    private WidgetManager widgetmanager;
	
	 @GetMapping(path="/")
	 public String HomePage() {
	  return "Hello!! from a widget API";
	 }
	 
	 @GetMapping(path="/widget/{id}")
     public Widget getWidget(@PathVariable String id) {
         Widget result = widgetmanager.getWidgetbyID(id);
         //if null, a 404 error will be automatically displayed
         if(result ==null) {
             throw new ResponseStatusException(
                     HttpStatus.NOT_FOUND, "Widget Not Found");
         }
         return result; 
     }
	 
	 @GetMapping(path="/widget")
     public List<Widget> getWidgetAll() {
         List<Widget> result = widgetmanager.getWidgetAll();
         return result; 
     }
	 
	 /*@PostMapping(path="/widget")
     public Widget createWidget(@RequestParam(value = "x") int x, 
             @RequestParam(value = "y") int y, 
             @RequestParam(value = "width") int width, 
             @RequestParam(value = "height") int height , 
             @RequestParam(value = "zindex") Optional<Integer> zindex) {
	     if(zindex.isPresent()) {
	         Widget result = widgetmanager.createWidget(x, y, width, height, zindex.get());
	         return result;
	     }
	     else {
	         Widget result = widgetmanager.createWidget(x, y, width, height);
	         return result;
	     }
     }*/
	 
	 @PostMapping(path="/widget" )
	 public Widget createWidget(@RequestBody HashMap<String, Integer> args) {
          Widget result = widgetmanager.createWidget(args);
          return result;
     }
	 
	 @PutMapping(path="/widget/{id}")
     public Widget updateWidget(@PathVariable String id,
             @RequestParam(value = "x") Optional<Integer> x, 
             @RequestParam(value = "y") Optional<Integer> y, 
             @RequestParam(value = "width") Optional<Integer> width, 
             @RequestParam(value = "height") Optional<Integer> height , 
             @RequestParam(value = "zindex") Optional<Integer> zindex) {
	     HashMap<String, Integer> args = new HashMap<String, Integer>();
	     if(x.isPresent())
	         args.put("x", x.get());
	     if(y.isPresent())
             args.put("y", y.get());
	     if(width.isPresent())
             args.put("width", width.get());
	     if(height.isPresent())
             args.put("height", height.get());
	     if(zindex.isPresent())
             args.put("zindex", zindex.get());
         Widget result = widgetmanager.changeWidget(id, args);
         return result;
     }
	 
	 @DeleteMapping(path="/widget/{id}")
     public void delWidget(@PathVariable String id) {
         boolean result = widgetmanager.deleteWidget(id);
         //if false, ie widget not found, a 404 error will be automatically displayed
         if(!result) {
             throw new ResponseStatusException(
                     HttpStatus.NOT_FOUND, "Widget Not Found");
         }
         return; 
     }
	 
}
