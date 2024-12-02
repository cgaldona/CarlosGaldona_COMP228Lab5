package com.example.carlosg_comp228lab5;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class HelloController {

    // Required FXML fields
    @FXML
    private TextField registerFirstNameField, registerLastNameField, registerAddressField,
            registerPostalCodeField, registerProvinceField, registerPhoneNumberField;
    @FXML
    private ComboBox<String> playerIdScoreComboBox,gameIdScoreComboBox;
    @FXML
    private TextField titleField, scoreField;
    @FXML
    private DatePicker playingDatePicker;
    @FXML
    private ComboBox<String> playerIdUpdatePlayerComboBox;
    @FXML
    private TextField updateFirstNameField, updateLastNameField, updateAddressField,
            updatePostalCodeField, updateProvinceField, updatePhoneNumberField;
    @FXML private TableView<Player> playerTable;
    @FXML private TableColumn<Player, Integer> playerIdColumn;
    @FXML private TableColumn<Player, String> firstNameColumn;
    @FXML private TableColumn<Player, String> lastNameColumn;
    @FXML private TableColumn<Player, String> addressColumn;
    @FXML private TableColumn<Player, String> postalCodeColumn;
    @FXML private TableColumn<Player, String> provinceColumn;
    @FXML private TableColumn<Player, String> phoneNumberColumn;
    @FXML private TableView<Games> gameTable;
    @FXML private TableColumn<Games, String> gameTitleColumn;
    @FXML private TableColumn<Games, String> playingDateColumn;
    @FXML private TableColumn<Games, String> scoreColumn;
    @FXML private TableColumn<Player, Integer> playerIdColumnInGameTable;

    // Connection object to interact with the database
    private Connection dbConnection;

    // ObservableLists to hold player and game data.
    private ObservableList<String> playerList = FXCollections.observableArrayList();
    private ObservableList<String> gameList = FXCollections.observableArrayList();

    // Constructor for HelloController
    public HelloController() {
        try {

            // Attempts to establish a connection to the database when the controller is created.
            dbConnection = HelloApplication.DatabaseConnector.connect();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to connect to the database: " + e.getMessage());
        }
    }

    // Initialize method: Called when the controller is loaded to set up components
    @FXML
    public void initialize() {
        // Load players and games into the list
        loadPlayers();
        loadGames();

        // Set the items for combo boxes using the loaded lists
        playerIdScoreComboBox.setItems(playerList);
        playerIdUpdatePlayerComboBox.setItems(playerList);
        gameIdScoreComboBox.setItems(gameList);

        // Set up TableView columns
        playerIdColumn.setCellValueFactory(new PropertyValueFactory<>("playerId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        gameTitleColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));
        playingDateColumn.setCellValueFactory(new PropertyValueFactory<>("playingDate"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        playerIdColumnInGameTable.setCellValueFactory(new PropertyValueFactory<>("playerId"));

    }

    // Method to load players from the database and populate the player list
    private void loadPlayers() {
        playerList.clear(); // Clear any existing data in the player list
        try {
            String query = "SELECT player_id, first_name, last_name FROM Carlos_Galdona_Bastidas_player order by 1 asc";
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Process each player returned from the query
            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                playerList.add(playerId + " - " + fullName);
            }

            // Check if no players were found
            if (playerList.isEmpty()) {
                showError("No players found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in loading players: " + e.getMessage());
        }
    }

    // Method to load games from the database and populate the game list
    private void loadGames() {
        gameList.clear();
        try {
            String query = "SELECT game_id, game_title FROM Carlos_Galdona_Bastidas_game order by 1 asc";
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Process each game returned from the query
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                String gameName = rs.getString("game_title");
                gameList.add(gameId + " - " + gameName);
            }

            // Check if no games were found
            if (gameList.isEmpty()) {
                showError("No Games found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in loading players: " + e.getMessage());
        }
    }

    // Handles the registration of a new player
    @FXML
    private void onSubmitRegisterPlayer(ActionEvent event) {
        try {

            // Check if required fields are empty
            if (registerFirstNameField.getText().isEmpty() || registerLastNameField.getText().isEmpty()) {
                showError("First Name and Last Name are required.");
                return;
            }

            // Prepare SQL query to insert new player into the database
            String query = "INSERT INTO Carlos_Galdona_Bastidas_player(first_name, last_name, address, postal_code, province, phone_number) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = dbConnection.prepareStatement(query);

            // Set query parameters from user input
            stmt.setString(1, registerFirstNameField.getText());
            stmt.setString(2, registerLastNameField.getText());
            stmt.setString(3, registerAddressField.getText());
            stmt.setString(4, registerPostalCodeField.getText());
            stmt.setString(5, registerProvinceField.getText());
            stmt.setString(6, registerPhoneNumberField.getText());

            // Execute the query
            stmt.executeUpdate();
            showMessage("Player registered successfully.");

            // Clear input fields and refresh the player list
            clearRegisterPlayerFields();
            loadPlayers(); // Refresh player list
        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in registering player: " + e.getMessage());
        }
    }

    private void clearRegisterPlayerFields() {
        registerFirstNameField.clear();
        registerLastNameField.clear();
        registerAddressField.clear();
        registerPostalCodeField.clear();
        registerProvinceField.clear();
        registerPhoneNumberField.clear();
    }

    // Handles the selection of a player from a ComboBox
    @FXML
    private void onPlayerSelection(ActionEvent event) {
        ComboBox<String> source = (ComboBox<String>) event.getSource();
        String selectedPlayer = source.getValue();

        if (selectedPlayer != null) {
            String[] parts = selectedPlayer.split(" - ");
            int playerId = Integer.parseInt(parts[0]);
            populatePlayerDetails(playerId, source);
        }
    }

    // Extracts the player ID from the ComboBox selection
    private int extractPlayerIdFromComboBox(String selectedPlayer) {
        String[] parts = selectedPlayer.split(" - ");
        String playerIdString = parts[0].trim();
        return Integer.parseInt(playerIdString);
    }

    // Populates player details into appropriate fields based on the selected player ID
    private void populatePlayerDetails(int playerId, ComboBox<String> sourceComboBox) {
        try {
            String query = "SELECT * FROM Carlos_Galdona_Bastidas_player WHERE player_id = ?";
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();

            // Fill the fields if data is found
            if (rs.next()) {
                if (sourceComboBox == playerIdUpdatePlayerComboBox) {
                    updateFirstNameField.setText(rs.getString("first_name"));
                    updateLastNameField.setText(rs.getString("last_name"));
                    updateAddressField.setText(rs.getString("address"));
                    updatePostalCodeField.setText(rs.getString("postal_code"));
                    updateProvinceField.setText(rs.getString("province"));
                    updatePhoneNumberField.setText(rs.getString("phone_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in loading player details: " + e.getMessage());
        }
    }

    // Handles the registration of a new game
    @FXML
    private void onSubmitRegisterGame(ActionEvent event) {
        try {

            // Check if the game title field is empty
            if (titleField.getText().isEmpty()) {
                showError("Game's title is required.");
                return;
            }

            // Prepare SQL query to insert new game into the database
            String query = "INSERT INTO Carlos_Galdona_Bastidas_game (game_title) " +
                    "VALUES (?)";
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, titleField.getText());

            // Execute the query
            stmt.executeUpdate();
            showMessage("Game registered successfully.");

            // Clear input fields and refresh the game list
            clearRegisterGameFields();
            loadGames(); // Refresh player list
        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in registering game: " + e.getMessage());
        }
    }

    @FXML
    private void clearRegisterGameFields() {

        titleField.clear();
    }

    // Handles the selection of a game from the ComboBox
    @FXML
    private void onGameSelection(ActionEvent event) {
        ComboBox<String> source = (ComboBox<String>) event.getSource();
        String selectedGame= source.getValue();

        if (selectedGame != null) {
            String[] parts = selectedGame.split(" - ");
            int gameId = Integer.parseInt(parts[0]);
            populateGameDetails(gameId, source);
        }
    }

    // Extracts the game ID from the ComboBox selection string
    private int extractGameIdFromComboBox(String selectedGame) {
        String[] parts = selectedGame.split(" - ");
        String gameIdString = parts[0].trim();
        return Integer.parseInt(gameIdString);
    }

    // Populates game details into appropriate fields based on the selected game ID
    private void populateGameDetails(int gameId, ComboBox<String> sourceComboBox) {
        try {
            String query = "SELECT * FROM Carlos_Galdona_Bastidas_game WHERE game_id = ?";
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();

            // Fill the fields if data is found
            if (rs.next()) {
                if (sourceComboBox == gameIdScoreComboBox) {
                    titleField.setText(rs.getString("game_title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in loading player details: " + e.getMessage());
        }
    }


    // Handles the submission of a score entry for a player and game
    @FXML
    private void onSubmitScore(ActionEvent event) {
        try {
            // Validate if the DatePicker or scoreField is empty
            if (playingDatePicker.getValue() == null || scoreField.getText().isEmpty()) {
                showError("Playing Date and Score are required.");
                return;
            }

            // Validate the selected Player from the ComboBox
            String selectedPlayer = playerIdScoreComboBox.getValue();
            if (selectedPlayer == null || selectedPlayer.isEmpty()) {
                showError("Please select a Player.");
                return;
            }

            // Validate the selected Game from the ComboBox
            String selectedGame = gameIdScoreComboBox.getValue();
            if (selectedGame == null || selectedGame.isEmpty()) {
                showError("Please select a Game.");
                return;
            }

            // Extract IDs from ComboBox selections
            int selectedPlayerId = extractPlayerIdFromComboBox(selectedPlayer);
            int selectedGameId = extractGameIdFromComboBox(selectedGame);

            // Convert LocalDate from DatePicker to java.sql.Date
            LocalDate playingDate = playingDatePicker.getValue();
            java.sql.Date sqlDate = java.sql.Date.valueOf(playingDate);

            // Prepare and execute the SQL query
            String query = "INSERT INTO Carlos_Galdona_player_and_game (game_id, player_id, playing_date, score) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, selectedGameId);
            stmt.setInt(2, selectedPlayerId);
            stmt.setDate(3, sqlDate);
            stmt.setInt(4, Integer.parseInt(scoreField.getText()));

            stmt.executeUpdate();
            showMessage("Score registered successfully.");
            clearScore();

        } catch (NumberFormatException e) {
            showError("Score must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in registering score: " + e.getMessage());
        }
    }

    @FXML
    private void clearScore() {

        playingDatePicker.setValue(null);
        scoreField.clear();
        playerIdScoreComboBox.getSelectionModel().clearSelection();
        gameIdScoreComboBox.getSelectionModel().clearSelection();
    }

    // Updates player details based on input from the update form
    @FXML
    private void onUpdatePlayer(ActionEvent event) {

        // Validate the selected Player from the ComboBox
        String selectedPlayer = playerIdUpdatePlayerComboBox.getValue();
        if (selectedPlayer == null || selectedPlayer.isEmpty()) {
            showError("Please select a Player to update.");
            return;
        }

        int selectedPlayerId = extractPlayerIdFromComboBox(selectedPlayer);

        // Get the input values
        String firstName = updateFirstNameField.getText();
        String lastName = updateLastNameField.getText();
        String address = updateAddressField.getText();
        String postalCode = updatePostalCodeField.getText();
        String province = updateProvinceField.getText();
        String phoneNumber = updatePhoneNumberField.getText();

        // Validate the input values
        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || postalCode.isEmpty()
                || province.isEmpty() || phoneNumber.isEmpty()) {
            showError("All fields must be filled out to update the player's information.");
            return;
        }

        // SQL query to update the player record
        String updateQuery = "UPDATE Carlos_Galdona_Bastidas_player SET first_name = ?, last_name = ?, address = ?, postal_code = ?, " +
                "province = ?, phone_number = ? WHERE player_id = ?";

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, postalCode);
            preparedStatement.setString(5, province);
            preparedStatement.setString(6, phoneNumber);
            preparedStatement.setInt(7, selectedPlayerId);  // Use the integer player ID

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showMessage("Player information updated successfully.");
                clearUpdatePlayerFields();
            } else {
                showError("No record was updated. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while updating the player's information: " + e.getMessage());
        }
    }

    private void clearUpdatePlayerFields() {
        updateFirstNameField.clear();
        updateLastNameField.clear();
        updateAddressField.clear();
        updatePostalCodeField.clear();
        updateProvinceField.clear();
        updatePhoneNumberField.clear();
    }

    // Handles the selection of the Information tab, populating the player and game tables
    @FXML
    public void onDisplayInformationTabSelected() {
        // Clear previous data in the TableViews
        playerTable.getItems().clear();
        gameTable.getItems().clear();
        initializeComboBox();
        loadAllGames(); // Carga todos los juegos inicialmente

        // Fetch player data from the database and populate the playerTable
        try (PreparedStatement stmt = dbConnection.prepareStatement("SELECT * FROM Carlos_Galdona_Bastidas_player ORDER BY 1 ASC");
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<Player> players = FXCollections.observableArrayList();

            while (rs.next()) {
                Player player = new Player(
                        rs.getInt("player_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("postal_code"),
                        rs.getString("province"),
                        rs.getString("phone_number")
                );
                players.add(player);
            }

            // Populate playerTable with retrieved data
            playerTable.setItems(players);

            // Add selection listener to update the gameTable when a player is selected
            playerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Fetch game data for the selected player
                    fetchGamesForPlayer(newValue.getPlayerId());
                } else {
                    // Si no hay selección, muestra todos los juegos
                    loadAllGames();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in fetching player data: " + e.getMessage());
        }
    }


    private void fetchGamesForPlayer(int playerId) {
        String gameQuery = "SELECT a.player_id, b.game_title, a.playing_date, a.score " +
                "FROM Carlos_Galdona_player_and_game " +
                "JOIN Carlos_Galdona_Bastidas_game   ON a.game_id = b.game_id " +
                "WHERE a.player_id = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(gameQuery)) {
            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                ObservableList<Games> games = FXCollections.observableArrayList();

                while (rs.next()) {
                    Games game = new Games(
                            rs.getInt("player_id"),  // Use the same player_id
                            rs.getString("game_title"),
                            rs.getDate("playing_date"),
                            rs.getInt("score")
                    );
                    games.add(game);
                }

                // Populate gameTable with filtered data
                gameTable.setItems(games);

            } catch (SQLException e) {
                e.printStackTrace();
                showError("SQL Error in fetching game data: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in fetching game data for selected player: " + e.getMessage());
        }
    }



    // Displays an error message in an alert dialog
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Displays an informational message in an alert dialog
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private ComboBox<String> playerIdComboBox;

    @FXML
    public void initializeComboBox() {
        String query = "SELECT player_id, first_name, last_name FROM Carlos_Galdona_Bastidas_player ORDER BY 1 ASC";

        try (PreparedStatement stmt = dbConnection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<String> playerOptions = FXCollections.observableArrayList();

            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                // Concatenar información para el ComboBox
                String displayValue = playerId + " - " + firstName + " " + lastName;
                playerOptions.add(displayValue);
            }

            // Configurar el ComboBox con las opciones generadas
            playerIdComboBox.setItems(playerOptions);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in loading Player IDs: " + e.getMessage());
        }
    }

    @FXML
    public void onPlayerIdSelected() {
        String selectedValue = playerIdComboBox.getValue();

        if (selectedValue != null) {
            int selectedPlayerId = extractPlayerIdFromComboBox(selectedValue);

            playerTable.getItems().clear();
            String query = "SELECT * FROM Carlos_Galdona_Bastidas_player WHERE player_id = ?";

            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setInt(1, selectedPlayerId);

                try (ResultSet rs = stmt.executeQuery()) {
                    ObservableList<Player> players = FXCollections.observableArrayList();

                    while (rs.next()) {
                        Player player = new Player(
                                rs.getInt("player_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("address"),
                                rs.getString("postal_code"),
                                rs.getString("province"),
                                rs.getString("phone_number")
                        );
                        players.add(player);
                    }

                    playerTable.setItems(players);
                    fetchGamesForPlayer(selectedPlayerId);

                } catch (SQLException e) {
                    e.printStackTrace();
                    showError("SQL Error in filtering player data: " + e.getMessage());
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showError("SQL Error in fetching player data: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadAllGames() {
        String query = "SELECT a.player_id, b.game_title, a.playing_date, a.score " +
                "FROM Carlos_Galdona_player_and_game a " +
                "JOIN Carlos_Galdona_Bastidas_game ON a.game_id = b.game_id";

        try (PreparedStatement stmt = dbConnection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<Games> games = FXCollections.observableArrayList();

            while (rs.next()) {
                Games game = new Games(
                        rs.getInt("player_id"),
                        rs.getString("game_title"),
                        rs.getDate("playing_date"),
                        rs.getInt("score")
                );
                games.add(game);
            }

            // Populate gameTable with all game data
            gameTable.setItems(games);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("SQL Error in loading all games: " + e.getMessage());
        }
    }




}