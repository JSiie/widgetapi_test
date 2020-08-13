package com.widget.apitest;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class TestWebApp {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private APIController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
    
    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello! from a widget API")));
    }
    
    @Test
    public void createWidgetFailure()
      throws Exception {
         
        this.mockMvc.perform(post("/widget")
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void createWidget()
      throws Exception {   
        this.mockMvc.perform(post("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"x\":1,\"y\":1,\"width\":100,\"height\":150}")
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.x").value(1))
                .andExpect(jsonPath("$.y").value(1))
                .andExpect(jsonPath("$.width").value(100))
                .andExpect(jsonPath("$.height").value(150))
                //widget with index 4 was created before, therefore this one will be on foreground
                .andExpect(jsonPath("$.zindex").value(5))
                ;
    }
    
    @Test
    public void createWidgetWithIndex()
      throws Exception {   
        this.mockMvc.perform(post("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"x\":1,\"y\":1,\"width\":100,\"height\":150,\"zindex\":3}}")
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.x").value(1))
                .andExpect(jsonPath("$.y").value(1))
                .andExpect(jsonPath("$.width").value(100))
                .andExpect(jsonPath("$.height").value(150))
                .andExpect(jsonPath("$.zindex").value(3))
                ;
    }
    
    @Test
    public void ListWidgets()
      throws Exception {   
        this.mockMvc.perform(get("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].x").value(1))
                .andExpect(jsonPath("$[0].y").value(1))
                .andExpect(jsonPath("$[0].width").value(100))
                .andExpect(jsonPath("$[0].height").value(150))
                .andExpect(jsonPath("$[0].zindex").value(3))
                ;
    }
    
    @Test
    public void UpdateWidget()
      throws Exception {   
        MvcResult result = this.mockMvc.perform(post("/widget")
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
        
        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        
        this.mockMvc.perform(put("/widget/"+id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"x\":30,\"zindex\":1}}")
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.x").value(30))
                .andExpect(jsonPath("$.y").value(10))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(250))
                .andExpect(jsonPath("$.zindex").value(1))
                ;
    }
    
    @Test
    public void DeleteWidget()
      throws Exception {   
        MvcResult result = this.mockMvc.perform(post("/widget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"x\":10,\"y\":10,\"width\":200,\"height\":250,\"zindex\":-1}}")
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.x").value(10))
                .andExpect(jsonPath("$.y").value(10))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(250))
                .andExpect(jsonPath("$.zindex").value(-1))
                .andReturn()
                ;
        
        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        
        this.mockMvc.perform(delete("/widget/"+id)
                            .contentType(MediaType.APPLICATION_JSON)
                            )
                .andDo(print())
                .andExpect(status().isOk())
                ;
        
        this.mockMvc.perform(get("/widget/"+id)
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
}
