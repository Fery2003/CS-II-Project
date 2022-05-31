package view;

import java.io.IOException;

import engine.Game;
import engine.Player;
import javafx.application.Application;

import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
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
					ChampSelect(game, stage);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println("Game Started with " + p1.getName() + " and " + p2.getName());	
			}
		});
	}

	private void ChampSelect(Game game, Stage stage) {
		VBox champButtonsBox = new VBox();
		champButtonsBox.setSpacing(10);
		champButtonsBox.setLayoutX(1055);
		champButtonsBox.setLayoutY(18);
		Pane p = new Pane(champButtonsBox);

		stage.setScene(new Scene(p, 1280, 720));
		for (Champion c : game.getAvailableChampions()) {
			Button b = new Button(c.getName());
			champButtonsBox.getChildren().add(b);
			b.setOnAction(event -> {
				if (turn == 1) {
					game.getFirstPlayer().getTeam().add(c);
					b.setDisable(true);
					turn = 2;
					counter++;
					System.out.println(counter);
				} else {
					game.getSecondPlayer().getTeam().add(c);
					b.setDisable(true);
					turn = 1;
					counter++;
					System.out.println(counter);
				}
				if (counter == 5) {
					System.out.println("First player team: ");
					for (Champion c1 : game.getFirstPlayer().getTeam())
						System.out.println(c1.getName());
					System.out.println("Second player team: ");
					for (Champion c1 : game.getFirstPlayer().getTeam())
						System.out.println(c1.getName());
				}
			});

		}

	}

	public static void main(String[] args) {
		launch();
	}
}
