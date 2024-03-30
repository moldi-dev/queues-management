package org.moldidev.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.moldidev.business.SimulationManager;

public class Controller {

    @FXML
    private Label simulationStatusLabel;
    @FXML
    private Label validInputLabel;
    @FXML
    private Label inputValidationErrorLabel;
    @FXML
    private Label numberOfClientsLabel;
    @FXML
    private TextField numberOfClientsTextField;
    @FXML
    private Label numberOfQueuesLabel;
    @FXML
    private TextField numberOfQueuesTextField;
    @FXML
    private Label simulationIntervalLabel;
    @FXML
    private TextField simulationIntervalTextField;
    @FXML
    private TextArea simulationLogsTextArea;
    @FXML
    private Label simulationLogsLabel;
    @FXML
    private TextField minimumArrivalTimeTextField;
    @FXML
    private TextField maximumArrivalTimeTextField;
    @FXML
    private Label minimumArrivalTimeLabel;
    @FXML
    private Label maximumArrivalTimeLabel;
    @FXML
    private Label minimumServiceTimeLabel;
    @FXML
    private Label maximumServiceTimeLabel;
    @FXML
    private TextField minimumServiceTimeTextField;
    @FXML
    private TextField maximumServiceTimeTextField;
    @FXML
    private Button startSimulationButton;
    @FXML
    private Label selectionPolicyLabel;
    @FXML
    private ChoiceBox<String> selectionPolicyChoiceBox;
    private Timeline inputErrorTimeline = new Timeline();

    /*
    * @return void
    *
    * Initializes the controller and sets the values of the selection policy choice box.
    */
    @FXML
    public void initialize() {
        this.selectionPolicyChoiceBox.getItems().add("SHORTEST TIME");
        this.selectionPolicyChoiceBox.getItems().add("SHORTEST QUEUE");

        this.selectionPolicyChoiceBox.getSelectionModel().select(0);
    }

    public Label getSimulationStatusLabel() {
        return this.simulationStatusLabel;
    }

    public TextField getNumberOfClientsTextField() {
        return this.numberOfClientsTextField;
    }

    public TextField getNumberOfQueuesTextField() {
        return this.numberOfQueuesTextField;
    }

    public TextField getSimulationIntervalTextField() {
        return this.simulationIntervalTextField;
    }

    public TextArea getSimulationLogsTextArea() {
        return this.simulationLogsTextArea;
    }

    public TextField getMinimumArrivalTimeTextField() {
        return this.minimumArrivalTimeTextField;
    }

    public TextField getMaximumArrivalTimeTextField() {
        return this.maximumArrivalTimeTextField;
    }

    public TextField getMinimumServiceTimeTextField() {
        return this.minimumServiceTimeTextField;
    }

    public TextField getMaximumServiceTimeTextField() {
        return this.maximumServiceTimeTextField;
    }

    public Label getValidInputLabel() {
        return this.validInputLabel;
    }

    public Label getInvalidInputLabel() {
        return this.inputValidationErrorLabel;
    }

    public int getSelectionPolicy() {
        if (this.selectionPolicyChoiceBox.getSelectionModel().isSelected(0)) {
            return 0;
        }

        else {
            return 1;
        }
    }

    /*
    * @return void
    *
    * Checks the given inputs, and if they're correct, starts the simulation.
    */
    @FXML
    private void onStartSimulationButtonClicked() {
        if (checkInputs()) {
            SimulationManager simulationManager = new SimulationManager(this);
            Thread thread = new Thread(simulationManager);
            thread.start();
        }
    }

    /*
    * @param message
    * @return void
    *
    * Sets a the value of the invalid input error label and then hides it after 4 seconds.
    */
    private void setInputValidationErrorLabelMessage(String message) {
        this.inputValidationErrorLabel.setText(message);

        if (this.inputErrorTimeline != null) {
            this.inputErrorTimeline.stop();
        }

        this.inputErrorTimeline = new Timeline(new KeyFrame(
                Duration.seconds(4),
                event -> {
                    this.inputValidationErrorLabel.setText("");
                }
        ));

        this.inputErrorTimeline.play();
    }

    /*
    * @return void
    *
    * Disables the user's ability to change/add inputs (used when a simulation is active).
    */
    public synchronized void disableInputs() {
        this.startSimulationButton.setDisable(true);
        this.maximumServiceTimeTextField.setDisable(true);
        this.minimumServiceTimeTextField.setDisable(true);
        this.numberOfQueuesTextField.setDisable(true);
        this.numberOfClientsTextField.setDisable(true);
        this.simulationIntervalTextField.setDisable(true);
        this.minimumArrivalTimeTextField.setDisable(true);
        this.maximumArrivalTimeTextField.setDisable(true);
        this.selectionPolicyChoiceBox.setDisable(true);
        this.simulationLogsTextArea.setText("");
    }

