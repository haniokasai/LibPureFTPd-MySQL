package com.haniokasai.pureftpd;


import com.mysql.cj.mysqlx.protobuf.MysqlxDatatypes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Objects;

public class LibPureDBMySQL {


    /*
    MariaDB [(none)]> USE pureftpd;

MariaDB [pureftpd]> CREATE TABLE ftpd (
User varchar(16) NOT NULL default '',
status enum('0','1') NOT NULL default '0',
Password varchar(64) NOT NULL default '',
Uid varchar(11) NOT NULL default '-1',
Gid varchar(11) NOT NULL default '-1',
Dir varchar(128) NOT NULL default '',
ULBandwidth smallint(5) NOT NULL default '0',
DLBandwidth smallint(5) NOT NULL default '0',
comment tinytext NOT NULL,
ipaccess varchar(15) NOT NULL default '*',
QuotaSize smallint(5) NOT NULL default '0',
QuotaFiles int(11) NOT NULL dSHA2efault 0,
PRIMARY KEY (User),
UNIQUE KEY User (User)
) ENGINE=InnoDB;
     */

    private String address;
    private String port;
    private String database;
    private String table;
    private boolean enablessl;
    private String user;
    private String password;

    private Connection conn =null;
    private String connname;

    /**
     * @param address1 MySQLアドレス
     * @param port1 MySQLポート番号
     * @param database1 MySQLデータベース名　値の型を確認しない。
     * @param table1 MySQLテーブル名
     * @param enablessl1 SSLをどうするか
     * @param user1　MySQLアカウントのユーザ名
     * @param password1 MySQLアカウントのパスワード
     */
    LibPureDBMySQL(String address1 ,String port1 ,String database1 ,String table1 ,boolean enablessl1 ,String user1 ,String password1){
        start(address1 ,port1 ,database1 ,table1 ,enablessl1 ,user1 ,password1);
    }

