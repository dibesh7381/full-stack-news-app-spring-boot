// import React, { useEffect, useState, useContext } from "react";
// import { AuthContext } from "../contexts/AuthContext";

// const AllNews = () => {
//   const { user } = useContext(AuthContext);
//   const [newsList, setNewsList] = useState([]);
//   const [commentInputs, setCommentInputs] = useState({});
//   const [editMode, setEditMode] = useState({});
//   const [editInputs, setEditInputs] = useState({});
//   const [commentsMap, setCommentsMap] = useState({});
//   const [showComments, setShowComments] = useState({});
//   const API_BASE = "http://localhost:8080/api/auth";

//   // ✅ Fetch all news (without likes/dislikes)
//   const fetchAllNews = async () => {
//     try {
//       const res = await fetch(`${API_BASE}/news/all`, {
//         credentials: "include",
//       });
//       const data = await res.json();
//       if (data.success && Array.isArray(data.data)) {
//         setNewsList(data.data);
//         // Fetch reactions for each news
//         data.data.forEach((n) => fetchReactions(n.id));
//       }
//     } catch (err) {
//       console.error("Error fetching news:", err);
//     }
//   };

//   // ✅ Fetch like/dislike data separately
//   const fetchReactions = async (newsId) => {
//     try {
//       const res = await fetch(`${API_BASE}/news/${newsId}/reactions`, {
//         credentials: "include",
//       });
//       const data = await res.json();
//       if (data.success) {
//         setNewsList((prev) =>
//           prev.map((n) =>
//             n.id === newsId
//               ? {
//                   ...n,
//                   likeCount: data.data.likeCount,
//                   dislikeCount: data.data.dislikeCount,
//                   userAction: data.data.userAction,
//                 }
//               : n
//           )
//         );
//       }
//     } catch (err) {
//       console.error("Error fetching reactions:", err);
//     }
//   };

//   // ✅ Toggle like/dislike
//   const handleReaction = async (newsId, action) => {
//     try {
//       const res = await fetch(`${API_BASE}/news/like-dislike`, {
//         method: "POST",
//         credentials: "include",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({ newsId, action }),
//       });
//       const data = await res.json();
//       if (data.success) {
//         setNewsList((prev) =>
//           prev.map((n) =>
//             n.id === newsId
//               ? {
//                   ...n,
//                   likeCount: data.data.likeCount,
//                   dislikeCount: data.data.dislikeCount,
//                   userAction: data.data.userAction,
//                 }
//               : n
//           )
//         );
//       }
//     } catch (err) {
//       console.error("Error updating reaction:", err);
//     }
//   };

//   // ✅ Fetch comments for one news
//   const fetchComments = async (newsId) => {
//     try {
//       const res = await fetch(`${API_BASE}/news/${newsId}/comments`, {
//         credentials: "include",
//       });
//       const data = await res.json();
//       if (data.success)
//         setCommentsMap((prev) => ({ ...prev, [newsId]: data.data }));
//     } catch (err) {
//       console.error("Error fetching comments:", err);
//     }
//   };

//   // ✅ Toggle comment visibility
//   const toggleComments = (newsId) => {
//     setShowComments((prev) => {
//       const isVisible = !prev[newsId];
//       if (isVisible && !commentsMap[newsId]) fetchComments(newsId);
//       return { ...prev, [newsId]: isVisible };
//     });
//   };

//   // ✅ Add comment
//   const addComment = async (newsId) => {
//     const content = commentInputs[newsId];
//     if (!content?.trim()) return;
//     try {
//       const res = await fetch(`${API_BASE}/news/comment`, {
//         method: "POST",
//         credentials: "include",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({ newsId, content }),
//       });
//       const data = await res.json();
//       if (data.success) {
//         fetchComments(newsId);
//         setCommentInputs((prev) => ({ ...prev, [newsId]: "" }));
//       }
//     } catch (err) {
//       console.error("Error adding comment:", err);
//     }
//   };

//   // ✅ Delete comment
//   const deleteComment = async (commentId, newsId) => {
//     if (!window.confirm("Delete this comment?")) return;
//     try {
//       const res = await fetch(`${API_BASE}/news/comment/delete/${commentId}`, {
//         method: "DELETE",
//         credentials: "include",
//       });
//       const data = await res.json();
//       if (data.success) fetchComments(newsId);
//     } catch (err) {
//       console.error("Error deleting comment:", err);
//     }
//   };

