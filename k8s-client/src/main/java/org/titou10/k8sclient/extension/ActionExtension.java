package org.titou10.k8sclient.extension;

import java.io.File;

import org.gradle.api.GradleException;

public class ActionExtension {

   private Strategy strategy;
   private File     yaml;

   // ---------------
   // toString()
   // ---------------
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("ActionExtension [strategy=");
      builder.append(strategy);
      builder.append(", yaml=");
      builder.append(yaml);
      builder.append("]");
      return builder.toString();
   }

   // ---------------
   // Getters/Setters
   // ---------------

   public void setStrategy(String strategy) {
      try {
         this.strategy = Strategy.valueOf(strategy);
      } catch (Exception e) {
         throw new GradleException("'" + strategy + "' is not a valid strategy.");
      }
   }

   public void setStrategy(Strategy strategy) {
      this.strategy = strategy;
   }

   public Strategy getStrategy() {
      return strategy;
   }

   public File getYaml() {
      return yaml;
   }

   public void setYaml(File yaml) {
      this.yaml = yaml;
   }

}
