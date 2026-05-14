package com.horgaring.diplombackednd.admin.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
public class MainLayout extends AppLayout {

    public MainLayout() {
        H2 title = new H2("Admin Panel");
        title.getStyle().set("font-size", "1.3em").set("margin", "0");

        Button logout = new Button("Logout", event ->
                getUI().ifPresent(ui -> ui.getPage().setLocation("/admin/logout")));

        addToNavbar(new DrawerToggle(), title, logout);

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", "admin/dashboard", VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Users", "admin/users", VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Messages", "admin/messages", VaadinIcon.CHAT.create()));
        nav.addItem(new SideNavItem("Cities", "admin/cities", VaadinIcon.BUILDING.create()));

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
    }
}
