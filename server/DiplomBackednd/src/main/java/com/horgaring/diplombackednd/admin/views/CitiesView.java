package com.horgaring.diplombackednd.admin.views;

import com.horgaring.diplombackednd.security.AdminService;
import com.horgaring.diplombackednd.user.CityDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Route(value = "admin/cities", layout = MainLayout.class)
@PageTitle("Cities")
@RolesAllowed("ADMIN")
public class CitiesView extends VerticalLayout {

    private final AdminService adminService;
    private final Grid<CityDto> grid = new Grid<>(CityDto.class, false);

    @Autowired
    public CitiesView(AdminService adminService) {
        this.adminService = adminService;
        setSizeFull();

        Button addBtn = new Button("+ Add City", e -> openEditDialog(null));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        grid.addColumn(CityDto::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(city -> {
            HorizontalLayout btns = new HorizontalLayout();
            Button edit = new Button("Edit", e -> openEditDialog(city));
            edit.addThemeVariants(ButtonVariant.LUMO_SMALL);
            Button delete = new Button("Delete", e -> {
                ConfirmDialog dialog = new ConfirmDialog("Confirm",
                        "Delete city '" + city.getName() + "'?",
                        "Delete", d -> {
                    adminService.deleteCity(city.getId());
                    Notification.show("City deleted");
                    refresh();
                }, "Cancel", d -> {});
                dialog.open();
            });
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            btns.add(edit, delete);
            return btns;
        })).setHeader("Actions").setAutoWidth(true);

        add(new H3("Cities"), addBtn, grid);
        refresh();
    }

    private void openEditDialog(CityDto existing) {
        Dialog dialog = new Dialog();
        TextField nameField = new TextField("City Name");
        nameField.setValue(existing != null ? existing.getName() : "");

        Button save = new Button("Save", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Name is required");
                return;
            }
            CityDto dto = new CityDto(null, nameField.getValue());
            if (existing != null) {
                adminService.updateCity(existing.getId(), dto);
            } else {
                adminService.createCity(dto);
            }
            Notification.show("City saved");
            dialog.close();
            refresh();
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(new VerticalLayout(nameField, new HorizontalLayout(save, cancel)));
        dialog.open();
    }

    private void refresh() {
        grid.setItems(adminService.getAllCities());
    }
}
