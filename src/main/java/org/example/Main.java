package org.example;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static Connection conn; // объявляем новую переменную типа Connection, через которую устанавливаем связь бд
    static {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bank_system",
                    "postgres","postgres"); // пытаемся подключиться с помощью введённых данных
        } catch (SQLException e) { // при наличии в программе ошибки типа SQLException
            System.out.println("Ошибка подключения! " + e);  // выводим сообщение об ошибке
        }
    }
    public static void main(String[] args) throws SQLException {
        while (true){ // зацикливаем (для выбора действия после окончания предыдущей операции)
            Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
            System.out.print("Просмотр всех клиентов - 1, добавить нового клиента - 2, " +
                    "удалить клиента - 3, просмотреть детали клиента - 4, " +
                    "редактировать клиента - 5, выход - 6\nВыберите действие: "); // выводим список доступных команд
            try {
                    int choice = sc.nextInt(); // запрашиваем номер операции
                switch (choice){
                    case 1:
                        allClients(); // при выборе операции №1 выполняем метод allClients
                        break;
                    case 2:
                        addClient(); // при выборе операции №2 выполняем метод addClient
                        break;
                    case 3:
                        deleteClient(); // при выборе операции №3 выполняем метод deleteClient
                        break;
                    case 4:
                        viewClient(); // при выборе операции №4 выполняем метод viewClient
                        break;
                    case 5:
                        updateClient(); // при выборе операции №5 выполняем метод updateClient
                        break;
                    case 6:
                        return; // при выборе операции №6 выходим из цикла и завершаем программу
                }
                if (choice < 1 || choice > 6){ // при вводе номера операции вне диапазона
                    throw new InputMismatchException(); // выкидываем ошибку InputMismatchException
                }
            } catch (InputMismatchException e){ // при наличии в программе ошибки типа InputMismatchException
                System.out.println("Ошибка ввода! Выберите 1-6 вариант"); // выводим сообщение об ошибке и перезапускаем программу
            }
        }
     }
    public static void allClients() throws SQLException {
            ResultSet rs = conn.createStatement().executeQuery
                    ("SELECT * FROM users"); // объявлям новую переменную типа ResultSet, в которой храним результат запроса к бд
            int num = 0; // объявлям новую переменную типа int, в которую будем "складывать" колличество клиентов
            System.out.println("--------------------------------------");
            while (rs.next()){ // зацикливаем для вывода всех записей в таблице
                System.out.println(rs.getString("id") + " | " + rs.getString("full_name")); // выводим из записи только данные из столбцов id и full_name
                num++;
            }
            System.out.println("--------------------------------------");
            System.out.println("Всего клиентов - " + num); // выводим количество записей
            System.out.println("--------------------------------------");
    }
    public static void addClient() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
            System.out.print("Введите полное ФИО: ");
            String fio = sc.nextLine(); // объявляем новую переменную типа String, в которую записываем ФИО нового клиента
            System.out.print("Введите возраст: ");
            int age = sc.nextInt(); // объявляем новую переменную типа int, в которую записываем ФИО нового клиента
            if (age < 1 || age > 120){ // при вводе некорректного возраста
                throw new InputMismatchException(); // выкидываем ошибку InputMismatchException
            }
            System.out.print("Введите номер банк. счёта: ");
            String num = sc.next(); // объявляем новую переменную типа String, в которую записываем банк. счёт нового клиента
            System.out.print("Введите баланс: ");
            int balance = sc.nextInt(); // объявляем новую переменную типа int, в которую записываем баланс на счету нового клиента
            PreparedStatement st = conn.prepareStatement
                    ("INSERT INTO users(full_name,age,bank_account,balance) VALUES (?,?,?,?)"); // объявляем
            // новую переменную типа PreparedStatement, через которую делаем запрос в бд
            st.setString(1, fio); // подставляем ФИО на место первого знака вопроса
            st.setInt(2, age); // подставляем возраст на место второго знака вопроса
            st.setString(3, num); // подставляем банк. счёт на место третьего знака вопроса
            st.setInt(4, balance); // подставляем баланс на место четвёртого знака вопроса
            st.execute(); // делаем запрос в бд
            System.out.println("Добавлено!"); // выводим сообщение об удачном выполнении
        }  catch (InputMismatchException e){ // при наличии в программе ошибки типа InputMismatchException
            System.out.println("Ошибка ввода! Попробуйте заново"); // выводим сообщение об ошибке
            addClient(); // перезапускаем операцию (метод)
        }
    }
    public static void deleteClient() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
            allClients(); // вызываем метод allClients (выводим всех клиентов)
            System.out.print("Напишите номер клиента для удаления: ");
            int id = sc.nextInt(); // объявляем новую переменную типа int, в которую записываем айди клиента
            if (id < 1){ // при вводе номера операции вне диапазона
                throw new InputMismatchException(); // выкидываем ошибку InputMismatchException
            }
            PreparedStatement st = conn.prepareStatement
                    ("DELETE FROM users WHERE id = ?"); // объявляем
            // новую переменную типа PreparedStatement, через которую делаем запрос в бд
            st.setInt(1,id); // подставляем айди на место первого знака вопроса
            st.execute(); // делаем запрос в бд
            System.out.println("Удалено!"); // выводим сообщение об удачном выполнении
        } catch (InputMismatchException e){ // при наличии в программе ошибки типа InputMismatchException
            System.out.println("Ошибка ввода! Попробуйте заново"); // выводим сообщение об ошибке
            deleteClient(); // перезапускаем операцию (метод)
        }
    }
    public static int viewClient() throws SQLException { // (int, т.к. мы должны запомнить
        // айди выбранного клиента для дальнейших действий с ним)
        try {
            Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
            allClients(); // вызываем метод allClients (выводим всех клиентов)
            System.out.print("Напишите номер клиента для детального просмотра: ");
            int id = sc.nextInt(); // объявляем новую переменную типа int, в которую записываем айди клиента
            PreparedStatement st = conn.prepareStatement
                    ("SELECT * FROM users WHERE id = ?"); // объявляем
            // новую переменную типа PreparedStatement, через которую делаем запрос в бд
            st.setInt(1,id); // подставляем айди на место первого знака вопроса
            ResultSet rs = st.executeQuery(); // "превращаем" PreparedStatement в выполненный ResultSet
            System.out.println("--------------------------------------");
            while (rs.next()){ // зацикливаем для вывода всех записей в таблице
                System.out.println("ФИО: " + rs.getString("full_name")); // выводим из записи только данные из столбца full_name
                System.out.println("Возраст: " + rs.getString("age")); // выводим из записи только данные из столбца age
                System.out.println("Номер банк. счёта: " + rs.getString("bank_account")); // выводим из записи только данные из столбца bank_account
                System.out.println("Баланс на счету: " + rs.getString("balance")); // выводим из записи только данные из столбца balance
            }
            System.out.println("--------------------------------------");
            return id;
        } catch (InputMismatchException e){ // при наличии в программе ошибки типа InputMismatchException
            System.out.println("Ошибка ввода! Попробуйте заново"); // выводим сообщение об ошибке
            viewClient(); // перезапускаем операцию (метод)
        }
        return -1;
    }
    public static void updateClient() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
            int client = viewClient(); // объявляем новую переменную типа int, в которой хранится результат метода viewClient
            System.out.println("""
                    Что Вы хотите изменить?
                    ФИО - 1, возраст - 2, номер банк. счёта - 3, баланс - 4
                    Введите номер:\s"""); // выводим список доступных команд
            int choice = sc.nextInt(); // запрашиваем номер операции
            switch (choice) {
                case 1 -> updateFIO(client); // при выборе операции №1 выполняем метод updateFIO и передаём в качестве параметра айди клиента
                case 2 -> updateAge(client); // при выборе операции №2 выполняем метод updateAge и передаём в качестве параметра айди клиента
                case 3 -> updateAccount(client); // при выборе операции №3 выполняем метод updateAccount и передаём в качестве параметра айди клиента
                case 4 -> updateBalance(client); // при выборе операции №4 выполняем метод updateBalance и передаём в качестве параметра айди клиента
            }
            if (choice < 1 || choice > 4){ // при вводе номера операции вне диапазона
                throw new InputMismatchException(); // выкидываем ошибку InputMismatchException
            }
        } catch (InputMismatchException e){ // при наличии в программе ошибки типа InputMismatchException
            System.out.println("Ошибка ввода! Попробуйте заново"); // выводим сообщение об ошибке
            updateClient(); // перезапускаем операцию (метод)
        }
    }
    public static void updateFIO(int client) throws SQLException {
        System.out.print("Введите новые ФИО: ");
        Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
        String fio = sc.nextLine(); // объявляем новую переменную типа String, в которую записываем ФИО клиента для изменения
        PreparedStatement st = conn.prepareStatement
                ("UPDATE users SET full_name = ? WHERE id = ?");// объявляем
        // новую переменную типа PreparedStatement, через которую делаем запрос в бд
        st.setString(1,fio); // подставляем ФИО на место первого знака вопроса
        st.setInt(2,client); // подставляем айди (передаваемый параметр) на место второго знака вопроса
        st.execute(); // делаем запрос в бд
        System.out.println("Изменено!"); // выводим сообщение об удачном выполнении
    }
    public static void updateAge(int client) throws SQLException {
        try {
            System.out.print("Введите новый возраст: ");
            Scanner sc = new Scanner(System.in);  // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
            int age = sc.nextInt(); // объявляем новую переменную типа int, в которую записываем возраст клиента для изменения
            if (age < 1){ // если возраст не положительное число
                throw new InputMismatchException(); // выкидываем ошибку InputMismatchException
            }
            PreparedStatement st = conn.prepareStatement
                    ("UPDATE users SET age = ? WHERE id = ?"); // объявляем
            // новую переменную типа PreparedStatement, через которую делаем запрос в бд
            st.setInt(1,age); // подставляем возраст на место первого знака вопроса
            st.setInt(2,client);// подставляем айди (передаваемый параметр) на место второго знака вопроса
            st.execute(); // делаем запрос в бд
            System.out.println("Изменено!"); // выводим сообщение об удачном выполнении
        } catch (InputMismatchException e){ // при наличии в программе ошибки типа InputMismatchException
            System.out.println("Ошибка ввода! Попробуйте заново"); // выводим сообщение об ошибке
            updateAge(client);  // перезапускаем операцию (метод)
        }
    }
    public static void updateAccount(int client) throws SQLException {
        System.out.print("Введите новый банк. счёт: ");
        Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
        String num = sc.nextLine(); // объявляем новую переменную типа String, в которую записываем банк. счёт клиента для изменения
        PreparedStatement st = conn.prepareStatement
                ("UPDATE users SET bank_account = ? WHERE id = ?"); // объявляем
        // новую переменную типа PreparedStatement, через которую делаем запрос в бд
        st.setString(1,num); // подставляем банк. счёт на место первого знака вопроса
        st.setInt(2,client);// подставляем айди (передаваемый параметр) на место второго знака вопроса
        st.execute(); // делаем запрос в бд
        System.out.println("Изменено!"); // выводим сообщение об удачном выполнении
    }
    public static void updateBalance(int client) throws SQLException {
        try {
            System.out.print("Введите новый баланс: ");
            Scanner sc = new Scanner(System.in); // объявляем новую переменную типа Scanner, которая читает ввод от пользователя
            int bal = sc.nextInt(); // объявляем новую переменную типа int, в которую записываем баланс клиента для изменения
            PreparedStatement st = conn.prepareStatement
                    ("UPDATE users SET balance = ? WHERE id = ?"); // объявляем
            // новую переменную типа PreparedStatement, через которую делаем запрос в бд
            st.setInt(1,bal);  // подставляем баланс на место первого знака вопроса
            st.setInt(2,client); // подставляем айди (передаваемый параметр) на место второго знака вопроса
            st.execute();  // делаем запрос в бд
            System.out.println("Изменено!"); // выводим сообщение об удачном выполнении
        } catch (InputMismatchException e){ // при наличии в программе ошибки типа InputMismatchException
            System.out.println("Ошибка ввода! Попробуйте заново");  // выводим сообщение об ошибке
            updateBalance(client); // перезапускаем операцию (метод)
        }
    }
}