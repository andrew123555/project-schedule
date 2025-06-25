package com.example.demo.repository;

import com.example.demo.model.entity.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    /**
     * 根據專案ID查詢所有待辦事項，並使用JOIN FETCH同時加載關聯的Project和Assignee（User）實體。
     * 這可以避免在後續訪問這些關聯實體的屬性時出現LazyInitializationException。
     *
     * @param projectId 專案的ID
     * @return 屬於指定專案的TodoItem列表，每個TodoItem都已預先加載其Project和Assignee信息。
     */
    @Query("SELECT ti FROM TodoItem ti JOIN FETCH ti.project p JOIN FETCH ti.assignee a WHERE p.id = :projectId")
    List<TodoItem> findByProjectId(@Param("projectId") Long projectId);

    // 如果您還有其他按用戶獲取待辦事項的需求，也可以添加類似的查詢：
    /**
     * 根據負責人（用戶）ID查詢所有待辦事項，並使用JOIN FETCH同時加載關聯的Project和Assignee（User）實體。
     *
     * @param assigneeId 負責人的用戶ID
     * @return 由指定用戶負責的TodoItem列表，每個TodoItem都已預先加載其Project和Assignee信息。
     */
    // @Query("SELECT ti FROM TodoItem ti JOIN FETCH ti.project p JOIN FETCH ti.assignee a WHERE a.id = :assigneeId")
    // List<TodoItem> findByAssigneeId(@Param("assigneeId") Long assigneeId);
}