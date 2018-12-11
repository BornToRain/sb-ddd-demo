package org.btr.ddd.demo.interfaces.api;

import org.btr.ddd.demo.infrastructure.tool.Restful;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexApi
{
  @GetMapping
  public ResponseEntity index()
  {
    return Restful.ok(Map.of("users", "http://localhost:8080/users{?page,size}"));
  }

  @GetMapping("/error")
  public ResponseEntity error()
  {
    throw new IllegalArgumentException();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity errorHandler()
  {
    return new ResponseEntity(Map.of(9999, "服务器异常,请联系管理员!"), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
