package service;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFBManager(File file) {
        return new FileBackedTasksManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
