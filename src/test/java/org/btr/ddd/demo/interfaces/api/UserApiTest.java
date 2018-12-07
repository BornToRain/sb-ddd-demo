package org.btr.ddd.demo.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.btr.ddd.demo.application.service.UserService;
import org.btr.ddd.demo.infrastructure.ApiConstant;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.infrastructure.config.ServerConfiguration;
import org.btr.ddd.demo.infrastructure.tool.IdWorker;
import org.btr.ddd.demo.interfaces.dto.user.UserCreate;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.btr.ddd.demo.interfaces.dto.user.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static io.vavr.API.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserApi.class)
@ImportAutoConfiguration(ServerConfiguration.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserApiTest
{
  @Autowired
  MockMvc      mvc;
  @Autowired
  ObjectMapper mapper;
  @MockBean
  UserService  service;

  private static final ResponseFieldsSnippet ERRORS  = responseFields(
    fieldWithPath("[].code").description("错误码"),
    fieldWithPath("[].msg").description("错误信息")
  );
  private static final PathParametersSnippet ID_PATH = pathParameters(
    parameterWithName("id").description("资源主键")
  );
  private static final ResponseFieldsSnippet RESULT  = responseFields(
    fieldWithPath("username").description("用户名"),
    fieldWithPath("create_at").description("创建时间 yyyy-MM-dd HH:mm:ss"),
    fieldWithPath("update_at").description("修改时间 yyyy-MM-dd HH:mm:ss")
  );

  @Test
  public void createShouldReturn201() throws Exception
  {
    val request = new UserCreate("BornToRain", "123456");

    when(service.create(any(UserCreate.class))).thenReturn(Right(IdWorker.getId()));

    mvc.perform(post(ApiConstant.USER_ENDPOINT)
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andDo(print())
      .andDo(document("user-create",
        requestFields(
          fieldWithPath("username").description("用户名"),
          fieldWithPath("password").description("密码")
        ),
        responseHeaders(
          headerWithName(HttpHeaders.LOCATION).description("资源唯一URI")
        )
      ));
  }

  @Test
  public void createShouldReturnLogic400() throws Exception
  {
    val request = new UserCreate(null, null);

    mvc.perform(post(ApiConstant.USER_ENDPOINT)
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andDo(print())
      .andDo(document("user-create-logic-400", ERRORS));
  }

  @Test
  public void createShouldReturnBusiness400() throws Exception
  {
    val request = new UserCreate("BornToRain", "123456");

    when(service.create(any(UserCreate.class))).thenReturn(Left(Seq(ApiError.create(2, "pre"))));

    mvc.perform(post(ApiConstant.USER_ENDPOINT)
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andDo(print())
      .andDo(document("user-create-business-400", ERRORS));
  }

  @Test
  public void getInfoShouldReturn200() throws Exception
  {
    val now = LocalDateTime.now();
    val dto = new UserInfo("BornToRain", now, now);

    when(service.getInfo(anyString())).thenReturn(Option(dto));

    mvc.perform(RestDocumentationRequestBuilders.get(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId()))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(document("user-info", ID_PATH, RESULT));


  }

  @Test
  public void getInfoShouldReturn404() throws Exception
  {
    when(service.getInfo(anyString())).thenReturn(None());

    mvc.perform(RestDocumentationRequestBuilders.get(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId()))
      .andExpect(status().isNotFound())
      .andDo(print())
      .andDo(document("user-info-404"));
  }

  @Test
  public void editShouldReturn200() throws Exception
  {
    val request = new UserEdit("BTR", "123456");
    val now     = LocalDateTime.now();
    val dto     = new UserInfo("BornToRain", now, now);

    when(service.edit(anyString(), any(UserEdit.class))).thenReturn(Option(Right(dto)));

    mvc.perform(RestDocumentationRequestBuilders.put(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId())
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(document("user-edit", ID_PATH,
        requestFields(
          fieldWithPath("username").description("用户名"),
          fieldWithPath("password").description("密码")
        ),
        RESULT
      ));
  }

  @Test
  public void editShouldReturnLogic400() throws Exception
  {
    val request = new UserEdit(null, null);

    mvc.perform(RestDocumentationRequestBuilders.put(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId())
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andDo(print())
      .andDo(document("user-edit-logic-400", ERRORS));
  }

  @Test
  public void editShouldReturnBusiness400() throws Exception
  {
    val request = new UserEdit("BTR", "123456");

    when(service.edit(anyString(), any(UserEdit.class)))
      .thenReturn(Option(Left(Seq(ApiError.create(3, "用户名已注册")))));

    mvc.perform(RestDocumentationRequestBuilders.put(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId())
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andDo(print())
      .andDo(document("user-edit-business-400", ERRORS));
  }

  @Test
  public void editShouldReturn404() throws Exception
  {
    val request = new UserEdit("BTR", "123456");
    when(service.edit(anyString(), any(UserEdit.class))).thenReturn(None());

    mvc.perform(RestDocumentationRequestBuilders.put(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId())
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andDo(print())
      .andDo(document("user-edit-404"));
  }

  @Test
  public void deleteShouldReturn204() throws Exception
  {
    mvc.perform(RestDocumentationRequestBuilders.delete(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId()))
      .andExpect(status().isNoContent())
      .andDo(print())
      .andDo(document("user-delete", ID_PATH));

    verify(service, times(1)).delete(anyString());
  }
}
