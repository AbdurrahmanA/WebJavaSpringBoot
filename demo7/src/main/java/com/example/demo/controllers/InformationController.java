package com.example.demo.controllers;

import com.example.demo.data.CategoryEntity;
import com.example.demo.data.InformationEntity;
import com.example.demo.data.UserEntity;
import com.example.demo.repositories.InformationEntityRepository;
import com.example.demo.services.CategoryService;
import com.example.demo.services.InformationService;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/information")
public class InformationController {

    private static final Logger logger = LoggerFactory.getLogger(InformationController.class);

    @Autowired
    private InformationService informationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private InformationEntityRepository informationEntityRepository;

    @GetMapping({"", "/"})
    public String listInformation(Model model, HttpSession session,
                                  @RequestParam(required = false) Integer categoryId,
                                  @RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(required = false) String sortField,
                                  @RequestParam(required = false) String sortDir) {

        UserEntity currentUser = (UserEntity) session.getAttribute("user");
        String username = (currentUser != null) ? currentUser.getLogin() : "anonymous";

        // ⬇ Kullanıcının tercihini uygula, eğer parametre yoksa
        if ((sortField == null || sortField.isBlank()) && currentUser != null) {
            String pref = currentUser.getSortPreference();
            if (pref != null && pref.contains(":")) {
                String[] parts = pref.split(":");
                sortField = parts[0];
                sortDir = parts.length > 1 ? parts[1] : "asc";
            } else {
                sortField = "date";
                sortDir = "asc";
            }
        }

        logger.info("User '{}' requested information list with filters - categoryId: {}, startDate: {}, endDate: {}, sortField: {}, sortDir: {}",
                username, categoryId, startDate, endDate, sortField, sortDir);

        List<CategoryEntity> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        List<InformationEntity> ownInfo;
        List<InformationEntity> otherInfo;

        if (currentUser == null) {
            ownInfo = Collections.emptyList();
            otherInfo = informationService.getAllInformationFiltered(categoryId, startDate, endDate).stream()
                    .filter(i -> i.getSharedWith() == null)
                    .toList();
        } else {
            ownInfo = informationService.getInformationByOwnerFiltered(currentUser.getId(), categoryId, startDate, endDate);
            otherInfo = informationService.getAllInformationFiltered(categoryId, startDate, endDate).stream()
                    .filter(i -> !i.getOwner().getId().equals(currentUser.getId()))
                    .filter(i -> i.getSharedWith() == null || i.getSharedWith().getId().equals(currentUser.getId()))
                    .toList();
        }

        Comparator<InformationEntity> comparator;

        if ("category".equals(sortField)) {
            comparator = Comparator.comparing(
                    i -> i.getCategory() != null ? i.getCategory().getName() : "",
                    String.CASE_INSENSITIVE_ORDER
            );
        } else {
            comparator = Comparator.comparing(
                    i -> i.getDateAdded() != null ? i.getDateAdded() : LocalDate.MIN
            );
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        ownInfo = ownInfo.stream().sorted(comparator).toList();
        otherInfo = otherInfo.stream().sorted(comparator).toList();

        model.addAttribute("myInformation", ownInfo);
        model.addAttribute("otherInformation", otherInfo);

        String sortPreference = sortField + ":" + sortDir;
        session.setAttribute("sortPreference", sortPreference);


        return "list";
    }

    @GetMapping("/add")
    public String addInformationForm(HttpSession session, Model model) {
        UserEntity currentUser = (UserEntity) session.getAttribute("user");
        if (currentUser == null ||
                !(currentUser.getRole() == UserEntity.Role.ROLE_ADMIN ||
                        currentUser.getRole() == UserEntity.Role.ROLE_FULL_USER)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("info", new InformationEntity());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("users", userService.getAllUsers());
        return "add";
    }

    @GetMapping("/shared/{id}")
    public String viewSharedInformation(@PathVariable Integer id, Model model) {
        Optional<InformationEntity> optionalInfo = informationService.getInformationById(id);
        if (optionalInfo.isPresent()) {
            model.addAttribute("info", optionalInfo.get());
            return "shared-info";
        } else {
            return "redirect:/";
        }
    }


    @PostMapping("/add")
    public String addInformation(@Valid @ModelAttribute("info") InformationEntity info,
                                 BindingResult result,
                                 HttpSession session,
                                 Model model) {
        UserEntity currentUser = (UserEntity) session.getAttribute("user");
        String username = (currentUser != null) ? currentUser.getLogin() : "anonymous";

        if (currentUser == null ||
                !(currentUser.getRole() == UserEntity.Role.ROLE_ADMIN ||
                        currentUser.getRole() == UserEntity.Role.ROLE_FULL_USER)) {
            return "redirect:/access-denied";
        }

        if (info.getCategory() != null && info.getCategory().getId() != null) {
            categoryService.getCategoryById(info.getCategory().getId()).ifPresent(info::setCategory);
        }

        if (info.getSharedWith() != null && info.getSharedWith().getId() != null) {
            userService.getUserById(info.getSharedWith().getId()).ifPresent(info::setSharedWith);
        } else {
            info.setSharedWith(null);
        }

        if (result.hasErrors()) {
            logger.warn("User '{}' tried to add information but validation failed.", username);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("users", userService.getAllUsers());
            return "add";
        }

        info.setOwner(currentUser);
        info.setDateAdded(LocalDate.now());

        List<InformationEntity> pendingCreates = (List<InformationEntity>) session.getAttribute("pendingCreates");
        if (pendingCreates == null) {
            pendingCreates = new ArrayList<>();
        }
        pendingCreates.add(info);
        session.setAttribute("pendingCreates", pendingCreates);

        logger.info("User '{}' temporarily added new information with title '{}', sharedWith: {}. It will be saved when session ends.",
                username,
                info.getTitle(),
                info.getSharedWith() == null ? "everyone" : info.getSharedWith().getLogin());

        return "redirect:/information/";
    }

    @GetMapping("/edit/{id}")
    public String editInformation(@PathVariable("id") Integer id, HttpSession session, Model model) {
        UserEntity currentUser = (UserEntity) session.getAttribute("user");
        if (currentUser == null ||
                !(currentUser.getRole() == UserEntity.Role.ROLE_ADMIN ||
                        currentUser.getRole() == UserEntity.Role.ROLE_FULL_USER)) {
            return "redirect:/access-denied";
        }

        Optional<InformationEntity> optionalInfo = informationService.getInformationById(id);
        if (optionalInfo.isPresent()) {
            model.addAttribute("info", optionalInfo.get());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("users", userService.getAllUsers());
            return "edit";
        } else {
            logger.warn("Information with id {} not found for edit.", id);
            return "redirect:/information/";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateInformation(@PathVariable("id") Integer id,
                                    @Valid @ModelAttribute("info") InformationEntity info,
                                    BindingResult result,
                                    HttpSession session,
                                    Model model) {

        UserEntity currentUser = (UserEntity) session.getAttribute("user");
        String username = (currentUser != null) ? currentUser.getLogin() : "anonymous";

        if (result.hasErrors()) {
            logger.warn("User '{}' tried to update information id {} but validation failed.", username, id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("users", userService.getAllUsers());
            return "edit";
        }

        info.setId(id);

        categoryService.getCategoryById(info.getCategory().getId())
                .ifPresent(info::setCategory);

        if (info.getSharedWith() != null && info.getSharedWith().getId() != null) {
            userService.getUserById(info.getSharedWith().getId()).ifPresent(info::setSharedWith);
        } else {
            info.setSharedWith(null);
        }

        if (currentUser == null) {
            logger.warn("Anonymous user tried to update information id {}.", id);
            return "redirect:/information/";
        }

        info.setOwner(currentUser);
        info.setDateAdded(LocalDate.now());
        informationService.createInformation(info);

        logger.info("User '{}' updated information id {}.", username, id);
        return "redirect:/information/";
    }

    @GetMapping("/delete/{id}")
    public String deleteInformation(@PathVariable("id") Integer id, HttpSession session) {
        UserEntity currentUser = (UserEntity) session.getAttribute("user");
        if (currentUser == null ||
                !(currentUser.getRole() == UserEntity.Role.ROLE_ADMIN ||
                        currentUser.getRole() == UserEntity.Role.ROLE_FULL_USER)) {
            return "redirect:/access-denied";
        }

        Optional<InformationEntity> optionalInfo = informationService.getInformationById(id);
        if (optionalInfo.isPresent()) {
            informationService.deleteInformation(id);
            logger.info("User '{}' deleted information id {} with title '{}'.", currentUser.getLogin(), id, optionalInfo.get().getTitle());
        } else {
            logger.warn("Information with id {} not found for delete.", id);
        }
        return "redirect:/information/";
    }
    @Controller
    public class ErrorViewController {

        @GetMapping("/access-denied")
        public String accessDenied() {
            return "access-denied";
        }
    }
}