    /**
     * @param address1 MySQLアドレス
     * @param port1 MySQLポート番号
     * @param database1 MySQLデータベース名
     * @param table1 MySQLテーブル名 値の型を確認しない。
     * @param enablessl1 SSLをどうするか
     * @param user1　MySQLアカウントのユーザ名
     * @param password1 MySQLアカウントのパスワード
     * @return 成功したらtrue。
     */
    private boolean start(String address1, String port1, String database1, String table1, boolean enablessl1, String user1, String password1) {
        address=address1;
        port=port1;
        database=database1;
        table=table1;
        enablessl=enablessl1;
        user=user1;
        password=password1;

        if (!table.matches("^[0-9a-zA-Z_]+$")) {
            try {
                throw new SymbolContainException("tablename must be numeric,alphabet,and _");
            } catch (SymbolContainException e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connname = "jdbc:mysql://" + address + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useSSL="+(enablessl?"true&verifyServerCertificate=false":"false")+"&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            //System.out.print(connname);
            conn = DriverManager.getConnection(connname, user, password);

            String sql = "CREATE TABLE IF NOT EXISTS "+table+" (User varchar(16) NOT NULL default '',status enum('0','1') NOT NULL default '0',Password varchar(64) NOT NULL default '',Uid varchar(11) NOT NULL default '-1',Gid varchar(11) NOT NULL default '-1',Dir varchar(128) NOT NULL default '',ULBandwidth smallint(5) NOT NULL default '0',DLBandwidth smallint(5) NOT NULL default '0',comment tinytext NOT NULL,ipaccess varchar(15) NOT NULL default '*',QuotaSize smallint(5) NOT NULL default '0',QuotaFiles int(11) NOT NULL default 0,PRIMARY KEY (User),UNIQUE KEY User (User)) ENGINE=InnoDB;";
            //System.out.println(sql);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(10);
            int count = stmt.executeUpdate();
            stmt.close();
            return (count > 0); // <-- something like this.
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 終了する。
     */
    public void shutdown(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return 再読み込みする
     */
    public boolean reload(){
        try {
            conn.close();
            conn = DriverManager.getConnection(connname, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param cu  アカウントの構造を渡しなさい。
     * @return アカウントを作れれば、true
     */
    public boolean CreateNewUser(CreateUserObject cu){
        try {
            /*
             INSERT INTO `ftpd` (`User`, `status`, `Password`, `Uid`, `Gid`, `Dir`, `ULBandwidth`, `DLBandwidth`, `comment`, `ipaccess`, `QuotaSize`, `QuotaFiles`)
VALUES ('testuser', '1', MD5('password'), '2001', '2001', '/var/www', '0', '0', '', '*', '0', '0');
             */

            String sql = "INSERT INTO "+table+" (`User`, `status`, `Password`, `Uid`, `Gid`, `Dir`, `ULBandwidth`, `DLBandwidth`, `comment`, `ipaccess`, `QuotaSize`, `QuotaFiles`) VALUES (? ,? ,MD5(?) ,? ,? ,? ,? ,? ,? ,? ,? ,? );";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(10);
            stmt.setString(1 ,cu.User);
            stmt.setString(2 ,cu.status);
            stmt.setString(3 ,cu.Password);
            stmt.setInt(4 ,cu.Uid);
            stmt.setInt(5 ,cu.Gid);
            stmt.setString(6 ,cu.dir);
            stmt.setInt(7 ,cu.ULBandwidth);
            stmt.setInt(8 ,cu.DLBandwidth);
            stmt.setString(9 ,cu.comment);
            stmt.setString(10 ,cu.ipaccess);
            stmt.setInt(11 ,cu.QuotaSize);
            stmt.setInt(12 ,cu.QuotaFiles);
            //https://stackoverflow.com/questions/24378270/how-do-you-determine-if-an-insert-or-update-was-successful-using-java-and-mysql
            int count = stmt.executeUpdate();
            stmt.close();
            return (count > 0); // <-- something like this.
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param username FTPユーザー名
     * @return 削除できればtrue
     */
    public boolean DeleteUser(String username){
        String sql =  "DELETE FROM "+table+" WHERE User = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(10);
            stmt.setString(1, username);
            int count = stmt.executeUpdate();
            stmt.close();
            return (count > 0); // <-- something like this.
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * //https://stackoverflow.com/questions/3155967/are-enums-supported-by-jdbc
     * @param username FTPユーザー名
     * @return 無効にできればtrue
     */
    public boolean DisableUser(String username){
        return setUser(username,"0");
    }

    /**
     * @param username FTPユーザー名
     * @return 無効にできればtrue
     */
    public boolean EnableUser(String username){
        return setUser(username,"1");
    }


    /**
     * @param username FTPユーザー名
     * @param val 値
     * @return できるかできないか
     */
    private boolean setUser(String  username,String val){
        //update uriage set price = 140 where name = 'Banana';
        String sql =  "UPDATE "+table+" SET `status` = ? WHERE User = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(10);
            stmt.setString(1, username);
            stmt.setString(2, val);
            int count = stmt.executeUpdate();
            stmt.close();
            return (count > 0); // <-- something like this.
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param username FTPユーザー名
     * @return ステータスの有効かどうか
     */
    public boolean isStatusUser(String username){
        String sql =  "SELECT `status` FROM "+table+" WHERE User = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(10);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String re = rs.getString("status");
                if(re.contains("1")){
                    stmt.close();
                    return true;
                }
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param username　FTPユーザー名
     * @return ユーザーが存在するか否か
     */
    public boolean isUserExist(String username){
        String sql =  "SELECT `status` FROM "+table+" WHERE User = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(10);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * @param username FTPユーザ名
     * @param password パスワード
     * @return パスワードが一致するか否か
     */
    public boolean PasswdCheck(String username , String password){
        if(!isUserExist(username))return false;
        String sql =  "SELECT `Password`  FROM "+table+" WHERE User = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setQueryTimeout(10);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String re = rs.getString("Password").trim();
                String givenpasswd = toEncryptedHashValue("MD5" ,password);
                return  (Objects.equals(re, givenpasswd));
            }else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @param algorithmName MD5など
     * @param value 対象の値
     * @return できた物
     */
    private String toEncryptedHashValue(String algorithmName, String value) {
        MessageDigest md = null;
        StringBuilder sb = null;
        try {
            md = MessageDigest.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md.update(value.getBytes());
        sb = new StringBuilder();
        for (byte b : md.digest()) {
            String hex = String.format("%02x", b);
            sb.append(hex);
        }
        return sb.toString();
    }


}
