package sample;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;


public class MainController {

    Boolean penDown = false;
    Color color = Color.WHITE;
    Image turtle;
    Image greenTurtle;
    BufferedImage bufferedTurtle;
    BufferedImage bufferedGreenTurtle;
    Interpreter interpreter = new Interpreter();
    private double CENTERX = 0;
    private double CENTERY = 0;
    double angle;
    double x;
    double y;
    Path path = new Path();


    private Reader reader;
    private String currentlySelectedLine = null;
    private ArrayList list = new ArrayList();


    private Stage primaryStage;

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    @FXML
    private MenuItem menuClear;

    @FXML
    private MenuItem menuClose;

    @FXML
    private MenuItem menuOpenFile;

    @FXML
    private MenuItem menuAbout;

    @FXML
    private Button btnDrawAll;

    @FXML
    private Button btnDrawLine;

    @FXML
    private Button btnRemove;

    @FXML
    private Canvas canvasTop;

    @FXML
    private Canvas canvasBtm;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField textField;

    @FXML
    private Button btnAddLine;

    @FXML
    private Group group;


    @FXML
    private void onMenuClose() {

    }


    GraphicsContext gcTop;
    GraphicsContext gcBtm;


    @FXML
    private void initialize() {

        gcTop = canvasTop.getGraphicsContext2D();
        gcBtm = canvasBtm.getGraphicsContext2D();

        CENTERX = gcBtm.getCanvas().getWidth() / 2;
        CENTERY = gcBtm.getCanvas().getHeight() / 2;
        x = CENTERX;
        y = CENTERY;
        angle = 0;


        drawImage();
////////////////////////RYSOWANIE Z KLAWIATURY
        canvasTop.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case S -> moveImageDown();
                    case W -> moveImageUp();
                    case A -> moveImageLeft();
                    case D -> moveImageRight();
                }
            }
        });
