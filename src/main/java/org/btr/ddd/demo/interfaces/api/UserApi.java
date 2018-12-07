package org.btr.ddd.demo.interfaces.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.btr.ddd.demo.application.service.UserService;
import org.btr.ddd.demo.infrastructure.ApiConstant;
import org.btr.ddd.demo.infrastructure.tool.Restful;
import org.btr.ddd.demo.interfaces.dto.user.UserCreate;
import org.btr.ddd.demo.interfaces.dto.user.UserEdit;
import org.btr.ddd.demo.interfaces.validator.UserLogicValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.vavr.API.*;
import static io.vavr.Patterns.*;

@RestController
@AllArgsConstructor
@RequestMapping(ApiConstant.USER_ENDPOINT)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserApi
{
  UserService service;

  @PostMapping
  public ResponseEntity create(@RequestBody UserCreate request)
  {
    val result = UserLogicValidator.validate(request)
                   .map(service::create);

    return Match(result).of(
      Case($Left($()), Restful::badRequest),
      Case($Right($()), Restful::created)
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity info(@PathVariable String id)
  {
    return Restful.ok(service.getInfo(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity edit(@PathVariable String id, @RequestBody UserEdit request)
  {
    val result = UserLogicValidator.validate(request)
                   .map(d -> service.edit(id, d));

    return Match(result).of(
      Case($Left($()), Restful::badRequest),
      Case($Right($None()), Restful::notFound),
      Case($Right($Some($Left($()))), d -> Restful.badRequest(d.get().getLeft())),
      Case($Right($Some($Right($()))), d -> Restful.ok(d.get().get()))
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity delete(@PathVariable String id)
  {
    service.delete(id);

    return Restful.noContent();
  }
}
