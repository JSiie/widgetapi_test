package com.widget.apitest;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTestWebApp {
    
    @Autowired
    private MockMvc mockMvc;
    
    
    @Test
    public void CreateWidgetList()
      throws Exception {
        
        /*
         * We create 3 widgets, with index 3, not defined and 3.
         * In the end we should have these widgets at indexes 3,4 and 5 (widget2, widget 0 widget 1)
         */
        
        MvcResult result0 = this.mockMvc.perform(post("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"x\":10,\"y\":10,\"width\":200,\"height\":250,\"zindex\":3}}")
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.x").value(10))
                .andExpect(jsonPath("$.y").value(10))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(250))
                .andExpect(jsonPath("$.zindex").value(3))
                .andReturn()
                ;
        
        String id0 = JsonPath.read(result0.getResponse().getContentAsString(), "$.id");
        
        MvcResult result1 = this.mockMvc.perform(post("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"x\":20,\"y\":20,\"width\":200,\"height\":250}}")
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.x").value(20))
                .andExpect(jsonPath("$.y").value(20))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(250))
                .andExpect(jsonPath("$.zindex").value(4))
                .andReturn()
            ;
        
        String id1 = JsonPath.read(result1.getResponse().getContentAsString(), "$.id");

        MvcResult result2 = this.mockMvc.perform(post("/widget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":30,\"y\":30,\"width\":200,\"height\":250,\"zindex\":3}}")
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.x").value(30))
                .andExpect(jsonPath("$.y").value(30))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(250))
                .andExpect(jsonPath("$.zindex").value(3))
                .andReturn()
        ;
        
        String id2 = JsonPath.read(result2.getResponse().getContentAsString(), "$.id");
        
        this.mockMvc.perform(get("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$[0].id").value(id2))
                .andExpect(jsonPath("$[0].x").value(30))
                .andExpect(jsonPath("$[0].y").value(30))
                .andExpect(jsonPath("$[0].width").value(200))
                .andExpect(jsonPath("$[0].height").value(250))
                .andExpect(jsonPath("$[0].zindex").value(3))
                
                .andExpect(jsonPath("$[1].id").value(id0))
                .andExpect(jsonPath("$[1].x").value(10))
                .andExpect(jsonPath("$[1].y").value(10))
                .andExpect(jsonPath("$[1].width").value(200))
                .andExpect(jsonPath("$[1].height").value(250))
                .andExpect(jsonPath("$[1].zindex").value(4))
                
                .andExpect(jsonPath("$[2].id").value(id1))
                .andExpect(jsonPath("$[2].x").value(20))
                .andExpect(jsonPath("$[2].y").value(20))
                .andExpect(jsonPath("$[2].width").value(200))
                .andExpect(jsonPath("$[2].height").value(250))
                .andExpect(jsonPath("$[2].zindex").value(5))
                ;
        
        //now we switch first and second widget 
        this.mockMvc.perform(put("/widget/"+id0)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"zindex\":3}}")
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(id0))
                .andExpect(jsonPath("$.x").value(10))
                .andExpect(jsonPath("$.y").value(10))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(250))
                .andExpect(jsonPath("$.zindex").value(3))
                ;
        
        this.mockMvc.perform(get("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$[0].id").value(id0))
                .andExpect(jsonPath("$[0].x").value(10))
                .andExpect(jsonPath("$[0].y").value(10))
                .andExpect(jsonPath("$[0].width").value(200))
                .andExpect(jsonPath("$[0].height").value(250))
                .andExpect(jsonPath("$[0].zindex").value(3))
                
                .andExpect(jsonPath("$[1].id").value(id2))
                .andExpect(jsonPath("$[1].x").value(30))
                .andExpect(jsonPath("$[1].y").value(30))
                .andExpect(jsonPath("$[1].width").value(200))
                .andExpect(jsonPath("$[1].height").value(250))
                .andExpect(jsonPath("$[1].zindex").value(4))
                
                .andExpect(jsonPath("$[2].id").value(id1))
                .andExpect(jsonPath("$[2].x").value(20))
                .andExpect(jsonPath("$[2].y").value(20))
                .andExpect(jsonPath("$[2].width").value(200))
                .andExpect(jsonPath("$[2].height").value(250))
                .andExpect(jsonPath("$[2].zindex").value(5))
                ;
    }

}
