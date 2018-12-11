package org.btr.ddd.demo.interfaces.dto.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDateTime;

@AllArgsConstructor
@Relation(collectionRelation = "data")
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class UserInfo extends ResourceSupport
{
  String        id;
  String        username;
  LocalDateTime createAt;
  LocalDateTime updateAt;
}
