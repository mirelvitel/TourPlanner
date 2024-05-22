import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './TourPlanner.css';

const TourPlanner = () => {
    const [tours, setTours] = useState([]);
    const [activeTour, setActiveTour] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [newTour, setNewTour] = useState({
        name: '',
        tourDistance: '',
        tourDescription: '',
        startLocation: '',
        endLocation: '',
        transportType: ''
    });
    const [editTour, setEditTour] = useState({});
    const [mapUrl, setMapUrl] = useState('');

    useEffect(() => {
        axios.get('/api/tour')
            .then(response => {
                console.log('Fetched tours:', response.data);
                if (Array.isArray(response.data)) {
                    setTours(response.data);
                } else {
                    console.error('Expected an array but received:', typeof response.data);
                }
            })
            .catch(error => {
                console.error('There was an error fetching the tour data!', error);
            });
    }, []);

    useEffect(() => {
        if (activeTour !== null) {
            const tour = tours[activeTour];
            // Geocode the location to get coordinates
            axios.get(`https://api.openrouteservice.org/geocode/search?api_key=5b3ce3597851110001cf6248f37a7f9f82f745b18af812787f0fb20f&text=${tour.name}`)
                .then(response => {
                    const coordinates = response.data.features[0].geometry.coordinates;
                    const lat = coordinates[1];
                    const lon = coordinates[0];
                    const zoom = 13;
                    const tileUrl = `https://tile.openstreetmap.org/${zoom}/${Math.floor((lon + 180) / 360 * Math.pow(2, zoom))}/${Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180) + 1 / Math.cos(lat * Math.PI / 180)) / Math.PI) / 2 * Math.pow(2, zoom))}.png`;
                    setMapUrl(tileUrl);
                })
                .catch(error => {
                    console.error('There was an error geocoding the location!', error);
                });
        }
    }, [activeTour]);

    const handleInputChange = (e, isEdit = false) => {
        const { name, value } = e.target;
        if (isEdit) {
            setEditTour({
                ...editTour,
                [name]: value
            });
        } else {
            setNewTour({
                ...newTour,
                [name]: value
            });
        }
    };

    const handleFormSubmit = (e) => {
        e.preventDefault();
        axios.post('/api/tour', newTour)
            .then(response => {
                window.location.reload(); // Refresh the page to reflect the new tour
            })
            .catch(error => {
                console.error('There was an error creating the tour!', error);
            });
    };

    const handleEditSubmit = (e) => {
        e.preventDefault();
        axios.put(`/api/tour/${editTour.id}`, editTour)
            .then(response => {
                window.location.reload(); // Refresh the page to reflect the updated tour
            })
            .catch(error => {
                console.error('There was an error updating the tour!', error);
            });
    };

    const handleDeleteTour = () => {
        if (activeTour === null) {
            alert("Please select a tour to delete.");
            return;
        }

        const confirmDelete = window.confirm("Are you sure you want to delete this tour?");
        if (confirmDelete) {
            const tourId = tours[activeTour]?.id;
            if (!tourId) {
                console.error('Tour ID is undefined. Active tour index:', activeTour);
                return;
            }

            console.log('Deleting tour with ID:', tourId); // Debug log

            axios.delete(`/api/tour/${tourId}`)
                .then(response => {
                    console.log('Deleted tour response:', response.data); // Debug log
                    const updatedTours = tours.filter((_, index) => index !== activeTour);
                    setTours(updatedTours);
                    setActiveTour(null);
                })
                .catch(error => {
                    console.error('There was an error deleting the tour!', error);
                });
        }
    };

    const handleEditClick = () => {
        setEditMode(true);
        setEditTour(tours[activeTour]);
    };

    return (
        <div className="main-container">
            <h1 className="heading">Tour Planner</h1>
            <div className="content-container">
                <div className="left-container">
                    <h2></h2>
                    {showForm ? (
                        <form className="tour-form" onSubmit={handleFormSubmit}>
                            <label>
                                Name:
                                <input type="text" name="name" value={newTour.name} onChange={handleInputChange} required />
                            </label>
                            <label>
                                Distance (km):
                                <input type="number" name="tourDistance" value={newTour.tourDistance} onChange={handleInputChange} required />
                            </label>
                            <label>
                                Description:
                                <textarea name="tourDescription" value={newTour.tourDescription} onChange={handleInputChange} required />
                            </label>
                            <label>
                                Start Location:
                                <input type="text" name="startLocation" value={newTour.startLocation} onChange={handleInputChange} required />
                            </label>
                            <label>
                                End Location:
                                <input type="text" name="endLocation" value={newTour.endLocation} onChange={handleInputChange} required />
                            </label>
                            <label>
                                Transport Type:
                                <input type="text" name="transportType" value={newTour.transportType} onChange={handleInputChange} required />
                            </label>
                            <div className="form-buttons">
                                <button type="submit">Create Tour</button>
                                <button type="button" onClick={() => setShowForm(false)}>Cancel</button>
                            </div>
                        </form>
                    ) : (
                        <>
                            <ul className="tour-list">
                                {Array.isArray(tours) ? (
                                    tours.map((tour, index) => (
                                        <li
                                            key={index}
                                            className={activeTour === index ? 'active' : ''}
                                            onClick={() => setActiveTour(index)}
                                        >
                                            {tour.name}
                                        </li>
                                    ))
                                ) : (
                                    <li>No tours available</li>
                                )}
                            </ul>
                            <div className="buttons">
                                <button className="add" onClick={() => setShowForm(true)}>Add</button>
                                <button className="delete" onClick={handleDeleteTour}>Delete</button>
                            </div>
                        </>
                    )}
                </div>
                <div className="right-container">
                    <div id="map" className="top-container">
                        {mapUrl && <img src={mapUrl} alt="Map" />}
                    </div>
                    <div className="bottom-container">
                        {activeTour !== null ? (
                            editMode ? (
                                <form className="edit-form" onSubmit={handleEditSubmit}>
                                    <label>
                                        Name:
                                        <input type="text" name="name" value={editTour.name} onChange={(e) => handleInputChange(e, true)} required />
                                    </label>
                                    <label>
                                        Distance (km):
                                        <input type="number" name="tourDistance" value={editTour.tourDistance} onChange={(e) => handleInputChange(e, true)} required />
                                    </label>
                                    <label>
                                        Description:
                                        <textarea name="tourDescription" value={editTour.tourDescription} onChange={(e) => handleInputChange(e, true)} required />
                                    </label>
                                    <label>
                                        Start Location:
                                        <input type="text" name="startLocation" value={editTour.startLocation} onChange={(e) => handleInputChange(e, true)} required />
                                    </label>
                                    <label>
                                        End Location:
                                        <input type="text" name="endLocation" value={editTour.endLocation} onChange={(e) => handleInputChange(e, true)} required />
                                    </label>
                                    <label>
                                        Transport Type:
                                        <input type="text" name="transportType" value={editTour.transportType} onChange={(e) => handleInputChange(e, true)} required />
                                    </label>
                                    <div className="form-buttons">
                                        <button type="submit">Save Changes</button>
                                        <button type="button" onClick={() => setEditMode(false)}>Cancel</button>
                                    </div>
                                </form>
                            ) : (
                                <div className="tour-details">
                                    <h3>{tours[activeTour].name} Tour</h3>
                                    <p><strong>Distance:</strong> {tours[activeTour].tourDistance} km</p>
                                    <p><strong>Description:</strong> {tours[activeTour].tourDescription}</p>
                                    <p><strong>Start Location:</strong> {tours[activeTour].startLocation}</p>
                                    <p><strong>End Location:</strong> {tours[activeTour].endLocation}</p>
                                    <p><strong>Transport Type:</strong> {tours[activeTour].transportType}</p>
                                    <button className="edit" onClick={handleEditClick}>Edit</button>
                                </div>
                            )
                        ) : (
                            <p>Plan your journey!</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TourPlanner;
