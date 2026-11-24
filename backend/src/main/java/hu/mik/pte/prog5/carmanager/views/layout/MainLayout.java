package hu.mik.pte.prog5.carmanager.views.layout;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import hu.mik.pte.prog5.carmanager.views.CarListView;

public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div content = new Div();

    public MainLayout() {
        this.setSizeFull();
        this.setMaxWidth("1100px");
        this.getStyle().set("margin", "0 auto");

        this.content.setWidthFull();
        this.add(this.createHeader(), this.content, this.createFooter());
        this.setFlexGrow(1, this.content);
    }

    private Header createHeader() {
        H2 title = new H2(new RouterLink("🚗 Car Manager - Flottakezelő", CarListView.class));
        title.getStyle().set("margin", "0");

        HorizontalLayout bar = new HorizontalLayout(title);
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.getStyle().set("padding", "1rem 0");
        bar.getStyle().set("border-bottom", "1px solid #e0e0e0");

        Header header = new Header(bar);
        header.setWidthFull();
        return header;
    }

    private Footer createFooter() {
        Footer footer = new Footer(new Span("PTE MIK - Programozás 5 | Car Manager Full-Stack Projekt"));
        footer.getStyle().set("color", "gray");
        footer.getStyle().set("padding", "1rem 0");
        footer.getStyle().set("border-top", "1px solid #e0e0e0");
        footer.getStyle().set("text-align", "center");
        footer.setWidthFull();
        return footer;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.content.removeAll();
        if (content != null) {
            this.content.getElement().appendChild(content.getElement());
        }
    }
}
