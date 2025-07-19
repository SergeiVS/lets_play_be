package org.lets_play_be.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum UserRoleEnum {

   ROLE_ADMIN,
   ROLE_USER;

   private static final List<UserRoleEnum> ROLE_ENUMS = Arrays.asList(values());

   public static Optional<UserRoleEnum> findRole(String name) {
       if (name == null) return Optional.empty();

       return ROLE_ENUMS.stream()
               .filter(role -> role.name()
                       .equalsIgnoreCase(name))
               .findFirst();
   }


}
