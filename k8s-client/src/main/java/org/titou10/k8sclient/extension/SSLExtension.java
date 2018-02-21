package org.titou10.k8sclient.extension;

public class SSLExtension {

   private boolean trustAll;
   private String  certificate;

   // ---------------
   // toString()
   // ---------------

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder(256);
      builder.append("SSLExtension [trustAll=");
      builder.append(trustAll);
      builder.append(", certificate=");
      builder.append(certificate);
      builder.append("]");
      return builder.toString();
   }

   // ---------------
   // Getters/Setters
   // ---------------

   public boolean isTrustAll() {
      return trustAll;
   }

   public void setTrustAll(boolean trustAll) {
      this.trustAll = trustAll;
   }

   public String getCertificate() {
      return certificate;
   }

   // public void setCertificate(File certificateFile) throws ResourceConfigException, EnvironmentConfigException {
   // String fileContent = FileUtil.readFileContent(certificateFile);
   // if (!fileContent.isEmpty()) {
   // this.certificate = fileContent;
   // } else {
   // throw new EnvironmentConfigException(PLUGIN_MESSAGE_BUNDLE
   // .getMessage("DEPLOYER_PLUGIN_ERROR_RETRIEVING_CERTIFICATE_FROM_FILE", certificateFile.getName()));
   // }
   // }
}
