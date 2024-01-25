package com.todo.app;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.todo.app.controller.TodoItemController;
import com.todo.app.controller.TodoItemFormData;
import com.todo.app.model.TodoItem;
import com.todo.app.repo.TodoItemRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoItemControllerTest {

	@MockBean
	private TodoItemRepository repo;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testActiveTodos() throws Exception {
		when(repo.count()).thenReturn(2L);
		this.mockMvc.perform(get("/active")).andExpect(status().isOk())
				.andExpect(model().attribute("totalNumberOfItems", 2L)).andExpect(view().name("index"));
	}

	@Test
	public void testCompletedTodos() throws Exception {
		when(repo.count()).thenReturn(10L);
		when(repo.countAllByCompleted(true)).thenReturn(5);
		this.mockMvc.perform(get("/completed")).andExpect(status().isOk())
		.andExpect(model().attribute("totalNumberOfItems", 10L))
		.andExpect(model().attribute("numberOfCompletedItems", 5))
		.andExpect(view().name("index"));
	}
	
	@Test
	public void testAddTodoItem() throws Exception {
		TodoItem todoitem = new TodoItem("perform dance", false);
		when(repo.save(todoitem)).thenReturn(todoitem);
		TodoItemFormData item = new TodoItemFormData(); 
		item.setTitle("perform dance");

		this.mockMvc.perform(post("/") 
		.flashAttr("item", item))
		.andReturn();
	}
	
	@Test
	public void testToggleById() throws Exception {
		TodoItem todoitem = new TodoItem("perform dance", false);

		when(repo.findById(2L)).thenReturn(Optional.of(todoitem));
		when(repo.save(todoitem)).thenReturn(todoitem);
		
		this.mockMvc.perform(put("/2/toggle").queryParam("id", "2")).andReturn();
	}
}
