package com.example.market.controller;

import com.example.market.exception.BadRequestException;
import com.example.market.exception.NoPermissionException;
import com.example.market.exception.ProductNotFoundException;
import com.example.market.exception.UserNotFoundException;
import com.example.market.model.entity.Product;
import com.example.market.model.network.Header;
import com.example.market.repository.BookmarkRepository;
import com.example.market.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/{pid}")
    public Header create(@PathVariable Long pid){
        return bookmarkService.create(pid);
    }

    @GetMapping
    public Header<Optional<List<Product>>> read(){
        return bookmarkService.read();
    }

    @DeleteMapping("/{pid}")
    public Header delete(@PathVariable Long pid){
        return bookmarkService.delete(pid);
    }

    @GetMapping("/{pid}")
    public Header<Long> bookmarkCount(@PathVariable Long pid){
        return bookmarkService.getBookmarkCount(pid);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UserNotFoundException.class, ProductNotFoundException.class,
            NoPermissionException.class, BadRequestException.class})
    public Header exceptionHandler(Exception ex) {
        return Header.ERROR(ex.getMessage());
    }
}
