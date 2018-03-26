package com.example.mudit.projecttracker;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * Created by Mudit on 25-03-2018.
 */

public class fileContentmanager {
    private String Ctitle, CNumber, IName, Pnumber, ProjectDesc,dateview, status;

    public fileContentmanager(File file){
        byte[] bytes ;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int x = fileInputStream.available();
            bytes = new byte[x];
            fileInputStream.read(bytes);
            fileInputStream.close();
            String temp = Arrays.toString(bytes);
            String text[]=temp.split(";");
            Ctitle=text[0];
            CNumber=text[1];
            IName=text[2];
            Pnumber=text[3];
            ProjectDesc=text[4];
            dateview=text[5];
            status=text[6];
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public String getCNumber() {
        return CNumber;
    }

    public String getCtitle() {
        return Ctitle;
    }

    public String getDateview() {
        return dateview;
    }

    public String getIName() {
        return IName;
    }

    public String getPnumber() {
        return Pnumber;
    }

    public String getProjectDesc() {
        return ProjectDesc;
    }

    public String getStatus() {
        return status;
    }

    public String getSummary() {
        return "Course Title: "+Ctitle+"\nCourse Number: "+CNumber+"\nInstructor: "+IName+"\nProject Description:\n"+ProjectDesc+
                "\nDue Date:"+dateview+"\nStatus: "+status;
    }
}
