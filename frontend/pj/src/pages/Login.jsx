import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";

const Login = () => {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState(""); // 🔴 Error state
  const navigate = useNavigate();
  const { setUser } = useContext(AuthContext);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); // clear old error
    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
        credentials: "include",
      });

      const data = await res.json();
      console.log("Login Response:", data);

      if (!data.success) {
        // 🔴 Backend error message shown to user
        setError(data.message || "Login failed. Please try again.");
        return;
      }

      setUser(data.data);
      navigate("/profile");
    } catch (err) {
      console.error("Error:", err.message);
      setError("Something went wrong. Please try again later.");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-900">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col space-y-3 w-full max-w-sm bg-gray-800 p-6 rounded-2xl shadow-lg"
      >
        <h2 className="text-2xl font-bold mb-4 text-center text-yellow-400">
          Login
        </h2>

        <input
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          className="p-2 border border-gray-600 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          className="p-2 border border-gray-600 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
        />

        {/* 🔴 Error message display */}
        {error && (
          <p className="text-red-400 text-sm text-center bg-red-900/40 p-2 rounded">
            {error}
          </p>
        )}

        <button
          type="submit"
          className="bg-yellow-500 hover:bg-yellow-600 text-black font-semibold p-2 rounded transition"
        >
          Login
        </button>
      </form>
    </div>
  );
};

export default Login;

