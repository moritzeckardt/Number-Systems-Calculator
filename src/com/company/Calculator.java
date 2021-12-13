package com.company;

import java.util.Hashtable;

public class Calculator {
    // Constants
    public static final Hashtable<Character, Integer> hexLetters = new Hashtable<Character, Integer>() {{
        put('A', 10); put('B', 11); put('C', 12); put('D', 13); put('E', 14); put('F', 15);
    }};
    public static final Hashtable<Integer, Character> hexNumbers = new Hashtable<Integer, Character>() {{
        put(10, 'A'); put(11, 'B'); put(12, 'C'); put(13, 'D'); put(14, 'E'); put(15, 'F');
    }};

    // Main
    public static void main(String[] args) {
        new CalculatorGUI();
    }

    //Methods
    public static boolean isKnownNumberSystem(int inputSystem) {
        // Check for correct input system
        return inputSystem != 2 && inputSystem != 8 && inputSystem != 10 && inputSystem != 16;
    }

    public static boolean isInNumberSystem(char number, int inputSystem) {
        // Check if number belongs to input system
        return !isKnownNumberSystem(inputSystem) && hexLetters.getOrDefault(number, number - '0') < inputSystem;
    }

    public static int convertToInt(String inputNumber, int inputSystem) {
        // Check for correct input system
        if (!isKnownNumberSystem(inputSystem) && inputNumber.equals(""))
            return -1;

        // Convert different number systems into decimal
        int result = 0;
        for (char c : inputNumber.toCharArray()) {
            if (!isInNumberSystem(c, inputSystem))
                return -1;

            result += hexLetters.getOrDefault(c, c - '0');
            result *= inputSystem;
        }

        return result / inputSystem;
    }

    public static String convertToNumberSystem(int number, int outputSystem) {
        // Check for correct input system
        if (isKnownNumberSystem(outputSystem)) {
            return "error";
        }

        // Convert decimal into different number systems
        StringBuilder s = new StringBuilder();
        do {
            int wantedNumber = number % outputSystem;
            s.insert(0, hexNumbers.getOrDefault(wantedNumber, (char) (wantedNumber + '0')));
            number /= outputSystem;
        } while (number != 0);

        // Add prefix
        switch (outputSystem) {
            case 2: s.insert(0, "b"); break;
            case 8: s.insert(0, "0"); break;
            case 16: s.insert(0, "0x"); break;
        }

        return s.toString();
    }

    public static String computeArithmetic (int firstNumber, int secondNumber, String operator, int outputSystem) {
        int result;

        // Do calculations
        switch (operator) {
            case "+": result = firstNumber + secondNumber; break;
            case "-": result = firstNumber - secondNumber; break;
            case "*": result = firstNumber * secondNumber; break;
            case "/": result = (secondNumber == 0) ? -1 : firstNumber / secondNumber; break;
            default: result = -1;
        }

        // Check for negative result
        return result < 0 ? "Calculation Error" : convertToNumberSystem(result, outputSystem);
    }
}
