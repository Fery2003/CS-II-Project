package engine;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
// import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.world.Champion;

public class GameController {

  private static Game game;
  private static int counter = 0;
  private static int turn = ((int) (Math.random() * 2) + 1);

  // private static Scene scene = game.getScene();

  @FXML
  private Label firstPlayerChampsMenu, secondPlayerChampsMenu;

  @FXML
  private Label whosChoosing;

  @FXML
  private TextField firstPlayerName, secondPlayerName;

  @FXML
  private Button chooseLeader;

  @FXML
  void onGameStart(ActionEvent event) throws IOException {
    Player p1 = firstPlayerName.getText().equals("") ? new Player("Player 1") : new Player(firstPlayerName.getText());
    Player p2 = secondPlayerName.getText().equals("") ? new Player("Player 2") : new Player(secondPlayerName.getText());
    game = new Game(p1, p2);
    Alert a = new Alert(AlertType.INFORMATION);
    a.setContentText("Welcome to the game!\n" + p1.getName() + " vs " + p2.getName());
    a.setHeaderText("Game started!");
    a.setTitle("");
    a.show();
    a.setOnCloseRequest(e -> {
      try {
        Game.setRoot("ChampSelect");
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    });
    chooseLeader.setVisible(false);
  }

  @FXML
  void onChooseChamp(ActionEvent event) throws IOException {
    whosChoosing.setText((turn == 1) ? game.getSecondPlayer().getName() + " is choosing..." : game.getFirstPlayer().getName() + " is choosing...");
    Button b = (Button) event.getSource();
    String champName = b.getText();
    if (counter < 6) {
      if (turn == 1) {
        //whosChoosing.setText(game.getFirstPlayer().getName() + " is choosing...");
        firstPlayerChampsMenu.setText(firstPlayerChampsMenu.getText() + '\n' + champName);
        for (Champion c : Game.getAvailableChampions())
          if (c.getName().equals(champName))
            game.getFirstPlayer().getTeam().add(c);
        turn = 2;
      } else {
        //whosChoosing.setText(game.getSecondPlayer().getName() + " is choosing...");
        secondPlayerChampsMenu.setText(secondPlayerChampsMenu.getText() + '\n' + champName);
        for (Champion c : Game.getAvailableChampions())
          if (c.getName().equals(champName))
            game.getSecondPlayer().getTeam().add(c);
        turn = 1;
      }
      b.setDisable(true);
      counter++;
    } else {
      for (Champion c : game.getFirstPlayer().getTeam())
        System.out.println(c.getName());
      System.out.println("######################################");
      for (Champion c : game.getSecondPlayer().getTeam())
        System.out.println(c.getName());
      whosChoosing.setVisible(false);
    }
  }

  @FXML
  void onChooseLeader(ActionEvent event) {

  }
}
