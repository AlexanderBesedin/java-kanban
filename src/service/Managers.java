package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.utils.DataTimeAdapter;
import service.utils.DurationAdapter;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault(String uri) {
        return new HttpTaskManager(uri);
    }

    public static TaskManager getMemoryManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileManager(File file) {
        return new FileBackedTasksManager(file);
    }

    public static HistoryManager gettHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static Gson getDefaultGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DataTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }
}
