package service;

public class Status { //Класс для хранения статусов жизненного цикла задач
    private static String[] status  = new String[]{"NEW", "IN_PROGRESS", "DONE"};;

    public static String getStatus(int num) {
        return status[num];
    }
}
