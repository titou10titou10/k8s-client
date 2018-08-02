package org.titou10.k8sclient.utils;

import java.util.List;

public final class Utils {

   public static String replaceVariables(List<NameValue<String, String>> variables, String originalText) {
      if (variables.isEmpty()) {
         return originalText;
      }
      if (Utils.isEmpty(originalText)) {
         return originalText;
      }

      StringBuilder tag1 = new StringBuilder(32);
      StringBuilder tag2 = new StringBuilder(32);

      String res = originalText;

      // For each possible variable
      for (NameValue<String, String> v : variables) {
         tag2.setLength(0);
         tag2.append(buildVariableDisplayName(v.getName()));
         if (res.contains(tag2)) {
            tag1.setLength(0);
            tag1.append(buildVariableReplaceName(v.getName()));
            res = res.replaceAll(tag1.toString(), v.getValue());
         }
      }
      return res;
   }

   public static boolean isEmpty(final String s) {
      return s == null || s.trim().length() == 0;
   }

   // -------
   // Helpers
   // -------
   private static String buildVariableDisplayName(String name) {
      StringBuilder sb = new StringBuilder(64);
      sb.append("${");
      sb.append(name);
      sb.append("}");
      return sb.toString();
   }

   private static String buildVariableReplaceName(String name) {
      StringBuilder sb = new StringBuilder(64);
      sb.append("\\$\\{");
      sb.append(name);
      sb.append("\\}");
      return sb.toString();
   }

   private Utils() {
      // NOP
   }
}
