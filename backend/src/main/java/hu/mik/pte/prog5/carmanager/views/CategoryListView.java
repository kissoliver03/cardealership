package hu.mik.pte.prog5.carmanager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import hu.mik.pte.prog5.carmanager.model.Category;
import hu.mik.pte.prog5.carmanager.service.CategoryService;
import hu.mik.pte.prog5.carmanager.views.layout.MainLayout;

import java.util.List;

@Route(value = "categories", layout = MainLayout.class)
public class CategoryListView extends VerticalLayout implements HasDynamicTitle {

    private final CategoryService categoryService;
    private final Grid<Category> grid = new Grid<>(Category.class, false);

    private final TextField searchField = new TextField();

    public CategoryListView(CategoryService categoryService) {
        this.categoryService = categoryService;

        this.setSizeFull();
        this.setPadding(false);

        H3 viewTitle = new H3(getTranslation("category.list.title"));

        Button newCategoryButton = new Button(getTranslation("category.list.add"), e -> this.openDialog(new Category()));
        newCategoryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout topBar = new HorizontalLayout(viewTitle, newCategoryButton);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);
        topBar.expand(viewTitle);

        this.searchField.setPlaceholder(getTranslation("category.filter.searchPlaceholder"));
        this.searchField.setClearButtonVisible(true);
        this.searchField.setValueChangeMode(ValueChangeMode.LAZY);
        this.searchField.addValueChangeListener(e -> this.applyFilters());

        Button resetButton = new Button(getTranslation("filter.reset"), e -> this.resetFilters());

        HorizontalLayout filterBar = new HorizontalLayout(this.searchField, resetButton);
        filterBar.setWidthFull();
        filterBar.setAlignItems(Alignment.CENTER);
        filterBar.expand(this.searchField);

        this.grid.addColumn(Category::getCategoryId)
                .setHeader(getTranslation("category.id"))
                .setAutoWidth(true)
                .setFlexGrow(0);

        this.grid.addColumn(this::formatCategoryName)
                .setHeader(getTranslation("category.name"))
                .setAutoWidth(true);

        this.grid.addColumn(category -> category.getDailyFee() != null ? category.getDailyFee() + " " + getTranslation("app.dailyFeeUnit") : "-")
                .setHeader(getTranslation("category.dailyFee"))
                .setAutoWidth(true);

        this.grid.addComponentColumn(this::createActions)
                .setHeader(getTranslation("category.actions"))
                .setAutoWidth(true);

        this.grid.setEmptyStateText(getTranslation("category.list.empty"));

        this.add(topBar, filterBar, this.grid);
        this.applyFilters();
    }

    private HorizontalLayout createActions(Category category) {
        Button editButton = new Button(getTranslation("category.edit"), e -> this.openDialog(category));
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        return new HorizontalLayout(editButton);
    }

    private void openDialog(Category category) {
        Dialog dialog = new Dialog();
        boolean isNew = category.getCategoryId() == null;
        dialog.setHeaderTitle(isNew ? getTranslation("category.dialog.new.title") : getTranslation("category.dialog.edit.title"));

        TextField nameField = new TextField(getTranslation("category.name"));
        nameField.setWidthFull();

        BigDecimalField dailyFeeField = new BigDecimalField(getTranslation("category.dailyFee"));
        dailyFeeField.setWidthFull();

        BeanValidationBinder<Category> binder = new BeanValidationBinder<>(Category.class);
        binder.forField(nameField)
                .asRequired(getTranslation("category.name.required"))
                .bind(Category::getCategoryName, Category::setCategoryName);
        binder.forField(dailyFeeField)
                .asRequired(getTranslation("category.fee.required"))
                .bind(Category::getDailyFee, Category::setDailyFee);

        binder.readBean(category);

        VerticalLayout dialogLayout = new VerticalLayout(nameField, dailyFeeField);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(true);
        dialog.add(dialogLayout);

        Button saveButton = new Button(getTranslation("category.save"), e -> {
            if (binder.writeBeanIfValid(category)) {
                this.categoryService.save(category);
                this.applyFilters();
                dialog.close();
                Notification.show(getTranslation("category.saved"));
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button(getTranslation("category.cancel"), e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void applyFilters() {
        String query = this.searchField.getValue() != null ? this.searchField.getValue().trim().toLowerCase() : "";

        List<Category> filtered = this.categoryService.list().stream()
                .filter(c -> query.isEmpty() ||
                        (c.getCategoryName() != null && c.getCategoryName().toLowerCase().contains(query)) ||
                        formatCategoryName(c).toLowerCase().contains(query))
                .toList();

        this.grid.setItems(filtered);
    }

    private void resetFilters() {
        this.searchField.clear();
        this.applyFilters();
    }

    private String formatCategoryName(Category category) {
        if (category == null || category.getCategoryName() == null) {
            return "-";
        }
        String key = "category.type." + category.getCategoryName();
        String translation = getTranslation(key);
        return translation.startsWith("!") ? category.getCategoryName() : translation;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("category.list.title") + " | Car Manager";
    }
}
