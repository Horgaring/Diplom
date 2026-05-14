package com.horgaring.diplombackednd.admin.views;

import com.horgaring.diplombackednd.chat.AdminMessageDto;
import com.horgaring.diplombackednd.security.AdminService;
import com.horgaring.diplombackednd.chat.Message;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Route(value = "admin/messages", layout = MainLayout.class)
@PageTitle("Messages")
@RolesAllowed("ADMIN")
public class MessagesView extends VerticalLayout {

    private final AdminService adminService;
    private final Grid<AdminMessageDto> grid = new Grid<>(AdminMessageDto.class, false);
    private int currentPage = 0;

    @Autowired
    public MessagesView(AdminService adminService) {
        this.adminService = adminService;
        setSizeFull();

        grid.addColumn(AdminMessageDto::getSenderFirstName).setHeader("Sender").setAutoWidth(true);
        grid.addColumn(AdminMessageDto::getContent).setHeader("Content").setWidth("400px");
        grid.addColumn(AdminMessageDto::getCreatedAt).setHeader("Sent").setAutoWidth(true);
        grid.addColumn(AdminMessageDto::isRead).setHeader("Read").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(msg -> {
            Button delete = new Button("Delete", e -> {
                ConfirmDialog dialog = new ConfirmDialog("Confirm",
                        "Delete this message?",
                        "Delete", d -> {
                    adminService.deleteMessage(msg.getId());
                    Notification.show("Message deleted");
                    refresh();
                }, "Cancel", d -> {});
                dialog.open();
            });
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return delete;
        })).setHeader("Actions").setAutoWidth(true);

        HorizontalLayout pagination = new HorizontalLayout();
        Button prev = new Button("← Prev", e -> { currentPage--; refresh(); });
        Button next = new Button("Next →", e -> { currentPage++; refresh(); });
        pagination.add(prev, next);

        add(new H3("Messages"), grid, pagination);
        refresh();
    }

    private void refresh() {
        Page<Message> page = adminService.getAllMessages(currentPage, 20);
        grid.setItems(page.map(adminService::toMessageDto).toList());
    }
}
