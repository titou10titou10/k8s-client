Plugin gradle to parse kubernetes yaml files and create the components in k8s

THis is highly a work in progress...

Example usage:

```
buildscript {
   repositories { ... }
    dependencies { 
       classpath "org.titou10.k8sclient:k8s-client:0.9.0" 
       classpath "io.fabric8:kubernetes-client:3.1.10"
    }
}

apply plugin: 'org.titou10.k8s-deployer'

deployer {

   k8sServerURL = "https://<k8s host>:<k8s port>"
   auth { token = file("/k8s_token.txt") }
   ssl { trustAll = true }
   
   deploy {
      action {
         yaml = file("service.yml")
         strategy = "REPLACE"
      }
      action {
         yaml = file("configMap_wlp.yml")
         strategy = "REPLACE"
      }
      action {
         yaml = file(ymlPath + "secrets.yml")
         strategy = "REPLACE"
      }
      action {
         yaml = file(ymlPath + "deployment.yml")
         strategy = "REPLACE"
      }
   }
}
```
