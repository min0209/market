package com.example.market.service;

import com.example.market.exception.BadRequestException;
import com.example.market.exception.NoPermissionException;
import com.example.market.exception.ProductNotFoundException;
import com.example.market.exception.UserNotFoundException;
import com.example.market.model.entity.Bookmark;
import com.example.market.model.entity.Product;
import com.example.market.model.entity.User;
import com.example.market.model.network.Header;
import com.example.market.repository.BookmarkRepository;
import com.example.market.repository.ProductRepository;
import com.example.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Header create(Long pid){
        Authentication auth = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(NoPermissionException::new);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(NoPermissionException::new);
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .product(productRepository.findById(pid).orElseThrow(ProductNotFoundException::new))
                .build();
        if(!bookmarkRepository.findByUserUidAndProductPid(user.getUid(),pid).isEmpty()){
            throw new BadRequestException();
        }
        bookmarkRepository.save(bookmark);
        return Header.OK();
    }

    public Header<Optional<List<Product>>> read(){
        Authentication auth = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(NoPermissionException::new);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(UserNotFoundException::new);
        return Header.OK(bookmarkRepository.findBookmarkListOfUser(user.getUid()));
    }

    public Header delete(Long pid){
        Authentication auth = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(NoPermissionException::new);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(NoPermissionException::new);
        Bookmark bookmark = bookmarkRepository.findByUserUidAndProductPid(user.getUid(),pid)
                .orElseThrow(BadRequestException::new);
        bookmarkRepository.delete(bookmark);
        return Header.OK();
    }

    public Header<Long> getBookmarkCount(Long pid){
        return Header.OK(bookmarkRepository.countByProductPid(pid));
    }

    public Header<Boolean> isBookmarked(Long pid){
        Authentication auth = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(NoPermissionException::new);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(NoPermissionException::new);
        Boolean b = bookmarkRepository.findByUserUidAndProductPid(user.getUid(),pid)
                .map(bookmark -> true).orElseGet(() -> false);
        return Header.OK(b);
    }

}
