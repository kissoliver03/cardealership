package hu.mik.pte.prog5.carmanager.views.layout;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import hu.mik.pte.prog5.carmanager.views.CarListView;
import hu.mik.pte.prog5.carmanager.views.CategoryListView;
import hu.mik.pte.prog5.carmanager.views.RentalListView;

import java.util.Locale;

public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div content = new Div();

    public MainLayout() {
        this.setSizeFull();
        this.setMaxWidth("1100px");
        this.getStyle().set("margin", "0 auto");

        this.content.setWidthFull();
        this.add(this.createHeader(), this.content);
        this.setFlexGrow(1, this.content);
    }

    private Header createHeader() {
        Span title = new Span(getTranslation("app.title"));
        title.getStyle()
                .set("font-size", "1.25rem")
                .set("font-weight", "600")
                .set("color", "var(--lumo-primary-text-color)")
                .set("cursor", "pointer")
                .set("white-space", "nowrap")
                .set("margin-right", "1.5rem");
        title.addClickListener(e -> UI.getCurrent().navigate(CarListView.class));

        Button carsButton = new Button(getTranslation("nav.cars"), e -> UI.getCurrent().navigate(CarListView.class));
        Button rentalsButton = new Button(getTranslation("nav.rentals"), e -> UI.getCurrent().navigate(RentalListView.class));
        Button categoriesButton = new Button(getTranslation("nav.categories"), e -> UI.getCurrent().navigate(CategoryListView.class));

        Select<Locale> language = new Select<>();
        language.setItems(Locale.of("hu"), Locale.of("en"));
        language.setItemLabelGenerator(l -> l.getLanguage().equalsIgnoreCase("hu") ? "Magyar" : "English");
        language.setWidth("120px");

        Locale currentLocale = UI.getCurrent() != null && UI.getCurrent().getLocale() != null ?
                UI.getCurrent().getLocale() : Locale.of("hu");
        language.setValue(currentLocale);

        language.addValueChangeListener(e -> {
            if (e.getValue() != null && UI.getCurrent() != null) {
                UI.getCurrent().setLocale(e.getValue());
                if (VaadinSession.getCurrent() != null) {
                    VaadinSession.getCurrent().setLocale(e.getValue());
                }
                UI.getCurrent().getPage().reload();
            }
        });

        Div spacer = new Div();

        HorizontalLayout bar = new HorizontalLayout(title, carsButton, rentalsButton, categoriesButton, spacer, language);
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        bar.setFlexGrow(1, spacer);
        bar.getStyle()
                .set("padding", "0.75rem 0")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        Header header = new Header(bar);
        header.setWidthFull();
        return header;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.content.removeAll();
        if (content != null) {
            this.content.getElement().appendChild(content.getElement());
        }
    }
}
