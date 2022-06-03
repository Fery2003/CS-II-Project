package view;

import engine.Game;
import engine.Player;
import javafx.application.Application;

import javafx.event.*;
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
import model.world.Champion;

public class GameView extends Application {
	private static int turn = 1;
	private static int counter = 0;

	@Override
	public void start(Stage stage) throws Exception {

		Button startGame = new Button("Start Game");
		Button endGame = new Button("End Game");
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

		endGame.setLayoutX(264.0);
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

		startGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Player p1 = (firstPlayerName.getText().isEmpty()) ? new Player("Player 1") : new Player(firstPlayerName.getText());
				Player p2 = (secondPlayerName.getText().isEmpty()) ? new Player("Player 2") : new Player(secondPlayerName.getText());
				try {
					Game game = new Game(p1, p2);
					a.setContentText(p1.getName() + " vs. " + p2.getName());
					a.showAndWait();
					ChampSelect(game, stage);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		endGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
	}

	private void ChampSelect(Game game, Stage stage) {
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
		VBox secondPlayerTeamBox = new VBox();

		firstPlayerTeamBox.setLayoutX(86);
		firstPlayerTeamBox.setLayoutY(101);
		secondPlayerTeamBox.setLayoutX(352);
		secondPlayerTeamBox.setLayoutY(101);

		Label whosChoosing = new Label(game.getFirstPlayer().getName() + " is choosing");
		whosChoosing.setLayoutX(200);
		whosChoosing.setLayoutY(30);

		Label firstPlayerTeam = new Label(game.getFirstPlayer().getName() + "'s Team: ");
		Label secondPlayerTeam = new Label(game.getSecondPlayer().getName() + "'s Team: ");
		firstPlayerTeam.setLayoutX(87);
		firstPlayerTeam.setLayoutY(67);
		secondPlayerTeam.setLayoutX(345);
		secondPlayerTeam.setLayoutY(67);

		Label firstPlayerTeamList = new Label();
		Label secondPlayerTeamList = new Label();
		firstPlayerTeamList.setLayoutX(87);
		firstPlayerTeamList.setLayoutY(95);
		secondPlayerTeamList.setLayoutX(345);
		secondPlayerTeamList.setLayoutY(95);

		Pane champSelectPane = new Pane(champButtonsBox);
		champSelectPane.getChildren().addAll(chooseLeader, whosChoosing, firstPlayerTeam, secondPlayerTeam, firstPlayerTeamBox, secondPlayerTeamBox, firstPlayerTeamList, secondPlayerTeamList);

		stage.setScene(new Scene(champSelectPane, 1280, 720));
		stage.setTitle("Champion Select");

		ColorAdjust desaturate = new ColorAdjust();
		desaturate.setSaturation(-1);

		for (Champion c : Game.getAvailableChampions()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));
			img.setFitHeight(100);
			img.setFitWidth(100);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());
			champButtonsBox.getChildren().add(img);
			img.setOnMouseClicked((MouseEvent e) -> {
				if (turn == 1) {
					game.getFirstPlayer().getTeam().add(c);
					firstPlayerTeamBox.getChildren().addAll(new Label('\n' + c.getName()), img);
					img.setDisable(true);
					turn = 2;
					whosChoosing.setText(game.getSecondPlayer().getName() + " is choosing");
					counter++;
				} else {
					game.getSecondPlayer().getTeam().add(c);
					secondPlayerTeamBox.getChildren().addAll(new Label('\n' + c.getName()), img);
					img.setDisable(true);
					turn = 1;
					whosChoosing.setText(game.getFirstPlayer().getName() + " is choosing");
					counter++;
				}

				if (counter == 6) {
					System.out.println("First player team: "); // TODO: remove after testing
					for (Champion c1 : game.getFirstPlayer().getTeam())
						System.out.println(c1.getName());
					System.out.println("##########################\nSecond player team: ");
					for (Champion c1 : game.getSecondPlayer().getTeam())
						System.out.println(c1.getName());

					chooseLeader.setDisable(false);
					chooseLeader.setVisible(true);

					for (Node n : champButtonsBox.getChildren()) {
						n.setDisable(true); // disable all buttons
						n.setEffect(desaturate); // desaturate them
					}

				}
			});

