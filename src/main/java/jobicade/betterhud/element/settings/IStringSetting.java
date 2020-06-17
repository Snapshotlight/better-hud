package jobicade.betterhud.element.settings;

/**
 * Implemented by settings which have a value. The method
 * {@link Setting#getStringSetting()} lets these settings describe themselves.
 */
public interface IStringSetting {
    String getName();

    String getStringValue();
    String getDefaultValue();

	/**
	 * If {@code stringValue} is valid, sets the value accordingly.
     *
	 * @param stringValue A string as generated by {@link #getStringValue()}.
     * @throws SettingValueException if the setting value is invalid.
	 */
    void loadStringValue(String stringValue) throws SettingValueException;
    void loadDefaultValue();
}
