package org.titou10.k8sclient.task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.titou10.k8sclient.extension.ActionExtension;
import org.titou10.k8sclient.extension.AuthExtension;
import org.titou10.k8sclient.extension.DeployExtension;
import org.titou10.k8sclient.extension.SSLExtension;
import org.titou10.k8sclient.extension.Strategy;
import org.titou10.k8sclient.utils.NameValue;
import org.titou10.k8sclient.utils.TrustEverythingSSLTrustManager;
import org.titou10.k8sclient.utils.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * 
 * https://github.com/qaware/gradle-cloud-deployer/blob/master/deployer-commons/src/main/java/de/qaware/cloud/deployer/commons/resource/ClientFactory.java
 * 
 * @author forde000
 *
 */

public class DeployTask extends DefaultTask {

   private static final String       YAML_SEPARATOR = "---";
   private static final String       YAML_EOL       = "\n";
   private static final String       YAML_ENCODING  = "UTF-8";

   private static final String       S1             = "Processing component '%s' of kind '%s' in namespace '%s' ('%s')";

   private Property<String>          k8sServerURL;
   private Property<SSLExtension>    ssl;
   private Property<AuthExtension>   auth;
   private Property<DeployExtension> deploy;

   @TaskAction
   public void deploy() {

      System.out.println(" ");
      System.out.println("= Debug==============================");
      System.out.println("k8sServerURL : " + k8sServerURL.get());
      System.out.println("ssl          : " + ssl.get());
      System.out.println("auth         : " + auth.get());
      System.out.println("deploy       : " + deploy.get());
      System.out.println("substitutions: " + deploy.get().getSubstitutions().getNames());
      System.out.println("=====================================");
      System.out.println(" ");

      // Bypass certificate check + Host name validation
      if ((ssl != null) && (ssl.get() != null) && (ssl.get().isTrustAll())) {
         relaxSSLSecurity();
      }

      // Tell k8s Client to use a bearer token
      System.setProperty("kubernetes.auth.tryServiceAccount", "true");
      System.setProperty("kubernetes.auth.token", auth.get().getToken());

      Config config = new ConfigBuilder().withMasterUrl(k8sServerURL.get()).build();
      try (KubernetesClient client = new DefaultKubernetesClient(config);) {

         List<NameValue<String, String>> variables = deploy.get().toListVariables();

         // Execute all actions
         for (ActionExtension action : deploy.get().getActions()) {

            Strategy strategy = action.getStrategy();
            File yamlFile = action.getYaml();
            String yamlFileName = yamlFile.getCanonicalPath();

            if (!yamlFile.exists()) {
               throw new TaskExecutionException(this, new GradleException("File '" + yamlFileName + "' not found"));
            }

            // Parse Yaml
            System.out.println("Parsing file '" + yamlFileName + "'");

            // Split file on "---"
            List<StringBuffer> splittedFiles = new ArrayList<>(); // List of files
            StringBuffer currentFile = new StringBuffer(1024);
            List<String> yamlFileAsStrings = Files.readAllLines(yamlFile.toPath());
            for (String line : yamlFileAsStrings) {
               if (!line.startsWith(YAML_SEPARATOR)) {
                  currentFile.append(Utils.replaceVariables(variables, line)).append(YAML_EOL);
                  continue;
               }
               splittedFiles.add(currentFile);
               currentFile = new StringBuffer(1024);
            }
            splittedFiles.add(currentFile);
            System.out.println("'" + splittedFiles.size() + "' components found in input file");

            // Iterate on split files
            for (StringBuffer fileAsString : splittedFiles) {
               try (InputStream is = new ByteArrayInputStream(fileAsString.toString().getBytes(YAML_ENCODING));) {

                  ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                  HasMetadata metaData = mapper.readValue(is, HasMetadata.class);

                  String kind = metaData.getKind();
                  ObjectMeta om = metaData.getMetadata();
                  String nameSpaceName = om.getNamespace();
                  String name = om.getName();

                  System.out.println(String.format(S1, name, kind, nameSpaceName, strategy));

                  is.reset(); // Reposition InputStream at the beginning

                  switch (kind) {
                     case "Namespace":
                        switch (strategy) {
                           case CREATE:
                              System.out.println("Namespace created: " + client.namespaces().load(is).create());
                              break;
                           case DELETE:
                              System.out.println("Namespace deleted:" + client.namespaces().withName(name).delete());
                              break;
                           case REPLACE:
                              Namespace namespace = client.namespaces().withName(name).get();
                              if (namespace != null) {
                                 client.namespaces().withName(name).delete();
                              }
                              System.out.println("Namespace replaced: " + client.namespaces().load(is).create());
                              break;
                        }

                        break;

                     case "Service":
                        switch (strategy) {
                           case CREATE:
                              System.out.println("Service created: " + client.services().load(is).create());
                              break;
                           case DELETE:
                              System.out.println("Service deleted:"
                                                 + client.services().inNamespace(nameSpaceName).withName(name).delete());
                              break;
                           case REPLACE:
                              Service service = client.services().inNamespace(nameSpaceName).withName(name).get();
                              if (service != null) {
                                 client.services().inNamespace(nameSpaceName).withName(name).delete();
                              }
                              System.out.println("Service replaced: " + client.services().load(is).create());
                              break;
                        }
                        break;

                     case "Secret":
                        switch (strategy) {
                           case CREATE:
                              System.out.println("Secret created: " + client.secrets().load(is).create());
                              break;
                           case DELETE:
                              System.out.println("Secret deleted: "
                                                 + client.secrets().inNamespace(nameSpaceName).withName(name).delete());
                              break;
                           case REPLACE:
                              Secret secret = client.secrets().inNamespace(nameSpaceName).withName(name).get();
                              if (secret != null) {
                                 client.secrets().inNamespace(nameSpaceName).withName(name).delete();
                              }
                              System.out.println("Secret replaced: " + client.secrets().load(is).create());
                              break;
                        }
                        break;

                     case "ConfigMap":
                        switch (strategy) {
                           case CREATE:
                              System.out.println("ConfigMap created: " + client.configMaps().load(is).create());
                              break;
                           case DELETE:
                              System.out.println("ConfigMap deleted: "
                                                 + client.configMaps().inNamespace(nameSpaceName).withName(name).delete());
                              break;
                           case REPLACE:
                              ConfigMap configMap = client.configMaps().inNamespace(nameSpaceName).withName(name).get();
                              if (configMap != null) {
                                 client.configMaps().inNamespace(nameSpaceName).withName(name).delete();
                              }
                              System.out.println("ConfigMap replaced: " + client.configMaps().load(is).create());
                              break;
                        }
                        break;

                     case "Deployment":
                        switch (strategy) {
                           case CREATE:
                              System.out.println("Deployment created: " + client.apps().deployments().load(is).create());
                              break;
                           case DELETE:
                              System.out.println("Deployment deleted: "
                                                 + client.apps().deployments().inNamespace(nameSpaceName).withName(name).delete());
                              break;
                           case REPLACE:
                              Deployment deployment = client.apps().deployments().inNamespace(nameSpaceName).withName(name).get();
                              if (deployment != null) {
                                 client.apps().deployments().inNamespace(nameSpaceName).withName(name).delete();
                              }
                              System.out.println("Deployment replaced: " + client.apps().deployments().load(is).create());
                              break;
                        }
                        break;

                     default:
                        throw new TaskExecutionException(this,
                                                         new GradleException("Component of kind '" + kind
                                                                             + "' not (yet) supported by the plugin"));
                  }
                  System.out.println(" ");
               }
            }
         }
      } catch (IOException e) {
         throw new TaskExecutionException(this, e);
      }

      System.out.println(" ");
   }

   // ---------------
   // Helpers
   // ---------------

   private void relaxSSLSecurity() {
      try {
         // Accept all Untrust Certificates
         SSLContext ctx = SSLContext.getInstance("TLS");
         ctx.init(null, new TrustManager[] { new TrustEverythingSSLTrustManager() }, null);
         SSLContext.setDefault(ctx);

         // Disable Host Name verification
         HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
         HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
               return true;
            }
         };
         HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

         System.out.println("!!! Using the TrustEverythingSSLTrustManager TrustManager: No server certificate will be validated");
         System.out.println(" ");
      } catch (KeyManagementException | NoSuchAlgorithmException e) {
         throw new TaskExecutionException(this, e);
      }
   }

   // ---------------
   // Getters/Setters
   // ---------------

   public Property<String> getK8sServerURL() {
      return k8sServerURL;
   }

   public void setK8sServerURL(Property<String> k8sServerURL) {
      this.k8sServerURL = k8sServerURL;
   }

   public void setSsl(Property<SSLExtension> ssl) {
      this.ssl = ssl;
   }

   public void setAuth(Property<AuthExtension> auth) {
      this.auth = auth;
   }

   public void setDeploy(Property<DeployExtension> deploy) {
      this.deploy = deploy;
   }

}
