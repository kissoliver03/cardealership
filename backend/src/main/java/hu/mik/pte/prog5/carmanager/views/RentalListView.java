package hu.mik.pte.prog5.carmanager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import hu.mik.pte.prog5.carmanager.model.Rental;
import hu.mik.pte.prog5.carmanager.service.RentalService;
import hu.mik.pte.prog5.carmanager.views.layout.MainLayout;

import java.time.LocalDate;
import java.util.List;

@Route(value = "rentals", layout = MainLayout.class)
public class RentalListView extends VerticalLayout implements HasDynamicTitle {

    private final RentalService rentalService;
    private final Grid<Rental> grid = new Grid<>(Rental.class, false);

    private final TextField searchField = new TextField();
    private final Select<String> statusFilter = new Select<>();

    public RentalListView(RentalService rentalService) {
        this.rentalService = rentalService;

        this.setSizeFull();
        this.setPadding(false);

        H3 viewTitle = new H3(getTranslation("rental.list.title"));

        HorizontalLayout topBar = new HorizontalLayout(viewTitle);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);

        this.searchField.setPlaceholder(getTranslation("rental.filter.searchPlaceholder"));
        this.searchField.setClearButtonVisible(true);
        this.searchField.setValueChangeMode(ValueChangeMode.LAZY);
        this.searchField.addValueChangeListener(e -> this.applyFilters());

        this.statusFilter.setPlaceholder(getTranslation("rental.filter.allStatus"));
        this.statusFilter.setEmptySelectionAllowed(true);
        this.statusFilter.setEmptySelectionCaption(getTranslation("rental.filter.allStatus"));
        this.statusFilter.setItems("ACTIVE", "CLOSED");
        this.statusFilter.setItemLabelGenerator(s -> {
            if ("ACTIVE".equals(s)) {
                return getTranslation("rental.status.active");
            } else if ("CLOSED".equals(s)) {
                return getTranslation("rental.status.closed");
            }
            return getTranslation("rental.filter.allStatus");
        });
        this.statusFilter.setWidth("180px");
        this.statusFilter.addValueChangeListener(e -> this.applyFilters());

        Button resetButton = new Button(getTranslation("filter.reset"), e -> this.resetFilters());

        HorizontalLayout filterBar = new HorizontalLayout(this.searchField, this.statusFilter, resetButton);
        filterBar.setWidthFull();
        filterBar.setAlignItems(Alignment.CENTER);
        filterBar.expand(this.searchField);

        this.grid.addColumn(rental -> {
            String name = rental.getCustomer().getFullName();
            if (Boolean.TRUE.equals(rental.getCustomer().getIsRegularCustomer())) {
                name += " (VIP)";
            }
            return name;
        }).setHeader(getTranslation("rental.customer")).setAutoWidth(true);

        this.grid.addColumn(rental -> rental.getCar().getCarManufacturer() + " " +
                rental.getCar().getCarModel() + " (" + rental.getCar().getLicensePlate() + ")")
                .setHeader(getTranslation("rental.car")).setAutoWidth(true);

        this.grid.addColumn(Rental::getFromDate)
                .setHeader(getTranslation("rental.fromDate")).setAutoWidth(true);

        this.grid.addColumn(Rental::getToDate)
                .setHeader(getTranslation("rental.toDate")).setAutoWidth(true);

        this.grid.addColumn(rental -> rental.getReturnDate() != null ? rental.getReturnDate().toString() : "-")
                .setHeader(getTranslation("rental.returnDate")).setAutoWidth(true);

        this.grid.addColumn(rental -> rental.getRentalFee() != null ?
                String.format("%,.0f %s", rental.getRentalFee(), getTranslation("app.currency")) : "-")
                .setHeader(getTranslation("rental.fee")).setAutoWidth(true);

        this.grid.addComponentColumn(this::createStatusBadge)
                .setHeader(getTranslation("rental.status")).setAutoWidth(true);

        this.grid.addComponentColumn(this::createActions)
                .setHeader(getTranslation("rental.actions")).setAutoWidth(true);

        this.grid.setEmptyStateText(getTranslation("rental.empty"));

        this.add(topBar, filterBar, this.grid);
        this.applyFilters();
    }

    private Span createStatusBadge(Rental rental) {
        Span badge;
        if (rental.getReturnDate() == null) {
            badge = new Span(getTranslation("rental.status.active"));
            badge.getElement().getThemeList().add("badge primary");
        } else {
            badge = new Span(getTranslation("rental.status.closed"));
            badge.getElement().getThemeList().add("badge success");
        }
        return badge;
    }

    private HorizontalLayout createActions(Rental rental) {
        if (rental.getReturnDate() != null) {
            return new HorizontalLayout();
        }

        Button returnButton = new Button(getTranslation("rental.returnButton"), e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader(getTranslation("rental.return.dialog.header"));
            dialog.setText(getTranslation("rental.return.dialog.text", rental.getCar().getLicensePlate()));

            dialog.setCancelable(true);
            dialog.setCancelText(getTranslation("car.cancel"));
            dialog.setConfirmText(getTranslation("rental.return.dialog.confirm"));
            dialog.setConfirmButtonTheme("primary");

            dialog.addConfirmListener(ev -> {
                this.rentalService.returnCar(rental.getRentalId(), LocalDate.now());
                this.applyFilters();
            });

            dialog.open();
        });
        returnButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        return new HorizontalLayout(returnButton);
    }

    private void applyFilters() {
        String query = this.searchField.getValue() != null ? this.searchField.getValue().trim().toLowerCase() : "";
        String status = this.statusFilter.getValue();

        List<Rental> filtered = this.rentalService.list().stream()
                .filter(r -> query.isEmpty() ||
                        (r.getCustomer() != null && r.getCustomer().getFullName().toLowerCase().contains(query)) ||
                        (r.getCar() != null && (
                                (r.getCar().getLicensePlate() != null && r.getCar().getLicensePlate().toLowerCase().contains(query)) ||
                                (r.getCar().getCarManufacturer() != null && r.getCar().getCarManufacturer().toLowerCase().contains(query)) ||
                                (r.getCar().getCarModel() != null && r.getCar().getCarModel().toLowerCase().contains(query))
                        )))
                .filter(r -> {
                    if ("ACTIVE".equals(status)) {
                        return r.getReturnDate() == null;
                    } else if ("CLOSED".equals(status)) {
                        return r.getReturnDate() != null;
                    }
                    return true;
                })
                .toList();

        this.grid.setItems(filtered);
    }

    private void resetFilters() {
        this.searchField.clear();
        this.statusFilter.clear();
        this.applyFilters();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("rental.list.title") + " | Car Manager";
    }
}
