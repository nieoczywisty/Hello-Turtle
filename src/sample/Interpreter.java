package sample;

import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Interpreter {

    private String command;
    private int value;
    private int x;
    private int y;
    private int pointX;
    private int pointY;
    private int pointAngle = 0;
    private int angle = 0;
    private boolean penDown = false;




    public Interpreter() {

    }

    protected void interpretLine(String line) {
        command = null;
        value = 0;
        x = 0;
        y = 670;
        angle = 0;
        pointAngle = 0;
        String stringLine = line;
        command = stringLine.replaceAll("[\\d,.;\\[\\]]", "");
        
        String afterReplace;
        System.out.println("Tutej:["+command+"]");
        if (command.contains("obrot")) {
            System.out.println("Tutej obrot:"+command);
            String stringAngle = stringLine.replaceAll("[^0-9.]", "");
            System.out.println("stringAngle: " + stringAngle);
            angle -= Integer.parseInt(stringAngle);
            pointAngle += Integer.parseInt(stringAngle);
        }else if (command.contains("ustaw")) {
            afterReplace = line.replaceFirst(",","!");
            String stringX = afterReplace.substring(afterReplace.indexOf("[") + 1, afterReplace.indexOf("!"));
            System.out.println("stringX: " + stringX);
            x = Integer.parseInt(stringX);
            System.out.println("x: " + x);
            String stringY = afterReplace.substring(afterReplace.indexOf("!") + 1, afterReplace.indexOf("]"));
            System.out.println("stringY: " + stringY);
            y = 670 - Integer.parseInt(stringY);
            System.out.println("y: "+ y);
            String stringAngle = stringLine.substring(afterReplace.lastIndexOf(" ") + 1, afterReplace.indexOf(";"));
            angle +=  Integer.parseInt(stringAngle);
            pointAngle += Integer.parseInt(stringAngle);
            pointX = x;
            pointY = y;
        } else if (command.contains("naprzod")) {
            String stringValue = stringLine.replaceAll("[^0-9.]", "");
            value = Integer.parseInt(stringValue);
            if(penDown){
                pointX = (int) getNextPoint(value,pointAngle).getX();
                pointY = 670 - (int) getNextPoint(value,pointAngle).getY();
            }else if(!penDown){
                pointX = (int) getNextPoint(value,pointAngle).getX();
                pointY = 670 - (int) getNextPoint(value,pointAngle).getY();
            }
        }else if (command.contains("opusc")){
            penDown = true;
        }else if (command.contains("podnies")){
            penDown = false;
    }
        System.out.println(toString());
    }
    protected Point getNextPoint(int distance, double angle) {
        Point point = new Point();
        point.setLocation(pointX + distance * Math.cos(Math.toRadians(angle)), pointY + distance * Math.sin(Math.toRadians(angle)));
        System.out.println("point X: " + point.getX() + " point Y: " + point.getY());
        return point;
    }

    public String getCommand() {
        return command;
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAngle() {
        return angle;
    }

    public boolean isPenDown() {
        return penDown;
    }


    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setPointAngle(int pointAngle) {
        this.pointAngle = pointAngle;
    }

    @Override
    public String toString() {
        return command + " value: " + value + " x: " + x + " y: " + y + " angle: " + angle + " isPenDown: " + isPenDown();
    }
}

