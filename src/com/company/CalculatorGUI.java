package com.company;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 *
 * @author Matthias Utzschneider, Daniel Krauss, Robert Richer
 */
public class CalculatorGUI extends JFrame {

    private static final long serialVersionUID = -2626163811541207898L;
    private JTextField textField;
    private JTextField systField;
    private JTextField inputTextField;
    private JTextField outputTextField;

    private JButton[] numbers; // 1 to 0
    private JButton[] operators; // operator buttons

    // system choosing
    private JComboBox inputSystem;
    private JComboBox outputSystem;

    private String[] gapList = { "decimal", "binary", "octal", "hexadecimal" };

    private GridBagLayout gridLayout;
    private GridBagConstraints gbc;
    private String nextOperationString;
    private String resultString;

    private int numbersCount = 0;
    private int interimResult = 0;

    // control variables
    private int inputMode = 10;
    private int outputMode = 10;
    private boolean clear = false; // =-Button pressed
    private boolean resultCalled = false; // C-Button pressed
    private boolean lastInputOperator = false;

    private static Method intMethod;
    private static Method numberSystemMethod;
    private static Method computeArithmeticMethod;

    static {
        try {
            intMethod = Calculator.class.getMethod("convertToInt", String.class, int.class);
            numberSystemMethod = Calculator.class.getMethod("convertToNumberSystem", int.class, int.class);
            computeArithmeticMethod = Calculator.class.getMethod("computeArithmetic", int.class, int.class,
                    String.class, int.class);
        } catch (NoSuchMethodException | SecurityException e) {
            System.err.println("Method \"" + e.getMessage() + "\" is missing!");
            intMethod = null;
            numberSystemMethod = null;
            computeArithmeticMethod = null;
        }
    }

    // Gesamt: 10 Punkte
    public static String output(String firstNumber, String secondNumber, String operator, int inputSystem,
                                int outputSystem) throws InvocationTargetException, IllegalAccessException {
        String resultString = "";

        int firstInt = (int) intMethod.invoke(null, firstNumber, inputSystem);
        if (firstInt == -1) {
            return "Number Format Error";
        }

        String firstOut = (String) numberSystemMethod.invoke(null, firstInt, outputSystem);

        int secondInt = -1;
        String secondOut = "";

        resultString += firstOut;

        if (secondNumber.length() != 0) {
            secondInt = (int) intMethod.invoke(null, secondNumber, inputSystem);
            if (secondInt == -1) {
                return "Number Format Error";
            }

            secondOut = (String) numberSystemMethod.invoke(null, secondInt, outputSystem);
            resultString += (" " + operator + " " + secondOut + " = ");

            switch (operator) {
                case "+":
                case "-":
                case "*":
                case "/":
                    resultString += (String) computeArithmeticMethod.invoke(null, firstInt, secondInt, operator,
                            outputSystem);
                    break;
                default:
                    return "Calculation Error";
            }
        }

        return resultString;
    }

    private void setButton(Container pane, int choose, String buttonText, int x, int y) {
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        nextOperationString = "";
        resultString = "";

        if (choose == 12) {
            operators[3] = new JButton(buttonText);
            operators[3].setForeground(new Color(230, 81, 0));
            operators[3].setOpaque(true);
            operators[3].setFont(operators[3].getFont().deriveFont(Font.BOLD));
            pane.add(operators[3], gbc);
        } else if (choose == 14) {
            operators[4] = new JButton(buttonText);
            operators[4].setForeground(new Color(21, 101, 192));
            operators[4].setOpaque(true);
            operators[4].setFont(operators[4].getFont().deriveFont(Font.BOLD));
            pane.add(operators[4], gbc);
        } else if (choose == 15) {
            operators[5] = new JButton(buttonText);
            operators[5].setForeground(new Color(56, 142, 60));
            operators[5].setOpaque(true);
            operators[5].setFont(operators[5].getFont().deriveFont(Font.BOLD));
            pane.add(operators[5], gbc);
        } else if (choose > 0 && (choose + 1) % 4 == 0 && choose < 19) {
            operators[choose / 4] = new JButton(buttonText);
            operators[choose / 4].setForeground(new Color(67, 160, 71));
            operators[choose / 4].setOpaque(true);
            operators[choose / 4].setFont(operators[choose / 4].getFont().deriveFont(Font.BOLD));
            pane.add(operators[choose / 4], gbc);
        } else {
            numbers[numbersCount] = new JButton(buttonText);
            pane.add(numbers[numbersCount], gbc);
            numbersCount++;
        }
    }

