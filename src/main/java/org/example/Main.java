package org.example;

import BusinessLogic.SimulationManager;
import BusinessLogic.Controller;
import Presentation.SetupWindow;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        SimulationManager simulationManager = new SimulationManager(controller);
        SetupWindow setupWindow = new SetupWindow(controller);
        controller.setSimulationManager(simulationManager);
        controller.setSetupWindow(setupWindow);
    }
}