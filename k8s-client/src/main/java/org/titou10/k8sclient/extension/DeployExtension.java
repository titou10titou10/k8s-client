package org.titou10.k8sclient.extension;

import java.util.HashSet;
import java.util.Set;

import org.gradle.api.Project;

import groovy.lang.Closure;

@SuppressWarnings("rawtypes")
public class DeployExtension {

   private final Project        project;

   private Set<ActionExtension> actions = new HashSet<>();

   // ---------------
   // Constructeur
   // ---------------
   public DeployExtension(Project project) {
      this.project = project;
   }

   // ---------------
   // toString()
   // ---------------
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("DeployExtension [actions=");
      builder.append(actions);
      builder.append("]");
      return builder.toString();
   }

   // ---------------
   // Enfants
   // ---------------
   public void action(Closure c) {
      ActionExtension a = new ActionExtension();
      actions.add(a);
      project.configure(a, c);
   }

   // -------
   // Getters
   // -------
   public Set<ActionExtension> getActions() {
      return actions;
   }
}
