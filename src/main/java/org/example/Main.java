package org.example;

import BusinessLogic.SimulationManager;
import BusinessLogic.Controller;
import Presentation.SetupWindow;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        SimulationManager simulationManager = new SimulationManager(controller);
        new SetupWindow(controller);
        controller.setSimulationManager(simulationManager);
    }
}