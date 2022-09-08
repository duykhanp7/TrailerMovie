package com.example.movies.adapter.comment;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.firebaseObjectCommentManager;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataDeleteComment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.data.model.comment.Comment;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.data.model.users.UserProfile;
import com.example.movies.data.model.videos.TrailerObject;
import com.example.movies.databinding.ItemCommentBinding;
import com.example.movies.helper.TimeUtils;
import com.example.movies.helper.TranslateUtils;
import com.example.movies.ui.activity.main.MainActivity;
import com.google.android.exoplayer2.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> listComments = new ArrayList<>();
    private MovieObject.Movie movie;
    private TrailerObject.Trailer trailer;

    public CommentAdapter(MovieObject.Movie movie, TrailerObject.Trailer trailer, List<Comment> list) {
        this.listComments = list;
        this.movie = movie;
        this.trailer = trailer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = listComments.get(position);
        holder.bindData(comment);
    }

    @Override
    public int getItemCount() {
        return listComments.size();
    }


    public void addNewComment(Comment a) {
        if (a.getMovieID().equals(this.movie.getId()) && a.getTrailerID().equals(this.trailer.getId())) {
            int size = listComments.size();
            this.listComments.add(size, a);
            notifyItemInserted(size);
        }
    }

    public void updateComment(Comment a) {
        if (a.getMovieID().equals(this.movie.getId()) && a.getTrailerID().equals(this.trailer.getId())) {
            int position = findComment(a);
            listComments.set(position, a);
            notifyItemChanged(position);
        }
    }

    public void deleteComment(Comment a,int pos) {
        //int pos = findComment(a);
        listComments.remove(pos);
        notifyItemRemoved(pos);
    }


    public int findComment(Comment a) {
        for (int position = 0; position < listComments.size(); position++) {
            if (a.getId().equals(listComments.get(position).getId())) {
                return position;
            }
        }
        return -1;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        public ItemCommentBinding binding;
        private Comment comment;

        public ViewHolder(@NonNull ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(Comment a) {

            this.comment = a;

            Picasso.get().load(comment.getPathImage()).placeholder(R.drawable.background_shimmer_circle).error(R.drawable.background_shimmer_circle).into(binding.imageProfile);
            binding.textViewNameUser.setText(comment.getNameDisplay().replace("\\s{2,}", " "));
            binding.textViewComment.setText(this.comment.getTextComment());
            String txtDate = this.comment.getTimeComment().split(" ")[0];
            String txtTime = this.comment.getTimeComment().split(" ")[1];
            String[] txtTimeArray = txtTime.split(":");
            String txtHours = txtTimeArray[0];
            String txtMinutes = txtTimeArray[1];
            binding.textViewTimeComment.setText(txtDate.concat(" ").concat(txtHours).concat(":").concat(txtMinutes));

            bindClick();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.layoutShimmer.setVisibility(View.GONE);
                    binding.layoutNotShimmer.setVisibility(View.VISIBLE);
                }
            }, 1000);

        }

        public void bindClick() {
            binding.layoutDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseObjectCommentManager.deleteComment(movie, trailer, comment);
                }
            });

            binding.layoutEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

}
