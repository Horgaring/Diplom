package com.horgaring.diplombackednd.admin.views;

import com.horgaring.diplombackednd.security.AdminService;
import com.horgaring.diplombackednd.user.AdminUserDto;
import com.horgaring.diplombackednd.user.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Route(value = "admin/users", layout = MainLayout.class)
@PageTitle("Users")
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {

    private final AdminService adminService;
    private final Grid<AdminUserDto> grid = new Grid<>(AdminUserDto.class, false);
    private final TextField searchField = new TextField("Search");
    private final Select<String> verifiedFilter = new Select<>();
    private int currentPage = 0;

    @Autowired
    public UsersView(AdminService adminService) {
        this.adminService = adminService;
        setSizeFull();

        verifiedFilter.setLabel("Verified");
        verifiedFilter.setItems("All", "Verified", "Not Verified");
        verifiedFilter.setValue("All");

        Button searchBtn = new Button("Search", e -> { currentPage = 0; refresh(); });
        HorizontalLayout filters = new HorizontalLayout(searchField, verifiedFilter, searchBtn);
        filters.setAlignItems(Alignment.END);

        grid.addColumn(AdminUserDto::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(AdminUserDto::getFirstName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(AdminUserDto::getRole).setHeader("Role").setAutoWidth(true);
        grid.addColumn(AdminUserDto::getActive).setHeader("Active").setAutoWidth(true);
        grid.addColumn(AdminUserDto::getVerified).setHeader("Verified").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout btns = new HorizontalLayout();
            Button view = new Button("View", e ->
                    getUI().ifPresent(ui -> ui.navigate("admin/users/" + user.getId())));
            view.addThemeVariants(ButtonVariant.LUMO_SMALL);
            btns.add(view);
            return btns;
        })).setHeader("Actions").setAutoWidth(true);

        HorizontalLayout pagination = new HorizontalLayout();
        Button prev = new Button("← Prev", e -> { currentPage--; refresh(); });
        Button next = new Button("Next →", e -> { currentPage++; refresh(); });
        pagination.add(prev, next);

        add(new H3("Users"), filters, grid, pagination);
        refresh();
    }

    private void refresh() {
        String search = searchField.getValue();
        Boolean verified = switch (verifiedFilter.getValue()) {
            case "Verified" -> true;
            case "Not Verified" -> false;
            default -> null;
        };
        Page<User> page = adminService.getUsers(
                search.isEmpty() ? null : search,
                verified, currentPage, 20);
        grid.setItems(page.map(adminService::toAdminDto).toList());
    }
}
