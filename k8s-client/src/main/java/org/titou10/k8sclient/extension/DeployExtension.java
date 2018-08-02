package org.titou10.k8sclient.extension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gradle.api.NamedDomainObjectCollection;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.titou10.k8sclient.utils.NameValue;

import groovy.lang.Closure;

@SuppressWarnings("rawtypes")
public class DeployExtension {

   // http://mrhaki.blogspot.com/2016/02/gradle-goodness-using-nested-domain.html

   private final Project                                      project;

   private NamedDomainObjectContainer<SubstitutionsExtension> substitutions;
   private Set<ActionExtension>                               actions = new HashSet<>();

   // ---------------
   // Constructeur
   // ---------------
   public DeployExtension(Project project) {
      this.project = project;
      this.substitutions = project.container(SubstitutionsExtension.class);
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
   // Helpers
   // ---------------
   @SuppressWarnings("unchecked")
   public List<NameValue<String, String>> toListVariables() {
      // return substitutions.stream().map(s -> new NameValue(s.getName(), s.getValue())).collect(Collectors.toList());
      // https://bugs.openjdk.java.net/browse/JDK-8047797
      List<NameValue<String, String>> variables = new ArrayList<>();
      for (SubstitutionsExtension se : substitutions) {
         variables.add(new NameValue(se.getName(), se.getValue()));
      }
      return variables;
   }

   // ---------------
   // Children
   // ---------------

   public void substitutions(Closure c) {
      substitutions.configure(c);
   }

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

   public NamedDomainObjectCollection<SubstitutionsExtension> getSubstitutions() {
      return substitutions;
   }

}
