package view;

import java.io.IOException;

import engine.Game;
import engine.Player;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughResourcesException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import model.world.*;

public class GameView extends Application {
	private static int turn = 1;

	public void start(Stage stage) throws Exception {

		Button startGame = new Button("Start Game");
		Button endGame = new Button("Exit Game");
		Label firstPlayer = new Label("First Player Name: ");
		Label secondPlayer = new Label("Second Player Name: ");
		TextField firstPlayerName = new TextField();
		TextField secondPlayerName = new TextField();

		Pane mainMenu = new Pane();
		mainMenu.getChildren().addAll(firstPlayerName, secondPlayerName, startGame, endGame, firstPlayer, secondPlayer);

		firstPlayer.setLayoutX(146.0);
		firstPlayer.setLayoutY(93.0);

		secondPlayer.setLayoutX(129.0);
		secondPlayer.setLayoutY(136.0);

		startGame.setLayoutX(262.0);
		startGame.setLayoutY(289.0);

		endGame.setLayoutX(265.0);
		endGame.setLayoutY(323.0);

		firstPlayerName.setLayoutX(247.0);
		firstPlayerName.setLayoutY(89.0);

		secondPlayerName.setLayoutX(247.0);
		secondPlayerName.setLayoutY(132.0);

		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Game Started");
		a.setHeaderText("Get Ready!");

		stage.setResizable(false);
		stage.setTitle("Game");
		stage.setScene(new Scene(mainMenu, 600, 400));
		stage.getIcons().add(new Image("resources/Ironman.png"));
		stage.show();

		startGame.setOnMouseClicked((MouseEvent e) -> {

			Player p1 = (firstPlayerName.getText().isEmpty()) ? new Player("Player 1") : new Player(firstPlayerName.getText());
			Player p2 = (secondPlayerName.getText().isEmpty()) ? new Player("Player 2") : new Player(secondPlayerName.getText());

			try {
				ChampSelect(p1, p2, stage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		endGame.setOnMouseClicked((MouseEvent e) -> {
			stage.close();
		});
	}

	private void ChampSelect(Player p1, Player p2, Stage stage) throws IOException {

		FlowPane champButtonsBox = new FlowPane();
		champButtonsBox.setLayoutX(925);
		champButtonsBox.setLayoutY(100);
		champButtonsBox.setPrefWrapLength(300);

		Button chooseLeader = new Button("Choose your leaders!");
		chooseLeader.setLayoutX(407);
		chooseLeader.setLayoutY(605);
		chooseLeader.setDisable(true);
		chooseLeader.setVisible(false);

		VBox firstPlayerTeamBox = new VBox();
		firstPlayerTeamBox.setLayoutX(86);
		firstPlayerTeamBox.setLayoutY(101);
		firstPlayerTeamBox.setAlignment(Pos.CENTER);

		VBox secondPlayerTeamBox = new VBox();
		secondPlayerTeamBox.setLayoutX(352);
		secondPlayerTeamBox.setLayoutY(101);
		secondPlayerTeamBox.setAlignment(Pos.CENTER);

		Label whosChoosing = new Label(p1.getName() + " is choosing");
		whosChoosing.setLayoutX(200);
		whosChoosing.setLayoutY(30);

		Label firstPlayerTeam = new Label(p1.getName() + "'s Team: ");
		Label secondPlayerTeam = new Label(p2.getName() + "'s Team: ");
		firstPlayerTeamBox.getChildren().add(firstPlayerTeam);
		secondPlayerTeamBox.getChildren().add(secondPlayerTeam);

		Pane champSelectPane = new Pane(champButtonsBox);
		champSelectPane.getChildren().addAll(chooseLeader, whosChoosing, firstPlayerTeamBox, secondPlayerTeamBox);

		stage.setScene(new Scene(champSelectPane, 1280, 720));
		stage.setTitle("Champion Select");

		ColorAdjust desaturate = new ColorAdjust();
		desaturate.setSaturation(-1);

		new Game();

		for (Champion c : Game.getAvailableChampions()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));
			img.setFitHeight(100);
			img.setFitWidth(100);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());

			champButtonsBox.getChildren().add(img);

			img.setOnMouseClicked((MouseEvent e) -> {
				if (turn == 1) {
					p1.getTeam().add(c);

					firstPlayerTeamBox.getChildren().addAll(new Label('\n' + c.getName()), img);
					img.setDisable(true);
					whosChoosing.setText(p2.getName() + " is choosing...");

					turn = 2;
				} else {
					p2.getTeam().add(c);

					secondPlayerTeamBox.getChildren().addAll(new Label('\n' + c.getName()), img);
					img.setDisable(true);
					whosChoosing.setText(p1.getName() + " is choosing...");

					turn = 1;
				}

				if (p1.getTeam().size() + p2.getTeam().size() == 6) {

					chooseLeader.setDisable(false);
					chooseLeader.setVisible(true);

					for (Node n : champButtonsBox.getChildren()) {
						n.setDisable(true); // disable all buttons
						n.setEffect(desaturate); // desaturate them
					}

				}
			});
		}

		chooseLeader.setOnMouseClicked((MouseEvent e) -> {
			try {
				leaderSelect(p1, p2, stage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	private void leaderSelect(Player p1, Player p2, Stage stage) throws IOException {

		Line l = new Line();
		l.setStartX(-100);
		l.setStartY(-65);
		l.setEndX(-100);
		l.setEndY(430);
		l.setLayoutX(500);
		l.setLayoutY(81);

		VBox firstBox = new VBox();
		firstBox.setPrefSize(496, 425);
		firstBox.setLayoutX(14);
		firstBox.setLayoutY(16);

		VBox secondBox = new VBox();
		secondBox.setPrefSize(496, 425);
		secondBox.setLayoutX(415);
		secondBox.setLayoutY(16);

		Label firstPlayer = new Label(p1.getName() + "'s Team: ");
		firstBox.getChildren().add(firstPlayer);
		firstPlayer.setTranslateX(10);
		firstPlayer.setTranslateY(8);

		Label secondPlayer = new Label(p2.getName() + "'s Team: ");
		secondBox.getChildren().add(secondPlayer);
		secondPlayer.setTranslateX(10);
		secondPlayer.setTranslateY(8);

		Button startButton = new Button("Start Game!");
		startButton.setLayoutX(360);
		startButton.setLayoutY(544);
		startButton.setDisable(true);
		startButton.setVisible(false);

		Pane p = new Pane(l, firstBox, secondBox, startButton);
		stage.setScene(new Scene(p, 800, 600));
		stage.setResizable(false);
		stage.setTitle("Leader Select");

		ColorAdjust desaturate = new ColorAdjust();
		desaturate.setSaturation(-1);

		for (Champion c : p1.getTeam()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));
			img.setFitHeight(100);
			img.setFitWidth(100);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());

			HBox champStats = new HBox();
			champStats.setTranslateY(10);

			Label stats = new Label("\nName: " + c.getName() + "\nType: " + getHeroType(c) + "\nAttack Damage: " + c.getAttackDamage() + "\nAbility 1: " + c.getAbilities().get(0).getName() + "\nAbility 2: " + c.getAbilities().get(1).getName() + "\nAbility 3: " + c.getAbilities().get(2).getName());

			champStats.getChildren().addAll(img, stats);
			img.setTranslateY(10);
			stats.setTranslateX(10);

			firstBox.getChildren().add(champStats);

			champStats.setOnMouseClicked((MouseEvent e) -> {
				p1.setLeader(c);

				firstBox.getChildren().add(new Label("\n\nYou have chosen " + c.getName() + " as your leader!"));

				for (Node n : firstBox.getChildren()) {
					n.setDisable(true); // disable all buttons
					n.setEffect(desaturate); // desaturate them
				}

				if (p1.getLeader() != null && p2.getLeader() != null) {
					startButton.setDisable(false);
					startButton.setVisible(true);
				}
			});
		}

		for (Champion c : p2.getTeam()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));
			img.setFitHeight(100);
			img.setFitWidth(100);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());

			HBox champStats = new HBox();
			champStats.setTranslateY(10);

			Label stats = new Label("\nName: " + c.getName() + "\nType: " + getHeroType(c) + "\nAttack Damage: " + c.getAttackDamage() + "\nAbility 1: " + c.getAbilities().get(0).getName() + "\nAbility 2: " + c.getAbilities().get(1).getName() + "\nAbility 3: " + c.getAbilities().get(2).getName());

			champStats.getChildren().addAll(img, stats);
			img.setTranslateY(10);
			stats.setTranslateX(10);

			secondBox.getChildren().add(champStats);

			champStats.setOnMouseClicked((MouseEvent e) -> {
				p2.setLeader(c);

				secondBox.getChildren().add(new Label("\n\nYou have chosen " + c.getName() + " as your leader!"));

				for (Node n : secondBox.getChildren()) {
					n.setDisable(true); // disable all buttons
					n.setEffect(desaturate); // desaturate them
				}

				if (p1.getLeader() != null && p2.getLeader() != null) {
					startButton.setDisable(false);
					startButton.setVisible(true);
				}
			});

		}

		startButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				Game game = new Game(p1, p2);
				gameView(game, stage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

	}

	private void gameView(Game game, Stage stage) {

		BorderPane mainWindow = new BorderPane();

		ImageView attackImg = new ImageView(new Image("resources/Attack.png"));
		attackImg.setFitWidth(150);
		attackImg.setFitHeight(150);

		Button attack = new Button("", attackImg);
		attack.setPrefSize(200, 200);

		// Button ability1;
		// Button ability2;
		// Button ability3;

		ImageView upButton = new ImageView(new Image("resources/Arrow.png"));
		upButton.setRotate(-90);
		upButton.setFitHeight(60);
		upButton.setFitWidth(60);

		ImageView downButton = new ImageView(new Image("resources/Arrow.png"));
		downButton.setRotate(90);
		downButton.setFitHeight(60);
		downButton.setFitWidth(60);

		ImageView rightButton = new ImageView(new Image("resources/Arrow.png"));
		rightButton.setRotate(0);
		rightButton.setFitHeight(60);
		rightButton.setFitWidth(60);

		ImageView leftButton = new ImageView(new Image("resources/Arrow.png"));
		leftButton.setRotate(180);
		leftButton.setFitHeight(60);
		leftButton.setFitWidth(60);

		// Label turns = new Label("Turns:\n" + game.getCurrentChampion().getName());
		// PriorityQueue temp = game.getTurnOrder();
		// for (int i = 0; i < temp.size(); i++) {
		// 	temp.remove();
		// 	turns.setText(turns.getText() + '\n' + ((Champion) temp.peekMin()).getName());
		// }

		Pane arrowBox = new Pane();
		arrowBox.setPrefSize(200, 200);
		arrowBox.getChildren().addAll(upButton, downButton, rightButton, leftButton);
		arrowBox.setTranslateX(850);
		upButton.setLayoutX(72);
		upButton.setLayoutY(-2);
		downButton.setLayoutX(72);
		downButton.setLayoutY(75);
		rightButton.setLayoutX(158);
		rightButton.setLayoutY(75);
		leftButton.setLayoutX(-13);
		leftButton.setLayoutY(75);

		HBox bottomPanel = new HBox();
		bottomPanel.setPrefSize(100, 200);
		bottomPanel.getChildren().addAll(attack, arrowBox);
		arrowBox.setDisable(true);
		arrowBox.setVisible(false);

		VBox leftPanel = new VBox();
		leftPanel.setPrefSize(200, 200);
		leftPanel.setAlignment(Pos.TOP_CENTER);
		leftPanel.getChildren().add(new Label(game.getFirstPlayer().getName()));

		VBox rightPanel = new VBox();
		rightPanel.setPrefSize(200, 200);
		rightPanel.setAlignment(Pos.TOP_CENTER);
		rightPanel.getChildren().add(new Label(game.getSecondPlayer().getName()));

		// HBox topPanel = new HBox();
		// topPanel.setPrefSize(200, 200);

		GridPane gameGrid = new GridPane();
		gameGrid.setPrefSize(500, 500);
		gameGrid.gridLinesVisibleProperty().set(true);

		updateBoard(game, gameGrid, leftPanel, rightPanel);

		attack.setOnMouseClicked(e -> {

			arrowBox.setDisable(false);
			arrowBox.setVisible(true);

			upButton.setOnMouseClicked(e1 -> {
				Direction d = Direction.UP;
				try {
					game.attack(d);
					updateBoard(game, gameGrid, leftPanel, rightPanel);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			downButton.setOnMouseClicked(e1 -> {
				Direction d = Direction.DOWN;
				try {
					game.attack(d);
					updateBoard(game, gameGrid, leftPanel, rightPanel);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			rightButton.setOnMouseClicked(e1 -> {
				Direction d = Direction.RIGHT;
				try {
					game.attack(d);
					updateBoard(game, gameGrid, leftPanel, rightPanel);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			leftButton.setOnMouseClicked(e1 -> {
				Direction d = Direction.LEFT;
				try {
					game.attack(d);
					updateBoard(game, gameGrid, leftPanel, rightPanel);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

		});

		mainWindow.setRight(rightPanel);
		mainWindow.setLeft(leftPanel);
		mainWindow.setBottom(bottomPanel);
		mainWindow.setCenter(gameGrid);
		// mainWindow.setTop(topPanel);

		stage.setScene(new Scene(mainWindow, 1280, 720));
		stage.setTitle("Game");
	}

	//#region HELPER METHODS

	private String getHeroType(Champion c) {
		return (c instanceof Hero) ? "Hero" : (c instanceof AntiHero) ? "Anti-Hero" : "Villain";
	}

	private void updateBoard(Game game, GridPane gameGrid, VBox leftPanel, VBox rightPanel) {
		Button[][] btn = new Button[5][5];

		for (int i = 0; i < game.getBoardheight(); i++) {
			for (int j = 0; j < game.getBoardwidth(); j++) {
				if (game.getBoard()[i][j] instanceof Champion) {

					Champion c = (Champion) game.getBoard()[i][j];
					ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));

					img.setFitWidth(100);
					img.setFitHeight(100);

					btn[i][j] = new Button("HP: " + c.getCurrentHP(), img);
					btn[i][j].wrapTextProperty().set(true);
					btn[i][j].setPrefSize(200, 200);

					gameGrid.add(btn[i][j], i, j);

				} else if (game.getBoard()[i][j] instanceof Cover) {

					String[] randomImg = { "Cover1", "Cover2", "Cover3" };
					ImageView img = new ImageView(new Image("resources/" + randomImg[(int) (Math.random() * 3)] + ".png"));
					img.setFitWidth(100);
					img.setFitHeight(100);

					btn[i][j] = new Button("HP: " + ((Cover) game.getBoard()[i][j]).getCurrentHP(), img);
					btn[i][j].setPrefSize(200, 200);

					gameGrid.add(btn[i][j], i, j);

				} else {

					btn[i][j] = new Button();
					btn[i][j].setPrefSize(200, 200);

					gameGrid.add(btn[i][j], i, j);

				}
			}
		}

		for (Champion c : game.getFirstPlayer().getTeam()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));
			img.setFitHeight(25);
			img.setFitWidth(25);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());

			HBox champStats = new HBox();
			champStats.setTranslateX(10);

			Label stats = new Label("\nName: " + c.getName() + "\nType: " + getHeroType(c) + "\nAttack Range: " + c.getAttackRange() + "\nAttack Damage: " + c.getAttackDamage() + "\nMax Action Points: " + c.getMaxActionPointsPerTurn() + "\nMana: " + c.getMana() + "\nSpeed: " + c.getSpeed());

			champStats.getChildren().addAll(img, stats);
			img.setTranslateY(20);
			stats.setTranslateX(10);

			leftPanel.getChildren().add(champStats);
		}

		for (Champion c : game.getSecondPlayer().getTeam()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));
			img.setFitHeight(25);
			img.setFitWidth(25);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());

			HBox champStats = new HBox();
			champStats.setTranslateX(10);

			Label stats = new Label("\nName: " + c.getName() + "\nType: " + getHeroType(c) + "\nAttack Range: " + c.getAttackRange() + "\nAttack Damage: " + c.getAttackDamage() + "\nMax Action Points: " + c.getMaxActionPointsPerTurn() + "\nMana: " + c.getMana() + "\nSpeed: " + c.getSpeed());

			if (game.getSecondPlayer().getLeader().equals(c)) {
				ImageView leaderImg = new ImageView(new Image("resources/LeaderIcon.png"));
				// LeaderImg.setFitHeight(25);
				img.setFitWidth(25);
				champStats.getChildren().addAll(img, stats);
			} else
				champStats.getChildren().addAll(img, stats);
			img.setTranslateY(20);
			stats.setTranslateX(10);

			rightPanel.getChildren().add(champStats);
		}
	}

	//#endregion

	public static void main(String[] args) {
		launch();
	}
}
