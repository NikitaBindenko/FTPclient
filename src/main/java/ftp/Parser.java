package src.main.java.ftp;

import java.util.*;

public class Parser {
    private String json;
    private LinkedList<Student> students;

    public Parser(String input){
        this.json = input;
        students = new LinkedList<>();

        int arrayLeftBorder = json.indexOf('[');
        int arrayRightBorder = json.indexOf(']');

        String studentsSubstring = json.substring(arrayLeftBorder + 1, arrayRightBorder);
        ArrayList<String> studentStrings = new ArrayList<String>();

        try {
            while (true) {
                int openingBracketIndex = studentsSubstring.indexOf('{');
                int closingBracketIndex = studentsSubstring.indexOf('}');
                String firstBracket = studentsSubstring.substring(openingBracketIndex + 1, closingBracketIndex);
                studentsSubstring = studentsSubstring.substring(closingBracketIndex + 1);
                studentStrings.add(firstBracket);
            }
        }catch(StringIndexOutOfBoundsException e){

            for(String s : studentStrings){
                s = s.replaceAll(",", " ");
                s = s.replaceAll(":", " ");
                Scanner sc = new Scanner(s);
                String attribute = sc.next();
                if(attribute.equals("\"id\"")){
                    int id = sc.nextInt();
                    attribute = sc.next();
                    if(attribute.equals("\"name\"")){
                        String name = sc.next();
                        students.add(new Student(id, name.substring(1, name.length()-1)));
                    }
                }
            }
        }

    }

    public String showStudentNames(){

        Comparator<Student> comparator = new Comparator<Student>(){
            public int compare(Student st1, Student st2) {
                return st1.getName().compareTo(st2.getName());
            }
        };
        students.sort(comparator);

        StringBuilder studentNames = new StringBuilder();
        for(Student s : students){
            studentNames.append(s.getName() + "\n");
        }
        return studentNames.toString();
    }

    public String getStudent(int id){
        for(Student s : students){
            if(s.getId() == id){
                return "studentID:" + "\t" + s.getId() + "\n" + "name:" + "\t" + s.getName();
            }
        }
        return "Таких студентов нет,\nпопробуйте другой id";
    }

    public String addStudent(String name){
        int minID = 0;
        int curID;
        for(Student s : students){
            if((curID = s.getId()) >= minID){
                minID = curID;
            }
        }
        students.add(new Student(minID + 1, name));
        this.updateJSON();
        return json;
    }

    public String deleteStudent(int id){
        for(Student s : students){
            if(s.getId() == id){
                students.remove(s);
                this.updateJSON();
                return json;
            }
        }
        return "Таких студентов нет,\nпопробуйте другой id";
    }

    private void updateJSON(){
        StringBuilder newJSON = new StringBuilder();
        newJSON.append("{\n\t\"students\": [\n");

        ListIterator<Student> nextCheck = students.listIterator();
        for(Student s : students) {
            newJSON.append("\t{\n\t\t\"id\": " + s.getId() + ",\n\t\t\"name\": \"" + s.getName() + "\"\n\t}");
            nextCheck.next();
            if(nextCheck.hasNext()){
                newJSON.append(",\n");
            }else{
                newJSON.append("\n");
            }
        }
        newJSON.append("\t]\n}");
        json = newJSON.toString();
    }
}