//////////////////////////////////////////////

    }

    @FXML
    void onMenuClear(ActionEvent event) {
        System.out.println("test clear");
        gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
        gcBtm.clearRect(0, 0, canvasBtm.getWidth(), canvasBtm.getHeight());
        group.getChildren().clear();
        group.getChildren().add(canvasBtm);
        group.getChildren().add(canvasTop);
        x = CENTERX;
        y = CENTERY;
        angle = 0;
        interpreter.setAngle(0);
        interpreter.setPointAngle(0);
        drawImage();

    }

    @FXML
    void onMenuClose(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Kończenie pracy");
        alert.setTitle("( ͡° ͜ʖ ͡°)");
        alert.setContentText("Czy chcesz zamknąć aplikację?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            event.consume();
            primaryStage.close();
        }
    }

    @FXML
    void onMenuAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Hello Turtle \nversion 0.1");
        alert.setTitle("( ͡° ͜ʖ ͡°)");
        alert.setContentText("Dev: Mateusz Żebrowski");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            event.consume();
            primaryStage.close();
        }
    }

    @FXML
    void onMenuLoad(ActionEvent event) throws IOException {
        textArea.clear();
        String text;
        String line = null;
        reader = new Reader();
        try {
            list = (ArrayList) reader.getText();
        } catch (NullPointerException e) {

        }

        for (int i = 0; i < list.size(); i++) {
            line = list.get(i).toString();
            textArea.appendText(line);
            textArea.appendText("\n");
        }

        textArea.selectPositionCaret(0);
        int caretPosition = textArea.getCaretPosition();
        text = textArea.getText();
        int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
        int lineBreak2 = text.indexOf('\n', caretPosition);
        textArea.selectRange(lineBreak1, lineBreak2);
        currentlySelectedLine = textArea.getSelectedText();
        interpreter.interpretLine(currentlySelectedLine);
        textArea.requestFocus();
        System.out.println(currentlySelectedLine);
    }

    @FXML
    void onTxtMouse(MouseEvent evt) {

        if (evt.getButton() == MouseButton.PRIMARY) {
            // check, if click was inside the content area
            Node n = evt.getPickResult().getIntersectedNode();
            while (n != textArea) {
                if (n.getStyleClass().contains("content")) {
                    // find previous/next line break
                    int caretPosition = textArea.getCaretPosition();
                    String text = textArea.getText();
                    int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
                    int lineBreak2 = text.indexOf('\n', caretPosition);
                    if (lineBreak2 < 0) {
                        // if no more line breaks are found, select to end of text
                        lineBreak2 = text.length();
                    }

                    textArea.selectRange(lineBreak1, lineBreak2);
                    System.out.println(textArea.getSelectedText());
                    evt.consume();
                    break;
                }
                n = n.getParent();
            }
        }
        currentlySelectedLine = textArea.getSelectedText();
        interpreter.interpretLine(currentlySelectedLine);
    }

    @FXML
    void onRemove(ActionEvent event) {
        textArea.deleteText(textArea.getSelection());
    }

    @FXML
    void onAddLine(ActionEvent event) {
        interpreter.setAngle(0);
        interpreter.setPointAngle(0);
        onAddLine();
    }

    @FXML
    void onTxtFiledEnterPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            onAddLine();
        }
    }

    @FXML
    void onDrawAll(ActionEvent event) {

        if ("".equals(textArea.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Linia komend Pusta");
            alert.setTitle("( ͡° ͜ʖ ͡°)");
            alert.setContentText("Załaduj polecenia");
            alert.showAndWait();
        } else {
            String toCount = textArea.getText();
            String[] lineArray = toCount.split("\n");
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        onDrawLine();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }));
            timeline.setCycleCount(lineArray.length);
            timeline.play();

        }
    }

    @FXML
    void onDrawLine(ActionEvent event) throws InterruptedException {
        onDrawLine();

    }

    private void onDrawLine() throws InterruptedException {
        if (textArea.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Linia komend Pusta");
            alert.setTitle("( ͡° ͜ʖ ͡°)");
            alert.setContentText("Załaduj polecenia");
            alert.showAndWait();
        } else {
            penDown = interpreter.isPenDown();

            if (interpreter.getCommand().contains("ustaw")) {
                angle += interpreter.getAngle();
                x = interpreter.getX();
                y = interpreter.getY();
                path.getElements().clear();
                path.getElements().add(new MoveTo(x, y));
                gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
                gcTop.drawImage(getTurtleImage(getTurtleImg(), angle), x - 12, y - 12);


            } else if (interpreter.getCommand().contains("obrot")) {
                if (penDown) {
                    gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
                    gcTop.drawImage(getGreenTurtle(getTurtleImg(), angle), x - 12, y - 12);
                } else {
                    gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
                    gcTop.drawImage(getTurtleImage(getTurtleImg(), angle), x - 12, y - 12);
                }
                if (angle >= 360) {
                    angle = 0;
                } else {
                    angle += interpreter.getAngle();

                }
            } else if (interpreter.getCommand().contains("podnies")) {
                gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
                gcTop.drawImage(getTurtleImage(getBufferedTurtle(), angle), x - 12, y - 12);
            } else if (interpreter.getCommand().contains("opusc")) {
                gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
                gcTop.drawImage(getGreenTurtle(getGreenTurtleImg(), angle), x - 12, y - 12);
            } else if (interpreter.getCommand().contains("naprzod")) {
                if (penDown) {
                    Point newPoint;
                    newPoint = getNextPoint(interpreter.getValue(), angle);
                    gcBtm.setStroke(color);
                    gcBtm.setLineWidth(3);
                    gcBtm.beginPath();
                    gcBtm.moveTo(x, y);
                    gcBtm.lineTo(newPoint.getX(), newPoint.getY());
                    //KeyFrame kf = new KeyFrame(Duration.seconds(1), e -> gcBtm.strokeLine(x,y,newPoint.getX(), newPoint.getY()));
                    gcBtm.stroke();
                    gcBtm.closePath();
                    gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
                    x = newPoint.getX();
                    y = newPoint.getY();
                    gcTop.drawImage(getGreenTurtle(getBufferedGreenTurtleTurtle(), angle), x - 12, y - 12);

                } else {
                    gcTop.clearRect(0, 0, canvasTop.getWidth(), canvasTop.getHeight());
                    Point newPoint;
                    newPoint = getNextPoint(interpreter.getValue(), angle);
                    x = newPoint.getX();
                    y = newPoint.getY();
                    gcTop.drawImage(getTurtleImage(getBufferedTurtle(), angle), x - 12, y - 12);
                }
            }
            System.out.println(angle);
            System.out.println(interpreter.toString());
            selectNextLineInTextArea();
            textArea.requestFocus();

        }
    }

    private void onAddLine() {
        textArea.appendText(textField.getText() + ";");
        textField.clear();
        textArea.appendText("\n");
    }

    private void drawImage() {
        gcTop.drawImage(SwingFXUtils.toFXImage(getTurtleImg(), null), x, y);
    }

    private void selectNextLineInTextArea() throws InterruptedException {
        String text = textArea.getText();
        int caretPosition = textArea.getCaretPosition() + 1;

        int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
        int lineBreak2 = text.indexOf('\n', caretPosition);
        if (lineBreak2 < 0) {
            // if no more line breaks are found, select to end of text
            lineBreak2 = text.length();
        }

        textArea.selectRange(lineBreak1, lineBreak2);
        currentlySelectedLine = textArea.getSelectedText();
        interpreter.interpretLine(currentlySelectedLine);

    }

    protected BufferedImage getTurtleImg() {
        try {
            bufferedTurtle = ImageIO.read(new File("src/resources/turtle.png"));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return bufferedTurtle;
    }

    protected BufferedImage getGreenTurtleImg() {
        try {
            bufferedGreenTurtle = ImageIO.read(new File("src/resources/greenTurtle.png"));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return bufferedGreenTurtle;
    }


    protected Image getTurtleImage(BufferedImage img, double angle) {
        BufferedImage temp = img;
        AffineTransform tx = new AffineTransform();
        tx.rotate(Math.toRadians(angle), temp.getWidth() / 2, temp.getHeight() / 2);

        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
        temp = op.filter(temp, null);

        turtle = SwingFXUtils.toFXImage(temp, null);
        return turtle;
    }

    protected Image getGreenTurtle(BufferedImage img, double angle) {

        BufferedImage temp;
        BufferedImage bi = img;
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        temp = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        AffineTransform tx = new AffineTransform();
        tx.rotate(Math.toRadians(angle), temp.getWidth() / 2, temp.getHeight() / 2);

        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);

        temp = op.filter(temp, null);
        greenTurtle = SwingFXUtils.toFXImage(temp, null);
        return greenTurtle;
    }

    public BufferedImage getBufferedTurtle() {
        return bufferedTurtle;
    }

    public BufferedImage getBufferedGreenTurtleTurtle() {
        return bufferedGreenTurtle;
    }

    protected Point getNextPoint(int distance, double angle) {
        Point point = new Point();
        point.setLocation(this.x + distance * Math.cos(Math.toRadians(angle)), this.y + distance * Math.sin(Math.toRadians(angle)));
        System.out.println(point.getX() + " " + point.getY());
        return point;
    }

    private void moveImageDown() {
        gcTop.clearRect(x, y, 24, 24);
        y += 6;
        gcTop.drawImage(getTurtleImage(getTurtleImg(), 90), x, y);
        gcBtm.fillRect(x + 9, y + 9, 6, 6);
    }

    private void moveImageUp() {
        gcTop.clearRect(x, y, 24, 24);
        y -= 6;
        gcTop.drawImage(getTurtleImage(getTurtleImg(), 270), x, y);
        gcBtm.fillRect(x + 9, y + 9, 6, 6);
    }

    private void moveImageLeft() {
        gcTop.clearRect(x, y, 24, 24);
        x -= 6;
        gcTop.drawImage(getTurtleImage(getTurtleImg(), 180), x, y);
        gcBtm.fillRect(x + 9, y + 9, 6, 6);
    }

    private void moveImageRight() {
        gcTop.clearRect(x, y, 24, 24);
        x += 6;
        gcTop.drawImage(getTurtleImage(getTurtleImg(), 0), x, y);
        gcBtm.fillRect(x + 9, y + 9, 6, 6);
    }


    public Canvas getCanvasTop() {
        return canvasTop;
    }

    public Canvas getCanvasBtm() {
        return canvasBtm;
    }

}
