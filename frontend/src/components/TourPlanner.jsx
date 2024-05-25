import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import L from 'leaflet';
import './TourPlanner.css';

const TourPlanner = () => {
    const [tours, setTours] = useState([]);
    const [activeTour, setActiveTour] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [logEditMode, setLogEditMode] = useState(false);
    const [tourLogs, setTourLogs] = useState([]);
    const [showLogForm, setShowLogForm] = useState(false);
    const [newTour, setNewTour] = useState({
        name: '',
        tourDistance: '',
        tourDescription: '',
        startLocation: '',
        endLocation: '',
        transportType: ''
    });
    const [newLog, setNewLog] = useState({
        comment: '',
        dateTime: '',
        difficulty: 1,
        rating: 1,
        totalDistance: 0,
        totalTime: 0
    });
    const [editTour, setEditTour] = useState({});
    const [editLog, setEditLog] = useState({});
    const mapRef = useRef(null);
    const mapInstance = useRef(null);

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
        if (mapRef.current && !mapInstance.current) {
            mapInstance.current = L.map(mapRef.current).setView([0, 0], 2); // World view
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(mapInstance.current);
        }
    }, []);

    useEffect(() => {
        if (activeTour !== null && mapInstance.current) {
            const tour = tours[activeTour];
            // Geocode the location to get coordinates
            axios.get(`https://api.openrouteservice.org/geocode/search?api_key=5b3ce3597851110001cf6248f37a7f9f82f745b18af812787f0fb20f&text=${tour.name}`)
                .then(response => {
                    const coordinates = response.data.features[0].geometry.coordinates;
                    const lat = coordinates[1];
                    const lon = coordinates[0];
                    mapInstance.current.setView([lat, lon], 13);
                    L.marker([lat, lon]).addTo(mapInstance.current);
                })
                .catch(error => {
                    console.error('There was an error geocoding the location!', error);
                });

            // Fetch tour logs for the selected tour
            axios.get(`/api/tour/${tour.id}/log`)
                .then(response => {
                    console.log('Fetched tour logs:', response.data);
                    setTourLogs(response.data);
                })
                .catch(error => {
                    console.error('There was an error fetching the tour logs!', error);
                });
        } else if (mapInstance.current) {
            // If no tour is selected, reset to the world view
            mapInstance.current.setView([0, 0], 2);
            setTourLogs([]);
        }
    }, [activeTour, tours]);

    const handleInputChange = (e, isEdit = false, isLog = false) => {
        const { name, value } = e.target;
        if (isLog) {
            if (isEdit) {
                setEditLog({
                    ...editLog,
                    [name]: value
                });
            } else {
                setNewLog({
                    ...newLog,
                    [name]: value
                });
            }
        } else {
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
        }
    };

    const validateTour = (tour) => {
        if (tour.tourDistance < 1 || tour.tourDistance > 100) {
            alert("Tour distance must be between 1 and 100 km.");
            return false;
        }
        if (tour.tourDescription.length > 500) {
            alert("Tour description cannot exceed 500 characters.");
            return false;
        }
        return true;
    };

    const validateLog = (log) => {
        if (log.difficulty < 1 || log.difficulty > 5) {
            alert("Difficulty must be between 1 and 5.");
            return false;
        }
        if (log.totalDistance < 1 || log.totalDistance > 100) {
            alert("Total distance must be between 1 and 100 km.");
            return false;
        }
        if (log.totalTime < 1 || log.totalTime > 10) {
            alert("Total time must be between 1 and 10 hours.");
            return false;
        }
        return true;
    };

    const handleFormSubmit = (e) => {
        e.preventDefault();
        if (!validateTour(newTour)) return;
        axios.post('/api/tour', newTour)
            .then(response => {
                window.location.reload(); // Refresh the page to reflect the new tour
            })
            .catch(error => {
                console.error('There was an error creating the tour!', error);
            });
    };

    const handleLogSubmit = (e) => {
        e.preventDefault();
        if (!validateLog(newLog)) return;
        axios.post(`/api/tour/${tours[activeTour].id}/log`, newLog)
            .then(response => {
                setTourLogs([...tourLogs, response.data]);
                setNewLog({
                    comment: '',
                    dateTime: '',
                    difficulty: 1,
                    rating: 1,
                    totalDistance: 1,
                    totalTime: 1
                });
                setShowLogForm(false);
            })
            .catch(error => {
                console.error('There was an error creating the tour log!', error);
            });
    };

    const handleEditSubmit = (e) => {
        e.preventDefault();
        if (!validateTour(editTour)) return;
        axios.put(`/api/tour/${editTour.id}`, editTour)
            .then(response => {
                window.location.reload(); // Refresh the page to reflect the updated tour
            })
            .catch(error => {
                console.error('There was an error updating the tour!', error);
            });
    };

    const handleEditLogSubmit = (e) => {
        e.preventDefault();
        if (!validateLog(editLog)) return;
        axios.put(`/api/tour/${editLog.tourId}/log/${editLog.id}`, editLog)
            .then(response => {
                const updatedLogs = tourLogs.map(log => log.id === editLog.id ? editLog : log);
                setTourLogs(updatedLogs);
                setLogEditMode(false);
                setEditLog({});
            })
            .catch(error => {
                console.error('There was an error updating the tour log!', error);
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

    const handleDeleteLog = (logId) => {
        const confirmDelete = window.confirm("Are you sure you want to delete this tour log?");
        if (confirmDelete) {
            axios.delete(`/api/tour/${tours[activeTour].id}/log/${logId}`)
                .then(response => {
                    const updatedLogs = tourLogs.filter(log => log.id !== logId);
                    setTourLogs(updatedLogs);
                    window.location.reload(); // Refresh the page to reflect the updated logs
                })
                .catch(error => {
                    console.error('There was an error deleting the tour log!', error);
                });
        }
    };

    const handleEditClick = () => {
        setEditMode(true);
        setEditTour(tours[activeTour]);
    };

    const handleEditLogClick = (log) => {
        setLogEditMode(true);
        setEditLog(log);
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
                                <input type="text" name="name" value={newTour.name} onChange={(e) => handleInputChange(e, false, false)} required />
                            </label>
                            <label>
                                Distance (km):
                                <input type="number" name="tourDistance" value={newTour.tourDistance} onChange={(e) => handleInputChange(e, false, false)} required min="1" max="100" />
                            </label>
                            <label>
                                Description:
                                <textarea name="tourDescription" value={newTour.tourDescription} onChange={(e) => handleInputChange(e, false, false)} required />
                            </label>
                            <label>
                                Start Location:
                                <input type="text" name="startLocation" value={newTour.startLocation} onChange={(e) => handleInputChange(e, false, false)} required />
                            </label>
                            <label>
                                End Location:
                                <input type="text" name="endLocation" value={newTour.endLocation} onChange={(e) => handleInputChange(e, false, false)} required />
                            </label>
                            <label>
                                Transport Type:
                                <input type="text" name="transportType" value={newTour.transportType} onChange={(e) => handleInputChange(e, false, false)} required />
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
                    <div ref={mapRef} id="map" className="top-container"></div>
                    <div className="bottom-container">
                        {activeTour !== null ? (
                            <>
                                {editMode ? (
                                    <form className="edit-form" onSubmit={handleEditSubmit}>
                                        <label>
                                            Name:
                                            <input type="text" name="name" value={editTour.name} onChange={(e) => handleInputChange(e, true, false)} required />
                                        </label>
                                        <label>
                                            Distance (km):
                                            <input type="number" name="tourDistance" value={editTour.tourDistance} onChange={(e) => handleInputChange(e, true, false)} required min="1" max="100" />
                                        </label>
                                        <label>
                                            Description:
                                            <textarea name="tourDescription" value={editTour.tourDescription} onChange={(e) => handleInputChange(e, true, false)} required />
                                        </label>
                                        <label>
                                            Start Location:
                                            <input type="text" name="startLocation" value={editTour.startLocation} onChange={(e) => handleInputChange(e, true, false)} required />
                                        </label>
                                        <label>
                                            End Location:
                                            <input type="text" name="endLocation" value={editTour.endLocation} onChange={(e) => handleInputChange(e, true, false)} required />
                                        </label>
                                        <label>
                                            Transport Type:
                                            <input type="text" name="transportType" value={editTour.transportType} onChange={(e) => handleInputChange(e, true, false)} required />
                                        </label>
                                        <div className="form-buttons">
                                            <button type="submit">Save Changes</button>
                                            <button type="button" onClick={() => setEditMode(false)}>Cancel</button>
                                        </div>
                                    </form>
                                ) : logEditMode ? (
                                    <form className="edit-form" onSubmit={handleEditLogSubmit}>
                                        <label>
                                            Comment:
                                            <textarea name="comment" value={editLog.comment} onChange={(e) => handleInputChange(e, true, true)} required />
                                        </label>
                                        <label>
                                            Date/Time:
                                            <input type="datetime-local" name="dateTime" value={editLog.dateTime} onChange={(e) => handleInputChange(e, true, true)} required />
                                        </label>
                                        <label>
                                            Difficulty:
                                            <input type="number" name="difficulty" value={editLog.difficulty} onChange={(e) => handleInputChange(e, true, true)} required min="1" max="5" />
                                        </label>
                                        <label>
                                            Rating:
                                            <input type="number" name="rating" value={editLog.rating} onChange={(e) => handleInputChange(e, true, true)} required min="1" max="5" />
                                        </label>
                                        <label>
                                            Total Distance (km):
                                            <input type="number" name="totalDistance" value={editLog.totalDistance} onChange={(e) => handleInputChange(e, true, true)} required min="1" max="100" />
                                        </label>
                                        <label>
                                            Total Time (hours):
                                            <input type="number" name="totalTime" value={editLog.totalTime} onChange={(e) => handleInputChange(e, true, true)} required min="1" max="10" />
                                        </label>
                                        <div className="form-buttons">
                                            <button type="submit">Save Changes</button>
                                            <button type="button" onClick={() => setLogEditMode(false)}>Cancel</button>
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
                                        <p><strong>Popularity:</strong> {tours[activeTour].popularity}</p>
                                        <p><strong>Child Friendliness:</strong> {tours[activeTour].childFriendliness.toFixed(2)}</p>
                                        <button className="edit" onClick={handleEditClick}>Edit</button>

                                        <h3>Tour Logs</h3>
                                        <ul className="log-list">
                                            {tourLogs.map(log => (
                                                <li key={log.id}>
                                                    <p><strong>Date/Time:</strong> {log.dateTime}</p>
                                                    <p><strong>Comment:</strong> {log.comment}</p>
                                                    <p><strong>Difficulty:</strong> {log.difficulty}</p>
                                                    <p><strong>Rating:</strong> {log.rating}</p>
                                                    <p><strong>Total Distance:</strong> {log.totalDistance} km</p>
                                                    <p><strong>Total Time:</strong> {log.totalTime} hours</p>
                                                    <button onClick={() => handleEditLogClick(log)}>Edit</button>
                                                    <button onClick={() => handleDeleteLog(log.id)}>Delete</button>
                                                </li>
                                            ))}
                                        </ul>
                                        <button className="add-log" onClick={() => setShowLogForm(true)}>Add Log</button>
                                        {showLogForm && (
                                            <form className="log-form" onSubmit={handleLogSubmit}>
                                                <label>
                                                    Comment:
                                                    <textarea name="comment" value={newLog.comment} onChange={(e) => handleInputChange(e, false, true)} required />
                                                </label>
                                                <label>
                                                    Date/Time:
                                                    <input type="datetime-local" name="dateTime" value={newLog.dateTime} onChange={(e) => handleInputChange(e, false, true)} required />
                                                </label>
                                                <label>
                                                    Difficulty:
                                                    <input type="number" name="difficulty" value={newLog.difficulty} onChange={(e) => handleInputChange(e, false, true)} required min="1" max="5" />
                                                </label>
                                                <label>
                                                    Rating:
                                                    <input type="number" name="rating" value={newLog.rating} onChange={(e) => handleInputChange(e, false, true)} required min="1" max="5" />
                                                </label>
                                                <label>
                                                    Total Distance (km):
                                                    <input type="number" name="totalDistance" value={newLog.totalDistance} onChange={(e) => handleInputChange(e, false, true)} required min="1" max="100" />
                                                </label>
                                                <label>
                                                    Total Time (hours):
                                                    <input type="number" name="totalTime" value={newLog.totalTime} onChange={(e) => handleInputChange(e, false, true)} required min="1" max="10" />
                                                </label>
                                                <div className="form-buttons">
                                                    <button type="submit">Add Log</button>
                                                    <button type="button" onClick={() => setShowLogForm(false)}>Cancel</button>
                                                </div>
                                            </form>
                                        )}
                                    </div>
                                )}
                            </>
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
