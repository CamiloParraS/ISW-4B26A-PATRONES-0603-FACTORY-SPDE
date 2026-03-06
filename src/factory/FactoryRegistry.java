package factory;

import java.util.Map;
import model.CountryCode;

/**
 * Registry that maps CountryCode to its corresponding DocumentProcessorFactory.
 *
 * Clients use this to get the right factory without knowing concrete class names. This is the
 * standard way to bridge the Factory Method pattern with runtime selection.
 *
 * Usage: DocumentProcessorFactory factory = FactoryRegistry.getFactory(CountryCode.CO);
 * DocumentProcessor processor = factory.getProcessor(DocumentType.ELECTRONIC_INVOICE,
 * FileFormat.PDF, "invoice.pdf");
 */
public class FactoryRegistry {

    private static final Map<CountryCode, DocumentProcessorFactory> REGISTRY =
            Map.of(CountryCode.CO, new ColombiaFactory(), CountryCode.MX, new MexicoFactory(),
                    CountryCode.AR, new ArgentinaFactory(), CountryCode.CL, new ChileFactory());

    private FactoryRegistry() {
        // Utility class — no instantiation
    }

    /**
     * Returns the factory for the given country.
     *
     * @param country the target country
     * @return the corresponding DocumentProcessorFactory
     * @throws IllegalArgumentException if no factory is registered for the country
     */
    public static DocumentProcessorFactory getFactory(CountryCode country) {
        DocumentProcessorFactory factory = REGISTRY.get(country);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for country: " + country);
        }
        return factory;
    }

    /**
     * Returns all registered factories.
     */
    public static Map<CountryCode, DocumentProcessorFactory> getAllFactories() {
        return REGISTRY;
    }
}
