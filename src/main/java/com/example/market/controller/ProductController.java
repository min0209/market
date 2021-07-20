package com.example.market.controller;

import com.example.market.exception.NoPermissionException;
import com.example.market.exception.ProductNotFoundException;
import com.example.market.exception.UserNotFoundException;
import com.example.market.model.enumclass.ProductStatus;
import com.example.market.model.network.Header;
import com.example.market.model.network.request.ProductApiRequest;
import com.example.market.model.network.response.ProductApiResponse;
import com.example.market.service.ProductService;
import com.example.market.service.UserService;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String getCreate(Model model){
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser().getData());
        }
        return "product_form";
    }

    @PostMapping("/create")
    public RedirectView create(ProductApiRequest productApiRequest){
        Header header = productService.create(productApiRequest);
        return new RedirectView("/product/");
    }

    @GetMapping("/{getPid}")
    public String read(Model model, @PathVariable String getPid){

        Long pid;

        if(StringUtils.isStrictlyNumeric(getPid)){
            pid = Long.parseLong(getPid);
        }else {
            return "error";
        }

        Header header = productService.read(pid);
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser().getData());
        }
        if(header.getResultCode().equals("OK")){
            ProductApiResponse response = (ProductApiResponse) header.getData();
            if(userService.isLoggedIn().getData()){
                response.setBookmarked(productService.isBookmarked(response.getPid()).getData());
            }
            model.addAttribute("product",response);
            model.addAttribute("author",productService.getAuthor(pid).getData());
        }else {
            model.addAttribute("error_message",header.getDescription());
        }
        return "product_detail";

    }
    @GetMapping("/{pid}/update")
    public String update(Model model, @PathVariable Long pid){
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser());
        }
        Header header = productService.read(pid);
        if(header.getResultCode().equals("OK")){
            ProductApiResponse getProduct = (ProductApiResponse) header.getData();
            ProductApiResponse response = ProductApiResponse.builder()
                    .pid(pid)
                    .title(getProduct.getTitle())
                    .content(getProduct.getContent())
                    .price(getProduct.getPrice())
                    .productStatus(getProduct.getProductStatus())
                    .imageList(getProduct.getImageList())
                    .build();

            model.addAttribute("product",response);
        }else {
            model.addAttribute("error_message",header.getDescription());
        }
        return "product_update";
    }

    @PostMapping("/{pid}/update")
    public String update(Model model,ProductApiRequest productApiRequest, @PathVariable Long pid){
        Header header = productService.update(productApiRequest, pid);
        if(header.getResultCode().equals("OK")){
            return "redirect:/product/"+pid;
        }else {
            model.addAttribute("error_message",header.getDescription());
            return "/product/"+pid+"/update";
        }
    }

    @DeleteMapping("/{pid}")
    @ResponseBody
    public Boolean delete(HttpServletRequest request){
        Header header = productService.delete(Long.parseLong(request.getParameter("pid")));
        if(header.getResultCode().equals("OK")){
            return true;
        }else return false;
    }

    @GetMapping("/{pid}/reUp")
    @ResponseBody
    public Boolean reUp(HttpServletRequest request,@PathVariable Long pid){

//        Header header = productService.reUp(Long.parseLong(request.getParameter("pid")));
        Header header = productService.reUp(pid);
        if(header.getResultCode().equals("OK")){
            return true;
        }else {
            return false;
        }
    }

    @PatchMapping("/{pid}/update")
    public Header status(@PathVariable Long pid, @RequestBody ProductStatus status){
        return productService.changeStatus(pid, status);
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(value = "query", required = false) String title,
                            @RequestParam(value="page",required = false,defaultValue= "1")int page,
                         @RequestParam(required = false,defaultValue= "8")int pageNum){
        Header header = productService.searchTitle(title);
        List<ProductApiResponse> productList = (List<ProductApiResponse>) header.getData();
        if(header.getResultCode().equals("OK")){
            model.addAttribute("products",productService.subListProduct(productList,page,pageNum).getData());
            model.addAttribute("pageCount",productService.countPage(productList.size(),pageNum).getData());
            model.addAttribute("page",page);
        }else {
            model.addAttribute("error_message",header.getDescription());
        }
        model.addAttribute("query",title);
        return "product_list";
    }

    @GetMapping
    public String getAll(Model model, @RequestParam(value="page",required = false,defaultValue= "1")int page,
                         @RequestParam(required = false,defaultValue= "8")int pageNum){
        Header header = productService.getAll();
        List<ProductApiResponse> productList = (List<ProductApiResponse>) header.getData();
        if(header.getResultCode().equals("OK")){
            model.addAttribute("page",page);
            model.addAttribute("products",productService.subListProduct(productList,page,pageNum).getData());
            model.addAttribute("pageCount",productService.countPage(productList.size(),pageNum).getData());
        }else {
            model.addAttribute("error_message",header.getDescription());
        }
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser().getData());
        }
        return "product_list";
    }

    @PostMapping("/deleteImage")
    @ResponseBody
    public Boolean deleteImage(HttpServletRequest request){
        Header header = productService.deleteImage(Long.parseLong(request.getParameter("pid")));
        System.out.println(header);
        if(header.getResultCode().equals("ERROR")){
            return false;
        }
        return true;
    }

    @GetMapping("/{pid}/bookmark")
    @ResponseBody
    public Header bookmark(@PathVariable long pid){
        ProductApiResponse response = new ProductApiResponse();
        if(!userService.isLoggedIn().getData()){
            return Header.ERROR("no permission");
        }
        Header header = productService.bookmark(pid);
        if(header.getResultCode().equals("OK")){
            response.setBookmarkCount(productService.bookmarkCount(pid).getData());
            response.setBookmarked(productService.isBookmarked(pid).getData());

            return Header.OK(response);
        }else {
            return Header.ERROR("error");
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UserNotFoundException.class,
            ProductNotFoundException.class, NoPermissionException.class})
    public Header exceptionHandler(Exception ex) {
        return Header.ERROR(ex.getMessage());
    }
}
