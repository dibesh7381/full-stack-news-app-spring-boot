// import React, { useContext, useState } from "react";
// import { AuthContext } from "../contexts/AuthContext";

// const BecomeReporter = () => {
//   const { user, setUser } = useContext(AuthContext);
//   const [loading, setLoading] = useState(false);
//   const [message, setMessage] = useState("");

//   const handleBecomeReporter = async () => {
//     setLoading(true);
//     setMessage("");
//     try {
//       const res = await fetch("http://localhost:8080/api/auth/become-reporter", {
//         method: "POST",
//         credentials: "include",
//       });
//       const data = await res.json();

//       if (!data.success) throw new Error(data.message || "Failed to update role");

//       // ✅ Update context user
//       setUser(prev => ({ ...prev, role: data.data.role }));
//       setMessage("You are now a REPORTER!");
//     } catch (err) {
//       setMessage(err.message);
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="flex justify-center items-center h-screen p-4">
//       <div className="bg-white rounded shadow-lg p-6 w-full max-w-sm text-center">
//         <h2 className="text-2xl font-bold mb-4">Become a Reporter</h2>
//         <p className="mb-4">Current role: <span className="font-semibold">{user?.role}</span></p>
//         <button
//           onClick={handleBecomeReporter}
//           disabled={loading || user?.role === "REPORTER"}
//           className={`w-full py-2 rounded text-white font-semibold ${
//             user?.role === "REPORTER" ? "bg-gray-400 cursor-not-allowed" : "bg-blue-500 hover:bg-blue-600"
//           }`}
//         >
//           {loading ? "Updating..." : user?.role === "REPORTER" ? "Already a Reporter" : "Become Reporter"}
//         </button>
//         {message && <p className="mt-4 text-green-600">{message}</p>}
//       </div>
//     </div>
//   );
// };

// export default BecomeReporter;

import { useBecomeReporterMutation, useProfileQuery } from "../features/api/newsApi";

const BecomeReporter = () => {
  const { data: profileData, isLoading: profileLoading } = useProfileQuery();
  const user = profileData?.data;

  const [becomeReporter, { isLoading, isSuccess, error }] =
    useBecomeReporterMutation();

  const handleBecomeReporter = async () => {
    try {
      await becomeReporter().unwrap();
      // RTK Query automatically refetches /profile
    } catch (err) {
      console.log("Error:", err);
    }
  };

  if (profileLoading) return <p>Loading...</p>;

  return (
    <div className="flex justify-center items-center h-screen p-4">
      <div className="bg-white rounded shadow-lg p-6 w-full max-w-sm text-center">
        <h2 className="text-2xl font-bold mb-4">Become a Reporter</h2>

        <p className="mb-4">
          Current role: <span className="font-semibold">{user?.role}</span>
        </p>

        <button
          onClick={handleBecomeReporter}
          disabled={isLoading || user?.role === "REPORTER"}
          className={`w-full py-2 rounded text-white font-semibold ${
            user?.role === "REPORTER"
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-blue-500 hover:bg-blue-600"
          }`}
        >
          {isLoading
            ? "Updating..."
            : user?.role === "REPORTER"
            ? "Already a Reporter"
            : "Become Reporter"}
        </button>

        {isSuccess && (
          <p className="mt-4 text-green-600">You are now a REPORTER!</p>
        )}

        {error && (
          <p className="mt-4 text-red-600">
            {error?.data?.message || "Failed to update role"}
          </p>
        )}
      </div>
    </div>
  );
};

export default BecomeReporter;
