package org.btr.ddd.demo.domain.user;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.btr.ddd.demo.infrastructure.tool.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryTest
{
  @Autowired
  UserRepository    repository;
  @Autowired
  TestEntityManager tsm;

  @Test
  public void saveShouldReturnInfo() throws Exception
  {
    val id   = IdWorker.getId();
    val data = new User();
    data.setId(id);
    data.setUsername("BornToRain");
    data.setPassword("123456");
    data.setCreateAt(LocalDateTime.now());

    repository.save(data);

    val result = tsm.find(User.class, id);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result).isInstanceOf(User.class);
    Assertions.assertThat(result.getId()).isEqualTo(id);
  }

  @Test
  public void findByUsernameShouldReturnSome() throws Exception
  {
    val data = new User();
    data.setId("1");
    data.setUsername("BornToRain");
    data.setPassword("123456");
    data.setCreateAt(LocalDateTime.now());

    tsm.persist(data);

    val result = repository.findByUsername("BornToRain");

    Assertions.assertThat(result).isInstanceOf(Option.Some.class);
    Assertions.assertThat(result.get().getId()).isEqualTo("1");
  }

  @Test
  public void findByUsernameShouldReturnNone() throws Exception
  {
    val result = repository.findByUsername("BornToRain");

    Assertions.assertThat(result).isInstanceOf(Option.None.class);
  }
}