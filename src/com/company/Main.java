package com.company;

import java.util.Scanner;
import java.util.Stack;
/**Класс вычисления выражения. Не имеет полей!!!*/
class Expression {
    /**Проверяет, что заданная последовательность символов конвертируема в число.*/
    public static boolean isNumber(String str) {
        int point = 0;
        int it = 0;
        if (str.charAt(0) == '-') it++;
        for (int i = it; i < str.length(); i++) {
            if (str.charAt(0) == '-') continue;
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') continue;
            else if (str.charAt(i) == '.' && point == 0) point++;
            else return false;
        }
        return true;
    }
    /**Считает приоритет операции*/
    public static int Prior(char c) {
        if (c == '(' || c == ')') return 1;
        else if (c == '+' || c == '-') return 2;
        else if (c == '*' || c == '/') return 3;
        return 0;
    }
    /**Конвертирует символ операции в операцию*/
    public static double Operator(double elem1, double elem2, Character op) {
        if (op == '+') return elem2 + elem1;
        else if (op == '-') return elem2 - elem1;
        else if (op == '*') return elem2 * elem1;
        else if (op == '/') return elem2 / elem1;
        else throw new RuntimeException();
    }
    /**Находит индекс закрывающейся скобки для конкретной открывающейся.*/
    public static int idOfNextBracket(String str, int indexOfFirst) {
        int num = 0;
        for (int i =indexOfFirst ; i < str.length(); i++) {
            if (str.charAt(i) == '(') num++;
            else if (str.charAt(i) == ')') num--;
            if (num == 0) return i;
        }
        return -1;
    }
    /**Конвертирует подстроку в число, если может.
     * Если подстрока - не число, то запрашивает значение переменной.*/
    public static double convertToDouble(String str1, Scanner scanner) {
        String str2 = str1;
        if (isNumber(str1)) {
            return Double.parseDouble(str1);
        } else {
            do {
                System.out.println("Введите значение переменной " + str1);
                str2 = scanner.nextLine();
                if (isNumber(str2)) {
                    return Double.parseDouble(str2);
                }
            }
            while (!isNumber(str2));
        }
        return 0;
    }
    /**
     * Основной модуль программы , переводит выражение в постфиксную форму,
     * Если встречает открывающуюся скобку , то рекурсивно перезапускает
     * подсчет выражения в скобке
     */
    public static double calculator(String str, Scanner scanner) {
        String str1 = new String();
        Stack<Character> operators = new Stack<>();
        Stack<Double> nums = new Stack<>();
        int point = 0;
        int idOfLastElemSubs = -1;
        boolean trust = true;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)==' ') continue;
            else if (str.charAt(i) >= '0' && str.charAt(i) <= '9' ||
                    str.charAt(i) >= 'A' && str.charAt(i) <= 'Z' ||
                    str.charAt(i) >= 'a' && str.charAt(i) <= 'z') str1 += str.charAt(i);
            else if (str.charAt(i) == '.') {
                if (isNumber(str1)) {
                    if (point == 0) {
                        str1 += '.';
                        point++;
                    } else {
                        trust = false;
                        break;
                    }
                } else str1 += '.';
            } else {
                if (str1.length() != 0) {
                    nums.add(convertToDouble(str1, scanner));
                    str1="";
                }
                if (str.charAt(i) == '(') /*Потенциальное место краша программы. Открыть блок*/ {
                    idOfLastElemSubs = idOfNextBracket(str,i);
                    if (idOfLastElemSubs != -1 ) /*В некоторых случаях пытаемся добавить в список рунтайм эксепшн!!!!*/ {
                        try {
                            nums.add(calculator(str.substring(i + 1, idOfLastElemSubs), scanner));
                            i = idOfLastElemSubs;
                            continue;
                        } catch (RuntimeException e) {
                            trust=false;
                            break;
                        }
                    }
                    else {
                        trust = false;
                        break;
                    }
                } else if (operators.size() == 0 ||
                        Prior(operators.peek()) < Prior(str.charAt(i))) operators.push(str.charAt(i));
                else {
                    if(str.charAt(i)==')')
                    {
                        trust=false;
                        break;
                    }
                    while (operators.size() != 0 && Prior(operators.peek()) >= Prior(str.charAt(i))) {
                            if (nums.peek() == '/') {
                                if (nums.peek() == 0) {
                                    trust = false;
                                    break;
                                }
                                nums.add(Operator(nums.pop(), nums.pop(), operators.pop()));
                            } else nums.add(Operator(nums.pop(), nums.pop(), operators.pop()));
                        }
                        operators.push(str.charAt(i));
                }
            }
        }
        if (trust) {
            if (str1.length() != 0) nums.add(convertToDouble(str1, scanner));
            while (operators.size() != 0) {
                if (operators.peek() == '-' && nums.size() == 1) {
                    nums.add(nums.pop() * (-1));
                    operators.pop();
                }
                else if (operators.peek() == '/') {
                    if (nums.peek() == 0) {
                        trust = false;
                        break;
                    }
                    nums.add(Operator(nums.pop(), nums.pop(), operators.pop()));
                }
                else if(operators.size() >= 1 && nums.size() == 1 ||
                        operators.size() == 1 && nums.size() >= 3)
                {
                    trust = false;
                    break;
                }
                else nums.add(Operator(nums.pop(), nums.pop(), operators.pop()));
            }
        }
        if(trust) return nums.pop();
        throw new RuntimeException();
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        System.out.println("Введите выражение. Отрицательные числа в водите в формате (-Num)!");
        String str = scanner.nextLine();
        try {
            double d = Expression.calculator(str, scanner);
            System.out.println(d);
        }catch (RuntimeException e)
        {
            System.out.println("Неправильное выражение!!!");
        }
    }
}
