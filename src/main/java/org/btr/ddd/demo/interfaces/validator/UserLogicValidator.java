package org.btr.ddd.demo.interfaces.validator;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.interfaces.dto.user.UserCreate;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.springframework.util.StringUtils;

import static io.vavr.API.Invalid;
import static io.vavr.API.Valid;

public class UserLogicValidator
{
  public static Either<Seq<ApiError>, UserCreate> validate(UserCreate request)
  {
    return validateUsername(request.username)
             .combine(validatePassword(request.password))
             .ap((a, b) -> request)
             .toEither();
  }

  public static Either<Seq<ApiError>, UserEdit> validate(UserEdit request)
  {
    return validateUsername(request.username)
             .combine(validatePassword(request.password))
             .ap((a, b) -> request)
             .toEither();
  }

  private static Validation<ApiError, String> validatePassword(String password)
  {
    return StringUtils.isEmpty(password) ? Invalid(ApiError.create(1, "密码为空")) :
             Valid(password);
  }

  private static Validation<ApiError, String> validateUsername(String username)
  {
    return StringUtils.isEmpty(username) ? Invalid(ApiError.create(0, "用户名为空")) :
             Valid(username);
  }
}
