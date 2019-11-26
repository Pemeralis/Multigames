package multigames.competition;

import multigames.stages.Stage;

public class CompetitionManager {
    private Stage currentStage;

    public CompetitionManager() {
        currentStage = null;
    }

    public Stage getStage() {
        return currentStage;
    }

    public void startStage(Stage stage) throws StageAlreadyLoadedException {
        if (currentStage != null)
            throw new StageAlreadyLoadedException();
        currentStage = stage;
        currentStage.beginPhase();
    }

    public void stopStage() {
        currentStage.endPhase();
    }

    public void dereferenceStage() {
        currentStage = null;
    }
}