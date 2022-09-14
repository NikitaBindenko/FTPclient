package src.test.java.unit;

import src.test.java.unit.*;
import org.testng.TestNG;
import src.main.java.ftp.*;

public class MainTest {
    public static void main(String[] args) {
    	ParserTest test = new ParserTest();
        TestNG suite = new TestNG();
        if(args.length > 0){
            if(args[0].equals("online")) {
                suite.setGroups("online");
            }else{
                suite.setGroups("offline");
            }
        }else {
            suite.setGroups("offline");
        }
        suite.setTestClasses(new Class[] {StudentTest.class, ParserTest.class});
        suite.run();
    }
}
