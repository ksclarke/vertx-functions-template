
package info.freelibrary.vertx.functions;

import java.util.Locale;

import info.freelibrary.util.I18nException;

/**
 * An exception thrown when there is a configuration error.
 */
public class ConfigurationException extends I18nException {

    /** The <code>serialVersionUID</code> for the ConfigurationException class. */
    private static final long serialVersionUID = -2523974218753089303L;

    /**
     * Creates a new ConfigurationException from the supplied root cause.
     *
     * @param aCause An underlying cause of the configuration exception
     */
    public ConfigurationException(final Throwable aCause) {
        super(aCause);
    }

    /**
     * Creates a new ConfigurationException from the supplied message key.
     *
     * @param aMessageKey A message key
     */
    public ConfigurationException(final String aMessageKey) {
        super(MessageCodes.BUNDLE, aMessageKey);
    }

    /**
     * Creates a new ConfigurationException from the supplied locale and message key.
     *
     * @param aLocale A locale
     * @param aMessageKey A message key
     */
    public ConfigurationException(final Locale aLocale, final String aMessageKey) {
        super(aLocale, MessageCodes.BUNDLE, aMessageKey);
    }

    /**
     * Creates a new ConfigurationException from the supplied message key and additional details.
     *
     * @param aMessageKey A message key
     * @param aVarargs Additional details to be used in the message
     */
    public ConfigurationException(final String aMessageKey, final Object... aVarargs) {
        super(MessageCodes.BUNDLE, aMessageKey, aVarargs);
    }

    /**
     * Creates a new ConfigurationException from the supplied root cause and message key.
     *
     * @param aCause A root cause
     * @param aMessageKey A message key
     */
    public ConfigurationException(final Throwable aCause, final String aMessageKey) {
        super(aCause, MessageCodes.BUNDLE, aMessageKey);
    }

    /**
     * Creates a new ConfigurationException from the supplied locale, message key, and additional details.
     *
     * @param aLocale A locale
     * @param aMessageKey A message key
     * @param aVarargs Additional details to be used in the message
     */
    public ConfigurationException(final Locale aLocale, final String aMessageKey, final Object... aVarargs) {
        super(aLocale, MessageCodes.BUNDLE, aMessageKey, aVarargs);
    }

    /**
     * Creates a new ConfigurationExceptoin from the supplied root cause, locale, and message key.
     *
     * @param aCause A root cause
     * @param aLocale A locale
     * @param aMessageKey A message key
     */
    public ConfigurationException(final Throwable aCause, final Locale aLocale, final String aMessageKey) {
        super(aCause, aLocale, MessageCodes.BUNDLE, aMessageKey);
    }

    /**
     * Creates a new ConfigurationException from the supplied root cause, message key, and additional details.
     *
     * @param aCause A root cause
     * @param aMessageKey A message key
     * @param aVarargs Additional details to be used in the message
     */
    public ConfigurationException(final Throwable aCause, final String aMessageKey, final Object... aVarargs) {
        super(aCause, MessageCodes.BUNDLE, aMessageKey, aVarargs);
    }

    /**
     * Creates a new ConfigurationException from the supplied root cause, locale, message key, and additional details.
     *
     * @param aCause A root cause
     * @param aLocale A locale
     * @param aMessageKey A message key
     * @param aVarargs Additional details to be used in the message
     */
    public ConfigurationException(final Throwable aCause, final Locale aLocale, final String aMessageKey,
            final Object... aVarargs) {
        super(aCause, aLocale, MessageCodes.BUNDLE, aMessageKey, aVarargs);
    }

}
