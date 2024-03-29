package com.todo.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.todo.app.exception.NotFoundException;
import com.todo.app.model.TodoItem;
import com.todo.app.repo.TodoItemRepository;

@Controller
@RequestMapping("/")
public class TodoItemController {

	private final TodoItemRepository repository;

	public TodoItemController(TodoItemRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public String index(Model model) {
		addAttributesForIndex(model, ListFilter.ALL);
		return "index";
	}

	@GetMapping("/active")
	public String indexActive(Model model) {
		addAttributesForIndex(model, ListFilter.ACTIVE);
		return "index";
	}

	@GetMapping("/completed")
	public String indexCompleted(Model model) {
		addAttributesForIndex(model, ListFilter.COMPLETED);
		return "index";
	}

	private void addAttributesForIndex(Model model, ListFilter listFilter) {
		model.addAttribute("item", new TodoItemFormData());
		model.addAttribute("filter", listFilter);
		model.addAttribute("todos", getTodoItems(listFilter));
		model.addAttribute("totalNumberOfItems", repository.count());
		model.addAttribute("numberOfActiveItems", getNumberOfActiveItems());
		model.addAttribute("numberOfCompletedItems", getNumberOfCompletedItems());
	}

	@PostMapping
	public String addNewTodoItem(@Valid @ModelAttribute("item") TodoItemFormData formData) {
		repository.save(new TodoItem(formData.getTitle(), false));

		return "redirect:/";
	}

	@PutMapping("/{id}/toggle")
	public String toggleSelection(@PathVariable("id") Long id) {
		TodoItem todoItem = repository.findById(id).orElseThrow(() -> new NotFoundException(id));

		todoItem.setCompleted(!todoItem.isCompleted());
		repository.save(todoItem);
		return "redirect:/";
	}

	@PutMapping("/toggle-all")
	public String toggleAll() {
		List<TodoItem> todoItems = repository.findAll();
		for (TodoItem todoItem : todoItems) {
			todoItem.setCompleted(!todoItem.isCompleted());
			repository.save(todoItem);
		}
		return "redirect:/";
	}

	@DeleteMapping("/{id}")
	public String deleteTodoItem(@PathVariable("id") Long id) {
		repository.deleteById(id);

		return "redirect:/";
	}

	@DeleteMapping("/completed")
	public String deleteCompletedItems() {
		List<TodoItem> items = repository.findAllByCompleted(true);
		for (TodoItem item : items) {
			repository.deleteById(item.getId());
		}
		return "redirect:/";
	}

	private List<TodoItem> getTodoItems(ListFilter filter) {
		if (filter.name().equals("ALL")) {
			return convertToDto(repository.findAll());
		} else if (filter.name().equals("ACTIVE")) {
			return convertToDto(repository.findAllByCompleted(false));
		}
		return convertToDto(repository.findAllByCompleted(true));
	}

	private List<TodoItem> convertToDto(List<TodoItem> todoItems) {
		return todoItems.stream()
				.map(todoItem -> new TodoItem(todoItem.getId(), todoItem.getTitle(), todoItem.isCompleted()))
				.collect(Collectors.toList());
	}

	private int getNumberOfActiveItems() {
		return repository.countAllByCompleted(false);
	}

	private int getNumberOfCompletedItems() {
		return repository.countAllByCompleted(true);
	}

	public enum ListFilter {
		ALL, ACTIVE, COMPLETED
	}
}