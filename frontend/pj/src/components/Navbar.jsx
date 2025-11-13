// import { useState, useContext } from "react";
// import { Link, useNavigate } from "react-router-dom";
// import { AuthContext } from "../contexts/AuthContext";
// import { Menu, X } from "lucide-react";

// const Navbar = () => {
//   const { user, logout } = useContext(AuthContext);
//   const navigate = useNavigate();
//   const [isOpen, setIsOpen] = useState(false);

//   const handleLogout = async () => {
//     await logout();
//     navigate("/login");
//     setIsOpen(false);
//   };

//   return (
//     <nav className="w-full bg-blue-600 text-white p-4 flex justify-between items-center relative shadow-md">
//       {/* ✅ Logo */}
//       <Link to="/" className="font-bold text-xl z-50 relative">
//         NewsApp
//       </Link>

//       {/* ✅ Desktop Links */}
//       <div className="hidden md:flex space-x-3 items-center">
//         <Link to="/" className="px-3 py-1 rounded bg-blue-500">Home</Link>
//         <Link to="/all-news" className="px-3 py-1 rounded bg-indigo-500">All News</Link>
//         <Link to="/profile" className="px-3 py-1 rounded bg-green-500">Profile</Link>
//         <Link to="/become-reporter" className="px-3 py-1 rounded bg-purple-500">Become Reporter</Link>

//         {/* ✅ Reporter Dashboard visible only for REPORTER */}
//         {user?.role === "REPORTER" && (
//           <Link to="/reporter-dashboard" className="px-3 py-1 rounded bg-pink-500">
//             Dashboard
//           </Link>
//         )}

//         {user ? (
//           <button onClick={handleLogout} className="px-3 py-1 rounded bg-red-500">Logout</button>
//         ) : (
//           <>
//             <Link to="/login" className="px-3 py-1 rounded bg-yellow-500">Login</Link>
//             <Link to="/signup" className="px-3 py-1 rounded bg-green-500">Signup</Link>
//           </>
//         )}
//       </div>

//       {/* ✅ Hamburger Button (Mobile) */}
//       <button
//         className="md:hidden p-2 relative z-50"
//         onClick={() => setIsOpen(!isOpen)}
//       >
//         {isOpen ? <X size={28} /> : <Menu size={28} />}
//       </button>

//       {/* ✅ Mobile Drawer */}
//       <div
//         className={`fixed top-0 right-0 h-full w-64 bg-blue-700 text-white p-6 transform transition-transform duration-300 ease-in-out z-40
//           ${isOpen ? "translate-x-0" : "translate-x-full"}`}
//       >
//         <div className="flex flex-col space-y-4 mt-10">
//           <Link to="/" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-blue-500">
//             Home
//           </Link>
//           <Link to="/all-news" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-indigo-500">
//             All News
//           </Link>
//           <Link to="/profile" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-green-500">
//             Profile
//           </Link>
//           <Link to="/become-reporter" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-purple-500">
//             Become Reporter
//           </Link>

//           {/* ✅ Reporter Dashboard visible only for REPORTER */}
//           {user?.role === "REPORTER" && (
//             <Link to="/reporter-dashboard" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-pink-500">
//               Reporter Dashboard
//             </Link>
//           )}

//           {user ? (
//             <button onClick={handleLogout} className="px-3 py-2 rounded bg-red-500">
//               Logout
//             </button>
//           ) : (
//             <>
//               <Link to="/login" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-yellow-500">
//                 Login
//               </Link>
//               <Link to="/signup" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-green-500">
//                 Signup
//               </Link>
//             </>
//           )}
//         </div>
//       </div>

//       {/* ✅ Overlay for drawer */}
//       {isOpen && (
//         <div
//           className="fixed inset-0 bg-black opacity-50 z-30"
//           onClick={() => setIsOpen(false)}
//         ></div>
//       )}
//     </nav>
//   );
// };

// export default Navbar;


import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Menu, X } from "lucide-react";
import {
  useProfileQuery,
  useLogoutMutation,
  newsApi,
} from "../features/api/newsApi";
import { useDispatch } from "react-redux";

const Navbar = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [isOpen, setIsOpen] = useState(false);

  // 🟢 Auto fetch logged-in user
  const { data: profileData } = useProfileQuery();
  const user = profileData?.data || null;

  // 🔴 Logout API
  const [logoutApi] = useLogoutMutation();

const handleLogout = async () => {
  try {
    await logoutApi(); // delete cookie on backend

    // 🧹 RTK Query FULL cache reset → profile immediately becomes NULL
    dispatch(newsApi.util.resetApiState());

    navigate("/login");
    setIsOpen(false);
  } catch (err) {
    console.error("Logout error:", err);
  }
};


  return (
    <nav className="w-full bg-blue-600 text-white p-4 flex justify-between items-center relative shadow-md">
      {/* Logo */}
      <Link to="/" className="font-bold text-xl z-50 relative">
        NewsApp
      </Link>

      {/* Desktop Links */}
      <div className="hidden md:flex space-x-3 items-center">
        <Link to="/" className="px-3 py-1 rounded bg-blue-500">Home</Link>
        <Link to="/all-news" className="px-3 py-1 rounded bg-indigo-500">All News</Link>
        <Link to="/profile" className="px-3 py-1 rounded bg-green-500">Profile</Link>
        <Link to="/become-reporter" className="px-3 py-1 rounded bg-purple-500">
          Become Reporter
        </Link>

        {/* Reporter Dashboard */}
        {user?.role === "REPORTER" && (
          <Link to="/reporter-dashboard" className="px-3 py-1 rounded bg-pink-500">
            Dashboard
          </Link>
        )}

        {user ? (
          <button onClick={handleLogout} className="px-3 py-1 rounded bg-red-500">
            Logout
          </button>
        ) : (
          <>
            <Link to="/login" className="px-3 py-1 rounded bg-yellow-500">Login</Link>
            <Link to="/signup" className="px-3 py-1 rounded bg-green-500">Signup</Link>
          </>
        )}
      </div>

      {/* Hamburger */}
      <button
        className="md:hidden p-2 relative z-50"
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? <X size={28} /> : <Menu size={28} />}
      </button>

      {/* Mobile Drawer */}
      <div
        className={`fixed top-0 right-0 h-full w-64 bg-blue-700 text-white p-6 transform transition-transform duration-300 ease-in-out z-40
          ${isOpen ? "translate-x-0" : "translate-x-full"}`}
      >
        <div className="flex flex-col space-y-4 mt-10">
          <Link to="/" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-blue-500">
            Home
          </Link>
          <Link to="/all-news" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-indigo-500">
            All News
          </Link>
          <Link to="/profile" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-green-500">
            Profile
          </Link>
          <Link to="/become-reporter" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-purple-500">
            Become Reporter
          </Link>

          {/* Reporter Dashboard */}
          {user?.role === "REPORTER" && (
            <Link to="/reporter-dashboard" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-pink-500">
              Reporter Dashboard
            </Link>
          )}

          {user ? (
            <button onClick={handleLogout} className="px-3 py-2 rounded bg-red-500">
              Logout
            </button>
          ) : (
            <>
              <Link to="/login" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-yellow-500">
                Login
              </Link>
              <Link to="/signup" onClick={() => setIsOpen(false)} className="px-3 py-2 rounded bg-green-500">
                Signup
              </Link>
            </>
          )}
        </div>
      </div>

      {/* Overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black opacity-50 z-30"
          onClick={() => setIsOpen(false)}
        />
      )}
    </nav>
  );
};

export default Navbar;






