package src.test.java.unit;

import src.main.java.ftp.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import ftp.Client;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class ClientTest {

    String JSONcontent = "{\n" +
            "\t\"students\": [\n" +
            "\t{\n" +
            "\t\t\"id\": 1,\n" +
            "\t\t\"name\": \"Student1\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"id\": 2,\n" +
            "\t\t\"name\": \"Student2\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"id\": 3,\n" +
            "\t\t\"name\": \"Student3\"\n" +
            "\t}\n" +
            "\t]\n" +
            "}\n";


    @Test (groups = "online")
    public void authorizationTest(){

        boolean testPassed = true;
        try {
            Client client = new Client("127.0.0.1");
            Assert.assertEquals(client.authorization("anonymous", "test"), "220331230");    //последовательность из трех кодов ответа
            client.disconnect();
            client.get();
        }catch(Exception e){
            testPassed = false;
        }finally{
            Assert.assertTrue(testPassed, "Connection Failed");
        }
    }


    @Test (groups = "online", dependsOnMethods = "authorizationTest")
    public void downloadPassiveTest(){
        boolean testPassed = true;
        try {
            Client client = new Client("127.0.0.1");
            client.authorization("anonymous", "test");
            String downloaded = client.downloadPassive("students.json");
            Assert.assertEquals(downloaded, JSONcontent);
        }catch(IOException e){
            testPassed = false;
        }finally{
            Assert.assertTrue(testPassed, "Connection Failed");
        }
    }

    @Test (groups = "online", dependsOnMethods = "authorizationTest")
    public void downloadActiveTest(){
        boolean testPassed = true;
        try {
            Client client = new Client("127.0.0.1");
            client.authorization("anonymous", "test");
            String downloaded = client.downloadActive("students.json");
            Assert.assertEquals(downloaded, JSONcontent);
        }catch(IOException e){
            testPassed = false;
        }finally{
            Assert.assertTrue(testPassed, "Connection Failed");
        }
    }

    @Test (groups = "offline")
    public void modeTest(){
        boolean testPassed = true;
        try {
            Client client = new Client("127.0.0.1");
            client.changeMode();
            Assert.assertTrue(client.isPassiveMode());
            client.changeMode();
            Assert.assertFalse(client.isPassiveMode());
        }catch(IOException e){
            testPassed = false;
        }finally{
            Assert.assertTrue(testPassed, "Connection Failed");
        }
    }
}
