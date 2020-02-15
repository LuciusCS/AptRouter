package com.example.glidemodule.resources;

import com.example.glidemodule.utils.Tool;

public class Key {

    private String key;  //例如  ac43474d52403e60fe21894520a67d6f417a6868994c145eeb26712472a78311

    /**
     * 加密前 sha256 (https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy90cm01Vk1lRnA5blVESWdHaWJkVnlFSnk3MmlieVdXOElDWGlhamZDNlliNTc3YnVRTUdtYmUxR1hxSmhwVjNvUzhyWktENTIzODNVZDFpYkh6VTJDakxrUUEvNjQw?x-oss-process=image/format,png)
     * 加密后  ac43474d52403e60fe21894520a67d6f417a6868994c145eeb26712472a78311
     * @param key
     */

    public Key(String key) {
        this.key = Tool.getSHA256StrJava( key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
