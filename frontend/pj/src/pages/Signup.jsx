// import { useState } from "react";
// import { useNavigate } from "react-router-dom";

// const Signup = () => {
//   const [form, setForm] = useState({ username: "", email: "", password: "" });
//   const [error, setError] = useState(""); // 🔴 For error messages
//   const [success, setSuccess] = useState(""); // 🟢 Optional success message
//   const navigate = useNavigate();

//   const handleChange = (e) =>
//     setForm({ ...form, [e.target.name]: e.target.value });

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     setError("");
//     setSuccess("");

//     try {
//       const res = await fetch("http://localhost:8080/api/auth/signup", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify(form),
//         credentials: "include",
//       });

//       const data = await res.json();
//       console.log("Signup Response:", data);

//       if (!data.success) {
//         setError(data.message || "Signup failed. Please try again.");
//         return;
//       }

//       // 🟢 Show success and redirect after 1 sec
//       setSuccess("Signup successful! Redirecting to login...");
//       setTimeout(() => navigate("/login"), 1000);
//     } catch (err) {
//       console.error("Error:", err.message);
//       setError("Something went wrong. Please try again later.");
//     }
//   };

//   return (
//     <div className="flex justify-center items-center h-screen bg-gray-900">
//       <form
//         onSubmit={handleSubmit}
//         className="flex flex-col space-y-3 w-full max-w-sm bg-gray-800 p-6 rounded-2xl shadow-lg"
//       >
//         <h2 className="text-2xl font-bold mb-4 text-center text-yellow-400">
//           Signup
//         </h2>

//         <input
//           name="username"
//           placeholder="Username"
//           value={form.username}
//           onChange={handleChange}
//           className="p-2 border border-gray-600 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
//         />

//         <input
//           name="email"
//           placeholder="Email"
//           value={form.email}
//           onChange={handleChange}
//           className="p-2 border border-gray-600 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
//         />

//         <input
//           type="password"
//           name="password"
//           placeholder="Password"
//           value={form.password}
//           onChange={handleChange}
//           className="p-2 border border-gray-600 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
//         />

//         {/* 🔴 Error message */}
//         {error && (
//           <p className="text-red-400 text-sm text-center bg-red-900/40 p-2 rounded">
//             {error}
//           </p>
//         )}

//         {/* 🟢 Success message */}
//         {success && (
//           <p className="text-green-400 text-sm text-center bg-green-900/40 p-2 rounded">
//             {success}
//           </p>
//         )}

//         <button
//           type="submit"
//           className="bg-yellow-500 hover:bg-yellow-600 text-black font-semibold p-2 rounded transition"
//         >
//           Signup
//         </button>
//       </form>
//     </div>
//   );
// };

// export default Signup;


import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSignupMutation } from "../features/api/newsApi"; // RTK Query hook

const Signup = () => {
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const navigate = useNavigate();

  // RTK Query mutation hook
  const [signup, { isLoading }] = useSignupMutation();

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    try {
      const res = await signup(form).unwrap();
      console.log("Signup Response:", res);

      if (!res.success) {
        setError(res.message || "Signup failed. Please try again.");
        return;
      }

      // 🟢 Success + Navigation
      setSuccess("Signup successful! Redirecting to login...");
      setTimeout(() => navigate("/login"), 1000);

    } catch (err) {
      console.error("RTK Error:", err);
      setError(err?.data?.message || "Something went wrong. Please try again.");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-900">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col space-y-3 w-full max-w-sm bg-gray-800 p-6 rounded-2xl shadow-lg"
      >
        <h2 className="text-2xl font-bold mb-4 text-center text-yellow-400">
          Signup
        </h2>

        <input
          name="username"
          placeholder="Username"
          value={form.username}
          onChange={handleChange}
          className="p-2 border border-gray-600 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
        />

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

        {/* 🔴 Error message */}
        {error && (
          <p className="text-red-400 text-sm text-center bg-red-900/40 p-2 rounded">
            {error}
          </p>
        )}

        {/* 🟢 Success message */}
        {success && (
          <p className="text-green-400 text-sm text-center bg-green-900/40 p-2 rounded">
            {success}
          </p>
        )}

        <button
          type="submit"
          disabled={isLoading}
          className={`${
            isLoading ? "bg-gray-600" : "bg-yellow-500 hover:bg-yellow-600"
          } text-black font-semibold p-2 rounded transition`}
        >
          {isLoading ? "Signing up..." : "Signup"}
        </button>
      </form>
    </div>
  );
};

export default Signup;
