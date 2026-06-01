//Code written by Göktuğ Kuyumcuoğlu with the help of https://github.com/iamshaunjp/Complete-React-Tutorial and ChatGPT
import React, { useEffect, useState } from "react";
import { supabase } from "../supabaseClient";
import { useNavigate } from "react-router-dom";
import "../styles/Home.css";

const Home = () => {
    const [dispatches, setDispatches] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDispatches = async () => {
            const { data, error } = await supabase
                .from("dispatch")
                .select("*")
                .order("date", { ascending: false });

            if (error) {
                console.error("Error fetching dispatch reports:", error);
            } else {
                setDispatches(data);
            }
        };

        fetchDispatches();
    }, []);

    return (
        <div className="home-container">
            <div className="home-header">
                <h1 className="home-title">Fire Dispatch</h1>
            </div>
            <div className="dispatch-list">
                {dispatches.length > 0 ? (
                    dispatches.map((dispatch) => (
                        <div key={dispatch.dispatch_id} className="dispatch-card">
                            <h3>{dispatch.situation}</h3>
                            <p><strong>Location:</strong> {dispatch.location}</p>
                            <p><strong>Date:</strong> {dispatch.date}</p>
                            <p><strong>Time:</strong> {dispatch.time} minutes</p>
                        </div>
                    ))
                ) : (
                    <p>No active dispatches.</p>
                )}
            </div>
            <button className="report-button" onClick={() => navigate("/report")}>
                Submit Fire Report
            </button>
        </div>
    );
};

export default Home;
