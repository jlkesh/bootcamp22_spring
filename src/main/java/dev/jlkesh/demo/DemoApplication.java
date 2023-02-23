package dev.jlkesh.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    @GetMapping(value = "/books/update/{id}")
    public String bookUpdatePage(Model model, @PathVariable String id) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        return "book/update";
    }

    @PostMapping(value = "/books/update/{id}")
    public String bookUpdatePage(@ModelAttribute BookCreateVO bookCreateVO, @PathVariable String id) {
        bookService.update(id, bookCreateVO);
        return "redirect:/books";
    }

}


interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findAllByAuthor(String a);
}

@Service
record BookService(BookRepository repository) {

    public List<Book> getAll() {
        return repository.findAll();
    }

    public Book findById(String id) {
        Supplier<RuntimeException> supplier = () -> new RuntimeException("Book not found by id '%s'".formatted(id));
        return repository.findById(id).orElseThrow(supplier);
    }

    public String create(@NonNull BookCreateVO vo) {
        Book book = Book.builder()
                .title(vo.title())
                .author(vo.author())
                .build();
        repository.save(book);
        return book.getId();
    }

    public boolean delete(String id) {
        repository.deleteById(id);
        return true;
    }

    public void update(String id, BookCreateVO vo) {
        Book updatingBook = new Book(id, vo.title(), vo.author());
        repository.save(updatingBook);
    }
}


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
final class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String author;
}

record BookCreateVO(String title, String author) {
}
