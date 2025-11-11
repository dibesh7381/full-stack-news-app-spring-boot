import React, { useEffect, useState } from "react";

const AllNews = () => {
  const [newsList, setNewsList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [userRole, setUserRole] = useState(null);
  const API_BASE = "http://localhost:8080/api/auth";

  // ✅ Fetch user profile to get role
  const fetchUserProfile = async () => {
    try {
      const res = await fetch(`${API_BASE}/profile`, {
        method: "GET",
        credentials: "include",
      });
      const data = await res.json();
      if (data.success && data.data) {
        setUserRole(data.data.role);
      }
    } catch (err) {
      console.error("Error fetching profile:", err);
    }
  };

  // ✅ Fetch all news
  const fetchAllNews = async () => {
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/news/all`, {
        method: "GET",
        credentials: "include",
      });
      const data = await res.json();

      if (data.success && Array.isArray(data.data)) {
        setNewsList(data.data);
      } else if (Array.isArray(data)) {
        setNewsList(data);
      } else {
        setNewsList([]);
      }
    } catch (err) {
      console.error("Error fetching all news:", err);
      setNewsList([]);
    } finally {
      setLoading(false);
    }
  };

  // ✅ Like / Dislike functionality
  const handleLikeDislike = async (newsId, action) => {
    if (userRole === "REPORTER") return; // reporters can’t interact

    try {
      const res = await fetch(`${API_BASE}/news/like-dislike`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ newsId, action }),
      });

      const data = await res.json();
      if (data.success && data.data) {
        setNewsList((prev) =>
          prev.map((item) =>
            item.id === newsId
              ? {
                  ...item,
                  likeCount: data.data.likeCount,
                  dislikeCount: data.data.dislikeCount,
                  userAction: data.data.userAction,
                }
              : item
          )
        );
      }
    } catch (err) {
      console.error("Error liking/disliking:", err);
    }
  };

  useEffect(() => {
    fetchUserProfile();
    fetchAllNews();
  }, []);

  return (
    <div className="min-h-screen bg-gray-900 text-white p-5 md:p-10">
      <div className="max-w-6xl mx-auto">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-8">
          <h1 className="text-3xl font-bold text-yellow-400 text-center md:text-left">
            📰 All Latest News
          </h1>
          <button
            onClick={fetchAllNews}
            disabled={loading}
            className="mt-3 md:mt-0 px-4 py-2 bg-yellow-500 hover:bg-yellow-600 text-black rounded-lg font-semibold shadow-md transition"
          >
            {loading ? "Refreshing..." : "🔄 Refresh"}
          </button>
        </div>

        {loading && (
          <p className="text-gray-400 text-center animate-pulse">
            Loading news...
          </p>
        )}

        {!loading && newsList.length === 0 && (
          <p className="text-gray-400 text-center">
            No news available right now.
          </p>
        )}

        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {newsList.map((news) => (
            <div
              key={news.id}
              className="bg-gray-800 border border-gray-700 rounded-2xl p-5 shadow-lg hover:shadow-yellow-500/20 hover:-translate-y-1 transition-all duration-300"
            >
              <h3 className="text-xl font-semibold text-yellow-400 mb-2 line-clamp-1">
                {news.title}
              </h3>
              <p className="text-gray-300 text-sm mb-4 line-clamp-3">
                {news.content}
              </p>

              <div className="border-t border-gray-700 pt-3 text-sm text-gray-400 space-y-1">
                <p>
                  📅 <span className="text-gray-300">Published on:</span>{" "}
                  {news.createdAt
                    ? new Date(news.createdAt).toLocaleDateString("en-IN", {
                        day: "2-digit",
                        month: "short",
                        year: "numeric",
                      })
                    : "Unknown"}
                </p>
                <p>
                  🧑‍💼 <span className="text-gray-300">Published by:</span>{" "}
                  {news.reporterName || "Unknown Reporter"}
                </p>
              </div>

              {/* ✅ Like / Dislike Buttons (visible to everyone but disabled for reporter) */}
              <div className="flex justify-between items-center mt-4 text-sm">
                <button
                  onClick={() => handleLikeDislike(news.id, "LIKE")}
                  disabled={userRole === "REPORTER"}
                  className={`px-3 py-1 rounded-lg font-semibold transition ${
                    news.userAction === "LIKE"
                      ? "bg-green-600 text-white"
                      : "bg-gray-700 hover:bg-green-700"
                  } ${userRole === "REPORTER" ? "opacity-50 cursor-not-allowed" : ""}`}
                >
                  👍 {news.likeCount || 0}
                </button>

                <button
                  onClick={() => handleLikeDislike(news.id, "DISLIKE")}
                  disabled={userRole === "REPORTER"}
                  className={`px-3 py-1 rounded-lg font-semibold transition ${
                    news.userAction === "DISLIKE"
                      ? "bg-red-600 text-white"
                      : "bg-gray-700 hover:bg-red-700"
                  } ${userRole === "REPORTER" ? "opacity-50 cursor-not-allowed" : ""}`}
                >
                  👎 {news.dislikeCount || 0}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AllNews;




