package org.btr.ddd.demo.domain.user;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.btr.ddd.demo.infrastructure.ApiError;
import org.btr.ddd.demo.infrastructure.tool.IdWorker;
import org.btr.ddd.demo.interfaces.dto.user.UserCreate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

import static io.vavr.API.*;

@Data
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User
{
  @Id
  @Column(length = 32)
  String        id;
  @Column(length = 32)
  String        username;
  @Column(length = 32)
  String        password;
  @Column(length = 32)
  LocalDateTime createAt;
  @Column(length = 32)
  LocalDateTime updateAt;

  private User(UserCreate request)
  {
    val now = LocalDateTime.now();

    this.id = IdWorker.getId();
    this.username = request.username;
    this.password = request.password;
    this.createAt = now;
    this.updateAt = now;
  }

  public static Either<Seq<ApiError>, User> create(UserCreate request)
  {
    return validatePassword(request.password)
             .map(d -> new User(request));
  }

  public static Either<Seq<ApiError>, String> validatePassword(String password)
  {
    if (password.length() < 6) return Left(Seq(ApiError.create(2, "密码不足六位")));
    return Right(password);
  }
}
