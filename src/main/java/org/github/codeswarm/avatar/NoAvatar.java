package org.github.codeswarm.avatar;

public class NoAvatar extends AvatarFetcher {

   public NoAvatar() {
      super();
   }

   @Override
   public String fetchUserImage(String username) {
      return null;
   }
}
