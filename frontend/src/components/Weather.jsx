// Weather.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const Weather = ({ latitude, longitude, tourName }) => {
    const [weatherData, setWeatherData] = useState(null);

    useEffect(() => {
        const fetchWeather = async () => {
            try {
                const response = await axios.get('https://api.open-meteo.com/v1/forecast', {
                    params: {
                        latitude,
                        longitude,
                        hourly: 'temperature_2m,precipitation'
                    }
                });
                setWeatherData(response.data);
            } catch (error) {
                console.error('There was an error fetching the weather data!', error);
            }
        };

        if (latitude && longitude) {
            fetchWeather();
        }
    }, [latitude, longitude]);

    if (!weatherData) {
        return <div>Loading weather data...</div>;
    }

    return (
        <div className="weather-container">
            <h3>Current Weather</h3>
            <p><strong>Current temperature in {tourName}:</strong> {weatherData.hourly.temperature_2m[0]}°C</p>
            <p><strong>Precipitation:</strong> {weatherData.hourly.precipitation[0]} mm</p>
        </div>
    );
};

export default Weather;
