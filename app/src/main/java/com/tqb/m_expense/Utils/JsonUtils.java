package com.tqb.m_expense.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import kotlin.NotImplementedError;

public class JsonUtils {
    public static class Response extends JSONObject {
        private boolean success;

        public Response() {
        }

        public void setContent(String json) throws JSONException {
            super.put("content", new JSONObject(json));
        }
        public JSONObject getContent() throws JSONException {
            return super.getJSONObject("content");
        }
        public boolean isSuccess() throws JSONException {
            return this.getBoolean("success");
        }

        public void setSuccess(boolean success) throws JSONException {
            this.success = success;
            this.put("success", success);
        }
    }
    public static class Request {
        private final URL url;
        public Request(String url) throws MalformedURLException {
            this.url = new URL(url);
        }
        public Response post(JSONObject payload) {
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                Response response = new Response();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream output = connection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(output,
                        StandardCharsets.UTF_8);
                writer.write(payload.toString());
                writer.close();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    response.setContent(builder.toString());
                    response.setSuccess(true);
                } else {
                    response.setSuccess(false);
                }
                connection.disconnect();
                return response;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        public Response get() {
            throw new NotImplementedError("Still not implement yet");
        }
    }
}
