package com.justin;

import org.apache.commons.io.FileUtils;
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
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ListMessagesResponse;
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
import java.util.Map;
import java.util.HashMap;
import java.lang.Character;


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
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);

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

        public void main(String[] args) throws IOException, GeneralSecurityException{
            // Delete token if found 
            resetToken();

            // Build a new authorized API client service.

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

            // Get messages from user's account based on parameters.
            // me here is a special value to indicate the current authenticated account.
            String user = "me";
            Scanner input = new Scanner(System.in); 
            Long maxResults = 500L;

            //initialize builder
            QString qString = new QString();

            //list of options to print here
            Map<String, String> options = qString.getNumOptions();

            options.put(String.valueOf(options.size() + 1), "Continue");
            options.put(String.valueOf(options.size() + 1), "Exit");

            //main menu
            Map<String, String> opInstructions = new HashMap<String, String>() {{
                put("6", "(Provide date in YYYY/MM/DD foramt)");
                put("7", "(Provide date in YYYY/MM/DD format)");
            }};

            while (true){
                System.out.println("Please select an option (Enter the number of the selection): ");
                options.entrySet().forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    System.out.println(key + ". " + value + ": " + qString.parameters.get(value));
                });
                
                String userChoice = input.nextLine();

                //User inputting query
                if (options.keySet().contains(userChoice)){
                    if (options.get(userChoice).equals("Continue")){
                        break;
                    } else if (options.get(userChoice).equals("Exit")) {
                            System.exit(0);
                    }

                    System.out.println(String.format("Please enter query for %s: ", options.getOrDefault(userChoice, "")));
                    System.out.println(opInstructions.getOrDefault(userChoice, ""));
                    System.out.println("(Leave input empty and hit enter to erase previous input)");

                    String queryInput = input.nextLine();
                    while (!validateInput(userChoice, queryInput)) { 
                        System.out.println("Invalid input: " + queryInput);
                        queryInput = input.nextLine();
                    }

                    //assign input to param 
                    if (validateInput(userChoice, queryInput)){
                        qString.addParam(options.get(userChoice), queryInput);
                    }

                } else if (userChoice.isEmpty()) {
                    // Do nothing 
                } else {
                    System.out.println("Invalid input: " + userChoice);
                }
            }

            //Build q string 
            String queryString = qString.buildQueryString();
            System.out.println("query: " + queryString);

            //Get messages according to query
            ListMessagesResponse ListMessages = service
                                                .users()
                                                .messages()
                                                .list(user)
                                                .setMaxResults(maxResults)
                                                .setQ(queryString)
                                                .execute();

            List<Message> messages = ListMessages.getMessages();

            if (messages == null){
                System.out.println("No messages found.");
                System.exit(0);
            }

            messages.forEach(msg -> {
                System.out.println(msg.getId());
            });


            //Erase token
            resetToken();
        }


    private static Boolean validateInput(String choice, String userInput) {
        //if empty input let it pass
        if (userInput.isEmpty() || userInput.replace(" ", "").length() == 0) { 
            return true;
        }

        //check date format for options 6 and 7
        if (choice.equals("6") || choice.equals("7")) { 
            if (userInput.length() == 10) {
                for (int i = 0; i < 10; i++){ 
                    if (i == 4 || i == 7) { 
                        if (userInput.charAt(i) != '/') {
                            return false;
                        }
                    } else {
                        if (!Character.isDigit(userInput.charAt(i))) { 
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        }
        //all other options
        return true;
        }

    
    private void resetToken(){
        File tokenDir = new File(TOKENS_DIRECTORY_PATH);
        if (tokenDir.exists()){
            FileUtils.cleanDirectory(tokenDir);
            FileUtils.deleteDirectory(tokenDir);
        }
    }
}
