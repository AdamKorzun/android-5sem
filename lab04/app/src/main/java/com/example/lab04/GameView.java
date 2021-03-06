package com.example.lab04;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GameView extends View {
    private Cell[][] cells;
    int COLS = 7;
    int ROWS = 5;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint,  exitPaint;
    private static final float WALL_THICKNESS = 7;
    private Random random;
    private float playerSize;
    private Cell player, exit;


    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Constants.CURRENT_CONTEXT = context;
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        playerPaint = new Paint();
        playerPaint.setColor(Color.BLUE);

        exitPaint = new Paint();
        exitPaint.setColor(Color.GREEN);

        random = new Random();
        createMaze();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawMaze(canvas);

        playerSize = (float)(cellSize  / 2.2);
        canvas.drawCircle(player.col * cellSize + cellSize / 2, player.row * cellSize + cellSize /2, playerSize, playerPaint);
        canvas.drawRect(exit.col * cellSize + WALL_THICKNESS,
                exit.row * cellSize + WALL_THICKNESS,
                (exit.col + 1) * cellSize - WALL_THICKNESS,
                (exit.row + 1) * cellSize - WALL_THICKNESS,
                exitPaint);

    }
    private void drawMaze(Canvas canvas){
        int width = getWidth();
        int height = getHeight();
        if (width / height < COLS/ROWS){
            cellSize = width / (COLS  + 1);
        }
        else{
            cellSize = height / (ROWS + 1);
        }

        for (int x =0; x < COLS; x++){
            for (int y=0; y < ROWS; y++){
                if (cells[x][y].topWall){
                    canvas.drawLine(x * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            y * cellSize,
                            wallPaint);
                }
                if (cells[x][y].leftWall){
                    canvas.drawLine(x * cellSize,
                            y * cellSize,
                            x * cellSize,
                            (y + 1) * cellSize,
                            wallPaint);
                }
                if (cells[x][y].bottomWall){
                    canvas.drawLine(x * cellSize,
                            (y + 1) * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint);
                }
                if (cells[x][y].rightWall){
                    canvas.drawLine((x+ 1) * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint);
                }
            }
        }
    }
    public void moveUp(){
        if (!player.topWall){
            player = cells[player.col ][player.row - 1];
        }
    }
    public void moveDown(){

        if (!player.bottomWall){
            player = cells[player.col ][player.row + 1];
        }
    }
    public void moveLeft(){
        if (!player.leftWall){
            player = cells[player.col - 1][player.row];
        }
    }
    public void moveRight(){
        if (!player.rightWall){
            player = cells[player.col + 1][player.row];
        }
    }
    public boolean hasWon(){
        if (player == exit){
            return true;
        }
        return false;
    }

    private void createMaze(){
        Stack<Cell> stack = new Stack<>();
        Cell current, next;
        cells = new Cell[COLS][ROWS];
        for (int x =0; x < COLS; x++){
            for (int y=0; y < ROWS; y++){
                cells[x][y] = new Cell(x, y);
            }
        }
        player = cells[0][0];
        exit = cells[COLS - 1][ROWS - 1];

        current = cells[0][0];
        current.visited = true;
        do {
            next = getNeighbour(current);
            if (next != null){
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            }
            else {
                current = stack.pop();
            }
        }while (!stack.empty());

    }
    private Cell getNeighbour(Cell cell){
        ArrayList<Cell> neighbours = new ArrayList<>();
        // left
        if (cell.col > 0){
            if (!cells[cell.col - 1][cell.row].visited){
                neighbours.add(cells[cell.col - 1][cell.row]);
            }
        }
        // right
        if (cell.col < COLS - 1){
            if (!cells[cell.col + 1][cell.row].visited){
                neighbours.add(cells[cell.col + 1][cell.row]);
            }
        }
        // top
        if (cell.row > 0){
            if (!cells[cell.col][cell.row - 1].visited){
                neighbours.add(cells[cell.col][cell.row - 1]);
            }
        }
        // bottom
        if (cell.row < ROWS - 1){
            if (!cells[cell.col][cell.row + 1].visited){
                neighbours.add(cells[cell.col][cell.row + 1]);
            }
        }
        if (neighbours.size() > 0){
            int index = random.nextInt(neighbours.size());
            return neighbours.get(index);
        }
        return null;


    }
    private void removeWall(Cell current, Cell next){
        if (current.col == next.col && current.row == next.row + 1){
            current.topWall = false;
            next.bottomWall = false;
        }

        if (current.col == next.col && current.row == next.row - 1){
            current.bottomWall = false;
            next.topWall = false;
        }

        if (current.col == next.col + 1 && current.row == next.row){
            current.leftWall = false;
            next.rightWall = false;
        }
        if (current.col == next.col - 1 && current.row == next.row){
            current.rightWall = false;
            next.leftWall = false;
        }
    }
    private class Cell{
        boolean topWall = true;
        boolean leftWall = true;
        boolean bottomWall = true;
        boolean rightWall = true;
        boolean visited = false;
        int col, row;
        public Cell(int col, int row){
            this.col = col;
            this.row = row;
        }
    }
    public void recreate(int columns, int rows){
        COLS = columns;
        ROWS = rows;
        createMaze();
    }
    public void restartLevel(){
        player = cells[0][0];
    }


}
