package org.titou10.k8sclient.extension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AuthExtension {

   private String token;

   // ---------------
   // toString()
   // ---------------

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("AuthExtension [token=");
      builder.append(token);
      builder.append("]");
      return builder.toString();
   }

   // ---------------
   // Getters/Setters
   // ---------------

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public void setToken(File fileWithToken) throws IOException {
      this.token = (Files.readAllLines(fileWithToken.toPath())).get(0);
   }

}
