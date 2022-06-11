package view;

import java.io.IOException;

import engine.Game;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;

public class Quiz extends Application {

	public void start(Stage stage) {
		GridPane grid = new GridPane();

		try {
			new Game();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Label abilityName = new Label("Ability Name");
		grid.add(abilityName, 0, 0);
		abilityName.setPrefSize(250, 250);
		abilityName.setAlignment(Pos.CENTER);

		Label abilityType = new Label("Ability Type");
		grid.add(abilityType, 1, 0);
		abilityType.setPrefSize(250, 250);
		abilityType.setAlignment(Pos.CENTER);

		Label abilityIndex = new Label("Index");
		grid.add(abilityIndex, 0, 1);
		abilityIndex.setPrefSize(250, 250);
		abilityIndex.setAlignment(Pos.CENTER);

		Button next = new Button("Next");
		grid.add(next, 1, 1);
		next.setPrefSize(250, 250);
		next.setAlignment(Pos.CENTER);

		next.setOnAction(e -> {
			int x = (int) (Math.random() * Game.getAvailableAbilities().size());
			abilityName.setText(Game.getAvailableAbilities().get(x).getName());

			if (Game.getAvailableAbilities().get(x) instanceof DamagingAbility)
				abilityType.setText("Damaging");
			else if (Game.getAvailableAbilities().get(x) instanceof HealingAbility)
				abilityType.setText("Healing");
			else
				abilityType.setText("CC");

			abilityIndex.setText(Integer.toString(x));
		});

		stage.setResizable(false);
		stage.setScene(new Scene(grid, 500, 500));
		stage.show();
	}
}
