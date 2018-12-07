package org.btr.ddd.demo.interfaces.assembler;

import org.btr.ddd.demo.domain.user.User;
import org.btr.ddd.demo.interfaces.dto.user.UserInfo;

public interface UserAssembler
{
  static UserInfo toDTO(User aggregate)
  {
    return new UserInfo(aggregate.getUsername(), aggregate.getCreateAt(), aggregate.getUpdateAt());
  }
}
