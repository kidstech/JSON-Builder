package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONTest {

    public static void main (String [] args) {
        Gson gsonBuilder = new GsonBuilder().create();
        Map personMap = new HashMap();
        Map subMap = new HashMap();
        List<String> qwe = new ArrayList<>();
        qwe.add("Bandit");
        qwe.add("Sadie");
        personMap.put("lastName", qwe);
        subMap.put("firstname", "MAAA");
        personMap.put("firstName", subMap);
        String jsonFromJavaMap = gsonBuilder.toJson(personMap);

        System.out.println(jsonFromJavaMap);
    }



}
