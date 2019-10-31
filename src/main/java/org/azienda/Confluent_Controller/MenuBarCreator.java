package org.azienda.Confluent_Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuBarCreator {

    public MenuBar init(MenuBar menuBar){
        menuBar.getMenus().clear();

        Menu menu1 = new Menu("File");
//        menu1.setGraphic(new ImageView("file:volleyball.png"));
//        menu1.setOnShowing(e -> { System.out.println("Showing Menu 1"); });
//        menu1.setOnShown  (e -> { System.out.println("Shown Menu 1"); });
//        menu1.setOnHiding (e -> { System.out.println("Hiding Menu 1"); });
//        menu1.setOnHidden (e -> { System.out.println("Hidden Menu 1"); });

        MenuItem menuItem1 = new MenuItem("Restart");
//        menuItem1.setGraphic(new ImageView("file:basketball.png"));
        menuItem1.setOnAction(e -> {
            swap("index.fxml", menuBar);
        });

        MenuItem menuItem2 = new MenuItem("Topic");
        menuItem2.setOnAction(e -> {
            swap("Topic/topics.fxml", menuBar);
        });

        MenuItem menuItem3 = new MenuItem("Connectors");
        menuItem3.setOnAction(e -> {
            swap("Connector/connectors.fxml", menuBar);
        });

        MenuItem menuItem4 = new MenuItem("Ksql");
        menuItem4.setOnAction(e -> {
            swap("KSQL/ksqls.fxml", menuBar);
        });

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem menuItem5 = new MenuItem("Close");
        menuItem5.setOnAction(e -> {
            MainClass.close();
        });

        menu1.getItems().add(menuItem1);
        menu1.getItems().add(menuItem2);
        menu1.getItems().add(menuItem3);
        menu1.getItems().add(menuItem4);
        menu1.getItems().add(separator);
        menu1.getItems().add(menuItem5);

        menuBar.getMenus().add(menu1);


        Menu menu2 = new Menu("Help");


//      // ESEMPIO SOTTOMENU
//        Menu menu = new Menu("Menu 1");
//
        Menu subMenu2_1 = new Menu("Menu 1");
        MenuItem menuItem2_1_1 = new MenuItem("Item 1.1");
        subMenu2_1.getItems().add(menuItem2_1_1);
        MenuItem menuItem2_1_2 = new MenuItem("Item 1.2");
        subMenu2_1.getItems().add(menuItem2_1_2);
        menu2.getItems().add(subMenu2_1);

        MenuItem menuItem2_2 = new MenuItem("Item 2");
        menu2.getItems().add(menuItem2_2);

        MenuItem menuItem2_3 = new MenuItem("Item 3");
        menu2.getItems().add(menuItem2_3);
//
//        MenuBar menuBar = new MenuBar();
//        menuBar.getMenus().add(menu);
        menuBar.getMenus().add(menu2);

        return menuBar;
    }

    private void swap(String view, MenuBar menuBar) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(view));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Swap screen
        stage.setScene(new Scene(root));
    }



}
