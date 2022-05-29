package view;

import java.io.IOException;

import engine.Game;
import engine.Player;
import javafx.application.Application;

import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainMenu extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		VBox vbox = new VBox();
		Button startGame = new Button("Start Game");
		TextField firstPlayerName = new TextField();
		TextField secondPlayerName = new TextField();

		vbox.setTranslateX(325);
		vbox.setTranslateY(200);
		vbox.setSpacing(10);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(firstPlayerName, secondPlayerName, startGame);

		Pane pane = new Pane(vbox);

		stage.setResizable(false);
		stage.setTitle("Game");
		stage.setScene(new Scene(pane, 800, 600));
		stage.show();

		startGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Player p1 = (firstPlayerName.getText().isEmpty()) ? new Player("Player 1") : new Player(firstPlayerName.getText());
				Player p2 = (secondPlayerName.getText().isEmpty()) ? new Player("Player 2") : new Player(secondPlayerName.getText());
				try {
					Game game = new Game(p1, p2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Game Started with " + p1.getName() + " and " + p2.getName());
			}
		});
	}

	public static void main(String[] args) {
		launch();
	}
}
