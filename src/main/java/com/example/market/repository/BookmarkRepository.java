package com.example.market.repository;

import com.example.market.model.entity.Bookmark;
import com.example.market.model.entity.Product;
import com.example.market.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    public Optional<List<Bookmark>> findByProductPid(Long pid);
    public long countByProductPid(Long pid);

    @Query(value = "SELECT B.product FROM Bookmark B WHERE B.user.uid = ?1")
    public Optional<List<Product>> findBookmarkListOfUser(Long uid);

    @Query(value = "SELECT B.user FROM Bookmark B WHERE B.product.pid = ?1")
    public Optional<List<User>> findBookmarkUserListOfProduct(Long pid);

    public Optional<Bookmark> findByUserUidAndProductPid(Long uid, Long pid);
}
