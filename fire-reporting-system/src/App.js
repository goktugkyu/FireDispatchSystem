import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import ReportForm from "./components/ReportForm";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/report" element={<ReportForm />} />
            </Routes>
        </Router>
    );
}

export default App;
