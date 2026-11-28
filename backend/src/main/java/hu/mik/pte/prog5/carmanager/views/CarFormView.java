package hu.mik.pte.prog5.carmanager.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import hu.mik.pte.prog5.carmanager.model.Category;
import hu.mik.pte.prog5.carmanager.repository.CategoryRepository;
import hu.mik.pte.prog5.carmanager.service.CarService;
import hu.mik.pte.prog5.carmanager.views.layout.MainLayout;

import java.util.List;

@Route(value = "cars/new", layout = MainLayout.class)
@RouteAlias(value = "cars/:id/edit", layout = MainLayout.class)
@PageTitle("Autó szerkesztése | Car Manager")
public class CarFormView extends VerticalLayout implements BeforeEnterObserver {

    private final CarService carService;
    private final CategoryRepository categoryRepository;

    private final BeanValidationBinder<Car> binder = new BeanValidationBinder<>(Car.class);
    private Car car;

    private final H3 title = new H3("Autó adatainak megadása");
    private final TextField licensePlate = new TextField("Rendszám (pl. ABC-123)");
    private final TextField carManufacturer = new TextField("Gyártó (pl. Toyota)");
    private final TextField carModel = new TextField("Modell (pl. Corolla)");
    private final ComboBox<Category> categoryBox = new ComboBox<>("Kategória");
    private final IntegerField mileage = new IntegerField("Futásteljesítmény (km)");
    private final Select<CarStatus> statusSelect = new Select<>();

    public CarFormView(CarService carService, CategoryRepository categoryRepository) {
        this.carService = carService;
        this.categoryRepository = categoryRepository;

        this.setMaxWidth("600px");
        this.getStyle().set("margin", "0 auto");

        // Kategóriák betöltése a legördülőbe
        List<Category> categories = categoryRepository.findAll();
        categoryBox.setItems(categories);
        categoryBox.setItemLabelGenerator(cat -> cat.getCategoryName() + " (" + cat.getDailyFee() + " Ft/nap)");

        // Státusz legördülő
        statusSelect.setLabel("Státusz");
        statusSelect.setItems(CarStatus.values());
        statusSelect.setItemLabelGenerator(CarStatus::getDisplayName);
        statusSelect.setValue(CarStatus.AVAILABLE);

        // Kétirányú adatkapcsolat (Binder)
        binder.forField(licensePlate).bind("licensePlate");
        binder.forField(carManufacturer).bind("carManufacturer");
        binder.forField(carModel).bind("carModel");
        binder.forField(mileage).bind("mileage");
        binder.forField(statusSelect).bind("status");

        // Közvetlen összekapcsolás a Category entitással
        binder.forField(categoryBox)
                .asRequired("A kategória kiválasztása kötelező!")
                .bind(Car::getCategory, Car::setCategory);

        FormLayout formLayout = new FormLayout(
                licensePlate,
                categoryBox,
                carManufacturer,
                carModel,
                mileage,
                statusSelect
        );

        Button saveButton = new Button("Mentés", e -> this.save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Mégse", e -> UI.getCurrent().navigate(CarListView.class));

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);

        this.add(this.title, formLayout, buttons);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.car = event.getRouteParameters().getLong("id")
                .map(this.carService::findById)
                .orElseGet(Car::new);

        this.binder.readBean(this.car);

        if (this.car.getCarId() == null) {
            this.title.setText("Új autó hozzáadása");
        } else {
            this.title.setText("Autó szerkesztése: " + this.car.getLicensePlate());
        }
    }

    private void save() {
        if (this.binder.writeBeanIfValid(this.car)) {
            this.carService.save(this.car);
            UI.getCurrent().navigate(CarListView.class);
        }
    }
}
