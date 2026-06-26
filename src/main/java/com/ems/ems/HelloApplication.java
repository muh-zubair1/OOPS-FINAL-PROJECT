package com.ems.ems;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class HelloApplication extends Application {

    private final TableView<Employee> table = new TableView<>();
    private final ObservableList<Employee> employeeDataList = FXCollections.observableArrayList();
    private FilteredList<Employee> filteredData;
    private SortedList<Employee> sortedData;
    private final String DATA_FILE = "saved_employees.txt";
    private Stage mainStage;

    // --- Admin Credentials ---
    private final String ADMIN_USER = "Admin";
    private final String ADMIN_PASS = "admin123";

    // --- Premium Dark Purple Theme Colors ---
    private final String BG_GRADIENT = "linear-gradient(to bottom right, #4a154b, #2c0b30, #110214)";
    private final String PURPLE_PREMIUM = "#9d4edd";
    private final String PURPLE_HOVER = "#c77dff";

    // --- Upgraded High-Contrast Slate Blue Selection Theme ---
    private final String ROW_HOVER = "#e0aaff";
    private final String ROW_SELECTED = "#023e8a";
    private final String GREEN_SOLID = "#2d6a4f";
    private final String GREEN_HOVER = "#40916c";
    private final String RED_SOLID = "#b7094c";
    private final String RED_HOVER = "#a01a58";
    private final String ERROR_RED = "#ff4d6d";

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        loadRecordsFromFile();
        showLoginScreen();
    }

    private void showLoginScreen() {
        mainStage.setTitle("Login - Secure Access");

        VBox loginContainer = new VBox(20);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setStyle("-fx-background-color: " + BG_GRADIENT + ";");

        VBox innerCard = new VBox(25);
        innerCard.setAlignment(Pos.CENTER);
        innerCard.setPadding(new Insets(40));
        innerCard.setMaxWidth(360);
        innerCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.07);" +
                " -fx-background-radius: 16;" +
                " -fx-border-radius: 16;" +
                " -fx-border-color: rgba(255, 255, 255, 0.15);" +
                " -fx-border-width: 1;" +
                " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 20, 0, 0, 10);");

        Label titleLabel = new Label("SYSTEM LOGIN");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: white; -fx-letter-spacing: 2px;");

        TextField usernameField = createLoginField("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(44);
        passwordField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-prompt-text-fill: #a29bac; -fx-background-radius: 20; -fx-padding: 0 15; -fx-font-size: 14px;");

        Button loginButton = createPremiumButton("Sign In", PURPLE_PREMIUM, PURPLE_HOVER);
        loginButton.setPrefHeight(44);
        loginButton.setPrefWidth(300);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: " + ERROR_RED + "; -fx-font-weight: bold;");

        loginButton.setOnAction(e -> {
            if (usernameField.getText().trim().equals(ADMIN_USER) && passwordField.getText().trim().equals(ADMIN_PASS)) {
                showMainDashboard();
            } else {
                errorLabel.setText("Invalid credentials!");
            }
        });

        innerCard.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, errorLabel);
        loginContainer.getChildren().add(innerCard);

        mainStage.setScene(new Scene(loginContainer, 450, 520));
        mainStage.show();
    }

    private void showMainDashboard() {
        mainStage.setTitle("Employee Management Workspace");

        filteredData = new FilteredList<>(employeeDataList, p -> true);
        sortedData = new SortedList<>(filteredData, Comparator.comparingInt(Employee::getId));

        // --- TOP GLOBAL HEADER ---
        Label mainHeading = new Label("Employee Management System");
        mainHeading.setFont(Font.font("System", FontWeight.BOLD, 26));
        mainHeading.setStyle("-fx-text-fill: white;");

        Button btnLogout = createPremiumButton("Logout", GREEN_SOLID, GREEN_HOVER);
        btnLogout.setOnAction(event -> showLoginScreen());

        HBox topBannerBar = new HBox(mainHeading, new Pane(), btnLogout);
        HBox.setHgrow(topBannerBar.getChildren().get(1), Priority.ALWAYS);
        topBannerBar.setPadding(new Insets(25, 30, 15, 30));
        topBannerBar.setAlignment(Pos.CENTER_LEFT);

        // --- FILTER BLOCK BAR & STRUCTURAL CONTROLS ---
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search by name...");
        searchBar.setPrefWidth(260); searchBar.setPrefHeight(38);
        searchBar.setStyle("-fx-background-color: white; -fx-text-fill: #2c0b30; -fx-prompt-text-fill: #888888; -fx-background-radius: 6 0 0 6; -fx-padding: 0 15; -fx-font-size: 13px;");

        Button btnSearchAction = new Button("Search");
        btnSearchAction.setPrefHeight(38); btnSearchAction.setPrefWidth(90);
        btnSearchAction.setStyle("-fx-background-color: " + PURPLE_PREMIUM + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0 6 6 0; -fx-cursor: hand; -fx-font-size: 13px;");
        btnSearchAction.setOnMouseEntered(e -> btnSearchAction.setStyle("-fx-background-color: " + PURPLE_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0 6 6 0; -fx-cursor: hand; -fx-font-size: 13px;"));
        btnSearchAction.setOnMouseExited(e -> btnSearchAction.setStyle("-fx-background-color: " + PURPLE_PREMIUM + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0 6 6 0; -fx-cursor: hand; -fx-font-size: 13px;"));

        ComboBox<String> sortFilterMenu = new ComboBox<>();
        sortFilterMenu.getItems().addAll("Normal (Ascending)", "Newest Added", "Oldest Added");
        sortFilterMenu.setValue("Normal (Ascending)");
        sortFilterMenu.setPrefHeight(38); sortFilterMenu.setPrefWidth(180);
        sortFilterMenu.getStyleClass().add("premium-combo");

        sortFilterMenu.valueProperty().addListener((observable, oldOrder, newOrder) -> {
            if ("Newest Added".equals(newOrder)) {
                sortedData.setComparator((e1, e2) -> Integer.compare(employeeDataList.indexOf(e2), employeeDataList.indexOf(e1)));
            } else if ("Oldest Added".equals(newOrder)) {
                sortedData.setComparator(Comparator.comparingInt(employeeDataList::indexOf));
            } else {
                sortedData.setComparator(Comparator.comparingInt(Employee::getId));
            }
        });

        Runnable performFiltering = () -> {
            String filterText = searchBar.getText().trim().toLowerCase();
            filteredData.setPredicate(emp -> filterText.isEmpty() || emp.getName().toLowerCase().contains(filterText));
        };

        searchBar.textProperty().addListener((o, old, newVal) -> performFiltering.run());
        btnSearchAction.setOnAction(e -> performFiltering.run());

        HBox searchBlockWrapper = new HBox(15, new HBox(searchBar, btnSearchAction), sortFilterMenu);
        searchBlockWrapper.setPadding(new Insets(5, 30, 20, 30));
        searchBlockWrapper.setAlignment(Pos.CENTER_LEFT);

        // --- HIGH-CONTRAST DATA TABLE ---
        TableColumn<Employee, Integer> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(120);

        TableColumn<Employee, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(280);

        TableColumn<Employee, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        deptCol.setPrefWidth(240);

        TableColumn<Employee, Double> salaryCol = new TableColumn<>("Salary (PKR)");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryCol.setPrefWidth(180);

        table.getColumns().clear();
        table.getColumns().addAll(idCol, nameCol, deptCol, salaryCol);
        table.setItems(sortedData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No records matched your search query."));
        table.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: rgba(255,255,255,0.1);");

        // Embedded modern stylesheet modifications
        String inlineCSSStyles =
                ".table-view { -fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; }" +
                        ".table-view .column-header-background { -fx-background-color: #f7f4fb; -fx-background-radius: 12 12 0 0; }" +
                        ".table-view .column-header { -fx-background-color: transparent; -fx-size: 45; -fx-border-color: #e1d8eb; -fx-border-width: 0 1 1 0; }" +
                        ".table-view .column-header .label { -fx-text-fill: #4a154b !important; -fx-font-size: 13px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT; }" +
                        ".table-view .table-row-cell { -fx-background-color: white; -fx-text-fill: #333333; -fx-border-color: #f0ebf7; -fx-border-width: 0 0 1 0; }" +
                        // Clean slate-blue hover styling fix
                        ".table-view .table-row-cell:filled:hover { -fx-background-color: " + ROW_HOVER + "; }" +
                        ".table-view .table-row-cell:filled:hover .table-cell { -fx-text-fill: #2c0b30; }" +
                        // Crisp selection coloring rules
                        ".table-view .table-row-cell:filled:selected { -fx-background-color: " + ROW_SELECTED + "; }" +
                        ".table-view .table-row-cell:filled:selected .table-cell { -fx-text-fill: white; -fx-font-weight: bold; }" +
                        ".table-view .table-cell { -fx-font-size: 14px; -fx-padding: 14 15; }" +
                        ".table-view .placeholder .label { -fx-text-fill: #888888; -fx-font-size: 14px; }" +
                        // ComboBox Dropdown Hover Styling
                        ".premium-combo { -fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 6; -fx-font-size: 13px; -fx-border-color: #e1d8eb; -fx-border-radius: 6; }" +
                        ".premium-combo .list-cell { -fx-text-fill: black; -fx-padding: 6 12; }" +
                        ".combo-box-popup .list-view { -fx-background-color: white; -fx-background-radius: 6; -fx-border-color: " + PURPLE_PREMIUM + "; -fx-border-width: 1; }" +
                        ".combo-box-popup .list-cell:filled:hover { -fx-background-color: " + ROW_HOVER + "; -fx-text-fill: #2c0b30; }";

        // --- CREATE EMPLOYEE PROFILE SCREEN (FIXED ALIGNMENTS & WIDTHS) ---
        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(40, 45, 45, 45));
        formCard.setMinWidth(820); // Expanded card shell base
        formCard.setAlignment(Pos.CENTER);
        formCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.06); -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: rgba(255, 255, 255, 0.12); -fx-border-width: 1;");

        Label formTitle = new Label("Create Employee Profile");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        formTitle.setStyle("-fx-text-fill: white;");
        formCard.getChildren().add(formTitle);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(20);
        inputGrid.setVgap(15);
        inputGrid.setAlignment(Pos.CENTER);

        // Explicit width layout allocation to absolutely guard labels from truncation
        ColumnConstraints col1 = new ColumnConstraints(120); // Label column 1
        ColumnConstraints col2 = new ColumnConstraints(230); // Input column 1
        ColumnConstraints col3 = new ColumnConstraints(120); // Label column 2
        ColumnConstraints col4 = new ColumnConstraints(230); // Input column 2
        inputGrid.getColumnConstraints().addAll(col1, col2, col3, col4);

        // Inputs
        TextField idInput = createFormInputField("ENTER ID ...");
        TextField nameInput = createFormInputField("ENTER NAME ...");
        TextField deptInput = createFormInputField("ENTER DEPT ...");
        TextField salaryInput = createFormInputField("ENTER SALARY IN PKR ...");

        // Real-time error text elements
        Label idError = createValidationErrorLabel();
        Label nameError = createValidationErrorLabel();
        Label deptError = createValidationErrorLabel();
        Label salaryError = createValidationErrorLabel();

        // Standardized component boxes with consistent vertical formatting
        VBox idBox = new VBox(4, idInput, idError);
        VBox nameBox = new VBox(4, nameInput, nameError);
        VBox deptBox = new VBox(4, deptInput, deptError);
        VBox salaryBox = new VBox(4, salaryInput, salaryError);

        // Labels centered inside row heights perfectly matching text field medians
        Label lblId = createFormLabel("Employee ID:");
        Label lblName = createFormLabel("Full Name:");
        Label lblDept = createFormLabel("Department:");
        Label lblSalary = createFormLabel("Salary (PKR):");

        GridPane.setValignment(lblId, javafx.geometry.VPos.TOP);
        GridPane.setMargin(lblId, new Insets(10, 0, 0, 0));
        GridPane.setValignment(lblName, javafx.geometry.VPos.TOP);
        GridPane.setMargin(lblName, new Insets(10, 0, 0, 0));
        GridPane.setValignment(lblDept, javafx.geometry.VPos.TOP);
        GridPane.setMargin(lblDept, new Insets(10, 0, 0, 0));
        GridPane.setValignment(lblSalary, javafx.geometry.VPos.TOP);
        GridPane.setMargin(lblSalary, new Insets(10, 0, 0, 0));

        inputGrid.add(lblId, 0, 0);       inputGrid.add(idBox, 1, 0);
        inputGrid.add(lblName, 2, 0);     inputGrid.add(nameBox, 3, 0);
        inputGrid.add(lblDept, 0, 1);    inputGrid.add(deptBox, 1, 1);
        inputGrid.add(lblSalary, 2, 1);  inputGrid.add(salaryBox, 3, 1);

        formCard.getChildren().add(inputGrid);

        VBox formCenteredOuterContainer = new VBox(formCard);
        formCenteredOuterContainer.setAlignment(Pos.CENTER);
        formCenteredOuterContainer.setPadding(new Insets(0, 30, 0, 30));
        VBox.setVgrow(formCenteredOuterContainer, Priority.ALWAYS);

        // --- REAL-TIME LIVE VALIDATION INPUT LOGIC INTERCEPTORS ---
        idInput.textProperty().addListener((o, old, newVal) -> {
            String val = newVal.trim();
            if (val.isEmpty()) {
                idError.setText("");
            } else if (!val.matches("\\d+")) {
                idError.setText("Must be positive numbers only!");
            } else {
                try {
                    int id = Integer.parseInt(val);
                    boolean taken = false;
                    for (Employee emp : employeeDataList) {
                        if (emp.getId() == id) { taken = true; break; }
                    }
                    if (taken) {
                        idError.setText("This ID is already taken!");
                    } else {
                        idError.setText("");
                    }
                } catch (NumberFormatException ex) {
                    idError.setText("Value is too large!");
                }
            }
        });

        nameInput.textProperty().addListener((o, old, newVal) -> {
            if (newVal.trim().isEmpty()) {
                nameError.setText("");
            } else if (!newVal.matches("[a-zA-Z\\s]+")) {
                nameError.setText("Letters only! No numbers/symbols.");
            } else {
                nameError.setText("");
            }
        });

        deptInput.textProperty().addListener((o, old, newVal) -> {
            if (newVal.trim().isEmpty()) {
                deptError.setText("");
            } else if (!newVal.matches("[a-zA-Z\\s]+")) {
                deptError.setText("Letters only! No numbers/symbols.");
            } else {
                deptError.setText("");
            }
        });

        salaryInput.textProperty().addListener((o, old, newVal) -> {
            String val = newVal.trim();
            if (val.isEmpty()) {
                salaryError.setText("");
            } else if (!val.matches("\\d+(\\.\\d+)?")) {
                salaryError.setText("Must be positive numbers only!");
            } else {
                try {
                    double sal = Double.parseDouble(val);
                    if (sal <= 0) {
                        salaryError.setText("Must be greater than zero!");
                    } else {
                        salaryError.setText("");
                    }
                } catch (NumberFormatException ex) {
                    salaryError.setText("Value configuration faulty!");
                }
            }
        });

        // --- OPERATIONAL CONTROL BUTTON SCHEMES ---
        Button btnViewRecords = createPremiumButton("View Records", "white", "rgba(255,255,255,0.9)");
        btnViewRecords.setStyle(btnViewRecords.getStyle() + "-fx-text-fill: #4a154b;");

        Button btnAdd = createPremiumButton("Add Employee", GREEN_SOLID, GREEN_HOVER);
        Button btnDelete = createPremiumButton("Delete Employee", RED_SOLID, RED_HOVER);
        Button btnUpdate = createPremiumButton("Update Employee", "white", "rgba(255,255,255,0.9)");
        btnUpdate.setStyle(btnUpdate.getStyle() + "-fx-text-fill: #4a154b;");

        Button btnBack = createPremiumButton("Back", PURPLE_PREMIUM, PURPLE_HOVER);

        HBox operationalActionBar = new HBox(15);
        operationalActionBar.setAlignment(Pos.CENTER_RIGHT);
        operationalActionBar.setPadding(new Insets(20, 30, 30, 30));

        VBox displayContentWrapper = new VBox(15);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPadding(new Insets(0, 30, 0, 30));

        Runnable setMainEntryViewMode = () -> {
            searchBlockWrapper.setVisible(false); searchBlockWrapper.setManaged(false);
            table.setVisible(false); table.setManaged(false);
            formCenteredOuterContainer.setVisible(true); formCenteredOuterContainer.setManaged(true);

            operationalActionBar.getChildren().clear();
            operationalActionBar.getChildren().addAll(btnViewRecords, btnAdd);

            displayContentWrapper.getChildren().clear();
            displayContentWrapper.getChildren().addAll(formCenteredOuterContainer);
        };

        Runnable setRecordListViewMode = () -> {
            formCenteredOuterContainer.setVisible(false); formCenteredOuterContainer.setManaged(false);
            searchBlockWrapper.setVisible(true); searchBlockWrapper.setManaged(true);
            table.setVisible(true); table.setManaged(true);

            operationalActionBar.getChildren().clear();
            operationalActionBar.getChildren().addAll(btnBack, btnUpdate, btnDelete);

            displayContentWrapper.getChildren().clear();
            displayContentWrapper.getChildren().addAll(searchBlockWrapper, table);
        };

        btnViewRecords.setOnAction(e -> setRecordListViewMode.run());
        btnBack.setOnAction(e -> {
            searchBar.clear();
            sortFilterMenu.setValue("Normal (Ascending)");
            setMainEntryViewMode.run();
        });

        // --- SUBMIT SAVE GUARD RULES ---
        btnAdd.setOnAction(event -> {
            if (!idError.getText().isEmpty() || !nameError.getText().isEmpty() ||
                    !deptError.getText().isEmpty() || !salaryError.getText().isEmpty()) {
                showNotification("Fix Input Faults", "Please resolve active validation errors flagged in red.", Alert.AlertType.WARNING);
                return;
            }

            String idStr = idInput.getText().trim();
            String nameStr = nameInput.getText().trim();
            String deptStr = deptInput.getText().trim();
            String salStr = salaryInput.getText().trim();

            if(idStr.isEmpty() || nameStr.isEmpty() || deptStr.isEmpty() || salStr.isEmpty()) {
                showNotification("Incomplete Profile", "All entry boxes must be filled before data sync.", Alert.AlertType.WARNING);
                return;
            }

            try {
                int id = Integer.parseInt(idStr);
                double salary = Double.parseDouble(salStr);

                employeeDataList.add(new Employee(id, nameStr, deptStr, salary));
                saveRecordsToFile();
                showNotification("Success", "Employee entry added successfully.", Alert.AlertType.INFORMATION);

                idInput.clear(); nameInput.clear(); deptInput.clear(); salaryInput.clear();
            } catch (Exception ex) {
                showNotification("Processing Error", "Could not map inputs cleanly. Check fields.", Alert.AlertType.ERROR);
            }
        });

        btnDelete.setOnAction(event -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                employeeDataList.remove(selected);
                saveRecordsToFile();
                showNotification("Removed", "Profile deleted completely.", Alert.AlertType.INFORMATION);
            } else {
                showNotification("No Row Highlighted", "Please click a line item row inside the database view first.", Alert.AlertType.WARNING);
            }
        });

        btnUpdate.setOnAction(event -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog updateDialog = new TextInputDialog(String.valueOf((int)selected.getSalary()));
                updateDialog.setTitle("Modify Salary Data");
                updateDialog.setHeaderText(null);
                updateDialog.setContentText("Enter updated base Salary (PKR):");

                DialogPane pane = updateDialog.getDialogPane();
                pane.setStyle("-fx-background-color: #2c0b30; -fx-border-color: " + PURPLE_PREMIUM + "; -fx-border-width: 1.5;");

                // This targets the main content text label inside the dialog directly
                if (pane.lookup(".content") instanceof Label) {
                    pane.lookup(".content").setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                }
                // Fallback selector to make absolutely sure any header or text defaults to white
                pane.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: white;"));
                updateDialog.getEditor().setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white;");

                Optional<String> result = updateDialog.showAndWait();
                result.ifPresent(newSal -> {
                    if (!newSal.trim().matches("\\d+(\\.\\d+)?") || Double.parseDouble(newSal.trim()) <= 0) {
                        showNotification("Validation Blocked", "Salary must be a clean, positive numeric value.", Alert.AlertType.ERROR);
                    } else {
                        selected.setSalary(Double.parseDouble(newSal.trim()));
                        table.refresh();
                        saveRecordsToFile();
                    }
                });
            } else {
                showNotification("Selection Missing", "Select an active record row to modify fields.", Alert.AlertType.WARNING);
            }
        });

        setMainEntryViewMode.run();

        BorderPane containerRoot = new BorderPane();
        containerRoot.setTop(topBannerBar);
        containerRoot.setCenter(displayContentWrapper);
        containerRoot.setBottom(operationalActionBar);
        containerRoot.setStyle("-fx-background-color: " + BG_GRADIENT + ";");

        Scene appScene = new Scene(containerRoot, 1120, 700);
        appScene.getStylesheets().add("data:text/css," + inlineCSSStyles.replace(" ", "%20"));

        mainStage.setScene(appScene);
        mainStage.centerOnScreen();
    }

    private Label createFormLabel(String labelText) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        lbl.setWrapText(false);
        return lbl;
    }

    private Label createValidationErrorLabel() {
        Label lbl = new Label("");
        lbl.setStyle("-fx-text-fill: " + ERROR_RED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
        lbl.setWrapText(true);
        lbl.setMaxWidth(220);
        lbl.setMinHeight(16); // Reserve layout height to avoid jumpy shifting
        return lbl;
    }

    private TextField createFormInputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(38); tf.setPrefWidth(220);
        tf.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-prompt-text-fill: #aaaaaa; -fx-background-radius: 6; -fx-padding: 0 12; -fx-border-color: #e1d8eb; -fx-border-radius: 6; -fx-border-width: 1.5;");
        return tf;
    }

    private TextField createLoginField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(44);
        tf.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-prompt-text-fill: #a29bac; -fx-background-radius: 20; -fx-padding: 0 15; -fx-font-size: 14px;");
        return tf;
    }

    private Button createPremiumButton(String text, String normalBg, String hoverBg) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: " + normalBg + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 0 25; -fx-cursor: hand; -fx-font-size: 13px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + hoverBg + "; -fx-text-fill: " + (normalBg.equals("white") ? "#4a154b" : "white") + "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 0 25; -fx-cursor: hand; -fx-font-size: 13px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + normalBg + "; -fx-text-fill: " + (normalBg.equals("white") ? "#4a154b" : "white") + "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 0 25; -fx-cursor: hand; -fx-font-size: 13px;"));
        return btn;
    }

    private void saveRecordsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(new ArrayList<>(employeeDataList));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void loadRecordsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Employee> list = (ArrayList<Employee>) ois.readObject();
            employeeDataList.clear(); employeeDataList.addAll(list);
        } catch (Exception e) { System.out.println("No saved instances found."); }
    }

    private void showNotification(String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(header); alert.setHeaderText(null); alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c0b30; -fx-border-color: " + PURPLE_PREMIUM + "; -fx-border-width: 1.5;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        alert.showAndWait();
    }
}