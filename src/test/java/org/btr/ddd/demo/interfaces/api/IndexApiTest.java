package org.btr.ddd.demo.interfaces.api;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.btr.ddd.demo.infrastructure.config.ServerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(IndexApi.class)
@ImportAutoConfiguration(ServerConfiguration.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IndexApiTest
{
  @Autowired
  MockMvc mvc;

  @Test
  public void getErrorShouldReturn5XX() throws Exception
  {
    mvc.perform(get("/error"))
      .andExpect(status().is5xxServerError())
      .andDo(print());
  }

  @Test
  public void getIndexShouldReturnIndex() throws Exception
  {
    mvc.perform(get("/"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("users").isNotEmpty())
      .andDo(print())
      .andDo(document("index",
        responseFields(
          fieldWithPath("users").description("用户资源链接")
        )
      ));
  }
}
