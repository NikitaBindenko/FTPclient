package src.main.java.ftp;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            Client client = new Client(args[2]);
            client.authorization(args[0], args[1]);

            Scanner console = new Scanner(System.in);
            boolean quit = false;
            String json = "";

            System.out.println("Добро пожаловать в FTP-клиент!\nВведите имя файла, с которым собираетесь работать: ");
            String filename = console.nextLine();

            System.out.println("\nВведите одну из команд: ");
            System.out.println(client.menu());
            if(client.isPassiveMode()){
                System.out.println("Текущий режим:\tпассивный");
            }else{
                System.out.println("Текущий режим:\tактивный");
            }

            while(!quit) {
                String command = console.nextLine();

                switch (command) {
                    case "1":
                        if (client.isPassiveMode()) {
                            json = client.downloadPassive(filename);
                        } else {
                            json = client.downloadActive(filename);
                        }
                        System.out.println(new Parser(json).showStudentNames());
                        break;

                    case "2":
                        System.out.println("Введите ID студента: ");
                        int id = Integer.parseInt(console.nextLine());
                        if (client.isPassiveMode()) {
                            json = client.downloadPassive(filename);
                        } else {
                            json = client.downloadActive(filename);
                        }
                        System.out.println(new Parser(json).getStudent(id));
                        break;

                    case "3":
                        System.out.println("Введите имя нового студента: ");
                        String name = console.nextLine();
                        if(name.contains(" ")){
                            System.out.println("Вместо пробелов используйте _ в именах");
                            break;
                        }
                        if (client.isPassiveMode()) {
                            json = client.downloadPassive(filename);
                            client.uploadPassive(filename, new Parser(json).addStudent(name));
                        } else {
                            json = client.downloadActive(filename);
                            client.uploadActive(filename, new Parser(json).addStudent(name));
                        }
                        System.out.println("Студент " + name + " успешно добавлен\n");
                        break;

                    case "4":
                        System.out.println("Введите ID студента: ");
                        int ID = Integer.parseInt(console.nextLine());
                        if (client.isPassiveMode()) {
                            json = client.downloadPassive(filename);
                            String deletionResult = new Parser(json).deleteStudent(ID);
                            if (deletionResult.equals("Таких студентов нет,\nпопробуйте другой id")) {
                                System.out.println(deletionResult);
                            } else {
                                client.uploadPassive(filename, deletionResult);
                                System.out.println("Студент ( id:  " + ID + " ) успешно удален");
                            }
                        } else {
                            json = client.downloadActive(filename);
                            String deletionResult = new Parser(json).deleteStudent(ID);
                            if (deletionResult.equals("Таких студентов нет,\nпопробуйте другой id")) {
                                System.out.println(deletionResult);
                            } else {
                                client.uploadActive(filename, deletionResult);
                                System.out.println("Студент ( id:  " + ID + " ) успешно удален");
                            }
                        }
                        break;

                    case "5":
                        client.disconnect();
                        quit = true;
                        System.out.println("До встречи!");
                        break;

                    case "6":
                        client.changeMode();
                        if(client.isPassiveMode()){
                            System.out.println("Текущий режим:\tпассивный");
                        }else{
                            System.out.println("Текущий режим:\tактивный");
                        }
                        break;

                    case "h":
                        System.out.println(client.menu());
                        break;

                    default:
                        System.out.println("Команда не определена, попробуйте еще раз");
                }
            }

        }catch(IOException e){
            System.out.println("Не удалось подключиться, попробуйте снова\n");
        }
        catch(NumberFormatException e){
            System.out.println("Неверный ввод, установите соединение заново\n");
        }
    }
}
