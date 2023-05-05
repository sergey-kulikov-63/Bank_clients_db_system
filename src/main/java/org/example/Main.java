package org.example;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static Connection conn;
    static {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bank_system",
                    "postgres","postgres");
        } catch (SQLException e) {
            System.out.println("Ошибка подключения! " + e);
        }
    }
    public static void main(String[] args) throws SQLException {
        while (true){
            Scanner sc = new Scanner(System.in);
            System.out.print("Просмотр всех клиентов - 1, " +
                    "добавить нового клиента - 2, " +
                    "удалить клиента - 3, " +
                    "просмотреть детали клиента - 4, " +
                    "редактировать клиента - 5, " +
                    "выход - 6\nВыберите действие: ");
            try {
                    int choice = sc.nextInt();
                switch (choice){
                    case 1:
                        allClients();
                        break;
                    case 2:
                        addClient();
                        break;
                    case 3:
                        deleteClient();
                        break;
                    case 4:
                        viewClient();
                        break;
                    case 5:
                        updateClient();
                        break;
                    case 6:
                        return;
                }
                if (choice < 1 || choice > 6){
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e){
                System.out.println("Ошибка ввода! Выберите 1-5 вариант");
            }
        }
     }
    public static void allClients() throws SQLException {
            ResultSet rs = conn.createStatement().executeQuery
                    ("SELECT * FROM users");
            int num = 0;
            System.out.println("--------------------------------------");
            while (rs.next()){
                System.out.println(rs.getString("id") + " | " + rs.getString("full_name"));
                num++;
            }
            System.out.println("--------------------------------------");
            System.out.println("Всего клиентов - " + num);
            System.out.println("--------------------------------------");
    }
    public static void addClient() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Введите полное ФИО: ");
            String fio = sc.nextLine();
            System.out.print("Введите возраст: ");
            int age = sc.nextInt();
            System.out.print("Введите номер банк. счёта: ");
            String num = sc.next();
            System.out.print("Введите баланс: ");
            int balance = sc.nextInt();
            PreparedStatement st = conn.prepareStatement
                    ("INSERT INTO users(full_name,age,bank_account,balance) VALUES (?,?,?,?)");
            st.setString(1, fio);
            st.setInt(2, age);
            st.setString(3, num);
            st.setInt(4, balance);
            st.execute();
            System.out.println("Добавлено!");
        }  catch (InputMismatchException e){
            System.out.println("Ошибка ввода! Попробуйте заново");
            addClient();
        }
    }
    public static void deleteClient() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in);
            allClients();
            System.out.print("Напишите номер клиента для удаления: ");
            int del = sc.nextInt();
            PreparedStatement st = conn.prepareStatement
                    ("DELETE FROM users WHERE id = ?");
            st.setInt(1,del);
            st.execute();
            System.out.println("Удалено!");
        } catch (InputMismatchException e){
            System.out.println("Ошибка ввода! Попробуйте заново");
            deleteClient();
        }
    }
    public static int viewClient() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in);
            allClients();
            System.out.print("Напишите номер клиента для детального просмотра: ");
            int view = sc.nextInt();
            PreparedStatement st = conn.prepareStatement
                    ("SELECT * FROM users WHERE id = ?");
            st.setInt(1,view);
            ResultSet rs = st.executeQuery();
            System.out.println("--------------------------------------");
            while (rs.next()){
                System.out.println("ФИО: " + rs.getString("full_name"));
                System.out.println("Возраст: " + rs.getString("age"));
                System.out.println("Номер банк. счёта: " + rs.getString("bank_account"));
                System.out.println("Баланс на счету: " + rs.getString("balance"));
            }
            System.out.println("--------------------------------------");
            return view;
        } catch (InputMismatchException e){
            System.out.println("Ошибка ввода! Попробуйте заново");
            viewClient();
        }
        return 0;
    }
    public static void updateClient() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in);
            int client = viewClient();
            System.out.println("""
                    Что Вы хотите изменить?
                    ФИО - 1, возраст - 2, номер банк. счёта - 3, баланс - 4
                    Введите номмер:\s""");
            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> updateFIO(client);
                case 2 -> updateAge(client);
                case 3 -> updateAccount(client);
                case 4 -> updateBalance(client);
            }
            if (choice < 1 || choice > 4){
                throw new InputMismatchException();
            }
        } catch (InputMismatchException e){
            System.out.println("Ошибка ввода! Попробуйте заново");
            updateClient();
        }
    }
    public static void updateFIO(int client) throws SQLException {
        System.out.print("Введите новые ФИО: ");
        Scanner sc = new Scanner(System.in);
        String fio = sc.nextLine();
        PreparedStatement st = conn.prepareStatement
                ("UPDATE users SET full_name = ? WHERE id = ?");
        st.setString(1,fio);
        st.setInt(2,client);
        st.execute();
    }
    public static void updateAge(int client) throws SQLException {
        try {
            System.out.print("Введите новый возраст: ");
            Scanner sc = new Scanner(System.in);
            int age = sc.nextInt();
            if (age < 1){
                System.out.println("Ошибка! Возраст должен быть положительным числом");
                updateAge(client);
            }
            PreparedStatement st = conn.prepareStatement
                    ("UPDATE users SET age = ? WHERE id = ?");
            st.setInt(1,age);
            st.setInt(2,client);
            st.execute();
        } catch (InputMismatchException e){
            System.out.println("Ошибка ввода! Попробуйте заново");
            updateAge(client);
        }
    }
    public static void updateAccount(int client) throws SQLException {
        System.out.print("Введите новый банкю счёт: ");
        Scanner sc = new Scanner(System.in);
        String acc = sc.nextLine();
        PreparedStatement st = conn.prepareStatement
                ("UPDATE users SET bank_account = ? WHERE id = ?");
        st.setString(1,acc);
        st.setInt(2,client);
        st.execute();
    }
    public static void updateBalance(int client) throws SQLException {
        try {
            System.out.print("Введите новый баланс: ");
            Scanner sc = new Scanner(System.in);
            int bal = sc.nextInt();
            PreparedStatement st = conn.prepareStatement
                    ("UPDATE users SET balance = ? WHERE id = ?");
            st.setInt(1,bal);
            st.setInt(2,client);
            st.execute();
        } catch (InputMismatchException e){
            System.out.println("Ошибка ввода! Попробуйте заново");
            updateBalance(client);
        }
    }
}