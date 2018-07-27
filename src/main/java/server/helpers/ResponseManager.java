package server.helpers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class ResponseManager implements IResponseManager {

    public static IResponseManager create() {
        return new ResponseManager();
    }

    private ResponseManager() {}

    @Override
    public void executeResponse(HttpExchange httpExchange, String response) throws IOException {
        byte[] bytes = response.getBytes();
        httpExchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes("utf-8"));
        os.close();
    }

    @Override
    public Map<String,String> getInput(HttpExchange httpExchange) throws IOException {

        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String data = br.readLine();
        Map<String,String> map = new HashMap<>();
        System.out.println("parser: " + data);
        String[] pairs = data.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

    @Override
    public Map parseEditData(HttpExchange httpExchange) throws IOException {
        Map<String,String> map = new HashMap<>();
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String data = br.readLine();
        String[] pairs = data.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2) {
                String value = URLDecoder.decode(keyValue[1], "UTF-8");
                map.put(keyValue[0], value);
            }
        }
        return map;
    }

    @Override
    public void redirectToStudentArtifact(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/mentor/show_student_artifacts");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }

    @Override
    public void redirectToLogin(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }



}