//   // ✅ Start editing
//   const startEdit = (commentId, currentText) => {
//     setEditMode((prev) => ({ ...prev, [commentId]: true }));
//     setEditInputs((prev) => ({ ...prev, [commentId]: currentText }));
//   };

//   // ✅ Cancel edit
//   const cancelEdit = (commentId) => {
//     setEditMode((prev) => ({ ...prev, [commentId]: false }));
//   };

//   // ✅ Save edited comment
//   const saveEdit = async (commentId, newsId) => {
//     const content = editInputs[commentId];
//     if (!content?.trim()) return;
//     try {
//       const res = await fetch(`${API_BASE}/news/comment/update`, {
//         method: "PUT",
//         credentials: "include",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({ commentId, content }),
//       });
//       const data = await res.json();
//       if (data.success) {
//         fetchComments(newsId);
//         setEditMode((prev) => ({ ...prev, [commentId]: false }));
//       }
//     } catch (err) {
//       console.error("Error updating comment:", err);
//     }
//   };

//   // ✅ Initial load
//   useEffect(() => {
//     fetchAllNews();
//   }, []);

//   const formatDate = (isoString) => {
//     const date = new Date(isoString);
//     return date.toLocaleString("en-IN", {
//       dateStyle: "medium",
//       timeStyle: "short",
//     });
//   };

//   // ✅ UI Rendering
//   return (
//     <div className="min-h-screen bg-gray-900 text-white p-5 md:p-10">
//       <div className="max-w-6xl mx-auto">
//         <h1 className="text-3xl font-bold text-yellow-400 text-center mb-8">
//           📰 All Latest News
//         </h1>

//         {newsList.map((news) => (
//           <div
//             key={news.id}
//             className="bg-gray-800 rounded-2xl p-5 mb-6 shadow-lg border border-gray-700"
//           >
//             {/* News Title & Date */}
//             <div className="flex justify-between items-center mb-2">
//               <h2 className="text-xl font-semibold text-yellow-400">
//                 {news.title}
//               </h2>
//               <p className="text-gray-400 text-sm">
//                 {formatDate(news.createdAt)}
//               </p>
//             </div>

//             {/* News Content */}
//             <p className="text-gray-300 mt-2">{news.content}</p>
//             <p className="text-gray-500 text-sm mt-2">
//               Published by:{" "}
//               <span className="text-yellow-400">{news.reporterName}</span>
//             </p>

//             {/* ✅ Like/Dislike Section */}
//             <div className="flex gap-4 mt-4 items-center">
//               <button
//                 className={`px-2 py-1 rounded ${
//                   news.userAction === "LIKE"
//                     ? "bg-green-500 text-black"
//                     : "bg-gray-700"
//                 }`}
//                 disabled={user?.role === "REPORTER"}
//                 onClick={() => handleReaction(news.id, "LIKE")}
//               >
//                 👍 {news.likeCount ?? 0}
//               </button>
//               <button
//                 className={`px-2 py-1 rounded ${
//                   news.userAction === "DISLIKE"
//                     ? "bg-red-500 text-black"
//                     : "bg-gray-700"
//                 }`}
//                 disabled={user?.role === "REPORTER"}
//                 onClick={() => handleReaction(news.id, "DISLIKE")}
//               >
//                 👎 {news.dislikeCount ?? 0}
//               </button>
//             </div>

//             {/* ✅ Comments Section */}
//             <div className="mt-5 border-t border-gray-700 pt-4">
//               <button
//                 onClick={() => toggleComments(news.id)}
//                 className="text-yellow-400 underline text-sm mb-2"
//               >
//                 {showComments[news.id] ? "Hide Comments" : "View Comments"}
//               </button>

//               {showComments[news.id] && (
//                 <>
//                   {user?.role !== "REPORTER" && (
//                     <div className="flex gap-2 mb-4">
//                       <input
//                         type="text"
//                         placeholder="Write a comment..."
//                         value={commentInputs[news.id] || ""}
//                         onChange={(e) =>
//                           setCommentInputs({
//                             ...commentInputs,
//                             [news.id]: e.target.value,
//                           })
//                         }
//                         className="flex-1 bg-transparent border-b border-gray-500 text-sm text-gray-200 placeholder-gray-400 focus:outline-none focus:border-yellow-400"
//                       />

