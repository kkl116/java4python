package com.justin;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Path;
import java.net.URL;
import java.io.File;


public class GmailAssistant {

    private static final String APPLICATION_NAME = "Gmail Assistant";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /*
     * directory to store authorization tokens 
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /*
     * singletonList: "Returns an immutable list containing only the specified object. The returned list is serializable."
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);

    /*
     * throws is indicating that this function can throw this exception, and so 
     * the caller has to handle the exception with a try catch block.
     *  - here the IOException happens if credentials.json is not found 
     * 
     * final before type of an argument means it cannot be changed within the function.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
        throws IOException {
            //Load client secrets.

            /*
             * getResourceAsStream vs getResource - as stream returns an inputstream for the resource
             * or null, vs getResource returns the URL or null
             * InputStream = ordered sequence of bytes, basically for reading data from file 
             * or receiving over a network
             */

            InputStream in =  GmailAssistant.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null){
                throw new FileNotFoundException("Resource not found " + CREDENTIALS_FILE_PATH);
            }

            /*
             * InputStreamReader converts bytes streams to character streams. Decodes bytes into 
             * chars using a specified charset
             */
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

                LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
                Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
                //returns an authorized Credential object.
                return credential;
        }

        public static void main(String[] args) throws IOException, GeneralSecurityException{
            // Build a new authorized API client service.

            /* 
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
            */

            // Get messages from user's account based on parameters.
            // me here is a special value to indicate the current authenticated account.
            String user = "me";
            Scanner input = new Scanner(System.in); 
            Integer maxResults = 500;
            //list of options to print here
            String path = System.getProperty("user.dir") + "/Statement 1 for £570.06.pdf";

            PDFProcessor.ExtractText(path);    
            
            /*
            while (true){
                System.out.println("Please select query parameter you wish to add: ")


                ListLabelsResponse listResponse = service.users().messages().list(user).execute();
                List<Label> messages = listResponse.getLabels();
            }
            */


    }

}
