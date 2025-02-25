package org.lets_play_be.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum UserRoleEnum {

   ROLE_ADMIN,
   ROLE_USER;

   private static final List<UserRoleEnum> VALUES = Arrays.asList(values());

   public static Optional<UserRoleEnum> findRole(String name) {
       if (name == null) return Optional.empty();

       return VALUES.stream().filter(role -> role.name().equalsIgnoreCase(name)).findFirst();
   }


}
