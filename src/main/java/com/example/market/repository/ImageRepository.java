package com.example.market.repository;

import com.example.market.model.entity.Bookmark;
import com.example.market.model.entity.Image;
import com.example.market.model.entity.Product;
import com.example.market.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    public List<Image> findByProductPid(Long pid);
}
