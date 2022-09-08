package com.example.movies.ui.bottomsheet;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.firebaseObjectCommentManager;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataAddComment;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataBottomSheetDismiss;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataDeleteComment;
import static com.example.movies.ui.activity.details.DetailsMovieActivity.mutableLiveDataUpdateComment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.movies.R;
import com.example.movies.adapter.comment.CommentAdapter;
import com.example.movies.data.firebase.FirebaseObjectCommentManager;
import com.example.movies.data.model.comment.Comment;
import com.example.movies.data.model.videos.TrailerObject;
import com.example.movies.databinding.BottomSheetCommentBinding;
import com.example.movies.helper.TimeUtils;
import com.example.movies.data.model.movie.MovieObject;
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class BottomSheetComment extends BottomSheetDialogFragment {


    private BottomSheetCommentBinding binding;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Dialog dialog;
    private int heightScreen = 0;
    private final List<Comment> comments = new ArrayList<>();
    //private CommentAdapter adapter;
    private final MovieObject.Movie movie;
    private final TrailerObject.Trailer trailer;
    private final ObservableField<List<Comment>> listCommentsObservableField = new ObservableField<>();
    private final ObservableField<CommentAdapter> commentAdapterObservableField = new ObservableField<>();

    public BottomSheetComment(MovieObject.Movie movie, TrailerObject.Trailer trailer, int height) {
        this.movie = movie;
        this.trailer = trailer;
        this.heightScreen = height;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCommentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseObjectCommentManager.setMode(2);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setDraggable(true);

        dialog.findViewById(R.id.bottomSheetComment).setMinimumHeight(heightScreen / 2);
        dialog.getWindow().getDecorView().setBackground(null);

        listCommentsObservableField.set(new ArrayList<>());
        listCommentsObservableField.set(trailer.getListComments().get());

        commentAdapterObservableField.set(new CommentAdapter(movie, trailer, listCommentsObservableField.get()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerViewComment.setHasFixedSize(true);
        binding.recyclerViewComment.setLayoutManager(layoutManager);
        binding.recyclerViewComment.setAdapter(commentAdapterObservableField.get());

        bindClick();

        showLayoutNoComment();

    }

    public void addCommentToRecyclerView(Comment a){
        if (commentAdapterObservableField.get() != null) {
            Objects.requireNonNull(commentAdapterObservableField.get()).addNewComment(a);
        }
        showLayoutNoComment();
    }

    public void updateCommentToRecyclerView(Comment a){
        if (commentAdapterObservableField.get() != null) {
            Objects.requireNonNull(commentAdapterObservableField.get()).updateComment(a);
        }
        showLayoutNoComment();
    }

    public void deleteCommentToRecyclerView(Comment a,int i){
        Objects.requireNonNull(commentAdapterObservableField.get()).deleteComment(a,i);
        showLayoutNoComment();
    }

    public void showLayoutNoComment(){
        if(isNoComment()){
            binding.layoutNoComment.setVisibility(View.VISIBLE);
            binding.recyclerViewComment.setVisibility(View.GONE);
        }
        else{
            binding.layoutNoComment.setVisibility(View.GONE);
            binding.recyclerViewComment.setVisibility(View.VISIBLE);
        }
    }

    public void bindClick() {
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseObjectCommentManager.setMode(2);
                Comment newComment = new Comment();
                newComment.setTextComment(getCommentFromTextInput());
                newComment.setTimeComment(TimeUtils.getCurrentTime());
                newComment.setMovieID(String.valueOf(movie.getId()));
                newComment.setTrailerID(String.valueOf(trailer.getId()));
                String text = movie.getId().concat(trailer.getId()).concat(newComment.getTextComment().concat(newComment.getTimeComment()));
                newComment.setId(getTextHex(text));
                firebaseObjectCommentManager.addComment(movie, trailer, newComment);
                clearText();
            }
        });

        binding.buttonCommentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mutableLiveDataBottomSheetDismiss.postValue(true);
                dismiss();
            }
        });

    }

    public String getCommentFromTextInput() {
        return Objects.requireNonNull(binding.textInputComment.getText()).toString();
    }

    public void clearText() {
        Objects.requireNonNull(binding.textInputComment.getText()).clear();
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        mutableLiveDataBottomSheetDismiss.postValue(true);
        super.onDismiss(dialog);
    }

    public String getTextHex(String text) {
        return removeInvalidCharacter(Base64.getEncoder().encodeToString(text.getBytes()));
    }

    public String removeInvalidCharacter(String text) {
        return text.replace(".", "").replace("$", "").replace("#", "").replace("[", "").replace("]", "")
                .replace("/", "");
    }

    public boolean isNoComment(){
        return Objects.requireNonNull(trailer.getListComments().get()).size() == 0;
    }

}
