import { useState } from "react";
import {
  useGetReactionsQuery,
  useToggleReactionMutation,
  useGetCommentsQuery,
  useAddCommentMutation,
  useDeleteCommentMutation,
  useUpdateCommentMutation,
} from "../features/api/newsApi";
import { skipToken } from "@reduxjs/toolkit/query";

const NewsCard = ({ news, user }) => {
  const [showComments, setShowComments] = useState(false);
  const [commentInput, setCommentInput] = useState("");
  const [editMode, setEditMode] = useState({});
  const [editInputs, setEditInputs] = useState({});

  // ⭐ Reactions
  const { data: reactionsData } = useGetReactionsQuery(news.id);
  const reactions = reactionsData?.data || {
    likeCount: 0,
    dislikeCount: 0,
    userAction: "NONE",
  };

  // ⭐ Comments -> Fetch only when opened
  const shouldFetch = showComments ? news.id : skipToken;
  const { data: commentsData } = useGetCommentsQuery(shouldFetch);

  const comments = commentsData?.data || [];

  // ⭐ Mutations
  const [toggleReaction] = useToggleReactionMutation();
  const [addComment] = useAddCommentMutation();
  const [deleteComment] = useDeleteCommentMutation();
  const [updateComment] = useUpdateCommentMutation();

  const handleReaction = (action) => {
    toggleReaction({ newsId: news.id, action });
  };

  const handleAddComment = async () => {
    if (!commentInput.trim()) return;

    await addComment({ newsId: news.id, content: commentInput }).unwrap();
    setCommentInput(""); // UI reset, data auto refetch because invalidatesTags
  };

  const handleDeleteComment = async (id) => {
    if (!confirm("Delete this comment?")) return;
    await deleteComment(id).unwrap();
  };

  const handleSaveEdit = async (commentId) => {
    await updateComment({
      commentId,
      content: editInputs[commentId],
    }).unwrap();

    setEditMode((p) => ({ ...p, [commentId]: false }));
  };

  const formatDate = (iso) =>
    new Date(iso).toLocaleString("en-IN", {
      dateStyle: "medium",
      timeStyle: "short",
    });

  return (
    <div className="bg-gray-800 rounded-2xl p-5 mb-6 shadow-lg border border-gray-700">
      <div className="flex justify-between items-center mb-2">
        <h2 className="text-xl font-semibold text-yellow-400">{news.title}</h2>
        <p className="text-gray-400 text-sm">{formatDate(news.createdAt)}</p>
      </div>

      <p className="text-gray-300 mt-2">{news.content}</p>
      <p className="text-gray-500 text-sm mt-2">
        Published by:{" "}
        <span className="text-yellow-400">{news.reporterName}</span>
      </p>

      {/* LIKE / DISLIKE */}
      <div className="flex gap-4 mt-4 items-center">
        <button
          className={`px-2 py-1 rounded ${
            reactions.userAction === "LIKE"
              ? "bg-green-500 text-black"
              : "bg-gray-700"
          }`}
          disabled={user?.role === "REPORTER"}
          onClick={() => handleReaction("LIKE")}
        >
          👍 {reactions.likeCount}
        </button>

        <button
          className={`px-2 py-1 rounded ${
            reactions.userAction === "DISLIKE"
              ? "bg-red-500 text-black"
              : "bg-gray-700"
          }`}
          disabled={user?.role === "REPORTER"}
          onClick={() => handleReaction("DISLIKE")}
        >
          👎 {reactions.dislikeCount}
        </button>
      </div>

      {/* COMMENTS */}
      <div className="mt-5 border-t border-gray-700 pt-4">
        <button
          onClick={() => setShowComments((v) => !v)}
          className="text-yellow-400 underline text-sm mb-2"
        >
          {showComments ? "Hide Comments" : "View Comments"}
        </button>

        {showComments && user && (
          <div>
            {/* Comment input */}
            {user?.role !== "REPORTER" && (
              <div className="flex gap-2 mb-4">
                <input
                  type="text"
                  placeholder="Write a comment..."
                  value={commentInput}
                  onChange={(e) => setCommentInput(e.target.value)}
                  className="flex-1 bg-transparent border-b border-gray-500"
                />
                <button
                  onClick={handleAddComment}
                  className="px-3 py-1 bg-yellow-500 text-black rounded-full"
                >
                  Post
                </button>
              </div>
            )}

            {/* Comments List */}
            <div className="max-h-40 overflow-y-auto flex flex-col space-y-3">
              {comments.map((c) => (
                <div
                  key={c.id}
                  className="bg-gray-700 rounded-lg p-3 flex justify-between"
                >
                  <div className="flex-1">
                    <p className="font-semibold text-yellow-400">
                      {c.userName}
                    </p>

                    {/* Edit Mode */}
                    {editMode[c.id] ? (
                      <div className="flex gap-2 mt-1">
                        <input
                          type="text"
                          value={editInputs[c.id] || ""}
                          onChange={(e) =>
                            setEditInputs((p) => ({
                              ...p,
                              [c.id]: e.target.value,
                            }))
                          }
                          className="flex-1 bg-transparent border-b border-gray-500"
                        />
                        <button
                          onClick={() => handleSaveEdit(c.id)}
                          className="text-green-400 text-sm"
                        >
                          Save
                        </button>
                        <button
                          onClick={() =>
                            setEditMode((p) => ({ ...p, [c.id]: false }))
                          }
                          className="text-red-400 text-sm"
                        >
                          Cancel
                        </button>
                      </div>
                    ) : (
                      <p className="text-gray-200 text-sm mt-1">{c.content}</p>
                    )}
                  </div>

                  {/* Edit / Delete */}
                  {!editMode[c.id] && user?.name === c.userName && (
                    <div className="flex flex-col gap-1 ml-3 text-xs">
                      <button
                        onClick={() => {
                          setEditMode((p) => ({ ...p, [c.id]: true }));
                          setEditInputs((p) => ({ ...p, [c.id]: c.content }));
                        }}
                        className="text-blue-400 hover:underline"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDeleteComment(c.id)}
                        className="text-red-400 hover:underline"
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default NewsCard;



