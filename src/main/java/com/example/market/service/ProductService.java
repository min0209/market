package com.example.market.service;

import com.example.market.exception.BadRequestException;
import com.example.market.exception.NoPermissionException;
import com.example.market.exception.ProductNotFoundException;
import com.example.market.exception.UserNotFoundException;
import com.example.market.model.entity.Bookmark;
import com.example.market.model.entity.Image;
import com.example.market.model.entity.User;
import com.example.market.model.enumclass.Auth;
import com.example.market.model.enumclass.ProductStatus;
import com.example.market.model.network.Header;
import com.example.market.model.network.request.ProductApiRequest;
import com.example.market.model.network.response.ProductApiResponse;
import com.example.market.model.entity.Product;
import com.example.market.model.network.response.UserApiResponse;
import com.example.market.repository.BookmarkRepository;
import com.example.market.repository.ImageRepository;
import com.example.market.repository.ProductRepository;
import com.example.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import java.util.Comparator;

@Validated
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserService userService;
    final String PATH = "D:/study/projects/spring_project/market-spring/" +
            "market/src/main/resources/static";

    public Header<ProductApiResponse> create(ProductApiRequest productApiRequest){
        Product product = Product.builder()
                .title(productApiRequest.getTitle())
                .content(productApiRequest.getContent())
                .price(productApiRequest.getPrice())
                .user(userRepository.findByUsername(
                    Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                    .orElseThrow(NoPermissionException::new).getName()
                    ).orElseThrow(UserNotFoundException::new)
                )
                .productStatus(ProductStatus.sale)
                .isDeleted(false)
                .uploadDate(LocalDateTime.now())
                .reUpDate(LocalDateTime.now())
                .build();
        Product newProduct = productRepository.save(product);
        if(productApiRequest.getImageList().get(0).getSize()!=0){
            createImage(productApiRequest,newProduct);
        }
        return Header.OK(response(product));
    }
    public Header<ProductApiResponse> read(Long pid){
        return Header.OK(response(productRepository.findByPid(pid)
                .orElseThrow(ProductNotFoundException::new)));
    }
    public Header<ProductApiResponse> update(ProductApiRequest productApiRequest, Long pid){
        User loggedInUser = userRepository.findByUsername(
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(NoPermissionException::new).getName()
                ).orElseThrow(UserNotFoundException::new);

        User owner = productRepository.findById(pid)
                .orElseThrow(ProductNotFoundException::new).getUser();
        if(loggedInUser.getAuth().equals(Auth.user)
                && !owner.equals(loggedInUser))  throw new NoPermissionException();

        Product updateProduct = productRepository.findById(pid)
            .map(product -> {
                product.setTitle(productApiRequest.getTitle());
                product.setContent(productApiRequest.getContent());
                product.setPrice(productApiRequest.getPrice());
                product.setProductStatus(productApiRequest.getProductStatus());

                return product;
            })
            .orElseThrow(ProductNotFoundException::new);
        createImage(productApiRequest,updateProduct);
        System.out.println(imageRepository.findByProductPid(136L));
        return Header.OK(response(updateProduct));
    }
    public Header delete(Long pid){
        User loggedInUser = userRepository.findByUsername(
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(NoPermissionException::new).getName()
                ).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findById(pid)
                .orElseThrow(ProductNotFoundException::new);
        if(loggedInUser.getAuth().equals(Auth.user)
                && !loggedInUser.equals(product.getUser())) throw new NoPermissionException();
        product.setDeleted(true);
        productRepository.save(product);
        return Header.OK();
    }

    public Header reUp(Long pid){
        User loggedInUser = userRepository.findByUsername(Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(NoPermissionException::new).getName())
                .orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findById(pid)
                .orElseThrow(ProductNotFoundException::new);
        if(loggedInUser.getAuth().equals(Auth.user) && !loggedInUser.equals(product.getUser())){
            throw new NoPermissionException();
        }
        product.setReUpDate(LocalDateTime.now());
        productRepository.save(product);
        return Header.OK();
    }

    public Header changeStatus(Long pid, ProductStatus status){
        User loggedInUser = userRepository.findByUsername(
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .orElseThrow(NoPermissionException::new).getName()
        ).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findById(pid)
                .orElseThrow(ProductNotFoundException::new);
        if(loggedInUser.getAuth().equals(Auth.user)
                && !loggedInUser.equals(product.getUser()))throw new NoPermissionException();
        product.setProductStatus(status);
        productRepository.save(product);
        return Header.OK();
    }

    public Header<ArrayList<ProductApiResponse>> getAll(){
        ArrayList<ProductApiResponse> response = response(productRepository.findAllByOrderByReUpDateDesc());
        if(userService.isLoggedIn().getData()){
            for (ProductApiResponse p: response) {
                if(isBookmarked(p.getPid()).getData()){
                    p.setBookmarked(true);
                }else p.setBookmarked(false);
            }
        }
        return Header.OK(response);
    }

    public Header<UserApiResponse> getAuthor(Long pid){
        Product product = productRepository.findById(pid).orElseThrow(ProductNotFoundException::new);
        User user = userRepository.findById(product.getUser().getUid()).orElseThrow(UserNotFoundException::new);
        return Header.OK(new UserApiResponse(user.getUsername(),user.getNickname(),user.getArea()));
    }

    //search title
    public Header<ArrayList<ProductApiResponse>> searchTitle (String title){
        HashSet<Product> products = new HashSet<Product>();
        if(title != null && !title.isEmpty()){
            for (String keyword: title.split(" ")) {
                products.addAll(productRepository.findByTitleContainingOrderByReUpDateDesc(keyword));
            }
        }
        ArrayList<Product> productList = new ArrayList<>(products);
        Collections.sort(productList,new Ascending());
        return Header.OK(response(productList));
    }

    public Header deleteImage(Long pid){
        Product newProduct = productRepository.findByPid(pid).orElseThrow(ProductNotFoundException::new);
        if(newProduct.getImageList() != null &&
                newProduct.getImageList().size()!=0) {
            try {
                for(Image i: newProduct.getImageList()){
                    if(!new File(PATH+i.getPath()).delete()){
                        throw new RuntimeException();
                    }
                    imageRepository.delete(i);
                }
            }catch (Exception e){
                return Header.ERROR(e.getMessage());
            }
        }
        return Header.OK();
    }

    public Header<Boolean> isBookmarked(Long pid){
        Product product = productRepository.findById(pid).orElseThrow(ProductNotFoundException::new);
        User user = userRepository.findByNickname(userService.loggedInUser().getData())
                .orElseThrow(UserNotFoundException::new);
        Boolean result = bookmarkRepository.findByUserUidAndProductPid(user.getUid(),pid)
                .map(bookmark -> true).orElseGet(()->false);
        return Header.OK(result);
    }

    public Header bookmark(Long pid){
        Product product = productRepository.findById(pid).orElseThrow(ProductNotFoundException::new);
        if(!userService.isLoggedIn().getData()){
            return Header.ERROR("No permission");
        }
        User user = userRepository.findByNickname(userService.loggedInUser().getData())
                .orElseThrow(UserNotFoundException::new);
        System.out.println(user.getUid());
        System.out.println(pid);
        System.out.println(bookmarkRepository.findByUserUidAndProductPid(user.getUid(),pid));
        bookmarkRepository.findByUserUidAndProductPid(user.getUid(),pid).map(bookmark -> {
            bookmarkRepository.delete(bookmark);
            return bookmark;
        }).orElseGet(() -> bookmarkRepository.save(Bookmark.builder().product(product).user(user).build()));
        return Header.OK();
    }

    public Header<Long> bookmarkCount(Long pid){
        return Header.OK(bookmarkRepository.countByProductPid(pid));
    }

    public Header<List<ProductApiResponse>> getProductByUser(String username){
        return Header.OK(response(productRepository.findAllByUserUsernameOrderByReUpDateDesc(username)));
    }
    public Header<Integer> countPage(int productListSize, int pageNum){
        return Header.OK((productListSize+pageNum-1)/pageNum);
    }
    public Header<List<ProductApiResponse>> subListProduct(List<ProductApiResponse> productList, int page, int pageNum){
        if(page>(productList.size()+pageNum-1)/pageNum){
            page = (productList.size()+pageNum-1)/pageNum;
        }
        if(page < 1){
            page = 1;
        }
        int start =(page-1)*pageNum;
        int end = page*pageNum;
        if(end > productList.size()){
            end= productList.size();
        }
        return Header.OK(productList.subList(start,end));
    }

    private ProductApiResponse response(Product product){
        return ProductApiResponse.builder()
                .pid(product.getPid())
                .title(product.getTitle())
                .content(product.getContent())
                .price(product.getPrice())
                .productStatus(product.getProductStatus())
                .uploadDate(product.getUploadDate())
                .reUpDate(product.getReUpDate())
                .bookmarkCount(bookmarkRepository.countByProductPid(product.getPid()))
                .userUid(product.getUser().getUid())
                .imageList(imageRepository.findByProductPid(product.getPid()))
                .userArea(userRepository.findById(product.getUser().getUid())
                        .map(user -> user.getArea()).orElseThrow(UserNotFoundException::new))
                .build();
    }
    private ArrayList<ProductApiResponse> response(List<Product> productList){
        ArrayList<ProductApiResponse> productApiResponseList = new ArrayList<ProductApiResponse>();
        productList.forEach(product -> productApiResponseList.add(response(product)));
        return productApiResponseList;
    }

    private void createImage(ProductApiRequest productApiRequest, Product newProduct){
        if(productApiRequest.getImageList()==null ||
                productApiRequest.getImageList().isEmpty()) return;
        List<MultipartFile> imageList = productApiRequest.getImageList();
        for(int i =0;i<imageList.size();i++){

            String year = new SimpleDateFormat("yy").format(new Date());
            String month = new SimpleDateFormat("MM").format(new Date());
            File folder = new File(PATH+"/img/product/"+year+"/"+month+"/");
            if (!folder.exists()) {
                try {
                    folder.mkdirs();
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
            String extension = null;
            if(imageList.get(i).getContentType().contains("image/jpeg")||
                    imageList.get(i).getContentType().contains("image/jpg")){
                extension=".jpg";
            }else if(imageList.get(i).getContentType().contains("image/png")){
                extension=".png";
            }else throw new BadRequestException();
            List<Image> images = imageRepository.findAll();
            Image image = imageRepository.save(Image.builder().path(extension).product(newProduct).build());
            image.setPath("/img/product/"+year+"/"+month+"/"+image.getFid()+image.getPath());
            System.out.println(image);
            imageRepository.save(image);
            try {
                imageList.get(i).transferTo(new File(PATH+image.getPath()));
            } catch (Exception e) {
                System.out.println(e);
                throw new RuntimeException();
            }
        }
    }
}
class Ascending implements Comparator<Product>{
    @Override
    public int compare(Product o1, Product o2) {
        return o2.getReUpDate().compareTo(o1.getReUpDate());
    }
}
