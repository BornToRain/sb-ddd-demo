package org.btr.ddd.demo.interfaces.assembler;

import org.btr.ddd.demo.domain.user.User;
import org.btr.ddd.demo.infrastructure.ApiConstant;
import org.btr.ddd.demo.interfaces.api.UserApi;
import org.btr.ddd.demo.interfaces.dto.user.UserInfo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public interface UserAssembler
{
  static UserInfo toDTO(User aggregate)
  {
    return new UserInfo(aggregate.getId(), aggregate.getUsername(), aggregate.getCreateAt(), aggregate.getUpdateAt());
  }

  static UserInfo addResource(UserInfo d)
  {
    d.add(linkTo(UserApi.class).slash(d.id).withSelfRel());
    d.add(linkTo(UserApi.class).slash(d.id).withRel(ApiConstant.REL_DELETE));

    return d;
  }

  static UserInfo addInfoResource(UserInfo d)
  {
    addResource(d).add(linkTo(UserApi.class).slash(d.id).withRel(ApiConstant.REL_EDIT));

    return d;
  }

  static UserInfo addEditResource(UserInfo d)
  {
    addResource(d).add(linkTo(UserApi.class).slash(d.id).withRel(ApiConstant.REL_INFO));

    return d;
  }
}
