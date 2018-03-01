package com.jyoti.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int ROWS = 6;
	private static final int COLUMNS = 7;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String DISC_COLOR1 = "#24303E";
	private static final String DISC_COLOR2 = "#4CAA88";

	private static String PLAYER_ONE = "Player One";
	private static String PLAYER_TWO = "Player Two";
	private static boolean isPlayerOneTurn = true;
	private boolean isAllowedToInsert = true;
	Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];
	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;
	@FXML
	public TextField playerOneTextField, playerTwoTextField;
	@FXML
	public Button setNamesButton;

	public void createPlayground() {
		Shape rectangleWithHoles = createGamesStructuralGrid();
		rectangleWithHoles.setFill(Color.WHITE);
		rootGridPane.add(rectangleWithHoles, 0, 1);
		List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
		setNamesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(playerOneTextField.getText()!=null)
				PLAYER_ONE = playerOneTextField.getText();
				if(playerTwoTextField.getText()!=null)
				PLAYER_TWO = playerTwoTextField.getText();
				playerNameLabel.setText(PLAYER_ONE);
			}
		});

	}

	private Shape createGamesStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle(CIRCLE_DIAMETER * (COLUMNS + 1), CIRCLE_DIAMETER * (ROWS + 1));
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);
				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns() {

		List<Rectangle> rectangleList = new ArrayList();
		for (int col = 0; col < COLUMNS; col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, CIRCLE_DIAMETER * (ROWS + 1));
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("eeeeee35")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert = false; //no multiple disc can be inserted by the same user
					insertDiscs(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}

		return rectangleList;
	}

	private void insertDiscs(Disc disc, int col) {
		int row = ROWS - 1;
		while (row >= 0) {
			if (getDiscIfPresent(row, col) == null)
				break;
			row--;
		}
		if (row < 0)//cannot insert any more discs when full
			return;
		insertedDiscsArray[row][col] = disc;
		insertedDiscsPane.getChildren().add(disc);
		disc.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		final int currentRow = row;
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true; // when disc is dropped, allow next player to insert
			if (gameEnded(currentRow, col)) {
				gameOver();
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
		});
		translateTransition.play();
	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is " + winner);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("Winner is " + winner);
		alert.setContentText("Want to play again?");
		ButtonType yesbtn = new ButtonType("YES");
		ButtonType nobtn = new ButtonType("NO, Exit");
		alert.getButtonTypes().setAll(yesbtn, nobtn);

		Platform.runLater(() -> { // to resolve IllegalStateException
			Optional<ButtonType> clickedBtn = alert.showAndWait();
			if (clickedBtn.isPresent() && clickedBtn.get() == yesbtn) {
				//Reset the game
				resetGame();
			} else {
				//Exit the game
				Platform.exit();
				System.exit(0);
			}
		});

	}

	public void resetGame()

	{
		insertedDiscsPane.getChildren().clear();
		for (int row = 0; row < insertedDiscsArray.length; row++) {
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true; // Player One starts the game again
		playerNameLabel.setText(PLAYER_ONE);
		createPlayground();
	}

	private boolean gameEnded(int row, int col) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
				.mapToObj(r -> new Point2D(r, col))
				.collect(Collectors.toList());
		List<Point2D> horizontalPoints = IntStream.rangeClosed(col - 3, col + 3)
				.mapToObj(c -> new Point2D(row, c))
				.collect(Collectors.toList());
		Point2D startPoint1 = new Point2D(row - 3, col + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint1.add(i, -i))
				.collect(Collectors.toList());
		Point2D startPoint2 = new Point2D(row - 3, col - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());
		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point : points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);
			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
				chain++;
				if (chain == 4)
					return true;
				;
			} else {
				chain = 0;
			}
		}
		return false;
	}

	private Disc getDiscIfPresent(int row, int col) {
		if (row >= ROWS || row < 0 || col >= COLUMNS || col < 0) {
			return null;
		}
		return insertedDiscsArray[row][col];
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove ? Color.valueOf(DISC_COLOR1) : Color.valueOf(DISC_COLOR2));
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