    /*
     * @return void
     *
     * Lets the user change/add inputs as his wishes (used when a simulation has finished).
     */
    public synchronized void enableInputs() {
        this.startSimulationButton.setDisable(false);
        this.maximumServiceTimeTextField.setDisable(false);
        this.minimumServiceTimeTextField.setDisable(false);
        this.numberOfQueuesTextField.setDisable(false);
        this.numberOfClientsTextField.setDisable(false);
        this.simulationIntervalTextField.setDisable(false);
        this.minimumArrivalTimeTextField.setDisable(false);
        this.maximumArrivalTimeTextField.setDisable(false);
        this.selectionPolicyChoiceBox.setDisable(false);
    }

    /*
     * @return boolean
     *
     * Checks the user's inputs and if they're wrong displays a custom error message.
     */
    private boolean checkInputs() {
        String numberRegex = "^[1-9]+[0-9]*$"; // regex which checks for an integer greater than 0
        int userInput1;
        int userInput2;

        // Validate the number of clients text field (should be an integer greater than 0)
        if (this.numberOfClientsTextField.getText().isEmpty() || this.numberOfClientsTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The number of clients input field can't be empty, nor blank!");
            return false;
        }

        if (!this.numberOfClientsTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The number of clients must be an integer strictly greater than 0!");
            return false;
        }

        // Validate the number of queues text field (should be an integer greater than 0)
        if (this.numberOfQueuesTextField.getText().isEmpty() || this.numberOfQueuesTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The number of queues input field can't be empty, nor blank!");
            return false;
        }

        if (!this.numberOfQueuesTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The number of queues must be an integer strictly greater than 0!");
            return false;
        }

        // Validate the simulation interval text field (should be an integer greater than 0)
        if (this.simulationIntervalTextField.getText().isEmpty() || this.simulationIntervalTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The simulation interval input field can't be empty, nor blank!");
            return false;
        }

        if (!this.simulationIntervalTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The simulation interval must be an integer strictly greater than 0!");
            return false;
        }

        // Validate the minimum arrival time text field (should be an integer greater than 0)
        if (this.minimumArrivalTimeTextField.getText().isEmpty() || this.minimumArrivalTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The minimum arrival time input field can't be empty, nor blank!");
            return false;
        }

        if (!this.minimumArrivalTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The minimum arrival time must be an integer strictly greater than 0!");
            return false;
        }

        // Validate the maximum arrival time text field (should be an integer greater than 0)
        if (this.maximumArrivalTimeTextField.getText().isEmpty() || this.maximumArrivalTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The maximum arrival time input field can't be empty, nor blank!");
            return false;
        }

        if (!this.maximumArrivalTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The maximum arrival time must be an integer strictly greater than 0!");
            return false;
        }

        userInput1 = Integer.parseInt(this.minimumArrivalTimeTextField.getText().replaceAll(" ", ""));
        userInput2 = Integer.parseInt(this.maximumArrivalTimeTextField.getText().replaceAll(" ", ""));

        if (userInput1 > userInput2) {
            setInputValidationErrorLabelMessage("The minimum arrival time must be less than or equal to the maximum arrival time!");
            return false;
        }

        // Validate the minimum service time text field (should be an integer greater than 0)
        if (this.minimumServiceTimeTextField.getText().isEmpty() || this.minimumServiceTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The minimum service time input field can't be empty, nor blank!");
            return false;
        }

        if (!this.minimumServiceTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The minimum service time must be an integer strictly greater than 0!");
            return false;
        }

        // Validate the maximum service time text field (should be an integer greater than 0)
        if (this.maximumServiceTimeTextField.getText().isEmpty() || this.maximumServiceTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The maximum service time input field can't be empty, nor blank!");
            return false;
        }

        if (!this.maximumServiceTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The maximum service time must be an integer strictly greater than 0!");
            return false;
        }

        // Validate the relationship between the minimum service time and maximum service time
        userInput1 = Integer.parseInt(this.minimumServiceTimeTextField.getText().replaceAll(" ", ""));
        userInput2 = Integer.parseInt(this.maximumServiceTimeTextField.getText().replaceAll(" ", ""));

        if (userInput1 > userInput2) {
            setInputValidationErrorLabelMessage("The minimum service time must be less than or equal to the maximum service time!");
            return false;
        }

        return true;
    }
}