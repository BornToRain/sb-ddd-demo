package org.btr.ddd.demo.domain.user;

import io.vavr.control.Option;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String>
{
  Option<User> findByUsername(String username);
}
