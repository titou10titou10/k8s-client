package org.titou10.k8sclient.extension;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

import groovy.lang.Closure;

@SuppressWarnings("rawtypes")
public class DeployerExtension {

   private final Project                   project;

   private final Property<String>          k8sServerURL;
   private final Property<SSLExtension>    ssl;
   private final Property<AuthExtension>   auth;
   private final Property<DeployExtension> deploy;

   // ---------------
   // Constructeur
   // ---------------

   public DeployerExtension(Project project) {
      this.project = project;
      this.k8sServerURL = project.getObjects().property(String.class);
      this.ssl = project.getObjects().property(SSLExtension.class);
      this.auth = project.getObjects().property(AuthExtension.class);
      this.deploy = project.getObjects().property(DeployExtension.class);
   }

   // ---------------
   // toString()
   // ---------------

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("DeployerExtension [k8sServerURL=");
      builder.append(k8sServerURL);
      builder.append(", ssl=");
      builder.append(ssl);
      builder.append(", auth=");
      builder.append(auth);
      builder.append(", deploy=");
      builder.append(deploy);
      builder.append("]");
      return builder.toString();
   }

   // ---------------
   // Enfants
   // ---------------
   public void auth(Closure c) {
      AuthExtension authExtension = new AuthExtension();
      auth.set(authExtension);
      project.configure(authExtension, c);
   }

   public void ssl(Closure c) {
      SSLExtension sslExtension = new SSLExtension();
      ssl.set(sslExtension);
      project.configure(sslExtension, c);
   }

   public void deploy(Closure c) {
      DeployExtension deployExtension = new DeployExtension(project);
      deploy.set(deployExtension);
      project.configure(deployExtension, c);
   }

   // -------
   // Getters
   // -------
   public Property<SSLExtension> getSsl() {
      return ssl;
   }

   public Property<AuthExtension> getAuth() {
      return auth;
   }

   public Property<DeployExtension> getDeploy() {
      return deploy;
   }

   public Property<String> getK8sServerURL() {
      return k8sServerURL;
   }

}
