package org.btr.ddd.demo.domain.user;

import io.vavr.API;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static io.vavr.API.Left;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDomainServiceImpl implements UserDomainService
{
  UserRepository repository;

  @Override
  public Option<Either<Seq<ApiError>, User>> edit(String id, UserEdit request)
  {
    return Option.ofOptional(repository.findById(id))
             .map(d ->
             {
               // 校验用户名
               // 是否用户名已注册
               val isExist = repository.findByUsername(request.username).isDefined();
               if (isExist) return Left(API.Seq(ApiError.create(3, "用户名已注册")));
               // 校验密码
               return User.validatePassword(request.password)
                        .map(s ->
                        {
                          d.setUsername(request.username);
                          d.setPassword(request.password);
                          d.setUpdateAt(LocalDateTime.now());
                          return d;
                        });
             });
  }
}
