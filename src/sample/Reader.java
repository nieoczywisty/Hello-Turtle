package sample;

import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Reader {
    File selectedFile;
    BufferedReader reader;
    FileChooser fileChooser;
    ArrayList text;


    protected ArrayList getText() throws IOException {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        selectedFile = fileChooser.showOpenDialog(null);
        reader = new BufferedReader(new FileReader(selectedFile));

        text = new ArrayList();
        String contentLine;
        while ((contentLine = reader.readLine()) != null) {
            text.add(contentLine);
        }

        return text;
    }

}
