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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;

import java.time.LocalDateTime;
import java.util.Arrays;

import static io.vavr.API.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    fieldWithPath("update_at").description("修改时间 yyyy-MM-dd HH:mm:ss"),
    subsectionWithPath("_links").description("资源链接集合")
  );

  private ResultHandler customDocument(String name, Snippet... snippets)
  {
    return document(name, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), snippets);
  }

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
      .andDo(customDocument("user-create",
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
      .andDo(customDocument("user-create-logic-400", ERRORS));
  }

  @Test
  public void createShouldReturnBusiness400() throws Exception
  {
    val request = new UserCreate("BornToRain", "123456");

    when(service.create(any(UserCreate.class))).thenReturn(Left(Seq(ApiError.create(2, "密码不足六位"))));

    mvc.perform(post(ApiConstant.USER_ENDPOINT)
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andDo(print())
      .andDo(customDocument("user-create-business-400", ERRORS));
  }

  @Test
  public void getInfoShouldReturn200() throws Exception
  {
    val id  = IdWorker.getId();
    val now = LocalDateTime.now();
    val dto = new UserInfo(id, "BornToRain", now, now);

    when(service.getInfo(anyString())).thenReturn(Option(dto));

    mvc.perform(RestDocumentationRequestBuilders.get(ApiConstant.USER_ENDPOINT + "/{id}", id))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(customDocument("user-info", ID_PATH, RESULT,
        links(
          linkWithRel(Link.REL_SELF).description("资源自身链接"),
          linkWithRel(ApiConstant.REL_EDIT).description("编辑用户链接"),
          linkWithRel(ApiConstant.REL_DELETE).description("删除用户链接")
        )
      ));
  }

  @Test
  public void getInfoShouldReturn404() throws Exception
  {
    when(service.getInfo(anyString())).thenReturn(None());

    mvc.perform(RestDocumentationRequestBuilders.get(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId()))
      .andExpect(status().isNotFound())
      .andDo(print())
      .andDo(customDocument("user-info-404"));
  }

  @Test
  public void editShouldReturn200() throws Exception
  {
    val request = new UserEdit("BTR", "123456");
    val id      = IdWorker.getId();
    val now     = LocalDateTime.now();
    val dto     = new UserInfo(id, "BornToRain", now, now);

    when(service.edit(anyString(), any(UserEdit.class))).thenReturn(Option(Right(dto)));

    mvc.perform(RestDocumentationRequestBuilders.put(ApiConstant.USER_ENDPOINT + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(mapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(customDocument("user-edit", ID_PATH,
        requestFields(
          fieldWithPath("username").description("用户名"),
          fieldWithPath("password").description("密码")
        ),
        RESULT,
        links(
          linkWithRel(Link.REL_SELF).description("资源自身链接"),
          linkWithRel(ApiConstant.REL_INFO).description("用户详情链接"),
          linkWithRel(ApiConstant.REL_DELETE).description("删除用户链接")
        )
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
      .andDo(customDocument("user-edit-logic-400", ERRORS));
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
      .andDo(customDocument("user-edit-business-400", ERRORS));
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
      .andDo(customDocument("user-edit-404"));
  }

  @Test
  public void deleteShouldReturn204() throws Exception
  {
    mvc.perform(RestDocumentationRequestBuilders.delete(ApiConstant.USER_ENDPOINT + "/{id}", IdWorker.getId()))
      .andExpect(status().isNoContent())
      .andDo(print())
      .andDo(customDocument("user-delete", ID_PATH));

    verify(service, times(1)).delete(anyString());
  }

  @Test
  public void getListShouldReturnList() throws Exception
  {
    val now  = LocalDateTime.now();
    val dto1 = new UserInfo(IdWorker.getId(), "BornToRain", now, now);
    val dto2 = new UserInfo(IdWorker.getId(), "BTR", now.plusDays(1), now.plusDays(1));
    val page = new PageImpl<>(Arrays.asList(dto1, dto2), PageRequest.of(1, 2), 2);

    when(service.getList(any(Pageable.class))).thenReturn(page);

    mvc.perform(get(ApiConstant.USER_ENDPOINT)
                  .param("page", page.getNumber() + "")
                  .param("size", page.getSize() + ""))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(customDocument("user-list",
        relaxedRequestParameters(
          parameterWithName("page").description("页码 从0开始"),
          parameterWithName("size").description("每页条数")
        ),
        links(
          linkWithRel(Link.REL_SELF).description("资源自身链接"),
          linkWithRel(Link.REL_FIRST).description("第一页资源链接"),
          linkWithRel(Link.REL_PREVIOUS).description("上一页资源链接").optional(),
          linkWithRel(Link.REL_NEXT).description("下一页资源链接").optional(),
          linkWithRel(Link.REL_LAST).description("最后一页资源链接")
        ),
        responseFields(
          fieldWithPath("page.size").description("每页条数"),
          fieldWithPath("page.total_elements").description("总条数"),
          fieldWithPath("page.total_pages").description("总页数"),
          fieldWithPath("page.number").description("每页条数"),
          subsectionWithPath("_links").description("资源地址"),
          fieldWithPath("_embedded.data[].username").description("用户名"),
          fieldWithPath("_embedded.data[].create_at").description("创建时间 yyyy-MM-dd HH:mm:ss"),
          fieldWithPath("_embedded.data[].update_at").description("修改时间 yyyy-MM-dd HH:mm:ss"),
          subsectionWithPath("_embedded.data[]._links").description("用户资源链接")
        )
      ));
  }
}
