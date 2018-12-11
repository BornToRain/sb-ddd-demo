package org.btr.ddd.demo.domain.user;

import io.vavr.control.Option;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, String>
{
  Option<User> findByUsername(String username);
}
