package com.justin;

import java.util.Map;
import java.util.LinkedHashMap;

public class QString {

    //Using linked hashmap here because when you iterate over it it preserves the insertion order
    public Map<String, String> parameters = new LinkedHashMap<String, String>() {{
        put("sender", "");
        put("recipient", "");
        put("subject", "");
        put("filename", "");
        put("phrase", "");
        put("after", "");
        put("before", "");
    }};

    public Map<String, String> getNumOptions() { 
        Map<String, String> numOptions = new LinkedHashMap<String, String>();
        int i = 1;
        for (String key: parameters.keySet()){
            numOptions.put(String.valueOf(i), key);
            i++;
        }

        return numOptions;
    }

    public void addParam(String paramKey, String input) {
        parameters.put(paramKey, input);
    }

    public String buildQueryString() {
        String queryString = "";

        for (Map.Entry<String, String> entry: parameters.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            if (value.length() > 0) { 
                queryString = queryString + String.format("%s: %s ", field, value);
            }
        }

        return queryString;
    }

}
