import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

export const newsApi = createApi({
  reducerPath: "newsApi",

  baseQuery: fetchBaseQuery({
    baseUrl: "http://localhost:8080/api/auth",
    credentials: "include",
  }),

  tagTypes: ["Profile", "News", "MyNews", "Reactions", "Comments", "Home"],

  endpoints: (builder) => ({
    signup: builder.mutation({
      query: (body) => ({
        url: "/signup",
        method: "POST",
        body,
      }),
    }),

    // login: builder.mutation({
    //   query: (body) => ({
    //     url: "/login",
    //     method: "POST",
    //     body,
    //   }),
    // }),

    login: builder.mutation({
      query: (body) => ({
        url: "/login",
        method: "POST",
        body,
      }),
      invalidatesTags: ["Profile"], // ⭐ This fixes everything
    }),

    logout: builder.mutation({
      query: () => ({
        url: "/logout",
        method: "POST",
      }),
    }),

    profile: builder.query({
      query: () => "/profile",
      providesTags: ["Profile"],
    }),

    home: builder.query({
      query: () => "/home",
      providesTags: ["Home"],
    }),

    becomeReporter: builder.mutation({
      query: () => ({
        url: "/become-reporter",
        method: "POST",
      }),
      invalidatesTags: ["Profile"],
    }),

    addNews: builder.mutation({
      query: (body) => ({
        url: "/news/add",
        method: "POST",
        body,
      }),
      invalidatesTags: ["News", "MyNews"],
    }),

    myNews: builder.query({
      query: () => "/news/my-news",
      providesTags: ["MyNews"],
    }),

    updateNews: builder.mutation({
      query: ({ id, ...body }) => ({
        url: `/news/update/${id}`,
        method: "PUT",
        body,
      }),
      invalidatesTags: ["News", "MyNews"],
    }),

    deleteNews: builder.mutation({
      query: (id) => ({
        url: `/news/delete/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["MyNews"],
    }),

    allNews: builder.query({
      query: () => "/news/all",
      providesTags: ["News"],
    }),

    toggleReaction: builder.mutation({
      query: (body) => ({
        url: "/news/like-dislike",
        method: "POST",
        body,
      }),
      invalidatesTags: ["Reactions", "News"],
    }),

    getReactions: builder.query({
      query: (newsId) => `/news/${newsId}/reactions`,
      providesTags: ["Reactions"],
    }),

    addComment: builder.mutation({
      query: (body) => ({
        url: "/news/comment",
        method: "POST",
        body,
      }),
      invalidatesTags: ["Comments"],
    }),

    getComments: builder.query({
      query: (newsId) => `/news/${newsId}/comments`,
      providesTags: ["Comments"],
    }),

    deleteComment: builder.mutation({
      query: (id) => ({
        url: `/news/comment/delete/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Comments"],
    }),

    updateComment: builder.mutation({
      query: (body) => ({
        url: "/news/comment/update",
        method: "PUT",
        body,
      }),
      invalidatesTags: ["Comments"],
    }),
  }),
});

export const {
  useSignupMutation,
  useLoginMutation,
  useLogoutMutation,
  useProfileQuery,
  useHomeQuery,
  useBecomeReporterMutation,
  useAddNewsMutation,
  useMyNewsQuery,
  useUpdateNewsMutation,
  useDeleteNewsMutation,
  useAllNewsQuery,
  useToggleReactionMutation,
  useGetReactionsQuery,
  useAddCommentMutation,
  useGetCommentsQuery,
  useDeleteCommentMutation,
  useUpdateCommentMutation,
} = newsApi;
