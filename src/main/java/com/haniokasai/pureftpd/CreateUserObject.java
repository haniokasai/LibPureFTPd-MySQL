package com.haniokasai.pureftpd;

public class CreateUserObject {
               /*
             INSERT INTO `ftpd` (`User`, `status`, `Password`, `Uid`, `Gid`, `Dir`, `ULBandwidth`, `DLBandwidth`, `comment`, `ipaccess`, `QuotaSize`, `QuotaFiles`)
VALUES ('testuser', '1', MD5('password'), '2001', '2001', '/var/www', '0', '0', '', '*', '0', '0');
             */
    String User;
    String status="1";
    String Password;
    int Uid = 2001;
    int Gid = 2001;
    String dir;
    int ULBandwidth = 0;
    int DLBandwidth = 0;
    String comment ="LibPureDBMySQL";
    String ipaccess = "*";
    int QuotaSize = 0;
    int QuotaFiles = 0;

    /**
     * @param User1 ユーザ名
     * @param password1　パスワード
     * @param dir1　√フォルダ
     */
    CreateUserObject(String User1,String password1,String dir1){
        User=User1;
        Password=password1;
        dir=dir1;
    }
}
