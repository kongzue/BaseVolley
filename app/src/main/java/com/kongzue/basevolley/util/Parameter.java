package com.kongzue.basevolley.util;

import java.util.TreeMap;

public class Parameter extends TreeMap<String,String> {

    public Parameter add(String key, String value){
        put(key,value);
        return this;
    }

    public String toParameterString(){
        String result="";
        for (Entry<String, String> entry : entrySet()){
            result = result + entry.getKey() + "=" + entry.getValue() + "&";
        }
        if (result.endsWith("&")){
            result = result.substring(0,result.length()-1);
        }
        return result;
    }
}
