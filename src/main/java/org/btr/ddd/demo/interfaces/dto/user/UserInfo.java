package org.btr.ddd.demo.interfaces.dto.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class UserInfo
{
  String        username;
  LocalDateTime createAt;
  LocalDateTime updateAt;
}
