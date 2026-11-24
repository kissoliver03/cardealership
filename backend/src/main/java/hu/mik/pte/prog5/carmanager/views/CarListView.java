package hu.mik.pte.prog5.carmanager.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import hu.mik.pte.prog5.carmanager.service.CarService;
import hu.mik.pte.prog5.carmanager.views.layout.MainLayout;

@Route(value = "cars", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Autók listája | Car Manager")
public class CarListView extends VerticalLayout {

    private final CarService carService;
    private final Grid<Car> grid = new Grid<>(Car.class, false);

    public CarListView(CarService carService) {
        this.carService = carService;

        this.setSizeFull();
        this.setPadding(false);

        H3 viewTitle = new H3("Flotta kezelése (PostgreSQL adatbázisból)");

        Button newCarButton = new Button("➕ Új autó felvétele", e -> UI.getCurrent().navigate(CarFormView.class));
        newCarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout topBar = new HorizontalLayout(viewTitle, newCarButton);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);
        topBar.expand(viewTitle);

        // Oszlopok beállítása
        this.grid.addColumn(Car::getLicensePlate)
                .setHeader("Rendszám")
                .setAutoWidth(true);

        this.grid.addColumn(car -> car.getCarManufacturer() + " " + car.getCarModel())
                .setHeader("Gyártó & Modell")
                .setAutoWidth(true);

        this.grid.addColumn(Car::getCategoryName)
                .setHeader("Kategória")
                .setAutoWidth(true);

        this.grid.addColumn(car -> car.getDailyFee() != null ? car.getDailyFee() + " Ft" : "-")
                .setHeader("Napi díj")
                .setAutoWidth(true);

        this.grid.addColumn(car -> String.format("%,d km", car.getMileage()))
                .setHeader("Futásteljesítmény")
                .setAutoWidth(true);

        this.grid.addComponentColumn(this::createStatusBadge)
                .setHeader("Státusz")
                .setAutoWidth(true);

        this.grid.addComponentColumn(this::createActions)
                .setHeader("Műveletek")
                .setAutoWidth(true);

        this.grid.setEmptyStateText("Nincsenek elérhető autók az adatbázisban.");

        this.add(topBar, this.grid);
        this.refresh();
    }

    private Span createStatusBadge(Car car) {
        Span badge = new Span(car.getStatus().getDisplayName());
        badge.getElement().getThemeList().add("badge");

        if (car.getStatus() == CarStatus.AVAILABLE) {
            badge.getElement().getThemeList().add("success");
        } else if (car.getStatus() == CarStatus.RENTED) {
            badge.getElement().getThemeList().add("primary");
        } else if (car.getStatus() == CarStatus.MAINTENANCE) {
            badge.getElement().getThemeList().add("error");
        } else if (car.getStatus() == CarStatus.RESERVED) {
            badge.getElement().getThemeList().add("contrast");
        }
        return badge;
    }

    private HorizontalLayout createActions(Car car) {
        Button editButton = new Button("Szerkesztés", e -> UI.getCurrent().navigate("cars/" + car.getCarId() + "/edit"));
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button deleteButton = new Button("Törlés", e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Autó törlése");
            dialog.setText("Biztosan törölni szeretnéd a(z) " + car.getLicensePlate() + " rendszámú autót?");

            dialog.setCancelable(true);
            dialog.setCancelText("Mégse");
            dialog.setConfirmText("Törlés");
            dialog.setConfirmButtonTheme("error primary");

            dialog.addConfirmListener(ev -> {
                this.carService.deleteById(car.getCarId());
                this.refresh();
            });

            dialog.open();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        return new HorizontalLayout(editButton, deleteButton);
    }

    private void refresh() {
        this.grid.setItems(this.carService.list());
    }
}
