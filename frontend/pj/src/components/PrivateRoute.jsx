// import { useContext } from "react";
// import { Navigate } from "react-router-dom";
// import { AuthContext } from "../contexts/AuthContext";

// const PrivateRoute = ({ children }) => {
//   const { user, loading } = useContext(AuthContext);

//   if (loading) {
//     // ✅ optional: loading state while fetching profile
//     return <div className="text-center mt-10">Loading...</div>;
//   }

//   // ✅ If not logged in, redirect to login
//   if (!user) {
//     return <Navigate to="/login" replace />;
//   }

//   // ✅ If logged in, show the requested component
//   return children;
// };

// export default PrivateRoute;

import { Navigate } from "react-router-dom";
import { useProfileQuery } from "../features/api/newsApi";

const PrivateRoute = ({ children }) => {
  const { data, isLoading, isError } = useProfileQuery();

  // ⏳ Loading profile (checking login)
  if (isLoading) {
    return <div className="text-center mt-10 text-white">Loading...</div>;
  }

  // ❌ If profile API failed => user NOT logged in
  if (isError || !data?.data) {
    return <Navigate to="/login" replace />;
  }

  // 🟢 Logged in -> show content
  return children;
};

export default PrivateRoute;