    private void addComponentsToPane(Container pane) {
        gbc = new GridBagConstraints();
        gridLayout = new GridBagLayout();

        setLayout(gridLayout);

        gbc.weighty = 1.0; // request any extra vertical space
        gbc.weightx = 1.0; // request any extra vertical space
        gbc.insets = new Insets(2, 2, 2, 2); // padding

        // Textfield row
        textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 15));
        textField.setEditable(false);
        gbc.ipady = 40;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pane.add(textField, gbc);

        systField = new JTextField();
        systField.setPreferredSize(new Dimension(150, 5));
        systField.setEditable(false);
        gbc.ipady = 40;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pane.add(systField, gbc);

        inputTextField = new JTextField("Input System:");
        inputTextField.setPreferredSize(new Dimension(75, 5));
        inputTextField.setEditable(false);
        inputTextField.setBackground(this.getBackground());
        inputTextField.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 6, 0, 4, this.getBackground()));
        gbc.ipady = 20;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pane.add(inputTextField, gbc);

        outputTextField = new JTextField("Output System:");
        outputTextField.setPreferredSize(new Dimension(75, 5));
        outputTextField.setEditable(false);
        outputTextField.setBackground(this.getBackground());
        outputTextField.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 6, 0, 4, this.getBackground()));
        gbc.ipady = 20;
        gbc.gridwidth = 2;
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pane.add(outputTextField, gbc);

        // Choose Number System Row
        inputSystem = new JComboBox<String>(gapList);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.ipady = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pane.add(inputSystem, gbc);

        outputSystem = new JComboBox<String>(gapList);
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.ipady = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pane.add(outputSystem, gbc);

        // number+operator rows
        int row = 4;

        numbers = new JButton[16];
        operators = new JButton[6];

        // setup buttons
        setButton(pane, 0, "1", 0, row);
        setButton(pane, 1, "2", 1, row);
        setButton(pane, 2, "3", 2, row);
        setButton(pane, 3, "+", 3, row);
        row++;

        setButton(pane, 4, "4", 0, row);
        setButton(pane, 5, "5", 1, row);
        setButton(pane, 6, "6", 2, row);
        setButton(pane, 7, "-", 3, row);
        row++;

        setButton(pane, 8, "7", 0, row);
        setButton(pane, 9, "8", 1, row);
        setButton(pane, 10, "9", 2, row);
        setButton(pane, 11, "*", 3, row);
        row++;

        setButton(pane, 12, "CE", 0, row);
        setButton(pane, 13, "0", 1, row);
        setButton(pane, 14, "=", 2, row);
        setButton(pane, 15, "/", 3, row);
        row++;

        setButton(pane, 16, "A", 0, row);
        setButton(pane, 17, "B", 1, row);
        setButton(pane, 18, "C", 2, row);
        row++;

        setButton(pane, 19, "D", 0, row);
        setButton(pane, 20, "E", 1, row);
        setButton(pane, 21, "F", 2, row);
        row++;

        gbc.weighty = 1.0;
        gbc.weightx = 1.0;

    }

    private String output(String[] array, int inputMode, int outputMode) {
        String result = "";
        try {
            switch (array.length) {
                case 1:
                    result = output(array[0], "", "", inputMode, outputMode);
                    break;
                case 2:
                    result = output(array[0], "", array[1], inputMode, outputMode);
                    break;
                case 3:
                    result = output(array[0], array[2], array[1], inputMode, outputMode);
                    break;
                case 4:
                    result = output(array[0], array[2], array[1], inputMode, outputMode);
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    private void resultCalled(boolean resultCalled) {
        String[] stringArray = nextOperationString.split(" ");
        String result = output(stringArray, inputMode, outputMode);
        resultString = result.split(" ")[result.split(" ").length - 1];

        String resultIntString = "";
        try {
            if (outputMode == 8) {
                resultIntString = Integer.toString(Integer.valueOf(resultString.substring(1), outputMode), inputMode);
            } else if (outputMode == 16) {
                resultIntString = Integer.toString(Integer.valueOf(resultString.substring(2), outputMode), inputMode);
            } else {
                resultIntString = Integer.toString(Integer.valueOf(resultString, outputMode), inputMode);
            }
        } catch (NumberFormatException e) {
            resultIntString = "";
        }

        if (resultCalled) {
            nextOperationString = "";
        } else {
            nextOperationString = resultIntString + " " + stringArray[stringArray.length - 1] + " ";
        }

        systField.setText(resultString);
        textField.setText(resultIntString + " ");

        return;
    }

    // display method - always called if things are changed
    private void display() {

        String[] stringArray = nextOperationString.split(" ");
        // C pressed
        if (clear) {
            nextOperationString = "";
            systField.setText("");
            textField.setText("");
            clear = false;
            return;
        }
        // = pressed
        else if (resultCalled) {
            if (stringArray.length > 2) {
                resultCalled(resultCalled);
                resultCalled = false;
            } else {
                nextOperationString = "";
                systField.setText("");
                textField.setText("");
                resultCalled = false;
            }
            return;
        }

        String result = output(stringArray, inputMode, outputMode);

        if (stringArray.length == 4) {
            resultCalled(false);
        }
        resultString = result;

        if (result.equals("Number Format Error") || result.equals("Calculation Error")) {
            nextOperationString = "";
            systField.setText(resultString);
        } else {
            systField.setText(resultString);
        }

        return;

    }

    // Initialization of button call-backs
    private void addActionListenersForButtons() {

        numbersCount = 0;

        // numbers 0-16
        for (final JButton number : numbers) {
            number.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    textField.setText(nextOperationString + number.getText());
                    nextOperationString += number.getText();
                    display();
                    lastInputOperator = false;
                }
            });
        }

        // operators
        for (final JButton operator : operators) {
            operator.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    if (!lastInputOperator) {
                        textField.setText(nextOperationString + operator.getText());
                        nextOperationString = nextOperationString + " " + operator.getText() + " ";
                        resultString += " " + operator.getText() + " ";
                        display();
                        lastInputOperator = true;
                    }
                }
            });
        }

        operators[3].addActionListener(new ActionListener() { // CE
            @Override
            public void actionPerformed(ActionEvent event) {
                textField.setText(nextOperationString + operators[3].getText());
                nextOperationString = nextOperationString + " " + operators[3].getText() + " ";
                resultString += " " + operators[3].getText() + " ";
                clear = true;
                display();
            }
        });
        operators[4].addActionListener(new ActionListener() { // =
            @Override
            public void actionPerformed(ActionEvent event) {
                textField.setText(nextOperationString + operators[4].getText());
                nextOperationString = nextOperationString + " " + operators[4].getText() + " ";
                resultString += " " + operators[4].getText() + " ";

                display();
            }
        });

        // inputMode menu
        inputSystem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox box = (JComboBox) event.getSource();
                String system = (String) box.getSelectedItem();
                if (system.equals("decimal")) {
                    inputMode = 10;
                    display();
                } else if (system.equals("binary")) {
                    inputMode = 2;
                    display();
                } else if (system.equals("octal")) {
                    inputMode = 8;
                    display();
                } else if (system.equals("hexadecimal")) {
                    inputMode = 16;
                    display();
                }

            }
        });

        // outputMode menu
        outputSystem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox box = (JComboBox) event.getSource();
                String system = (String) box.getSelectedItem();
                if (system.equals("decimal")) {
                    outputMode = 10;
                    display();
                } else if (system.equals("binary")) {
                    outputMode = 2;
                    display();
                } else if (system.equals("octal")) {
                    outputMode = 8;
                    display();
                } else if (system.equals("hexadecimal")) {
                    outputMode = 16;
                    display();
                }

            }
        });
    }

    CalculatorGUI() {
        super("Calculator");

        // initialize components and action listeners
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(getContentPane());
        addActionListenersForButtons();

        pack();
        setVisible(true);
    }

}
