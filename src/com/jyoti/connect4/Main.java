package com.jyoti.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();
		controller = loader.getController();
		controller.createPlayground();

		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		menuPane.getChildren().add(menuBar);


		Scene scene = new Scene(rootGridPane);
		Image icon = new Image(getClass().getResourceAsStream("icon_connect4.png"));
		primaryStage.getIcons().add(icon);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private MenuBar createMenu() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(event -> controller.resetGame());
		MenuItem resetGame = new MenuItem("Reset Game");
		resetGame.setOnAction(event -> controller.resetGame());
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem("Exit Game");
		exitGame.setOnAction(event -> exitGame());
		Menu helpMenu = new Menu("Help");
		MenuItem aboutConnect4 = new MenuItem("About connect4");
		aboutConnect4.setOnAction(event -> aboutGame());
		SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
		MenuItem aboutMe = new MenuItem("About me");
		aboutMe.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				aboutMe();
			}
		});
		fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
		helpMenu.getItems().addAll(aboutConnect4,separatorMenuItem1,aboutMe);
		menuBar.getMenus().addAll(fileMenu,helpMenu);
		return menuBar;


	}

	private void aboutMe() {
		String aboutMeContent = "I am a 3rd year CSE student. I love to code and create applications. Connect4 is one of them. Hope you enjoy the game.\nGood luck!";
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Developer");
		alert.setHeaderText("Jyoti Verma");
		alert.setContentText(aboutMeContent);
		alert.show();
	}

	private void aboutGame() {
		String aboutGameContent = "Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.";
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Connect Four");
		alert.setHeaderText("How to Play");
		alert.setContentText(aboutGameContent);
		alert.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
