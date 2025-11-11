import React, { useEffect, useState } from "react";

const ReporterDashboard = () => {
  const [newsList, setNewsList] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [editId, setEditId] = useState(null);
  const [loading, setLoading] = useState(false);

  const API_BASE = "http://localhost:8080/api/auth";

  // 🟢 --- API FUNCTIONS ---

  // Fetch all reporter's news
  const fetchMyNews = async () => {
    try {
      const res = await fetch(`${API_BASE}/news/my-news`, {
        method: "GET",
        credentials: "include",
      });
      const data = await res.json();
      if (data.success && Array.isArray(data.data)) setNewsList(data.data);
      else if (Array.isArray(data)) setNewsList(data);
    } catch (err) {
      console.error("Error fetching news:", err);
    }
  };

  // Add new news
  const addNews = async (payload) => {
    const res = await fetch(`${API_BASE}/news/add`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(payload),
    });
    return res.json();
  };

  // Update existing news
  const updateNews = async (id, payload) => {
    const res = await fetch(`${API_BASE}/news/update/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(payload),
    });
    return res.json();
  };

  // Delete news
  const deleteNews = async (id) => {
    const res = await fetch(`${API_BASE}/news/delete/${id}`, {
      method: "DELETE",
      credentials: "include",
    });
    return res.json();
  };

  // 🟢 --- EVENT HANDLERS ---

  useEffect(() => {
    fetchMyNews();
  }, []);

  // Add or update handler
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const payload = { title, content };

    try {
      let data;
      if (editId) data = await updateNews(editId, payload);
      else data = await addNews(payload);

      console.log("Add/Update Response:", data);

      if (data.success || data.id) {
        setTitle("");
        setContent("");
        setEditId(null);
        await fetchMyNews();
      } else {
        alert(data.message || "Something went wrong!");
      }
    } catch (err) {
      console.error("Error saving news:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = async (news) => {
    // Optionally fetch single news if needed
    setTitle(news.title);
    setContent(news.content);
    setEditId(news.id);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this news?")) return;
    const data = await deleteNews(id);
    if (data.success) {
      await fetchMyNews();
    } else {
      alert(data.message || "Failed to delete!");
    }
  };

  // 🟢 --- UI ---
  return (
    <div className="p-6 max-w-4xl mx-auto text-white">
      <h1 className="text-3xl font-bold mb-6 text-center text-yellow-400">
        📰 Reporter Dashboard
      </h1>

      {/* Form */}
      <form
        onSubmit={handleSubmit}
        className="mb-8 p-5 rounded-2xl bg-gray-900 shadow-xl border border-gray-700 backdrop-blur-sm"
      >
        <input
          type="text"
          placeholder="Enter news title..."
          className="w-full p-3 mb-3 rounded bg-gray-800 border border-gray-700 text-white focus:ring-2 focus:ring-yellow-500 outline-none"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <textarea
          placeholder="Enter news content..."
          className="w-full p-3 mb-3 rounded bg-gray-800 border border-gray-700 text-white h-28 focus:ring-2 focus:ring-yellow-500 outline-none"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        />
        <div className="flex items-center gap-3">
          <button
            type="submit"
            disabled={loading}
            className="px-5 py-2.5 rounded-lg bg-yellow-500 hover:bg-yellow-600 text-black font-semibold"
          >
            {loading ? "Saving..." : editId ? "Update News" : "Add News"}
          </button>
          {editId && (
            <button
              type="button"
              onClick={() => {
                setEditId(null);
                setTitle("");
                setContent("");
              }}
              className="px-5 py-2.5 rounded-lg bg-gray-600 hover:bg-gray-700 text-white font-semibold"
            >
              Cancel
            </button>
          )}
        </div>
      </form>

      {/* News Cards */}
      <h2 className="text-2xl font-semibold mb-4 text-yellow-300">
        My Uploaded News
      </h2>

      {newsList.length === 0 ? (
        <p className="text-gray-400 text-center">No news added yet.</p>
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {newsList.map((news) => (
            <div
              key={news.id}
              className="bg-gray-800 p-5 rounded-xl border border-gray-700 shadow-lg hover:shadow-yellow-500/20 transition-all duration-300"
            >
              <h3 className="text-lg font-bold text-yellow-400">
                {news.title}
              </h3>
              <p className="text-gray-300 mt-2 text-sm">{news.content}</p>
              <div className="flex justify-end mt-4 gap-2">
                <button
                  onClick={() => handleEdit(news)}
                  className="px-3 py-1.5 bg-yellow-500 hover:bg-yellow-600 text-black rounded-md text-sm font-semibold"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDelete(news.id)}
                  className="px-3 py-1.5 bg-red-600 hover:bg-red-700 text-white rounded-md text-sm font-semibold"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ReporterDashboard;




