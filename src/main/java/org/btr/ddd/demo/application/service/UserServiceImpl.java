package org.btr.ddd.demo.application.service;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.btr.ddd.demo.domain.user.User;
import org.btr.ddd.demo.domain.user.UserDomainService;
import org.btr.ddd.demo.domain.user.UserRepository;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.interfaces.assembler.UserAssembler;
import org.btr.ddd.demo.interfaces.dto.user.UserCreate;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.btr.ddd.demo.interfaces.dto.user.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService
{
  UserRepository    repository;
  UserDomainService domainService;

  @Override
  public Either<Seq<ApiError>, String> create(UserCreate request)
  {
    return User.create(request)
             .map(repository::save)
             .map(User::getId);
  }

  @Override
  public Option<UserInfo> getInfo(String id)
  {
    return Option.ofOptional(repository.findById(id))
      .map(UserAssembler::toDTO);
  }

  @Override
  public Option<Either<Seq<ApiError>, UserInfo>> edit(String id, UserEdit request)
  {
    val result = domainService.edit(id, request);

    return result.map(d -> d.map(UserAssembler::toDTO));
  }

  @Override
  public void delete(String id)
  {
    repository.deleteById(id);
  }
}
