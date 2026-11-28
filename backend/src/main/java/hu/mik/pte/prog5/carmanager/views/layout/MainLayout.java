package hu.mik.pte.prog5.carmanager.views.layout;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import hu.mik.pte.prog5.carmanager.views.CarListView;

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
        H2 title = new H2(new RouterLink(getTranslation("app.title"), CarListView.class));
        title.getStyle().set("margin", "0");

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

        HorizontalLayout bar = new HorizontalLayout(title, language);
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.expand(title);
        bar.getStyle().set("padding", "1rem 0");
        bar.getStyle().set("border-bottom", "1px solid #e0e0e0");

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
