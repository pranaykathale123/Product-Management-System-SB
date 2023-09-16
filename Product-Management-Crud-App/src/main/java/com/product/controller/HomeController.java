package com.product.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.product.entity.Products;
import com.product.entity.User;
import com.product.repository.ProductRepository;
import com.product.repository.userRepository;

@Controller
public class HomeController {

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private userRepository userRepo;
	
	
	@GetMapping("/uploadfile")
	public String viewUpload() {
		return "upload";
	}

	@GetMapping("/viewindex")
	public String viewIndexpage(Model m, Authentication authentication) {
		boolean isAdmin = false;
		if (authentication != null) {
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				if (authority.getAuthority().equals("ROLE_ADMIN")) {
					isAdmin = true;
					break;
				}
			}
		}

		m.addAttribute("isAdmin", isAdmin);
		return findPaginateAndSorting(0, "id", "asc", m);
	}

	@GetMapping("/")
	public String redirectToLogin() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("login")
	public String login(@RequestParam String username, @RequestParam String password, Model model) {
		User user = userRepo.findByUsername(username);

		if (user != null && user.getPassword().equals(password)) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				if ("ROLE_ADMIN".equals(authority.getAuthority())) {
					return findPaginateAndSorting(0, "id", "asc", model);
				}
			}
			return "redirect:/user/product-list";
		} else {
			model.addAttribute("error", "Invalid username or password");
			return "redirect:/login";
		}

	}

	@GetMapping("/page/{pageNo}")
	public String findPaginateAndSorting(@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model m) {

		System.out.println();
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo, 3, sort);

		Page<Products> page = productRepo.findAll(pageable);

		List<Products> list = page.getContent();

		m.addAttribute("pageNo", pageNo);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPage", page.getTotalPages());
		m.addAttribute("all_products", list);

		m.addAttribute("sortField", sortField);
		m.addAttribute("sortDir", sortDir);
		m.addAttribute("revSortDir", sortDir.equals("asc") ? "desc" : "asc");

		return "index";
	}

	@GetMapping("/product-list")
	public String getProductList(Model model) {
		Iterable<Products> products = productRepo.findAll();
		model.addAttribute("products", products);
		return "product_list";
	}

	@GetMapping("/load_form")
	public String loadForm() {
		return "add";
	}

	@GetMapping("/edit_form/{id}")
	public String editForm(@PathVariable(value = "id") long id, Model m) {

		Optional<Products> product = productRepo.findById(id);

		Products pro = product.get();
		m.addAttribute("product", pro);

		return "edit";
	}

	@PostMapping("/save_products")
	public String saveProducts(@ModelAttribute Products products, HttpSession session) {

		productRepo.save(products);
		session.setAttribute("msg", "Product Added Sucessfully..");

		return "redirect:/load_form";
	}

	@PostMapping("/update_products")
	public String updateProducts(@ModelAttribute Products products, HttpSession session) {

		productRepo.save(products);
		session.setAttribute("msg", "Product Update Sucessfully..");

		return "redirect:/viewindex";
	}

	@GetMapping("/delete/{id}")
	public String deleteProducts(@PathVariable(value = "id") long id, HttpSession session) {
		productRepo.deleteById(id);
		session.setAttribute("msg", "Product Delete Sucessfully..");

		return "redirect:/viewindex";

	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

}
