package com.example.selfRadioPosting.util;

import java.io.File;
import java.io.IOException;

public class FileManager {
    public static void deleteFile(String filePath) throws IOException{
        File file = new File(filePath);
        file.delete();
    }
    public static String getExtension(String filePath){
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }
}
