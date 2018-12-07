package org.btr.ddd.demo.application.service;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.interfaces.dto.user.UserCreate;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.btr.ddd.demo.interfaces.dto.user.UserInfo;

public interface UserService
{
  Either<Seq<ApiError>, String> create(UserCreate request);

  Option<UserInfo> getInfo(String id);

  Option<Either<Seq<ApiError>, UserInfo>> edit(String id, UserEdit request);

  void delete(String id);
}