//                       <button
//                         onClick={() => addComment(news.id)}
//                         className="px-3 py-1 bg-yellow-500 text-black rounded-full font-semibold text-sm"
//                       >
//                         Post
//                       </button>
//                     </div>
//                   )}

//                   <div className="max-h-40 overflow-y-auto flex flex-col space-y-3">
//                     {(commentsMap[news.id] || []).map((c) => (
//                       <div
//                         key={c.id}
//                         className="bg-gray-700 rounded-lg p-3 flex justify-between items-center"
//                       >
//                         <div className="flex-1">
//                           <p className="font-semibold text-yellow-400">
//                             {c.userName}
//                           </p>
//                           {editMode[c.id] ? (
//                             <div className="flex gap-2 mt-1">
//                               <input
//                                 type="text"
//                                 value={editInputs[c.id] || ""}
//                                 onChange={(e) =>
//                                   setEditInputs({
//                                     ...editInputs,
//                                     [c.id]: e.target.value,
//                                   })
//                                 }
//                                 className="flex-1 bg-transparent border-b border-gray-500 text-sm text-gray-200 focus:outline-none focus:border-yellow-400"
//                               />
//                               <button
//                                 onClick={() => saveEdit(c.id, news.id)}
//                                 className="text-green-400 text-sm"
//                               >
//                                 Save
//                               </button>
//                               <button
//                                 onClick={() => cancelEdit(c.id)}
//                                 className="text-red-400 text-sm"
//                               >
//                                 Cancel
//                               </button>
//                             </div>
//                           ) : (
//                             <p className="text-gray-200 text-sm mt-1">
//                               {c.content}
//                             </p>
//                           )}
//                         </div>

//                         {/* Edit/Delete for comment owner */}
//                         {!editMode[c.id] &&
//                           user &&
//                           user.name === c.userName && (
//                             <div className="flex flex-col gap-1 text-xs ml-3">
//                               <button
//                                 onClick={() => startEdit(c.id, c.content)}
//                                 className="text-blue-400 hover:underline"
//                               >
//                                 Edit
//                               </button>
//                               <button
//                                 onClick={() => deleteComment(c.id, news.id)}
//                                 className="text-red-400 hover:underline"
//                               >
//                                 Delete
//                               </button>
//                             </div>
//                           )}
//                       </div>
//                     ))}
//                   </div>
//                 </>
//               )}
//             </div>
//           </div>
//         ))}
//       </div>
//     </div>
//   );
// };

// export default AllNews;



import React, { useEffect, useState, useContext } from "react";
import { AuthContext } from "../contexts/AuthContext";

