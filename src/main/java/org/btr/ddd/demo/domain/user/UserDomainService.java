package org.btr.ddd.demo.domain.user;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;

public interface UserDomainService
{
  Option<Either<Seq<ApiError>, User>> edit(String id, UserEdit request);
}
