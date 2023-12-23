package com.instantrip.presentation.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instantrip.R

class CommentListAdapter(comments: ArrayList<String>) : RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>(){
    private var comments = comments

    class CommentViewHolder(comment: View): RecyclerView.ViewHolder(comment){
        //댓글 내용
        public var commentAuthor: TextView = comment.findViewById(R.id.comment_card_author)
        public var commentDate: TextView = comment.findViewById(R.id.comment_card_date)
        public var commentContent: TextView = comment.findViewById(R.id.comment_card_content)

        //댓글 좋아요
        public var commentLikeButton: Button = comment.findViewById(R.id.comment_card_like_button)
        public var commentLikeCount: TextView = comment.findViewById(R.id.comment_card_like_count)

        //댓글 싫어요
        public var commentDislikeButton: Button = comment.findViewById(R.id.comment_card_dislike_button)
        public var commentDislikeCount: TextView = comment.findViewById(R.id.comment_card_dislike_count)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_card_view, parent, false)

        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.commentContent.text = comments[position]
    }

}