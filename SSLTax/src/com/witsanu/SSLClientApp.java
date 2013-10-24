package com.witsanu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author Witsanu
 *
 */
public class SSLClientApp {
	
	private static final String endPoint = "https://rdws.rd.go.th/ServiceRD/CheckTINPINService.asmx";

	public static void main(String[] args) {
		
		// certificate_xxxx.cer
		String cfilepath = args[0];
		
		
		InputStream in = null ;
		
		try {
			
			//// custom SSL part ///////////////////
			
			in = new FileInputStream(cfilepath);
			
			// get public key from certificate
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate caCert = (X509Certificate)cf.generateCertificate(in);
			
			// create keyStore instance
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			// for JVM to load default key store , By default will not exist 
			keystore.load(null, null);
			// match key store to public key from   certificate's provider
			keystore.setCertificateEntry("caCert", caCert);
			
			// put keyStore to sslContext
			SSLContext sslContext =  SSLContexts.custom().loadTrustMaterial(keystore).build() ;	
			
			// link custom ssl context to  ssl factory 
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			
			/////////// END SSL Part ///////////////
			
			
			/**
			 * Inial POST
			 */
			
			String soapString = getSOAPRequst();
			
			HttpPost post = new  HttpPost(endPoint);
			HttpEntity IEntity = new StringEntity(soapString, ContentType.TEXT_XML);
			post.setEntity(IEntity) ;
			
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			CloseableHttpResponse response  = httpclient.execute(post);
			
			HttpEntity entity = response.getEntity();
			
            if (entity != null) {
               String soapStringResp = EntityUtils.toString(entity,"UTF-8");
               
               
               /**
                * Process XML to Object by soapStringResp
                */
               
               
            }
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getSOAPRequst(){
		StringBuilder soapString = new StringBuilder();
		soapString.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		soapString.append("<SOAP-ENV:Body>");
		soapString.append("<ns:ServicePIN xmlns:ns=\"https://rdws.rd.go.th/ServiceRD/CheckTINPINService\">");
		soapString.append("<ns:username>anonymous</ns:username>");
		soapString.append("<ns:password>anonymous</ns:password>");
		soapString.append("<ns:PIN>1180200031659</ns:PIN>");
		soapString.append("</ns:ServicePIN>");
		soapString.append("</SOAP-ENV:Body>");
		soapString.append("</SOAP-ENV:Envelope>");
		return soapString.toString();
	}
	
}
