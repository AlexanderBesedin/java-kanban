package ru.practicum.kanban.service.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @AfterEach
    void tearDown() {
        taskManager.id = 0;
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
    }


    /*@Test
    void checkTimeOverlaps() { - проверяется в TaskManagerTest тестами:
        shouldCreateTasksIfTimeNotOverlaps
        shouldCreateTaskIfStartTimeEqualPrevEndTime
        shouldDoNotCreateTaskIfTimeOverlapsOtherTask
        shouldDoNotCreateTaskIfTimeBooked
        shouldDoNotCreateTaskIfStartTimeBooked
        shouldDoNotCreateTaskIfEndTimeBooked
        shouldCreateTaskIfStartTimeEqualStartYear
        shouldDoNotCreateTaskIfStartTimeBeforeStartYear
        shouldDoNotCreateTaskIfStartTimeEqualEndYear
        shouldDoNotCreateTaskIfStartTimeAfterEndYear
        shouldCreateTaskIfEndTimeEqualEndYear
        shouldDoNotCreateTaskIfEndTimeAfterEndYear
    }*/

    /*@Test
    void updateEpicStatus() {  - проверяется в TaskManagerTest тестами:
        shouldBeEpicStatusNewIfNoSubtasks
        shouldBeEpicStatusNewIfSubtasksAreNew
        shouldBeEpicStatusDoneIfSubtasksAreDone
        shouldBeEpicStatusInProgressIfSubtasksAreDoneAndNew
        shouldBeEpicStatusInProgressIfSubtasksAreInProgress

    }*/

    /*@Test
    void updateEpicDuration() {  - проверяется в TaskManagerTest тестами:
        shouldBeEpicTimeIsNull
        shouldBeEpicStartTimeEqualStartTimeEarlySubtask
        shouldBeEpicEndTimeEqualEndTimeLateSubtask
        shouldBeEpicDurationEqualSumDurationSubtasks
    }*/
}