const AllNews = () => {
  const { user } = useContext(AuthContext);

  const [newsList, setNewsList] = useState([]);
  const [reactionsMap, setReactionsMap] = useState({}); // ✅ Separate reactions
  const [commentsMap, setCommentsMap] = useState({});
  const [commentInputs, setCommentInputs] = useState({});
  const [editMode, setEditMode] = useState({});
  const [editInputs, setEditInputs] = useState({});
  const [showComments, setShowComments] = useState({});

  const API_BASE = "http://localhost:8080/api/auth";


  // ✅ Fetch all news
const fetchAllNews = async () => {
  try {
    const res = await fetch(`${API_BASE}/news/all`, { credentials: "include" });
    const data = await res.json();
    if (data.success && Array.isArray(data.data)) {
      setNewsList(data.data);

      // ✅ Initialize reactionsMap with existing counts from backend
      const initialReactions = {};
      data.data.forEach((n) => {
        initialReactions[n.id] = {
          likeCount: n.likeCount ?? 0,
          dislikeCount: n.dislikeCount ?? 0,
          userAction: n.userAction ?? "NONE",
        };
      });
      setReactionsMap(initialReactions);

      // ✅ Then fetch latest reactions from server (updates async)
      data.data.forEach((n) => fetchReactions(n.id));
    }
  } catch (err) {
    console.error("Error fetching news:", err);
  }
};


  // ✅ Fetch reactions for one news
  const fetchReactions = async (newsId) => {
    try {
      const res = await fetch(`${API_BASE}/news/${newsId}/reactions`, {
        credentials: "include",
      });
      const data = await res.json();
      if (data.success) {
        setReactionsMap((prev) => ({
          ...prev,
          [newsId]: {
            likeCount: data.data.likeCount,
            dislikeCount: data.data.dislikeCount,
            userAction: data.data.userAction,
          },
        }));
      }
    } catch (err) {
      console.error("Error fetching reactions:", err);
    }
  };

  // ✅ Handle like/dislike
  const handleReaction = async (newsId, action) => {
    try {
      const res = await fetch(`${API_BASE}/news/like-dislike`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ newsId, action }),
      });
      const data = await res.json();
      if (data.success) {
        setReactionsMap((prev) => ({
          ...prev,
          [newsId]: {
            likeCount: data.data.likeCount,
            dislikeCount: data.data.dislikeCount,
            userAction: data.data.userAction,
          },
        }));
      }
    } catch (err) {
      console.error("Error updating reaction:", err);
    }
  };

  // ✅ Fetch comments for one news
  const fetchComments = async (newsId) => {
    try {
      const res = await fetch(`${API_BASE}/news/${newsId}/comments`, {
        credentials: "include",
      });
      const data = await res.json();
      if (data.success)
        setCommentsMap((prev) => ({ ...prev, [newsId]: data.data }));
    } catch (err) {
      console.error("Error fetching comments:", err);
    }
  };

  // ✅ Toggle comment visibility
  const toggleComments = (newsId) => {
    setShowComments((prev) => {
      const isVisible = !prev[newsId];
      if (isVisible && !commentsMap[newsId]) fetchComments(newsId);
      return { ...prev, [newsId]: isVisible };
    });
  };

  // ✅ Add comment
  const addComment = async (newsId) => {
    const content = commentInputs[newsId];
    if (!content?.trim()) return;
    try {
      const res = await fetch(`${API_BASE}/news/comment`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ newsId, content }),
      });
      const data = await res.json();
      if (data.success) {
        fetchComments(newsId);
        setCommentInputs((prev) => ({ ...prev, [newsId]: "" }));
      }
    } catch (err) {
      console.error("Error adding comment:", err);
    }
  };

  // ✅ Delete comment
  const deleteComment = async (commentId, newsId) => {
    if (!window.confirm("Delete this comment?")) return;
    try {
      const res = await fetch(`${API_BASE}/news/comment/delete/${commentId}`, {
        method: "DELETE",
        credentials: "include",
      });
      const data = await res.json();
      if (data.success) fetchComments(newsId);
    } catch (err) {
      console.error("Error deleting comment:", err);
    }
  };

  // ✅ Edit comment
  const startEdit = (commentId, currentText) => {
    setEditMode((prev) => ({ ...prev, [commentId]: true }));
    setEditInputs((prev) => ({ ...prev, [commentId]: currentText }));
  };

  const cancelEdit = (commentId) => {
    setEditMode((prev) => ({ ...prev, [commentId]: false }));
  };

  const saveEdit = async (commentId, newsId) => {
    const content = editInputs[commentId];
    if (!content?.trim()) return;
    try {
      const res = await fetch(`${API_BASE}/news/comment/update`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ commentId, content }),
      });
      const data = await res.json();
      if (data.success) {
        fetchComments(newsId);
        setEditMode((prev) => ({ ...prev, [commentId]: false }));
      }
    } catch (err) {
      console.error("Error updating comment:", err);
    }
  };

  // ✅ Initial load
  useEffect(() => {
    fetchAllNews();
  }, []);

  // ✅ Format date
  const formatDate = (isoString) => {
    const date = new Date(isoString);
    return date.toLocaleString("en-IN", {
      dateStyle: "medium",
      timeStyle: "short",
    });
  };

  // ✅ UI Rendering
  return (
    <div className="min-h-screen bg-gray-900 text-white p-5 md:p-10">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-bold text-yellow-400 text-center mb-8">
          📰 All Latest News
        </h1>

        {newsList.map((news) => {
          const reaction = reactionsMap[news.id] || {
            likeCount: 0,
            dislikeCount: 0,
            userAction: "NONE",
          };

          return (
            <div
              key={news.id}
              className="bg-gray-800 rounded-2xl p-5 mb-6 shadow-lg border border-gray-700"
            >
              {/* Title & Date */}
              <div className="flex justify-between items-center mb-2">
                <h2 className="text-xl font-semibold text-yellow-400">
                  {news.title}
                </h2>
                <p className="text-gray-400 text-sm">
                  {formatDate(news.createdAt)}
                </p>
              </div>

              <p className="text-gray-300 mt-2">{news.content}</p>
              <p className="text-gray-500 text-sm mt-2">
                Published by:{" "}
                <span className="text-yellow-400">{news.reporterName}</span>
              </p>

              {/* ✅ Like/Dislike */}
              <div className="flex gap-4 mt-4 items-center">
                <button
                  className={`px-2 py-1 rounded ${
                    reaction.userAction === "LIKE"
                      ? "bg-green-500 text-black"
                      : "bg-gray-700"
                  }`}
                  disabled={user?.role === "REPORTER"}
                  onClick={() => handleReaction(news.id, "LIKE")}
                >
                  👍 {reaction.likeCount}
                </button>
                <button
                  className={`px-2 py-1 rounded ${
                    reaction.userAction === "DISLIKE"
                      ? "bg-red-500 text-black"
                      : "bg-gray-700"
                  }`}
                  disabled={user?.role === "REPORTER"}
                  onClick={() => handleReaction(news.id, "DISLIKE")}
                >
                  👎 {reaction.dislikeCount}
                </button>
              </div>

              {/* ✅ Comments */}
              <div className="mt-5 border-t border-gray-700 pt-4">
                <button
                  onClick={() => toggleComments(news.id)}
                  className="text-yellow-400 underline text-sm mb-2"
                >
                  {showComments[news.id] ? "Hide Comments" : "View Comments"}
                </button>

                {showComments[news.id] && (
                  <>
                    {user?.role !== "REPORTER" && (
                      <div className="flex gap-2 mb-4">
                        <input
                          type="text"
                          placeholder="Write a comment..."
                          value={commentInputs[news.id] || ""}
                          onChange={(e) =>
                            setCommentInputs({
                              ...commentInputs,
                              [news.id]: e.target.value,
                            })
                          }
                          className="flex-1 bg-transparent border-b border-gray-500 text-sm text-gray-200 placeholder-gray-400 focus:outline-none focus:border-yellow-400"
                        />
                        <button
                          onClick={() => addComment(news.id)}
                          className="px-3 py-1 bg-yellow-500 text-black rounded-full font-semibold text-sm"
                        >
                          Post
                        </button>
                      </div>
                    )}

                    <div className="max-h-40 overflow-y-auto flex flex-col space-y-3">
                      {(commentsMap[news.id] || []).map((c) => (
                        <div
                          key={c.id}
                          className="bg-gray-700 rounded-lg p-3 flex justify-between items-center"
                        >
                          <div className="flex-1">
                            <p className="font-semibold text-yellow-400">
                              {c.userName}
                            </p>
                            {editMode[c.id] ? (
                              <div className="flex gap-2 mt-1">
                                <input
                                  type="text"
                                  value={editInputs[c.id] || ""}
                                  onChange={(e) =>
                                    setEditInputs({
                                      ...editInputs,
                                      [c.id]: e.target.value,
                                    })
                                  }
                                  className="flex-1 bg-transparent border-b border-gray-500 text-sm text-gray-200 focus:outline-none focus:border-yellow-400"
                                />
                                <button
                                  onClick={() => saveEdit(c.id, news.id)}
                                  className="text-green-400 text-sm"
                                >
                                  Save
                                </button>
                                <button
                                  onClick={() => cancelEdit(c.id)}
                                  className="text-red-400 text-sm"
                                >
                                  Cancel
                                </button>
                              </div>
                            ) : (
                              <p className="text-gray-200 text-sm mt-1">
                                {c.content}
                              </p>
                            )}
                          </div>

                          {!editMode[c.id] &&
                            user &&
                            user.name === c.userName && (
                              <div className="flex flex-col gap-1 text-xs ml-3">
                                <button
                                  onClick={() =>
                                    startEdit(c.id, c.content)
                                  }
                                  className="text-blue-400 hover:underline"
                                >
                                  Edit
                                </button>
                                <button
                                  onClick={() =>
                                    deleteComment(c.id, news.id)
                                  }
                                  className="text-red-400 hover:underline"
                                >
                                  Delete
                                </button>
                              </div>
                            )}
                        </div>
                      ))}
                    </div>
                  </>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default AllNews;

