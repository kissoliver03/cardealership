package hu.mik.pte.prog5.carmanager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import hu.mik.pte.prog5.carmanager.model.Rental;
import hu.mik.pte.prog5.carmanager.service.RentalService;
import hu.mik.pte.prog5.carmanager.views.layout.MainLayout;

import java.time.LocalDate;

@Route(value = "rentals", layout = MainLayout.class)
public class RentalListView extends VerticalLayout implements HasDynamicTitle {

    private final RentalService rentalService;
    private final Grid<Rental> grid = new Grid<>(Rental.class, false);

    public RentalListView(RentalService rentalService) {
        this.rentalService = rentalService;

        this.setSizeFull();
        this.setPadding(false);

        H3 viewTitle = new H3(getTranslation("rental.list.title"));

        HorizontalLayout topBar = new HorizontalLayout(viewTitle);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);

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
                String.format("%,.0f Ft", rental.getRentalFee()) : "-")
                .setHeader(getTranslation("rental.fee")).setAutoWidth(true);

        this.grid.addComponentColumn(this::createStatusBadge)
                .setHeader(getTranslation("rental.status")).setAutoWidth(true);

        this.grid.addComponentColumn(this::createActions)
                .setHeader(getTranslation("rental.actions")).setAutoWidth(true);

        this.grid.setEmptyStateText(getTranslation("rental.empty"));

        this.add(topBar, this.grid);
        this.refresh();
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
        HorizontalLayout actions = new HorizontalLayout();

        if (rental.getReturnDate() == null) {
            Button returnButton = new Button(getTranslation("rental.returnButton"), e -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader(getTranslation("rental.return.dialog.header"));
                dialog.setText(getTranslation("rental.return.dialog.text", rental.getCar().getLicensePlate()));

                dialog.setCancelable(true);
                dialog.setCancelText(getTranslation("car.cancel"));
                dialog.setConfirmText(getTranslation("rental.return.dialog.confirm"));
                dialog.setConfirmButtonTheme("primary");

                dialog.addConfirmListener(ev -> {
                    this.rentalService.returnCar(rental.getCar().getCarId(), LocalDate.now());
                    this.refresh();
                });

                dialog.open();
            });
            returnButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            actions.add(returnButton);
        }

        return actions;
    }

    private void refresh() {
        this.grid.setItems(this.rentalService.listAll());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("rental.list.title") + " | Car Manager";
    }
}
