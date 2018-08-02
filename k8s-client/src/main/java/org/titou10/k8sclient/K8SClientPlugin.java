package org.titou10.k8sclient;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.titou10.k8sclient.extension.DeployerExtension;
import org.titou10.k8sclient.task.DeployTask;

public class K8SClientPlugin implements Plugin<Project> {

   // https://github.com/fabric8io/kubernetes-client
   // https://github.com/fabric8io/kubernetes-model
   // https://github.com/fabric8io/kubernetes-client/blob/master/kubernetes-examples/src/main/java/io/fabric8/openshift/examples/TemplateExample.java

   private static final String K8SCLIENT_JAVA_CONFIGURATION_NAME = "clientk8s";
   private static final String K8SCLIENT_JAVA_DEFAULT_VERSION    = "0.10.0";
   private static final String DEFAULT_TASK_GROUP                = "titou10";
   private static final String EXTENSION_NAME                    = "deployer";
   private static final String TASK_NAME                         = "deploy";

   @Override
   public void apply(Project project) {

      final Configuration config = project.getConfigurations()
               .create(K8SCLIENT_JAVA_CONFIGURATION_NAME)
               .setVisible(false)
               .setTransitive(true)
               .setDescription("Private config");

      // if no repositories were defined fallback to buildscript repositories
      project.afterEvaluate(e ->
         {
            if (project.getRepositories().size() == 0) {
               project.getRepositories().addAll(project.getBuildscript().getRepositories());
            }
         });

      config.defaultDependencies(new Action<DependencySet>() {
         public void execute(DependencySet dependencies) {
            dependencies.add(project.getDependencies().create("io.fabric8:kubernetes-client:4.0.3"));
         }
      });

      // Parameter parsing
      DeployerExtension extension = project.getExtensions().create(EXTENSION_NAME, DeployerExtension.class, project);

      // Task creation
      DeployTask deployTask = project.getTasks().create(TASK_NAME, DeployTask.class);
      deployTask.setDescription("Deploy assets stored in yaml files to kubernetes via k8s API");
      deployTask.setGroup(DEFAULT_TASK_GROUP);

      deployTask.setAuth(extension.getAuth());
      deployTask.setSsl(extension.getSsl());
      deployTask.setK8sServerURL(extension.getK8sServerURL());
      deployTask.setDeploy(extension.getDeploy());
   }
}
