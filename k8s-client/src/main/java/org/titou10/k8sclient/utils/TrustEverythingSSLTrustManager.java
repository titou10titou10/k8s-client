package org.titou10.k8sclient.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Trust manager that accepts all certificates. Big Security Hole !!!
 * 
 * @author Denis Forveille
 *
 */
public class TrustEverythingSSLTrustManager implements X509TrustManager {
   @Override
   public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      // NOP
   }

   @Override
   public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      // NOP
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
   }

}
