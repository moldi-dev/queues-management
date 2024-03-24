package org.moldidev.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

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
    private TextArea currentSimulationTimeTextArea;
    @FXML
    private Label currentSimulationTimeLabel;
    private Timeline inputErrorTimeline = new Timeline();
    private Timeline validInputTimeline = new Timeline();
    private Timeline disableInputsTimeline = new Timeline();

    public Controller() {}

    public TextField getNumberOfClientsTextField() {
        return this.numberOfClientsTextField;
    }

    public TextField getNumberOfQueuesTextField() {
        return this.numberOfQueuesTextField;
    }

    public TextField getSimulationIntervalTextField() {
        return this.simulationIntervalTextField;
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

    public TextArea getCurrentSimulationTimeTextArea() {
        return this.currentSimulationTimeTextArea;
    }

    public void setSimulationLogsTextArea(TextArea simulationLogsTextArea) {
        this.simulationLogsTextArea = simulationLogsTextArea;
    }

    public void setCurrentSimulationTimeTextArea(TextArea currentSimulationTimeTextArea) {
        this.currentSimulationTimeTextArea = currentSimulationTimeTextArea;
    }

    @FXML
    private void onStartSimulationButtonClicked() {
        if (checkInputs()) {
            setInputValidationErrorLabelMessage("");
            setValidInputLabelMessage("The simulation has been successfully set up! Performing the simulation...", Integer.parseInt(simulationIntervalTextField.getText().replaceAll(" ", "")));
            disableInputs(Integer.parseInt(simulationIntervalTextField.getText().replaceAll(" ", "")));

            // start performing the simulation
        }
    }

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

    private void setValidInputLabelMessage(String message, int simulationDuration) {
        this.validInputLabel.setText(message);

        if (this.validInputTimeline != null) {
            this.validInputTimeline.stop();
        }

        this.validInputTimeline = new Timeline(new KeyFrame(
                Duration.seconds(simulationDuration),
                event -> {
                    this.validInputLabel.setText("");
                }
        ));

        this.validInputTimeline.play();
    }

    private void disableInputs(int simulationDuration) {
        this.startSimulationButton.setDisable(true);
        this.maximumServiceTimeTextField.setDisable(true);
        this.minimumServiceTimeTextField.setDisable(true);
        this.numberOfQueuesTextField.setDisable(true);
        this.numberOfClientsTextField.setDisable(true);
        this.simulationIntervalTextField.setDisable(true);
        this.minimumArrivalTimeTextField.setDisable(true);
        this.maximumArrivalTimeTextField.setDisable(true);

        if (this.disableInputsTimeline != null) {
            this.disableInputsTimeline.stop();
        }

        this.disableInputsTimeline = new Timeline(new KeyFrame(
                Duration.seconds(simulationDuration),
                event -> {
                    this.startSimulationButton.setDisable(false);
                    this.maximumServiceTimeTextField.setDisable(false);
                    this.minimumServiceTimeTextField.setDisable(false);
                    this.numberOfQueuesTextField.setDisable(false);
                    this.numberOfClientsTextField.setDisable(false);
                    this.simulationIntervalTextField.setDisable(false);
                    this.minimumArrivalTimeTextField.setDisable(false);
                    this.maximumArrivalTimeTextField.setDisable(false);
                }
        ));

        this.disableInputsTimeline.play();
    }

    private boolean checkInputs() {
        String numberRegex = "^[1-9]+[0-9]*$"; // regex which checks for an integer greater than 0
        int userInput1;
        int userInput2;

        // Validate the number of clients text field (should be an integer greater than 0)
        if (numberOfClientsTextField.getText().isEmpty() || numberOfClientsTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The number of clients text field can't be empty, nor blank!");
            return false;
        }

        else if (!numberOfClientsTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The number of clients should be an integer strictly greater than 0!");
            return false;
        }

        // Validate the number of queues text field (should be an integer greater than 0)
        else if (numberOfQueuesTextField.getText().isEmpty() || numberOfQueuesTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The number of queues text field can't be empty, nor blank!");
            return false;
        }

        else if (!numberOfQueuesTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The number of queues should be an integer strictly greater than 0!");
            return false;
        }

        // Validate the simulation interval text field (should be an integer greater than 0)
        else if (simulationIntervalTextField.getText().isEmpty() || simulationIntervalTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The simulation interval text field can't be empty, nor blank!");
            return false;
        }

        else if (!simulationIntervalTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The simulation interval should be an integer strictly greater than 0!");
            return false;
        }

        // Validate the minimum arrival time text field (should be an integer greater than 0)
        else if (minimumArrivalTimeTextField.getText().isEmpty() || minimumArrivalTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The minimum arrival time text field can't be empty, nor blank!");
            return false;
        }

        else if (!minimumArrivalTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The minimum arrival time should be an integer strictly greater than 0!");
            return false;
        }

        // Validate the maximum arrival time text field (should be an integer greater than 0)
        else if (maximumArrivalTimeTextField.getText().isEmpty() || maximumArrivalTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The maximum arrival time can't be empty, nor blank!");
            return false;
        }

        else if (!maximumArrivalTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The maximum arrival time should be an integer strictly greater than 0!");
            return false;
        }

        userInput1 = Integer.parseInt(minimumArrivalTimeTextField.getText().replaceAll(" ", ""));
        userInput2 = Integer.parseInt(maximumArrivalTimeTextField.getText().replaceAll(" ", ""));

        if (userInput1 > userInput2) {
            setInputValidationErrorLabelMessage("The minimum arrival time should be less than or equal to the maximum arrival time!");
            return false;
        }

        // Validate the minimum service time text field (should be an integer greater than 0)
        else if (minimumServiceTimeTextField.getText().isEmpty() || minimumServiceTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The minimum service time text field can't be empty, nor blank!");
            return false;
        }

        else if (!minimumServiceTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The minimum service time should be an integer strictly greater than 0!");
            return false;
        }

        // Validate the maximum service time text field (should be an integer greater than 0)
        else if (maximumServiceTimeTextField.getText().isEmpty() || maximumServiceTimeTextField.getText().isBlank()) {
            setInputValidationErrorLabelMessage("The maximum service time text field can't be empty, nor blank!");
            return false;
        }

        else if (!maximumServiceTimeTextField.getText().replaceAll(" ", "").matches(numberRegex)) {
            setInputValidationErrorLabelMessage("The maximum service time should be an integer strictly greater than 0!");
            return false;
        }

        userInput1 = Integer.parseInt(minimumServiceTimeTextField.getText().replaceAll(" ", ""));
        userInput2 = Integer.parseInt(maximumServiceTimeTextField.getText().replaceAll(" ", ""));

        if (userInput1 > userInput2) {
            setInputValidationErrorLabelMessage("The minimum service time should be less than or equal to the maximum service time!");
            return false;
        }

        return true;
    }
}