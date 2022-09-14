package src.test.java.unit;

import src.main.java.ftp.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StudentTest {

    Student student1 = new Student(0, "Kolya   Ivanov");
    Student student2 = new Student(-100, "Kolya Ivanov");
    Student student3  = new Student(100, "Kolya_Ivanov");


    @Test(groups = "offline")
    public void idTest(){
        Assert.assertEquals(student1.getId(), 0);
        Assert.assertEquals(student2.getId(), -100);
        Assert.assertEquals(student3.getId(), 100);
    }

    @Test(groups = "offline")
    public void nameTest(){
        Assert.assertEquals(student1.getName(), "Kolya   Ivanov");
        Assert.assertEquals(student2.getName(), "Kolya Ivanov");
        Assert.assertEquals(student3.getName(), "Kolya_Ivanov");
    }

}
