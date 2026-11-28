package hu.mik.pte.prog5.carmanager.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
public class SimpleI18NProvider implements I18NProvider {

    private static final String BUNDLE_PREFIX = "vaadin-i18n/translations";

    private final List<Locale> locales = List.of(Locale.of("hu"), Locale.of("en"));

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            return "";
        }

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale != null ? locale : Locale.of("hu"));

        String value;
        try {
            value = bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }

        if (params != null && params.length > 0) {
            value = MessageFormat.format(value, params);
        }

        return value;
    }
}
