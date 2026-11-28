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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import hu.mik.pte.prog5.carmanager.model.Category;
import hu.mik.pte.prog5.carmanager.service.CarService;
import hu.mik.pte.prog5.carmanager.service.CategoryService;
import hu.mik.pte.prog5.carmanager.views.layout.MainLayout;

import java.util.List;

@Route(value = "cars", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class CarListView extends VerticalLayout implements HasDynamicTitle {

    private final CarService carService;
    private final CategoryService categoryService;

    private final Grid<Car> grid = new Grid<>(Car.class, false);

    private final TextField searchField = new TextField();
    private final Select<Category> categoryFilter = new Select<>();
    private final Select<CarStatus> statusFilter = new Select<>();

    public CarListView(CarService carService, CategoryService categoryService) {
        this.carService = carService;
        this.categoryService = categoryService;

        this.setSizeFull();
        this.setPadding(false);

        H3 viewTitle = new H3(getTranslation("car.list.title"));

        Button newCarButton = new Button(getTranslation("car.list.add"), e -> UI.getCurrent().navigate(CarFormView.class));
        newCarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout topBar = new HorizontalLayout(viewTitle, newCarButton);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);
        topBar.expand(viewTitle);

        this.searchField.setPlaceholder(getTranslation("car.filter.searchPlaceholder"));
        this.searchField.setClearButtonVisible(true);
        this.searchField.setValueChangeMode(ValueChangeMode.LAZY);
        this.searchField.addValueChangeListener(e -> this.applyFilters());

        this.categoryFilter.setPlaceholder(getTranslation("car.filter.allCategories"));
        this.categoryFilter.setEmptySelectionAllowed(true);
        this.categoryFilter.setEmptySelectionCaption(getTranslation("car.filter.allCategories"));
        this.categoryFilter.setItems(this.categoryService.list());
        this.categoryFilter.setItemLabelGenerator(cat -> cat != null ? this.formatCategoryName(cat.getCategoryName()) : getTranslation("car.filter.allCategories"));
        this.categoryFilter.setWidth("180px");
        this.categoryFilter.addValueChangeListener(e -> this.applyFilters());

        this.statusFilter.setPlaceholder(getTranslation("car.filter.allStatus"));
        this.statusFilter.setEmptySelectionAllowed(true);
        this.statusFilter.setEmptySelectionCaption(getTranslation("car.filter.allStatus"));
        this.statusFilter.setItems(CarStatus.values());
        this.statusFilter.setItemLabelGenerator(status -> status != null ? getTranslation("status." + status.name()) : getTranslation("car.filter.allStatus"));
        this.statusFilter.setWidth("180px");
        this.statusFilter.addValueChangeListener(e -> this.applyFilters());

        Button resetButton = new Button(getTranslation("filter.reset"), e -> this.resetFilters());

        HorizontalLayout filterBar = new HorizontalLayout(this.searchField, this.categoryFilter, this.statusFilter, resetButton);
        filterBar.setWidthFull();
        filterBar.setAlignItems(Alignment.CENTER);
        filterBar.expand(this.searchField);

        this.grid.addColumn(Car::getLicensePlate)
                .setHeader(getTranslation("car.licensePlate"))
                .setAutoWidth(true);

        this.grid.addColumn(car -> car.getCarManufacturer() + " " + car.getCarModel())
                .setHeader(getTranslation("car.makeModel"))
                .setAutoWidth(true);

        this.grid.addColumn(car -> this.formatCategoryName(car.getCategoryName()))
                .setHeader(getTranslation("car.category"))
                .setAutoWidth(true);

        this.grid.addColumn(car -> car.getDailyFee() != null ? car.getDailyFee() + " " + getTranslation("app.currency") : "-")
                .setHeader(getTranslation("car.dailyFee"))
                .setAutoWidth(true);

        this.grid.addColumn(car -> String.format("%,d km", car.getMileage()))
                .setHeader(getTranslation("car.mileage"))
                .setAutoWidth(true);

        this.grid.addComponentColumn(this::createStatusBadge)
                .setHeader(getTranslation("car.status"))
                .setAutoWidth(true);

        this.grid.addComponentColumn(this::createActions)
                .setHeader(getTranslation("car.actions"))
                .setAutoWidth(true);

        this.grid.setEmptyStateText(getTranslation("car.list.empty"));

        this.add(topBar, filterBar, this.grid);
        this.applyFilters();
    }

    private Span createStatusBadge(Car car) {
        Span badge = new Span(getTranslation("status." + car.getStatus().name()));
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
        Button editButton = new Button(getTranslation("car.edit"), e -> UI.getCurrent().navigate("cars/" + car.getCarId() + "/edit"));
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button deleteButton = new Button(getTranslation("car.delete"), e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader(getTranslation("car.delete.header"));
            dialog.setText(getTranslation("car.delete.confirm", car.getLicensePlate()));

            dialog.setCancelable(true);
            dialog.setCancelText(getTranslation("car.cancel"));
            dialog.setConfirmText(getTranslation("car.delete"));
            dialog.setConfirmButtonTheme("error primary");

            dialog.addConfirmListener(ev -> {
                this.carService.deleteById(car.getCarId());
                this.applyFilters();
            });

            dialog.open();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        return new HorizontalLayout(editButton, deleteButton);
    }

    private void applyFilters() {
        String query = this.searchField.getValue() != null ? this.searchField.getValue().trim().toLowerCase() : "";
        Category selectedCategory = this.categoryFilter.getValue();
        CarStatus selectedStatus = this.statusFilter.getValue();

        List<Car> filtered = this.carService.list().stream()
                .filter(car -> query.isEmpty() ||
                        (car.getLicensePlate() != null && car.getLicensePlate().toLowerCase().contains(query)) ||
                        (car.getCarManufacturer() != null && car.getCarManufacturer().toLowerCase().contains(query)) ||
                        (car.getCarModel() != null && car.getCarModel().toLowerCase().contains(query)))
                .filter(car -> selectedCategory == null ||
                        (car.getCategory() != null && selectedCategory.getCategoryId().equals(car.getCategory().getCategoryId())))
                .filter(car -> selectedStatus == null || car.getStatus() == selectedStatus)
                .toList();

        this.grid.setItems(filtered);
    }

    private void resetFilters() {
        this.searchField.clear();
        this.categoryFilter.clear();
        this.statusFilter.clear();
        this.applyFilters();
    }

    private String formatCategoryName(String categoryName) {
        if (categoryName == null) {
            return "-";
        }
        String key = "category.type." + categoryName;
        String translation = getTranslation(key);
        return translation.startsWith("!") ? categoryName : translation;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("car.list.title") + " | Car Manager";
    }
}
