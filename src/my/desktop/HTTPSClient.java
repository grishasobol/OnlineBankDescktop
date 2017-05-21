package my.desktop; 

import java.io.BufferedReader; 
import java.io.FileInputStream; 
import java.io.IOException;
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.io.OutputStream; 
import java.io.OutputStreamWriter; 
import java.io.PrintWriter; 
import java.security.KeyManagementException;
import java.security.KeyStore; 
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager; 
import javax.net.ssl.KeyManagerFactory; 
import javax.net.ssl.SSLContext; 
import javax.net.ssl.SSLSession; 
import javax.net.ssl.SSLSocket; 
import javax.net.ssl.SSLSocketFactory; 
import javax.net.ssl.TrustManager; 
import javax.net.ssl.TrustManagerFactory; 

/**
 *
 * @author Dmitry
 */
public class HTTPSClient { 
    private String host = "127.0.0.1";//"192.168.43.120"; 
    private int port = 8000; 
    public String status = "Connection is not established.";

    BufferedReader bufferedReader = null; 
    PrintWriter printWriter = null; 

    public HTTPSClient() { 
        SSLContext sslContext = this.createSSLContext(); 

        try { 
            // Create socket factory 
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory(); 

            // Create socket 
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(this.host, this.port); 

            System.out.println("SSL client started"); 
            try { 
                // Start handshake 
                sslSocket.startHandshake(); 

                // Get session after the connection is established 
                SSLSession sslSession = sslSocket.getSession(); 

                System.out.println("SSLSession :"); 
                System.out.println("\tProtocol : " + sslSession.getProtocol()); 
                System.out.println("\tCipher suite : " + sslSession.getCipherSuite()); 

                // Start handling application content 
                InputStream inputStream = sslSocket.getInputStream(); 
                OutputStream outputStream = sslSocket.getOutputStream(); 

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); 
                printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
                
                status = "Successful connection with the manager is made.";
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
        } catch (IOException ex) { 
            ex.printStackTrace(); 
        } 
    } 

    public HTTPSClient(String host, int port) { 
        this(); 
        this.host = host; 
        this.port = port; 
    } 
    
    public void send(String str){
        printWriter.println(str);
        printWriter.flush();
    }
    
    public String receive() throws IOException{
        return bufferedReader.readLine();
    }

    private SSLContext createSSLContext() { 
        try { 
            KeyStore keyStore = KeyStore.getInstance("JKS"); 
            keyStore.load(new FileInputStream("testkey.jks"), "password".toCharArray()); 

            // Create key manager 
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509"); 
            keyManagerFactory.init(keyStore, "password".toCharArray()); 
            KeyManager[] km = keyManagerFactory.getKeyManagers(); 

            // Create trust manager 
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509"); 
            trustManagerFactory.init(keyStore); 
            TrustManager[] tm = trustManagerFactory.getTrustManagers(); 

            // Initialize SSLContext 
            SSLContext sslContext = SSLContext.getInstance("TLSv1"); 
            sslContext.init(km, tm, null); 

            return sslContext; 
        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException ex) { 
            ex.printStackTrace(); 
        } 
        return null; 
    } 
}