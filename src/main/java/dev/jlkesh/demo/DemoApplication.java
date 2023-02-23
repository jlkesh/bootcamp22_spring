package dev.jlkesh.demo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}


@Controller
@RequiredArgsConstructor
class BookController {

    private final BookService bookService;


    @GetMapping(value = {"/home", "/main"})
    public String homePage() {
        return "home";
    }

    @GetMapping(value = "/books")
    public ModelAndView getAllBooks() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("book/list");
        modelAndView.addObject("books", bookService.getAll());
        return modelAndView;
    }

    @GetMapping(value = "/books/delete/{id}")
    public String deletePage(Model model, @PathVariable String id) {
        var book = bookService.findById(id);
        model.addAttribute("book", book);
        return "book/delete";
    }

    @PostMapping(value = "/books/delete/{id}")
    public String delete(Model model, @PathVariable String id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @GetMapping(value = "/books/add")
    public String bookAddPage() {
        return "book/add";
    }

    @PostMapping(value = "/books/add")
    public String bookAddPage(@ModelAttribute BookCreateVO bookCreateVO) {
        bookService.create(bookCreateVO);
        return "redirect:/books";
    }

}


@Service
record BookService() {

    public static final List<Book> BOOKS = new ArrayList<>() {{
        add(new Book(UUID.randomUUID().toString(), "Spring For Beginners", "ME"));
        add(new Book(UUID.randomUUID().toString(), "Spring Core", "ME"));
        add(new Book(UUID.randomUUID().toString(), "Spring AOP", "ME"));
    }};

    public List<Book> getAll() {
        return BOOKS;
    }

    public Book findById(String id) {
        Supplier<RuntimeException> supplier = () -> new RuntimeException("Book not found by id '%s'".formatted(id));
        return BOOKS.stream().filter(book -> book.id().equals(id))
                .findAny()
                .orElseThrow(supplier);
    }

    public String create(@NonNull BookCreateVO bookCreateVO) {
        Book book = bookCreateVO.toBook();
        BOOKS.add(book);
        return book.id();
    }

    public boolean delete(String id) {
        return BOOKS.removeIf(book -> book.id().equals(id));
    }
}


record Book(String id, String title, String author) {
}

record BookCreateVO(String title, String author) {
    public Book toBook() {
        return new Book(
                UUID.randomUUID().toString(),
                this.title,
                this.author
        );
    }
}