			chooseLeader.setOnMouseClicked((MouseEvent e) -> {
				leaderSelect(game, stage);
			});
		}
	}

	private void leaderSelect(Game game, Stage stage) {

		Line l = new Line();
		l.setStartX(-100);
		l.setStartY(-65);
		l.setEndX(-100);
		l.setEndY(430);
		l.setLayoutX(500);
		l.setLayoutY(81);

		VBox firstBox = new VBox();
		firstBox.setPrefSize(496, 371);
		firstBox.setLayoutX(14);
		firstBox.setLayoutY(16);

		VBox secondBox = new VBox();
		secondBox.setPrefSize(496, 371);
		secondBox.setLayoutX(415);
		secondBox.setLayoutY(16);

		HBox firstPlayerChamp1 = new HBox();
		firstPlayerChamp1.setTranslateY(10);

		HBox firstPlayerChamp2 = new HBox();
		firstPlayerChamp2.setTranslateY(10);
		firstPlayerChamp2.setLayoutX(10);
		firstPlayerChamp2.setLayoutY(27);

		HBox firstPlayerChamp3 = new HBox();
		firstPlayerChamp3.setTranslateY(10);
		firstPlayerChamp3.setLayoutX(10);
		firstPlayerChamp3.setLayoutY(183);

		HBox secondPlayerChamp1 = new HBox();
		secondPlayerChamp1.setTranslateY(10);

		HBox secondPlayerChamp2 = new HBox();
		secondPlayerChamp2.setTranslateY(10);
		secondPlayerChamp2.setLayoutX(10);
		secondPlayerChamp2.setLayoutY(27);

		HBox secondPlayerChamp3 = new HBox();
		secondPlayerChamp3.setTranslateY(10);
		secondPlayerChamp3.setLayoutX(10);
		secondPlayerChamp3.setLayoutY(183);

		Label firstPlayer = new Label(game.getFirstPlayer().getName() + "'s Team: ");
		firstBox.getChildren().addAll(firstPlayer, firstPlayerChamp1, firstPlayerChamp2, firstPlayerChamp3);
		firstPlayer.setTranslateX(10);
		firstPlayer.setTranslateY(8);

		Label secondPlayer = new Label(game.getSecondPlayer().getName() + "'s Team: ");
		secondBox.getChildren().addAll(secondPlayer, secondPlayerChamp1, secondPlayerChamp2, secondPlayerChamp3);
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

		ImageView img;
		Label champDescription;

		img = new ImageView(new Image("resources/" + game.getFirstPlayer().getTeam().get(0).getName() + ".png"));
		img.setFitHeight(100);
		img.setFitWidth(100);
		champDescription = new Label("\nName: " + game.getFirstPlayer().getTeam().get(0).getName() + "\n" + "Health: " + game.getFirstPlayer().getTeam().get(0).getMaxHP() + "\n" + "Attack Damage: " + game.getFirstPlayer().getTeam().get(0).getAttackDamage() + "\n" + "Ability 1: " + game.getFirstPlayer().getTeam().get(0).getAbilities().get(0).getName() + "\n" + "Ability 2: " + game.getFirstPlayer().getTeam().get(0).getAbilities().get(1).getName() + "\n" + "Ability 3: "
				+ game.getFirstPlayer().getTeam().get(0).getAbilities().get(2).getName() + "\n");
		firstPlayerChamp1.getChildren().addAll(img, champDescription);
		img.setTranslateY(20);
		champDescription.setTranslateX(5);

		img = new ImageView(new Image("resources/" + game.getFirstPlayer().getTeam().get(1).getName() + ".png"));
		img.setFitHeight(100);
		img.setFitWidth(100);
		champDescription = new Label("\nName: " + game.getFirstPlayer().getTeam().get(1).getName() + "\n" + "Health: " + game.getFirstPlayer().getTeam().get(1).getMaxHP() + "\n" + "Attack Damage: " + game.getFirstPlayer().getTeam().get(1).getAttackDamage() + "\n" + "Ability 1: " + game.getFirstPlayer().getTeam().get(1).getAbilities().get(0).getName() + "\n" + "Ability 2: " + game.getFirstPlayer().getTeam().get(1).getAbilities().get(1).getName() + "\n" + "Ability 3: "
				+ game.getFirstPlayer().getTeam().get(1).getAbilities().get(2).getName() + "\n");
		firstPlayerChamp2.getChildren().addAll(img, champDescription);
		img.setTranslateY(20);
		champDescription.setTranslateX(5);

		img = new ImageView(new Image("resources/" + game.getFirstPlayer().getTeam().get(2).getName() + ".png"));
		img.setFitHeight(100);
		img.setFitWidth(100);
		champDescription = new Label("\nName: " + game.getFirstPlayer().getTeam().get(2).getName() + "\n" + "Health: " + game.getFirstPlayer().getTeam().get(2).getMaxHP() + "\n" + "Attack Damage: " + game.getFirstPlayer().getTeam().get(2).getAttackDamage() + "\n" + "Ability 1: " + game.getFirstPlayer().getTeam().get(2).getAbilities().get(0).getName() + "\n" + "Ability 2: " + game.getFirstPlayer().getTeam().get(2).getAbilities().get(1).getName() + "\n" + "Ability 3: "
				+ game.getFirstPlayer().getTeam().get(2).getAbilities().get(2).getName() + "\n");
		firstPlayerChamp3.getChildren().addAll(img, champDescription);
		img.setTranslateY(20);
		champDescription.setTranslateX(5);

		img = new ImageView(new Image("resources/" + game.getSecondPlayer().getTeam().get(0).getName() + ".png"));
		img.setFitHeight(100);
		img.setFitWidth(100);
		champDescription = new Label("\nName: " + game.getSecondPlayer().getTeam().get(0).getName() + "\n" + "Health: " + game.getSecondPlayer().getTeam().get(0).getMaxHP() + "\n" + "Attack Damage: " + game.getSecondPlayer().getTeam().get(0).getAttackDamage() + "\n" + "Ability 1: " + game.getSecondPlayer().getTeam().get(0).getAbilities().get(0).getName() + "\n" + "Ability 2: " + game.getSecondPlayer().getTeam().get(0).getAbilities().get(1).getName() + "\n" + "Ability 3: "
				+ game.getSecondPlayer().getTeam().get(0).getAbilities().get(2).getName() + "\n");
		secondPlayerChamp1.getChildren().addAll(img, champDescription);
		img.setTranslateY(20);
		champDescription.setTranslateX(5);

		img = new ImageView(new Image("resources/" + game.getSecondPlayer().getTeam().get(1).getName() + ".png"));
		img.setFitHeight(100);
		img.setFitWidth(100);
		champDescription = new Label("\nName: " + game.getSecondPlayer().getTeam().get(1).getName() + "\n" + "Health: " + game.getSecondPlayer().getTeam().get(1).getMaxHP() + "\n" + "Attack Damage: " + game.getSecondPlayer().getTeam().get(1).getAttackDamage() + "\n" + "Ability 1: " + game.getSecondPlayer().getTeam().get(1).getAbilities().get(0).getName() + "\n" + "Ability 2: " + game.getSecondPlayer().getTeam().get(1).getAbilities().get(1).getName() + "\n" + "Ability 3: "
				+ game.getSecondPlayer().getTeam().get(1).getAbilities().get(2).getName() + "\n");
		secondPlayerChamp2.getChildren().addAll(img, champDescription);
		img.setTranslateY(20);
		champDescription.setTranslateX(5);

		img = new ImageView(new Image("resources/" + game.getSecondPlayer().getTeam().get(2).getName() + ".png"));
		img.setFitHeight(100);
		img.setFitWidth(100);
		champDescription = new Label("\nName: " + game.getSecondPlayer().getTeam().get(2).getName() + "\n" + "Health: " + game.getSecondPlayer().getTeam().get(2).getMaxHP() + "\n" + "Attack Damage: " + game.getSecondPlayer().getTeam().get(2).getAttackDamage() + "\n" + "Ability 1: " + game.getSecondPlayer().getTeam().get(2).getAbilities().get(0).getName() + "\n" + "Ability 2: " + game.getSecondPlayer().getTeam().get(2).getAbilities().get(1).getName() + "\n" + "Ability 3: "
				+ game.getSecondPlayer().getTeam().get(2).getAbilities().get(2).getName() + "\n");
		secondPlayerChamp3.getChildren().addAll(img, champDescription);
		img.setTranslateY(20);
		champDescription.setTranslateX(5);
		
	}

	public static void main(String[] args) {
		launch();
	}
}
