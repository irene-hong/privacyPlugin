package com.cmu.privacyplugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtility {

    String basePath = "";
    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "http://18.191.10.209:8000/result/chenlyu/BankingSystem-Backend.zip";


    public void save(String path){
        basePath = path;
    }
    public int sendGET() throws IOException {
        URL obj = new URL(GET_URL);
        File f = new File(basePath+"/.privado/privado.json");
        if(f.exists() && !f.isDirectory()){
            f.delete();
        }
        FileWriter writer = new FileWriter(basePath+ "/.privado/privado.json", true);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK ) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                writer.write(inputLine);
            }
            in.close();
            writer.close();
            // print result
        }
        return responseCode;
    }
}
