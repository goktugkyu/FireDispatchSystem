//Code written by Göktuğ Kuyumcuoğlu with the help of https://github.com/iamshaunjp/Complete-React-Tutorial and ChatGPT

import React, { useState, useEffect } from "react";
import { supabase } from "../supabaseClient";
import "../styles/ReportForm.css"; 

const ReportForm = () => {
    const [location, setLocation] = useState("");
    const [situation, setSituation] = useState("");
    const [date, setDate] = useState("");
    const [time, setTime] = useState("");

    const [department, setDepartment] = useState("");


    const [departments, setDepartments] = useState([]);

    useEffect(() => {
        const fetchDepartments = async () => {
            const { data, error } = await supabase
                .from("department")
                .select("*");

            if (error) {
                console.error("Error fetching departments:", error);
            } else {
                setDepartments(data);
            }
        };

        fetchDepartments();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const formattedTime = time.length === 5 ? `${time}:00` : time;

        const { data, error } = await supabase.from("dispatch").insert([
            {
                location,
                situation,
                department: parseInt(department),
                date,
                time: formattedTime,
            },
        ]);

        if (error) {
            console.error("Error inserting data:", error);
            alert("Error inserting data. Check the console.");
        } else {
            alert("Fire report submitted successfully!");
            setLocation("");
            setSituation("");
            setDate("");
            setTime("");
            setDepartment(""); 
        }
    };

    return (
        <div className="report-form-container">
            <div className="report-form-header">
                <h2 className="report-form-title">Submit Fire Report</h2>
            </div>
            <form className="report-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Location"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    required
                />
                <textarea
                    placeholder="Situation"
                    value={situation}
                    onChange={(e) => setSituation(e.target.value)}
                    required
                />
                <input
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    required
                />
                <input
                    type="time"
                    placeholder="Select time"
                    value={time}
                    onChange={(e) => setTime(e.target.value)}
                    required
                />

                <select
                    value={department}
                    onChange={(e) => setDepartment(e.target.value)}
                    required
                >
                    <option value="" disabled>Select Department</option>
                    {departments.map((dept) => (
                        <option key={dept.department_id} value={dept.department_id}>
                            {dept.location}
                        </option>
                    ))}
                </select>

                <button type="submit">Submit</button>
            </form>
        </div>
    );
};

export default ReportForm;
