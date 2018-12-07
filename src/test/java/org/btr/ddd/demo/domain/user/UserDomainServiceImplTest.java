package org.btr.ddd.demo.domain.user;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDomainServiceImplTest
{
  UserDomainService domainService;
  @Mock
  UserRepository repository;

  @Before
  public void setUp()
  {
    domainService = new UserDomainServiceImpl(repository);
  }

  @Test
  public void editShouldReturnSomeRight() throws Exception
  {
    val now  = LocalDateTime.now();
    val data = new User();
    data.setId("1");
    data.setUsername("BornToRain");
    data.setPassword("123456");
    data.setCreateAt(now);
    data.setUpdateAt(now);

    when(repository.findById(anyString())).thenReturn(Optional.of(data));
    when(repository.findByUsername(anyString())).thenReturn(Option.none());

    val request = new UserEdit("BTR", "123456");

    val result = domainService.edit("1", request);

    Assertions.assertThat(result).isInstanceOf(Option.Some.class);
    Assertions.assertThat(result.get().get().getUsername()).isEqualTo(request.username);
  }

  @Test
  public void editShouldReturnSomeLeft() throws Exception
  {
    val now  = LocalDateTime.now();
    val data = new User();
    data.setId("1");
    data.setUsername("BornToRain");
    data.setPassword("123456");
    data.setCreateAt(now);
    data.setUpdateAt(now);

    when(repository.findById(anyString())).thenReturn(Optional.of(data));
    when(repository.findByUsername(anyString())).thenReturn(Option.none());

    val request1 = new UserEdit("BTR", "12345");

    val result1  = domainService.edit("1", request1);

    Assertions.assertThat(result1).isInstanceOf(Option.Some.class);
    Assertions.assertThat(result1.get().getLeft().head().msg).isEqualTo("密码不足六位");

    when(repository.findByUsername(anyString())).thenReturn(Option.some(data));

    val request2 = new UserEdit("BTR", "123456");

    val result2  = domainService.edit("1", request2);

    Assertions.assertThat(result2).isInstanceOf(Option.Some.class);
    Assertions.assertThat(result2.get().getLeft().head().msg).isEqualTo("用户名已注册");
  }

  @Test
  public void editShouldReturnNone() throws Exception
  {
    val request = new UserEdit("BTR", "123456");

    when(repository.findById(anyString())).thenReturn(Optional.empty());

    val result  = domainService.edit("1", request);

    Assertions.assertThat(result).isInstanceOf(Option.None.class);
  }
}