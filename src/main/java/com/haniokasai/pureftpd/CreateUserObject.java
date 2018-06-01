package com.haniokasai.pureftpd;

public class CreateUserObject {
               /*
             INSERT INTO `ftpd` (`User`, `status`, `Password`, `Uid`, `Gid`, `Dir`, `ULBandwidth`, `DLBandwidth`, `comment`, `ipaccess`, `QuotaSize`, `QuotaFiles`)
VALUES ('testuser', '1', MD5('password'), '2001', '2001', '/var/www', '0', '0', '', '*', '0', '0');
             */
    public String User;
    public String status="1";
    public String Password;
    public int Uid = 2001;
    public int Gid = 2001;
    public String dir;
    public int ULBandwidth = 0;
    public int DLBandwidth = 0;
    public String comment ="LibPureDBMySQL";
    public String ipaccess = "*";
    public int QuotaSize = 0;
    public int QuotaFiles = 0;

    /**
     * @param User1 ユーザ名
     * @param password1　パスワード
     * @param dir1　√フォルダ
     */
    public CreateUserObject(String User1,String password1,String dir1){
        User=User1;
        Password=password1;
        dir=dir1;
    }
}
