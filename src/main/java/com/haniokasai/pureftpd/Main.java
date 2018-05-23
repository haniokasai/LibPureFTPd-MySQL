package com.haniokasai.pureftpd;

public class Main {

    public static void main(String args[]){
        LibPureDBMySQL ftpmanager = new LibPureDBMySQL("localhost","3306","ftpdb","ftpd",false,"pureftpd","ftpdpass");
        CreateUserObject c = new CreateUserObject("test", "tes", "/test");
        System.out.print(ftpmanager.CreateNewUser(c));

    }

}
