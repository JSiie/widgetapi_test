package com.widget.apitest;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void contexLoads() throws Exception {
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
                .andExpect(jsonPath("$.zindex").value(0))
                ;
    }

}
