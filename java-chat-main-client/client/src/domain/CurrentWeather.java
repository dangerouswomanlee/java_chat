package domain;

public class CurrentWeather {

	private final String description;
	private final String temperature;
	private final String iconCode;
	private final String iconUrl;

	public CurrentWeather(String description, String temperature, String iconCode) {
		this.description = description;
		this.temperature = temperature;
		this.iconCode = iconCode;
		this.iconUrl = String.format("https://openweathermap.org/img/wn/%s@2x.png", iconCode);
	}

	public String getDescription() {
		return description;
	}

	public String getTemperature() {
		return temperature;
	}

	public String getIconCode() {
		return iconCode;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	@Override
	public String toString() {
		return String.format("%s, %s°C (%s)", description, temperature, iconCode);
	}
}
