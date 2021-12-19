package com.example.lab01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private String calculationResult = "";
    private String calcString = "";
    private Stack<String> stack = new Stack<>();
    private boolean prefixMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeAllButtons((LinearLayout)findViewById(R.id.buttonsLayout));
        System.out.println(calculationResult);
        if (!calcString.equals("")){
            TextView tv = findViewById(R.id.inputHistory);
            tv.setText(calcString.replace("*", "×"));
        }
        if (!!calculationResult.isEmpty()){
            TextView calcResView = findViewById(R.id.calculationResults);
            calcResView.setText(calculationResult);

        }
    }
    private void initializeAllButtons(LinearLayout mainLayout){
        Button equalsButton = findViewById(R.id.equalsButton);
        equalsButton.setOnClickListener(buttonClickListener);
        for (int i = 0; i < mainLayout.getChildCount(); i++){
            LinearLayout innerLayout = (LinearLayout)mainLayout.getChildAt(i);
            for (int j = 0; j < innerLayout.getChildCount(); j++){
                Button btn = (Button)innerLayout.getChildAt(j);
                System.out.println(btn);
                btn.setOnClickListener(buttonClickListener);
            }
        }
    }

    View.OnClickListener buttonClickListener = new View.OnClickListener(){
        public void onClick(View v){
            Button workingButton = (Button)v;
            String buttonText = workingButton.getText().toString();
            handleInput(buttonText);
            calcString = "";

            for (String el:stack) {
                calcString += el;
            }
            TextView tv = findViewById(R.id.inputHistory);
            tv.setText(calcString.replace("*", "×"));
        }
    };

    private void handleInput(String buttonText){
        if (isNumeric(buttonText)){
            stack.add(buttonText);
        }
        else{
            if (buttonText.equals("+") || buttonText.equals("/")){
                if ((!stack.empty() && isNumeric(stack.lastElement().toString())) || prefixMode){
                    stack.add(buttonText);
                }
                else if (stack.size() > 1){
                    stack.pop();
                    stack.add(buttonText);
                }
            }
            else if (buttonText.equals("-")){
                if (stack.empty() || prefixMode){
                    stack.add(buttonText);
                }
                else if (isNumeric(stack.lastElement().toString())){
                    stack.add(buttonText);
                }
                else if (!stack.empty()){
                    stack.pop();
                    if (!stack.empty()){
                        stack.add(buttonText);
                    }

                }
            }
            else if (buttonText.equals("☒")){
                if (!stack.empty()){
                    stack.pop();
                }
            }
            else if (buttonText.equals("×")){
                if ((!stack.empty() && isNumeric(stack.lastElement().toString()))|| this.prefixMode){
                    stack.add("*");
                }
                else if (stack.size() > 1){
                    stack.pop();
                    stack.add("*");
                }
            }
            else if (buttonText.equals(".")){

                if (!stack.empty()  && isNumeric(stack.lastElement().toString())){
                    stack.add(buttonText);
                }
            }
            else if (buttonText.equals("=")){
                if (!stack.empty()){
                    if (!isNumeric(stack.lastElement())){
                        stack.pop();
                    }
                }

                TextView calcResView = findViewById(R.id.calculationResults);
                if (prefixMode){
                    try{
                        calculationResult = String.valueOf(calculatePrefixNotation(tokenize(calcString)));
                    }
                    catch (Exception e){
                        calculationResult=getString(R.string.inputError);
                    }
                }
                else{
                    try {
                        calculationResult = String.valueOf(calculatePrefixNotation(convertToPrefixNotation(tokenize(calcString))));

                    }
                    catch (Exception e){
                        calculationResult = "Error";
                    }
                }
                calcResView.setText(calculationResult);
            }
            else if (buttonText.equals("AC")){
                stack.clear();
                calculationResult = "";
                TextView calcResView = findViewById(R.id.calculationResults);
                calcResView.setText("");
            }
            else if (buttonText.equals("PN")){
                this.prefixMode = !this.prefixMode;
                System.out.println(this.prefixMode);
            }
            else if (buttonText.equals("SPC")){
                if (!stack.empty() && prefixMode){
                    if (!stack.peek().equals(" ")){
                        stack.add(" ");
                    }
                }
            }
        }
    }
    public Stack<Object> convertToPrefixNotation(Stack<Object> expression){
        Dictionary operatorValues = new Hashtable();
        operatorValues.put("-", 1);
        operatorValues.put("+", 2);
        operatorValues.put("*", 3);
        operatorValues.put("/", 4);
        Stack<Object> prefixStack = new Stack<>();
        Stack<Object> operatorStack = new Stack<>();
        while (!expression.empty()){
            Object symbol = expression.pop();
            System.out.println("Input: " + symbol.toString() + " " +prefixStack.toString());

            if (symbol.getClass() == Float.class){
                prefixStack.push(symbol);
            }
            else {
                if (!operatorStack.empty()){
                    if ((int)operatorValues.get(symbol) >= (int)operatorValues.get(operatorStack.peek())){
                        operatorStack.add(symbol);
                    }
                    else {
                        while (!operatorStack.empty()){
                            if ((int)operatorValues.get(operatorStack.peek()) > (int)operatorValues.get(symbol)){
                                prefixStack.add(operatorStack.pop());
                            }
                            else{
                                break;
                            }
                        }
                        operatorStack.add(symbol);

                    }
                }
                else {
                    operatorStack.add(symbol);

                }

            }
        }
        while (!operatorStack.empty()){
            prefixStack.add(operatorStack.pop());
        }
        return reverseStack(prefixStack);
    }
    private Stack<Object> tokenize(String stringExpression){
        Stack<Object> expression = new Stack<>();
        String[] stringArray = stringExpression.split("(?<=op)|(?=op)".replace("op", "[-+*/() ]"));
        for (int i = 0; i < stringArray.length;i++){
            if (isNumeric(stringArray[i])){
                expression.add(Float.parseFloat(stringArray[i]));
            }
            else {
                expression.add(stringArray[i]);
            }
        }
        Stack<Object> tempStack = new Stack<>();
        while (!expression.empty()){
            if (expression.peek().getClass() == String.class){
                if (expression.peek().equals(" ")){
                    expression.pop();
                }
            }
            tempStack.add(expression.pop());
        }
        System.out.println(tempStack.toString());
       return reverseStack(tempStack);
    }
    private Stack<Object> reverseStack(Stack<Object> stack){
        Stack<Object> tempStack = new Stack<>();
        while (!stack.empty()){
            tempStack.add(stack.pop());
        }
        return tempStack;
    }
    private boolean isNumeric(String testString){
        try {
            Float.parseFloat(testString);
            return true;
        } catch (NumberFormatException e){
            ;
        }
        return false;
    }
    private float calculatePrefixNotation(Stack<Object> prefixNotation){
        Stack<Float> tempStack = new Stack<>();

        while (!prefixNotation.empty()){
            if (prefixNotation.peek().getClass() == Float.class){
                tempStack.add((float)prefixNotation.pop());
            }
            else {
                String operator = prefixNotation.pop().toString();
                float first = tempStack.pop();
                float second = tempStack.pop();
                if (operator.equals("+")){
                    tempStack.add(first + second);
                }
                if (operator.equals("*")){
                    tempStack.add(first * second);
                }
                if (operator.equals("-")){
                    tempStack.add(first - second);
                }
                if (operator.equals("/")){
                    tempStack.add(first / second);
                }
            }
        }

        return tempStack.pop();
    }
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("calcString", calcString);
        outState.putString("calculationResult", calculationResult);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        TextView tv = findViewById(R.id.inputHistory);
        calcString = savedInstanceState.getString("calcString");
        tv.setText(calcString);
        TextView calcResView = findViewById(R.id.calculationResults);
        calculationResult = savedInstanceState.getString("calculationResult");
        calcResView.setText(calculationResult);
        for (Object obj :tokenize(calcString) ){
            stack.add(obj.toString());
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}