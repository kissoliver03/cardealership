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

@Route(value = "cars/new", layout = MainLayout.class)
@RouteAlias(value = "cars/:id/edit", layout = MainLayout.class)
public class CarFormView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private final CarService carService;
    private final CategoryService categoryService;

    private final BeanValidationBinder<Car> binder = new BeanValidationBinder<>(Car.class);
    private Car car;

    private final H3 title = new H3();
    private final TextField licensePlate = new TextField();
    private final TextField carManufacturer = new TextField();
    private final TextField carModel = new TextField();
    private final ComboBox<Category> categoryBox = new ComboBox<>();
    private final IntegerField mileage = new IntegerField();
    private final Select<CarStatus> statusSelect = new Select<>();

    public CarFormView(CarService carService, CategoryService categoryService) {
        this.carService = carService;
        this.categoryService = categoryService;

        this.setMaxWidth("600px");
        this.getStyle().set("margin", "0 auto");

        licensePlate.setLabel(getTranslation("car.form.licensePlate"));
        carManufacturer.setLabel(getTranslation("car.form.make"));
        carModel.setLabel(getTranslation("car.form.model"));
        categoryBox.setLabel(getTranslation("car.form.category"));
        mileage.setLabel(getTranslation("car.form.mileage"));
        statusSelect.setLabel(getTranslation("car.form.status"));

        List<Category> categories = categoryService.list();
        categoryBox.setItems(categories);
        categoryBox.setItemLabelGenerator(cat -> cat.getCategoryName() + " (" + cat.getDailyFee() + " Ft/nap)");

        statusSelect.setItems(CarStatus.values());
        statusSelect.setItemLabelGenerator(status -> getTranslation("status." + status.name()));
        statusSelect.setValue(CarStatus.AVAILABLE);

        binder.forField(licensePlate).bind("licensePlate");
        binder.forField(carManufacturer).bind("carManufacturer");
        binder.forField(carModel).bind("carModel");
        binder.forField(mileage).bind("mileage");
        binder.forField(statusSelect).bind("status");

        binder.forField(categoryBox)
                .asRequired(getTranslation("car.form.category.required"))
                .bind(Car::getCategory, Car::setCategory);

        FormLayout formLayout = new FormLayout(
                licensePlate,
                categoryBox,
                carManufacturer,
                carModel,
                mileage,
                statusSelect
        );

        Button saveButton = new Button(getTranslation("car.form.save"), e -> this.save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button(getTranslation("car.form.cancel"), e -> UI.getCurrent().navigate(CarListView.class));

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
            this.title.setText(getTranslation("car.form.new.title"));
        } else {
            this.title.setText(getTranslation("car.form.edit.title", this.car.getLicensePlate()));
        }
    }

    private void save() {
        if (this.binder.writeBeanIfValid(this.car)) {
            this.carService.save(this.car);
            UI.getCurrent().navigate(CarListView.class);
        }
    }

    @Override
    public String getPageTitle() {
        if (this.car == null || this.car.getCarId() == null) {
            return getTranslation("car.form.new.title") + " | Car Manager";
        }
        return getTranslation("car.form.edit.title", this.car.getLicensePlate()) + " | Car Manager";
    }
}
