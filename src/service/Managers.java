package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFileBackedManager() {
        File file = new File("src/resources/forHttp.csv");
        return new FileBackedTasksManager(file);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls().setPrettyPrinting();
        return gsonBuilder.create();
    }

}
