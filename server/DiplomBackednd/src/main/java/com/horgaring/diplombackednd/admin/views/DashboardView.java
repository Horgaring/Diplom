package com.horgaring.diplombackednd.admin.views;

import com.horgaring.diplombackednd.security.AdminService;
import com.horgaring.diplombackednd.user.AdminStatsDto;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "admin/dashboard", layout = MainLayout.class)
@PageTitle("Dashboard")
@RolesAllowed("ADMIN")
public class DashboardView extends VerticalLayout {

    @Autowired
    public DashboardView(AdminService adminService) {
        AdminStatsDto stats = adminService.getStats();

        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.add(
                statCard("Total Users", String.valueOf(stats.getTotalUsers()), "var(--lumo-primary-color)"),
                statCard("Active", String.valueOf(stats.getActiveUsers()), "var(--lumo-success-color)"),
                statCard("Verified", String.valueOf(stats.getVerifiedUsers()), "var(--lumo-primary-text-color)"),
                statCard("Banned", String.valueOf(stats.getBannedUsers()), "var(--lumo-error-color)")
        );

        HorizontalLayout cards2 = new HorizontalLayout();
        cards2.setWidthFull();
        cards2.add(
                statCard("Matches", String.valueOf(stats.getTotalMatches()), "var(--lumo-warning-color)"),
                statCard("Messages", String.valueOf(stats.getTotalMessages()), "var(--lumo-secondary-color)"),
                statCard("Today", String.valueOf(stats.getRegistrationsToday()), "var(--lumo-tertiary-color)"),
                statCard("This Month", String.valueOf(stats.getRegistrationsThisMonth()), "var(--lumo-shade-20pct)")
        );

        VerticalLayout left = new VerticalLayout(new H3("Gender"), genderGrid(stats));
        VerticalLayout right = new VerticalLayout(new H3("Top Cities"), citiesGrid(stats));
        HorizontalLayout tables = new HorizontalLayout(left, right);
        tables.setWidthFull();

        add(cards, cards2, tables);
    }

    private VerticalLayout statCard(String title, String value, String color) {
        VerticalLayout card = new VerticalLayout();
        card.getStyle().set("background", color).set("color", "white")
                .set("border-radius", "8px").set("padding", "1.5em")
                .set("flex", "1").set("text-align", "center");
        card.add(new com.vaadin.flow.component.html.Span(title));
        card.add(new com.vaadin.flow.component.html.H2(value));
        card.getStyle().set("margin", "0");
        return card;
    }

    private Grid<AdminStatsDto.GenderEntry> genderGrid(AdminStatsDto stats) {
        Grid<AdminStatsDto.GenderEntry> grid = new Grid<>(AdminStatsDto.GenderEntry.class, false);
        grid.addColumn(AdminStatsDto.GenderEntry::getKey).setHeader("Gender");
        grid.addColumn(AdminStatsDto.GenderEntry::getValue).setHeader("Count");
        grid.setItems(stats.getGenderEntries());
        grid.setWidthFull();
        return grid;
    }

    private Grid<AdminStatsDto.CityEntry> citiesGrid(AdminStatsDto stats) {
        Grid<AdminStatsDto.CityEntry> grid = new Grid<>(AdminStatsDto.CityEntry.class, false);
        grid.addColumn(AdminStatsDto.CityEntry::getKey).setHeader("City");
        grid.addColumn(AdminStatsDto.CityEntry::getValue).setHeader("Users");
        grid.setItems(stats.getCityEntries());
        grid.setWidthFull();
        return grid;
    }
}
