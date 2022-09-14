package com.example.movies.adapter.comment;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.firebaseObjectCommentManager;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataDeleteComment;
import static com.example.movies.ui.activity.main.MainActivity.userProfile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.data.firebase.FirebaseObjectCommentManager;
import com.example.movies.data.model.comment.Comment;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.data.model.users.UserProfile;
import com.example.movies.data.model.videos.TrailerObject;
import com.example.movies.databinding.ItemCommentBinding;
import com.example.movies.databinding.LayoutDialogEditCommentBinding;
import com.example.movies.helper.TimeUtils;
import com.example.movies.helper.TranslateUtils;
import com.example.movies.ui.activity.main.MainActivity;
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> listComments = new ArrayList<>();
    private List<Comment> oldListComments = new ArrayList<>();
    private MovieObject.Movie movie;
    private TrailerObject.Trailer trailer;
    private Dialog dialogEditComment;

    //mode = 0 nếu show các comment mới nhất, mode = 1 show tất cả comment;
    //mặc định để mode = 0
    private int mode = 0;
    private Context context;
    private LayoutDialogEditCommentBinding layoutDialogEditCommentBinding;

    public CommentAdapter(Context contextA, MovieObject.Movie movie, TrailerObject.Trailer trailer, List<Comment> list) {
        this.listComments = new ArrayList<>(list);
        this.oldListComments = new ArrayList<>(list);
        this.movie = movie;
        this.trailer = trailer;
        this.context = contextA;
        initializedDialogEditComment(this.context);
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
            if (mode == 0) {
                this.listComments.add(0, a);
                notifyItemInserted(0);
            } else {
                int size = this.listComments.size();
                this.listComments.add(size, a);
                notifyItemInserted(size);
            }
            this.oldListComments.add(a);
        }
    }

    public void updateComment(Comment a) {
        if (a.getMovieID().equals(this.movie.getId()) && a.getTrailerID().equals(this.trailer.getId())) {
            int positionA = findComment(a);
            int positionB = findCommentOldList(a);
            this.listComments.set(positionA, a);
            notifyItemChanged(positionA);
            this.oldListComments.set(positionB, a);
        }
    }

    public void updateImageProfileComment(UserProfile userProfile) {

        String uid = userProfile.getUid();
        String pathImage = userProfile.getPathImage();

        for (Comment comment : listComments) {
            if (comment.getUserId().equals(uid)) {
                comment.setPathImage(pathImage);
                firebaseObjectCommentManager.updateComment(movie, trailer, comment);
                int positionA = findComment(comment);
                notifyItemChanged(positionA);
            }
        }

        for (Comment comment : oldListComments) {
            if (comment.getUserId().equals(uid)) {
                comment.setPathImage(pathImage);
            }
        }

    }

    public void deleteComment(Comment a) {
        int posA = findComment(a);
        int posB = findCommentOldList(a);
        if (posA != -1) {
            listComments.remove(posA);
            oldListComments.remove(posB);
            notifyItemRemoved(posA);
        }
    }

    public int findComment(Comment a) {
        for (int position = 0; position < listComments.size(); position++) {
            if (a.getId().equals(listComments.get(position).getId())) {
                return position;
            }
        }
        return -1;
    }

    public int findCommentOldList(Comment a) {
        for (int position = 0; position < oldListComments.size(); position++) {
            if (a.getId().equals(oldListComments.get(position).getId())) {
                return position;
            }
        }
        return -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMode(int a) {
        Log.i("AAA", "SET MODE : " + a);
        this.mode = a;
        if (this.mode == 0) {
            this.listComments = new ArrayList<>(this.oldListComments);
            limitItem(10);
        } else {
            this.listComments = new ArrayList<>(this.oldListComments);
        }
        notifyDataSetChanged();
    }

    public int getMode() {
        return this.mode;
    }

    public void limitItem(int size) {
        Collections.reverse(this.listComments);
        this.listComments = this.listComments.stream().limit(size).collect(Collectors.toList());
    }

    public List<Comment> getListComments() {
        return this.listComments;
    }

    public void initializedDialogEditComment(Context context) {

        layoutDialogEditCommentBinding = LayoutDialogEditCommentBinding.inflate(LayoutInflater.from(context), null, false);

        dialogEditComment = new Dialog(context);
        dialogEditComment.setContentView(layoutDialogEditCommentBinding.getRoot());
        dialogEditComment.getWindow().setGravity(Gravity.CENTER);
        dialogEditComment.getWindow().setLayout(dpToPx(350F, context), dpToPx(250F, context));
        dialogEditComment.getWindow().getDecorView().setBackgroundColor(context.getColor(android.R.color.transparent));

        layoutDialogEditCommentBinding.buttonConfirmEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTextComment = Objects.requireNonNull(layoutDialogEditCommentBinding.textInputEditComment.getText()).toString();
                Comment a = layoutDialogEditCommentBinding.getComment();
                a.setTextComment(newTextComment);
                a.setTimeComment(TimeUtils.getCurrentTime());
                firebaseObjectCommentManager.updateComment(movie, trailer, a);
                dialogEditComment.dismiss();
            }
        });

    }

    public int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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

            if (comment.getPathImage() != null && !comment.getPathImage().isEmpty()) {
                Picasso.get().load(comment.getPathImage()).placeholder(R.drawable.null_person_male).error(R.drawable.null_person_male).into(binding.imageProfile);
            }

            binding.textViewNameUser.setText(comment.getNameDisplay().replace("\\s{2,}", " "));
            binding.textViewComment.setText(this.comment.getTextComment());
            String txtDate = this.comment.getTimeComment().split(" ")[0];
            String txtTime = this.comment.getTimeComment().split(" ")[1];
            String[] txtTimeArray = txtTime.split(":");
            String txtHours = txtTimeArray[0];
            String txtMinutes = txtTimeArray[1];
            binding.textViewTimeComment.setText(txtDate.concat(" ").concat(txtHours).concat(":").concat(txtMinutes));

            String uid = FirebaseObjectCommentManager.getUserUID();

            if (this.comment.getUserId().equals(uid)) {

                binding.layoutEditParent.setVisibility(View.VISIBLE);
                updateImageProfileToComment(comment);

            } else {
                binding.layoutEditParent.setVisibility(View.GONE);
            }

            bindClick();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.layoutShimmer.setVisibility(View.GONE);
                    binding.layoutNotShimmer.setVisibility(View.VISIBLE);
                }
            }, 1000);

        }

        public void updateImageProfileToComment(Comment comment) {

            String userProfilePathImage = userProfile.getPathImage();
            String uid = userProfile.getUid();

            if (comment.getUserId().equals(uid)) {
                if (!comment.getPathImage().equals(userProfilePathImage)) {
                    comment.setPathImage(userProfilePathImage);
                    firebaseObjectCommentManager.updateComment(movie, trailer, comment);
                }
            }

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
                    layoutDialogEditCommentBinding.setComment(comment);
                    dialogEditComment.show();
                }
            });

        }

    }

}
