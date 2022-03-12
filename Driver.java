import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver {
    static final String JdbcURL = "jdbc:mysql://localhost:3306/picturepurrfect?useSSL=false";
    static final String Username = "root";
    static final String password = "ravsierra1";

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, SQLException {

        // Loop here to read files on hard drive and run MD5 Hash to write in database
        //Creating a File object for directory
        String sPath = "E:\\Do not use All Pictures -10-8";
        Connection conn = DriverManager.getConnection(JdbcURL, Username, password);

        String sql = "TRUNCATE file_list";
        java.sql.Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);

        ckDir(sPath, conn);

    }


    private static void ckDir(String curD, Connection conn) throws NoSuchAlgorithmException, IOException, SQLException {

        File directoryPath = new File(curD);
        //List of all files and directories ()
        String contents[] = directoryPath.list();
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");

        for(int i=0; i<contents.length; i++) {

            File cur = new File(curD + "\\" + contents[i]);

            if(!cur.isDirectory()) {

                String curPrint = curD + "\\" + contents[i];
                System.out.println(curPrint);

                String md5DigestPrint = getFileChecksum(md5Digest, cur);
                System.out.println(md5DigestPrint);

                // Execute a query
                //Set the INSERT string
                String sql = "INSERT INTO file_list VALUES (0, \"" + curPrint + "\", '" + md5DigestPrint + "')";
                //Need to add escape characters to store as path correctly
                sql=sql.replace("\\", "\\\\");

                java.sql.Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                System.out.println(sql);


            }else {

                System.out.println("-------------------------------------------");
                ckDir(curD + "\\" + contents[i],conn);
                System.out.println("-------------------------------------------");

            }
        }



    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }






}
