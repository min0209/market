package com.example.market.repository;

import com.example.market.model.entity.Product;
import com.example.market.model.enumclass.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    public List<Product> findByTitleContainingOrderByReUpDateDesc(String title);

    public Optional<Product> findByPid(Long pid);

    public List<Product> findAllByOrderByReUpDateDesc();

    public List<Product> findAllByUserAreaOrderByReUpDateDesc(Area area);

    public List<Product> findAllByUserUsernameOrderByReUpDateDesc(String username);
}
