package factory;

import model.CountryCode;

import java.util.Map;

public class FactoryRegistry {

    private static final Map<CountryCode, DocumentProcessorFactory> REGISTRY = Map.of(
        CountryCode.CO, new ColombiaFactory(),
        CountryCode.MX, new MexicoFactory(),
        CountryCode.AR, new ArgentinaFactory(),
        CountryCode.CL, new ChileFactory()
    );

    private FactoryRegistry() {
    }

    public static DocumentProcessorFactory getFactory(CountryCode country) {
        DocumentProcessorFactory factory = REGISTRY.get(country);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for country: " + country);
        }
        return factory;
    }

    public static Map<CountryCode, DocumentProcessorFactory> getAllFactories() {
        return REGISTRY;
    }
}
