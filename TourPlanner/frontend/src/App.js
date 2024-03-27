import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetch('http://localhost:8080/hello')
        .then(response => response.text())
        .then(data => setMessage(data));
  }, []);

  return (
      <div className="App">
        <header className="App-header">
          <p>{message}</p>
        </header>
      </div>
  );
}

export default App;
