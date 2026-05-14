package com.horgaring.diplombackednd.admin.views;

import com.horgaring.diplombackednd.security.AdminService;
import com.horgaring.diplombackednd.user.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Route(value = "admin/users", layout = MainLayout.class)
@PageTitle("User Detail")
@RolesAllowed("ADMIN")
public class UserDetailView extends VerticalLayout implements HasUrlParameter<UUID> {

    private final AdminService adminService;
    private UUID userId;

    @Autowired
    public UserDetailView(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void setParameter(BeforeEvent event, UUID parameter) {
        this.userId = parameter;
        removeAll();
        AdminUserDto user = adminService.getUserDetails(parameter);

        add(new H3("User: " + user.getEmail()));

        VerticalLayout info = new VerticalLayout();
        info.add(new Paragraph("Name: " + user.getFirstName() + " " + user.getLastName()));
        info.add(new Paragraph("Email: " + user.getEmail()));
        info.add(new Paragraph("Role: " + user.getRole()));
        info.add(new Paragraph("Active: " + user.getActive()));
        info.add(new Paragraph("Verified: " + user.getVerified()));
        info.add(new Paragraph("Gender: " + user.getGender()));
        info.add(new Paragraph("Birth Date: " + user.getBirthDate()));
        info.add(new Paragraph("City: " + user.getCityName()));
        info.add(new Paragraph("Bio: " + user.getBio()));
        info.add(new Paragraph("Created: " + user.getCreatedAt()));

        HorizontalLayout actions = new HorizontalLayout();

        ComboBox<String> roleSelect = new ComboBox<>("Role", "USER", "ADMIN");
        roleSelect.setValue(user.getRole());
        Button changeRole = new Button("Change Role", e -> {
            UpdateRoleRequest req = new UpdateRoleRequest();
            req.setRole(Role.valueOf(roleSelect.getValue()));
            adminService.updateUserRole(userId, req);
            Notification.show("Role updated");
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        changeRole.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button toggleActive = new Button(
                user.getActive() ? "Ban" : "Unban", e -> {
            adminService.toggleUserActive(userId, !user.getActive());
            Notification.show("Status updated");
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        toggleActive.addThemeVariants(
                user.getActive() ? ButtonVariant.LUMO_ERROR : ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_SMALL);

        Button verify = new Button(
                user.getVerified() ? "Unverify" : "Verify", e -> {
            adminService.verifyUser(userId, !user.getVerified());
            Notification.show("Verification updated");
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        verify.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button delete = new Button("Delete User", e -> {
            ConfirmDialog dialog = new ConfirmDialog("Confirm",
                    "Delete this user?",
                    "Delete", d -> {
                adminService.deleteUser(userId);
                Notification.show("User deleted");
                getUI().ifPresent(ui -> ui.navigate("admin/users"));
            }, "Cancel", d -> {});
            dialog.open();
        });
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

        Button back = new Button("← Back", e ->
                getUI().ifPresent(ui -> ui.navigate("admin/users")));
        back.addThemeVariants(ButtonVariant.LUMO_SMALL);

        actions.add(roleSelect, changeRole, toggleActive, verify, delete, back);
        add(info, actions);
    }
}
