package com.example.market.model.network.request;

import com.example.market.model.enumclass.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductApiRequest {

    private Long pid;

    private String title;

    private String content;

    private int price;

    private ProductStatus productStatus;

    private LocalDateTime uploadDate;

    private boolean isDeleted;

    private LocalDateTime reUpDate;

    private Long userUid;

    private Long bookmarkCount;

    private List<MultipartFile> imageList;

}
