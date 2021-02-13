package sample;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class ViewLoader<T,U> {
    private T layout = null;
    private U controller = null;
    public ViewLoader(String fxmlFilePath) {
        try {
            FXMLLoader fxmlLoader = new
                    FXMLLoader(getClass().getResource(fxmlFilePath));
            layout = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException ex) {
            System.out.println("ViewLoader exception: "+ ex.getMessage());
        }
    }
    public T getLayout(){ return layout; }
    public U getController(){ return controller; }
}
