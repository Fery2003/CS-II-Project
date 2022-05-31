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
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import model.world.Champion;

public class GameView extends Application {
	private static int turn = 1;
	private static int counter = 0;

	@Override
	public void start(Stage stage) throws Exception {

		Button startGame = new Button("Start Game");
		Label firstPlayer = new Label("First Player Name: ");
		Label secondPlayer = new Label("Second Player Name: ");
		TextField firstPlayerName = new TextField();
		TextField secondPlayerName = new TextField();

		Pane mainMenu = new Pane();
		mainMenu.getChildren().addAll(firstPlayerName, secondPlayerName, startGame, firstPlayer, secondPlayer);

		firstPlayer.setLayoutX(146.0);
		firstPlayer.setLayoutY(93.0);

		secondPlayer.setLayoutX(129.0);
		secondPlayer.setLayoutY(136.0);

		startGame.setLayoutX(262.0);
		startGame.setLayoutY(289.0);

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
	}

	private void ChampSelect(Game game, Stage stage) {
		FlowPane champButtonsBox = new FlowPane();
		champButtonsBox.setLayoutX(925);
		champButtonsBox.setLayoutY(100);
		champButtonsBox.setPrefWrapLength(300);

		Label whosChoosing = new Label(game.getFirstPlayer().getName() + " is choosing");
		whosChoosing.setLayoutX(200);
		whosChoosing.setLayoutY(30);

		Label firstPlayerTeam = new Label(game.getFirstPlayer().getName() + "'s Team: ");
		Label secondPlayerTeam = new Label(game.getSecondPlayer().getName() + "'s Team: ");
		firstPlayerTeam.setLayoutX(87);
		firstPlayerTeam.setLayoutY(67);
		secondPlayerTeam.setLayoutX(345);
		secondPlayerTeam.setLayoutY(67);

		Pane champSelectPane = new Pane(champButtonsBox);
		champSelectPane.getChildren().addAll(whosChoosing, firstPlayerTeam, secondPlayerTeam);

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
					firstPlayerTeam.setText(firstPlayerTeam.getText() + '\n' + c.getName());
					img.setEffect(desaturate);
					img.setDisable(true);
					turn = 2;
					whosChoosing.setText(game.getSecondPlayer().getName() + " is choosing");
					counter++;
					System.out.println(counter);
				} else {
					game.getSecondPlayer().getTeam().add(c);
					secondPlayerTeam.setText(secondPlayerTeam.getText() + '\n' + c.getName());
					img.setEffect(desaturate);
					img.setDisable(true);
					turn = 1;
					whosChoosing.setText(game.getFirstPlayer().getName() + " is choosing");
					counter++;
					System.out.println(counter);
				}

				if (counter == 6) {
					System.out.println("First player team: "); // TODO: remove after testing
					for (Champion c1 : game.getFirstPlayer().getTeam())
						System.out.println(c1.getName());
					System.out.println("##########################\nSecond player team: ");
					for (Champion c1 : game.getSecondPlayer().getTeam())
						System.out.println(c1.getName());

					for (Node n : champButtonsBox.getChildren())
						n.setDisable(true); // disable all buttons

					whosChoosing.setText("Choose your leaders!");
				}
			});

		}

	}

	public static void main(String[] args) {
		launch();
	}
}
