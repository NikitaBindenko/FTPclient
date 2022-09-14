package src.test.java.unit;

import src.main.java.ftp.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ParserTest {

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


    @Test(groups = "offline")
    public void showStudentsTest(){
        Parser testParser = new Parser(JSONcontent);
        String names = testParser.showStudentNames();
        Assert.assertEquals(names, "Student1\nStudent2\nStudent3\n");
    }

    @Test(groups = "offline")
    public void getStudentTest(){
        Parser testParser = new Parser(JSONcontent);
        Assert.assertEquals(testParser.getStudent(1), "studentID:\t1\nname:\tStudent1");
        Assert.assertEquals(testParser.getStudent(2), "studentID:\t2\nname:\tStudent2");
        Assert.assertEquals(testParser.getStudent(3), "studentID:\t3\nname:\tStudent3");
        Assert.assertEquals(testParser.getStudent(4), "Таких студентов нет,\nпопробуйте другой id");

    }

    @Test(groups = "offline", dependsOnMethods = "showStudentsTest")
    public void addDeleteTest(){
        Parser testParser = new Parser(JSONcontent);

        testParser.addStudent("Student4");
        Assert.assertEquals(testParser.showStudentNames(), "Student1\nStudent2\nStudent3\nStudent4\n");
        testParser.deleteStudent(4);
        Assert.assertEquals(testParser.showStudentNames(), "Student1\nStudent2\nStudent3\n");
    }
}
