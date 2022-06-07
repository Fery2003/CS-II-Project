package view;

import java.io.IOException;
import java.util.ArrayList;

import engine.Game;
import engine.Player;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Effect;
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

		startGame.setOnMouseClicked(e -> {

			Player p1 = (firstPlayerName.getText().isEmpty()) ? new Player("Player 1") : new Player(firstPlayerName.getText());
			Player p2 = (secondPlayerName.getText().isEmpty()) ? new Player("Player 2") : new Player(secondPlayerName.getText());

			try {
				ChampSelect(p1, p2, stage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		endGame.setOnMouseClicked(e -> {
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

			img.setOnMouseClicked(e -> {
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

		chooseLeader.setOnMouseClicked(e -> {
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

			champStats.setOnMouseClicked(e -> {
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

			champStats.setOnMouseClicked(e -> {
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

		startButton.setOnAction(e -> {
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

		ImageView upButton = new ImageView(new Image("resources/Arrow.png"));
		upButton.setPickOnBounds(true);
		upButton.setRotate(-90);
		upButton.setFitHeight(60);
		upButton.setFitWidth(60);

		ImageView downButton = new ImageView(new Image("resources/Arrow.png"));
		downButton.setPickOnBounds(true);
		downButton.setRotate(90);
		downButton.setFitHeight(60);
		downButton.setFitWidth(60);

		ImageView rightButton = new ImageView(new Image("resources/Arrow.png"));
		rightButton.setPickOnBounds(true);
		rightButton.setRotate(0);
		rightButton.setFitHeight(60);
		rightButton.setFitWidth(60);

		ImageView leftButton = new ImageView(new Image("resources/Arrow.png"));
		leftButton.setPickOnBounds(true);
		leftButton.setRotate(180);
		leftButton.setFitHeight(60);
		leftButton.setFitWidth(60);

		Pane arrowBox = new Pane();
		arrowBox.setPrefSize(200, 200);
		arrowBox.getChildren().addAll(upButton, downButton, rightButton, leftButton);
		arrowBox.setTranslateX(600);
		arrowBox.setTranslateY(15);

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

		arrowBox.setDisable(true);
		arrowBox.setVisible(false);

		VBox leftPanel = new VBox();
		leftPanel.setPrefSize(200, 350);
		leftPanel.setAlignment(Pos.TOP_CENTER);

		VBox rightPanel = new VBox();
		rightPanel.setPrefSize(200, 350);
		rightPanel.setAlignment(Pos.TOP_CENTER);

		HBox topPanel = new HBox();
		topPanel.setPrefSize(100, 30);
		topPanel.setAlignment(Pos.CENTER);

		// topPanel.getChildren().addAll(new Label("Turns: "), new ImageView(new Image("resources/" + game.getCurrentChampion().getName() + ".png", 20, 20, true, true)));

		GridPane gameGrid = new GridPane();
		gameGrid.setPrefSize(500, 500);
		gameGrid.gridLinesVisibleProperty().set(true);
		gameGrid.setBackground(new Background(new BackgroundImage(new Image("resources/Background.jpg"), null, null, null, null)));

		updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);

		mainWindow.setRight(rightPanel);
		mainWindow.setLeft(leftPanel);
		mainWindow.setBottom(bottomPanel);
		mainWindow.setCenter(gameGrid);
		mainWindow.setTop(topPanel);

		stage.setScene(new Scene(mainWindow, 1280, 720));
		stage.setTitle("Game");
	}

	//#region HELPER METHODS

	private String getHeroType(Champion c) {
		return (c instanceof Hero) ? "Hero" : (c instanceof AntiHero) ? "Anti-Hero" : "Villain";
	}

	private void updateBoard(Game game, GridPane gameGrid, VBox leftPanel, VBox rightPanel, HBox topPanel, HBox bottomPanel, Pane arrowBox, ImageView upButton, ImageView downButton, ImageView leftButton, ImageView rightButton) {
		Button[][] btn = new Button[5][5];
		clearBoard(gameGrid);

		rightPanel.getChildren().clear();
		rightPanel.getChildren().add(new Label(game.getSecondPlayer().getName()));

		leftPanel.getChildren().clear();
		leftPanel.getChildren().add(new Label(game.getFirstPlayer().getName()));

		topPanel.getChildren().clear();

		bottomPanel.getChildren().clear();

		ImageView attackImg = new ImageView(new Image("resources/Attack.png"));
		attackImg.setFitWidth(150);
		attackImg.setFitHeight(150);

		Button attack = new Button("", attackImg);
		attack.setPrefSize(200, 200);
		attack.setStyle("-fx-background-color: transparent;");

		ImageView moveImg = new ImageView(new Image("resources/Move.png"));
		moveImg.setFitWidth(150);
		moveImg.setFitHeight(150);

		Button move = new Button("", moveImg);
		move.setPrefSize(200, 200);
		move.setStyle("-fx-background-color: transparent;");

		bottomPanel.getChildren().addAll(attack, move, arrowBox);
		attack.setTranslateY(-15);
		move.setTranslateY(-10);

		attack.setOnMouseClicked(e -> {

			arrowBox.setDisable(false);
			arrowBox.setVisible(true);

			upButton.setOnMouseClicked(e1 -> {
				try {
					game.attack(Direction.DOWN);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			downButton.setOnMouseClicked(e1 -> {
				try {
					game.attack(Direction.UP);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			rightButton.setOnMouseClicked(e1 -> {
				try {
					game.attack(Direction.RIGHT);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			leftButton.setOnMouseClicked(e1 -> {
				try {
					game.attack(Direction.LEFT);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (ChampionDisarmedException | InvalidTargetException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

		});

		move.setOnMouseClicked(e -> {

			arrowBox.setDisable(false);
			arrowBox.setVisible(true);

			upButton.setOnMouseClicked(e1 -> {
				try {
					game.move(Direction.DOWN);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (UnallowedMovementException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			downButton.setOnMouseClicked(e1 -> {
				try {
					game.move(Direction.UP);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (UnallowedMovementException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			rightButton.setOnMouseClicked(e1 -> {
				try {
					game.move(Direction.RIGHT);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (UnallowedMovementException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});

			leftButton.setOnMouseClicked(e1 -> {
				try {
					game.move(Direction.LEFT);
					updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
				} catch (UnallowedMovementException | NotEnoughResourcesException e2) {
					Alert a = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
					a.showAndWait();
				}
				arrowBox.setDisable(true);
				arrowBox.setVisible(false);
			});
		});

		Button endTurn = new Button("End Turn");
		topPanel.getChildren().add(endTurn);
		endTurn.setTranslateX(-10);

		for (Ability a : game.getCurrentChampion().getAbilities()) {
			String type = (a instanceof CrowdControlAbility) ? "CC/" + ((CrowdControlAbility) a).getEffect().getDuration() + " turns" : (a instanceof HealingAbility) ? "Healing/" + ((HealingAbility) a).getHealAmount() + "HP" : "Damaging/" + ((DamagingAbility) a).getDamageAmount();
			Label abilityLabel = new Label("Name: " + a.getName() + "\nType: " + type + "\nArea Of Effect: " + a.getCastArea() + "\nCast Range: " + a.getCastRange() + "\nMana Cost: " + a.getManaCost() + "\nAction Cost: " + a.getRequiredActionPoints() + "\nCooldown: " + a.getCurrentCooldown() + "\nBase Cooldown:" + a.getBaseCooldown());
			Button b = new Button(abilityLabel.getText());
			b.setOnMouseClicked(e -> {
				try {
					if (a.getCastArea() == AreaOfEffect.SELFTARGET || a.getCastArea() == AreaOfEffect.SURROUND || a.getCastArea() == AreaOfEffect.TEAMTARGET)
						game.castAbility(a);
					else if (a.getCastArea() == AreaOfEffect.DIRECTIONAL) {
						arrowBox.setDisable(false);
						arrowBox.setVisible(true);
						upButton.setOnMouseClicked(e1 -> {
							try {
								game.castAbility(a, Direction.DOWN);
								updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
							} catch (AbilityUseException | CloneNotSupportedException | NotEnoughResourcesException e2) {
								Alert alert = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
								alert.showAndWait();
							}
							arrowBox.setDisable(true);
							arrowBox.setVisible(false);
						});
						downButton.setOnMouseClicked(e1 -> {
							try {
								game.castAbility(a, Direction.UP);
								updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
							} catch (AbilityUseException | CloneNotSupportedException | NotEnoughResourcesException e2) {
								Alert alert = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
								alert.showAndWait();
							}
							arrowBox.setDisable(true);
							arrowBox.setVisible(false);
						});
						rightButton.setOnMouseClicked(e1 -> {
							try {
								game.castAbility(a, Direction.RIGHT);
								updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
							} catch (AbilityUseException | CloneNotSupportedException | NotEnoughResourcesException e2) {
								Alert alert = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
								alert.showAndWait();
							}
							arrowBox.setDisable(true);
							arrowBox.setVisible(false);
						});
						leftButton.setOnMouseClicked(e1 -> {
							try {
								game.castAbility(a, Direction.LEFT);
								updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
							} catch (AbilityUseException | CloneNotSupportedException | NotEnoughResourcesException e2) {
								Alert alert = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
								alert.showAndWait();
							}
							arrowBox.setDisable(true);
							arrowBox.setVisible(false);
						});
					} else if (a.getCastArea() == AreaOfEffect.SINGLETARGET) {
						for (Node button : gameGrid.getChildren()) {
							button.setOnMouseClicked(event -> {
								try {
									game.castAbility(a, GridPane.getRowIndex(button), GridPane.getColumnIndex(button));
									updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
								} catch (AbilityUseException | CloneNotSupportedException | NotEnoughResourcesException | InvalidTargetException e2) {
									Alert alert = new Alert(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
									alert.showAndWait();
								}
								arrowBox.setDisable(true);
								arrowBox.setVisible(false);
							});
						}
					}
				} catch (AbilityUseException | CloneNotSupportedException | NotEnoughResourcesException e1) {
					Alert alert = new Alert(AlertType.ERROR, e1.getMessage(), ButtonType.OK);
					alert.showAndWait();
				}
			});
			bottomPanel.getChildren().add(b);
			b.setTranslateX(-190);
			b.setTranslateY(5);
		}

		endTurn.setOnMouseClicked(e -> {
			game.endTurn();
			// System.out.println(game.getCurrentChampion().getName());
			updateBoard(game, gameGrid, leftPanel, rightPanel, topPanel, bottomPanel, arrowBox, upButton, downButton, leftButton, rightButton);
		});

		ArrayList<Champion> turns = new ArrayList<Champion>();

		for (int i = game.getTurnOrder().size() - 1; i >= 0; i--) {
			turns.add(((Champion) game.getTurnOrder().peekMin()));
			game.getTurnOrder().remove();
		}

		Label turnsLabel = new Label("Turns: ");
		topPanel.getChildren().add(turnsLabel);

		for (Champion c : turns) {
			game.getTurnOrder().insert(c);
			topPanel.getChildren().add(new ImageView(new Image("resources/" + c.getName() + ".png", 20, 20, true, true)));
		}

		for (int i = 0; i < Game.getBoardheight(); i++) {
			for (int j = 0; j < Game.getBoardwidth(); j++) {
				if (game.getBoard()[i][j] instanceof Champion) {

					Champion c = (Champion) game.getBoard()[i][j];
					ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));

					img.setFitWidth(100);
					img.setFitHeight(100);
					img.setEffect(new Glow(3));

					btn[i][j] = new Button("HP: " + c.getCurrentHP() + "\nAP: " + c.getCurrentActionPoints() + "\nTeam: " + game.getChampionTeam(c), img);
					btn[i][j].wrapTextProperty().set(true);
					btn[i][j].setPrefSize(200, 200);
					btn[i][j].setStyle("-fx-background-color: transparent;");
					btn[i][j].setTextFill(Color.WHITE);

					gameGrid.add(btn[i][j], j, i);

				} else if (game.getBoard()[i][j] instanceof Cover) {

					String[] randomImg = { "Cover1", "Cover2", "Cover3" };
					ImageView img = new ImageView(new Image("resources/" + randomImg[(int) (Math.random() * 3)] + ".png"));
					img.setFitWidth(100);
					img.setFitHeight(100);

					btn[i][j] = new Button("HP: " + ((Cover) game.getBoard()[i][j]).getCurrentHP(), img);
					btn[i][j].setPrefSize(200, 200);
					btn[i][j].setStyle("-fx-background-color: transparent;");
					btn[i][j].setTextFill(Color.WHITE);

					gameGrid.add(btn[i][j], j, i);

				} else if (game.getBoard()[i][j] == null) {

					btn[i][j] = new Button();
					btn[i][j].setPrefSize(200, 200);
					btn[i][j].setStyle("-fx-background-color: transparent;");

					gameGrid.add(btn[i][j], j, i);

				}
			}
		}

		for (Champion c : game.getFirstPlayer().getTeam()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));

			if (game.getCurrentChampion().equals(c))
				img.setEffect(new Glow(0.9));

			img.setFitHeight(25);
			img.setFitWidth(25);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());

			HBox champStats = new HBox();
			champStats.setTranslateX(10);

			String effects = "";

			if (c.getAppliedEffects() != null) {
				for (Effect e : c.getAppliedEffects()) {
					effects = e.getName() + " for " + e.getDuration() + " turns";
				}
			}

			Label stats = new Label("\nName: " + c.getName() + "\nType: " + getHeroType(c) + "\nAttack Range: " + c.getAttackRange() + "\nAttack Damage: " + c.getAttackDamage() + "\nMax Action Points: " + c.getMaxActionPointsPerTurn() + "\nMana: " + c.getMana() + "\nSpeed: " + c.getSpeed() + "\nCurrent HP: " + c.getCurrentHP() + "\nEffects: " + effects);

			if (game.getFirstPlayer().getLeader().equals(c)) {
				ImageView leaderImg = new ImageView(new Image("resources/LeaderIcon.png"));
				String leaderAbilityUsed = (game.isFirstLeaderAbilityUsed()) ? "\nLeader Ability Used." : "\nLeader Ability Available.";
				stats.setText(leaderAbilityUsed + stats.getText());
				leaderImg.setFitHeight(12);
				leaderImg.setFitWidth(12);
				champStats.getChildren().addAll(leaderImg, img, stats);
			} else
				champStats.getChildren().addAll(img, stats);

			img.setTranslateY(20);
			stats.setTranslateX(10);

			leftPanel.getChildren().add(champStats);
		}

		for (Champion c : game.getSecondPlayer().getTeam()) {
			ImageView img = new ImageView(new Image("resources/" + c.getName() + ".png"));

			if (game.getCurrentChampion().equals(c))
				img.setEffect(new Glow(0.9));

			img.setFitHeight(25);
			img.setFitWidth(25);
			img.setPickOnBounds(true);
			img.setAccessibleHelp(c.getName());

			HBox champStats = new HBox();
			champStats.setTranslateX(10);

			String effects = "";

			if (c.getAppliedEffects() != null) {
				for (Effect e : c.getAppliedEffects()) {
					effects = e.getName() + " for " + e.getDuration() + " turns";
				}
			}

			Label stats = new Label("\nName: " + c.getName() + "\nType: " + getHeroType(c) + "\nAttack Range: " + c.getAttackRange() + "\nAttack Damage: " + c.getAttackDamage() + "\nMax Action Points: " + c.getMaxActionPointsPerTurn() + "\nMana: " + c.getMana() + "\nSpeed: " + c.getSpeed() + "\nCurrent HP: " + c.getCurrentHP() + "\nEffects: " + effects);

			if (game.getSecondPlayer().getLeader().equals(c)) {
				ImageView leaderImg = new ImageView(new Image("resources/LeaderIcon.png"));
				String leaderAbilityUsed = (game.isSecondLeaderAbilityUsed()) ? "\nLeader Ability Used." : "\nLeader Ability Available.";
				stats.setText(leaderAbilityUsed + stats.getText());
				leaderImg.setFitHeight(12);
				leaderImg.setFitWidth(12);
				champStats.getChildren().addAll(leaderImg, img, stats);
			} else
				champStats.getChildren().addAll(img, stats);

			img.setTranslateY(20);
			stats.setTranslateX(10);

			rightPanel.getChildren().add(champStats);

			game.checkGameOver();
		}
	}

	private void clearBoard(GridPane gameGrid) {
		for (int i = 0; i < gameGrid.getRowCount(); i++) {
			for (int j = 0; j < gameGrid.getColumnCount(); j++) {
				gameGrid.getChildren().clear();
			}
		}
	}

	//#endregion

	public static void main(String[] args) {
		launch();
	}
}
