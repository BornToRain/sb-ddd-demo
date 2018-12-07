package org.btr.ddd.demo.application.service;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.btr.ddd.demo.domain.user.User;
import org.btr.ddd.demo.domain.user.UserDomainService;
import org.btr.ddd.demo.domain.user.UserRepository;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.infrastructure.tool.IdWorker;
import org.btr.ddd.demo.interfaces.dto.user.UserCreate;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static io.vavr.API.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImplTest
{
  UserService service;
  @Mock
  UserRepository    repository;
  @Mock
  UserDomainService domainService;

  @Before
  public void setUp()
  {
    service = new UserServiceImpl(repository, domainService);
  }

  @Test
  public void createShouldReturnRight() throws Exception
  {
    val request = new UserCreate("BornToRain", "123456");
    val data    = User.create(request).get();
    when(repository.save(any(User.class))).thenReturn(data);

    val result = service.create(request);

    Assertions.assertThat(result).isInstanceOf(Either.Right.class);
    Assertions.assertThat(result.get()).isEqualTo(data.getId());
  }

  @Test
  public void createShouldReturnLeft() throws Exception
  {
    val request = new UserCreate("BornToRain", "12345");

    val result = service.create(request);

    Assertions.assertThat(result).isInstanceOf(Either.Left.class);
    Assertions.assertThat(result.getLeft().head().msg).isEqualTo("密码不足六位");
  }

  @Test
  public void getInfoShouldReturnSome() throws Exception
  {
    val id   = IdWorker.getId();
    val data = new User();
    data.setId(id);
    data.setUsername("BornToRain");

    when(repository.findById(anyString())).thenReturn(Optional.of(data));

    val result = service.getInfo(id);

    Assertions.assertThat(result).isInstanceOf(Option.Some.class);
    Assertions.assertThat(result.get().username).isEqualTo("BornToRain");
  }

  @Test
  public void getInfoShouldReturnNone() throws Exception
  {
    when(repository.findById(anyString())).thenReturn(Optional.empty());

    val result = service.getInfo("1");

    Assertions.assertThat(result).isInstanceOf(Option.None.class);
  }

  @Test
  public void editShouldReturnSomeRight() throws Exception
  {
    val request = new UserEdit("BTR", "123456");
    val id      = IdWorker.getId();
    val data    = new User();
    data.setId(id);
    data.setUsername("BornToRain");

    when(domainService.edit(anyString(), any(UserEdit.class))).thenReturn(Option.some(Right(data)));

    val result = service.edit(id, request);

    Assertions.assertThat(result).isInstanceOf(Option.Some.class);
    Assertions.assertThat(result.get().get().username).isEqualTo("BornToRain");
  }

  @Test
  public void editShouldReturnSomeLeft() throws Exception
  {
    val request = new UserEdit("BTR", "123456");

    when(domainService.edit(anyString(), any(UserEdit.class)))
      .thenReturn(Option.some(Left(Seq(ApiError.create(3, "用户名已注册")))));

    val result = service.edit("1", request);

    Assertions.assertThat(result).isInstanceOf(Option.Some.class);
    Assertions.assertThat(result.get().getLeft().head().msg).isEqualTo("用户名已注册");
  }

  @Test
  public void editShouldReturnNone() throws Exception
  {
    val request = new UserEdit("BTR", "123456");

    when(domainService.edit(anyString(), any(UserEdit.class))).thenReturn(None());

    val result = service.edit("1", request);

    Assertions.assertThat(result).isInstanceOf(Option.None.class);
  }

  @Test
  public void deleteShouldOneTime() throws Exception
  {
    service.delete("1");

    verify(repository, times(1)).deleteById(anyString());
  }
}