/****************************
 ListViewAdapterData.java
 작성 팀 : Hello World!
 주 작성자 : 안현종
 프로그램명 : Medication Helper
 ***************************/
package com.example.smartmedicationmanager;

public class ListViewAdapterData {

    private String uName;
    private String uBirth;
    private String uGender;

    public void setuName(String uName){this.uName = uName;}
    public void setuBirth(String uBirth){this.uBirth = uBirth;}
    public void setuGender(String uGender){this.uGender = uGender;}

    public String getuName(){return this.uName;}
    public String getuBirth(){return this.uBirth;}
    public String getuGender(){return this.uGender;}



}
