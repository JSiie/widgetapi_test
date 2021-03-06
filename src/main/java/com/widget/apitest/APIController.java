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
	  return "Hello! from a widget API";
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
     public List<Widget> getWidgetAll(@RequestParam Optional<Integer> limit,  
                                     @RequestParam Optional<Integer> offset,
                                     @RequestParam Optional<Integer> search_xstart,
                                     @RequestParam Optional<Integer> search_xend,
                                     @RequestParam Optional<Integer> search_ystart,
                                     @RequestParam Optional<Integer> search_yend
                                     ) {
	     if(search_xstart.isPresent() && search_xend.isPresent() && search_ystart.isPresent() && search_yend.isPresent()) {
	         List<Widget> result = widgetmanager.getWidgetLocated(search_xstart.get(), search_xend.get(), search_ystart.get(), search_yend.get());
	         return result; 
	     }
	     int realLimit = 10;
	     if(limit.isPresent() && limit.get() <= 500 && limit.get() > 0)
	         realLimit = limit.get();
	     if(limit.isPresent() && limit.get() > 500)
	         realLimit = 500;
         List<Widget> result = widgetmanager.getWidgetPartial(realLimit, offset);
         return result; 
     }
	 
	 @PostMapping(path="/widget" )
	 public Widget createWidget(@RequestBody HashMap<String, Integer> args) {
          Widget result = widgetmanager.createWidget(args);
          return result;
     }
	 
	 
	 @PutMapping(path="/widget/{id}")
     public Widget updateWidget(@PathVariable String id,
             @RequestBody HashMap<String, Integer> args) {
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
