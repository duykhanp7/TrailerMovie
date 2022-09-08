package com.example.movies.adapter.videos;

import static com.example.movies.ui.activity.details.DetailsMovieActivity.firebaseObjectCommentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.data.firebase.FirebaseObjectCommentManager;
import com.example.movies.data.model.comment.Comment;
import com.example.movies.data.model.reaction.Reaction;
import com.example.movies.listener.videos.IOnActionShare;
import com.example.movies.ui.activity.main.MainActivity;
import com.example.movies.databinding.ItemYoutubeViewBinding;
import com.example.movies.listener.videos.ITrailerItemClickListener;
import com.example.movies.data.model.movie.MovieObject;
import com.example.movies.data.model.videos.TrailerObject;
import com.example.movies.ui.bottomsheet.BottomSheetComment;
import com.example.movies.utils.ThemeUtils;
import com.example.movies.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    List<TrailerObject.Trailer> trailers;
    ITrailerItemClickListener iTrailerItemClickListener;
    MovieObject.Movie movie;
    Map<String, VideosAdapter.ViewHolder> mapItemHovered = new HashMap<>();
    FragmentManager fragmentManager;
    private final int heightScreen = 0;
    public BottomSheetComment bottomSheetComment = null;
    private Map<String, ViewHolder> mapViewHolders = new HashMap<>();
    IOnActionShare iOnActionShare;
    Context context;

    public VideosAdapter(Context contextA,ITrailerItemClickListener iTrailerItemClickListener, MovieObject.Movie a, FragmentManager fragmentManagerA, IOnActionShare i) {
        trailers = new ArrayList<>();
        this.iTrailerItemClickListener = iTrailerItemClickListener;
        this.movie = a;
        this.fragmentManager = fragmentManagerA;
        this.iOnActionShare = i;
        this.context = contextA;
    }

    public void setTrailers(List<TrailerObject.Trailer> trailers) {
        this.trailers = trailers;
    }

    public void setITrailerItemClickListener(ITrailerItemClickListener iTrailerItemClickListener) {
        this.iTrailerItemClickListener = iTrailerItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemYoutubeViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_youtube_view, parent, false);
        return new ViewHolder(binding, iTrailerItemClickListener, movie);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrailerObject.Trailer trailer = trailers.get(position);
        holder.bindData(trailer);
        mapViewHolders.put(String.valueOf(trailer.getId()), holder);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }


    public void resetLayoutViewItemTrailer() {
        if (mapItemHovered.size() > 0) {
            for (VideosAdapter.ViewHolder a : mapItemHovered.values()) {
                a.binding.layoutReaction.setVisibility(View.GONE);
            }
            mapItemHovered.clear();
        }
    }

    public void addCommentToTrailer(Comment a) {
        Log.i("AAAA", "ADD COMMENT");
        for (TrailerObject.Trailer trailer : trailers) {
            if (a.getTrailerID().equals(trailer.getId())) {
                Log.i("AAAA", "ADD COMMENT TRUEEEEEEEEEEEEEEEE");
                if (bottomSheetComment != null) {
                    bottomSheetComment.addCommentToRecyclerView(a);
                } else {
                    Objects.requireNonNull(trailer.getListComments().get()).add(a);
                }
                Objects.requireNonNull(mapViewHolders.get(String.valueOf(trailer.getId()))).binding.textCountComments.post(new Runnable() {
                    @Override
                    public void run() {
                        Objects.requireNonNull(mapViewHolders.get(String.valueOf(trailer.getId()))).binding.textCountComments.setText(String.valueOf(Objects.requireNonNull(trailer.getListComments().get()).size()).concat(" ").concat(context.getResources().getString(R.string.comment)));
                    }
                });
                break;
            }
        }
    }

    public void updateCommentInTrailer(Comment a) {
        for (TrailerObject.Trailer trailer : trailers) {
            if (a.getTrailerID().equals(trailer.getId())) {

                for (int i = 0; i < Objects.requireNonNull(trailer.getListComments().get()).size(); i++) {
                    Comment x = Objects.requireNonNull(trailer.getListComments().get()).get(i);
                    if (x.getId().equals(a.getId())) {
                        if (bottomSheetComment != null) {
                            bottomSheetComment.updateCommentToRecyclerView(a);
                        } else {
                            Objects.requireNonNull(trailer.getListComments().get()).set(i, a);
                        }
                        break;
                    }
                }

            }
        }
    }

    public void deleteCommentInTrailer(Comment a) {
        for (TrailerObject.Trailer trailer : trailers) {
            if (a.getTrailerID().equals(trailer.getId())) {

                for (int i = 0; i < Objects.requireNonNull(trailer.getListComments().get()).size(); i++) {
                    Comment comment = Objects.requireNonNull(Objects.requireNonNull(trailer.getListComments().get()).get(i));
                    if (comment.getId().equals(a.getId())) {
                        if (bottomSheetComment != null) {
                            bottomSheetComment.deleteCommentToRecyclerView(a, i);
                        } else {
                            Objects.requireNonNull(trailer.getListComments().get()).remove(i);
                        }
                        Objects.requireNonNull(mapViewHolders.get(String.valueOf(trailer.getId()))).binding.textCountComments.post(new Runnable() {
                            @Override
                            public void run() {
                                Objects.requireNonNull(mapViewHolders.get(String.valueOf(trailer.getId()))).binding.textCountComments.setText(String.valueOf(Objects.requireNonNull(trailer.getListComments().get()).size()).concat(" ").concat(context.getResources().getString(R.string.comment)));
                            }
                        });
                        break;
                    }
                }

            }
        }
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ItemYoutubeViewBinding binding;
        TrailerObject.Trailer trailer;
        ITrailerItemClickListener iTrailerItemClickListener;
        MovieObject.Movie movie;

        public ViewHolder(@NonNull ItemYoutubeViewBinding binding, ITrailerItemClickListener iTrailerItemClickListener, MovieObject.Movie a) {
            super(binding.getRoot());
            this.binding = binding;
            this.iTrailerItemClickListener = iTrailerItemClickListener;
            this.movie = a;
            bindClick();
        }

        public void bindData(TrailerObject.Trailer trailer) {

            this.trailer = trailer;
            binding.setItem(trailer);

            if (ThemeUtils.getCurrentTheme() == AppCompatDelegate.MODE_NIGHT_NO) {
                binding.videoMainLayout.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.color.white));
            } else if (ThemeUtils.getCurrentTheme() == AppCompatDelegate.MODE_NIGHT_YES) {
                binding.videoMainLayout.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.color.black));
            }

            firebaseObjectCommentManager.setMode(1);
            ObservableField<List<Comment>> list = new ObservableField<>(new ArrayList<>());
            list.set(firebaseObjectCommentManager.getAllComment(movie, this.trailer));
            this.trailer.getListComments().set(list.get());

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    binding.textCountComments.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.textCountComments.setText(String.valueOf(Objects.requireNonNull(trailer.getListComments().get()).size()).concat(" "+context.getResources().getString(R.string.comment)));
                        }
                    });
                }
            }, 2000);


        }

        public void bindClick() {

            binding.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.addMovieToWatchRecently(movie);
                    iTrailerItemClickListener.onTrailerItemClick(trailer);
                }
            });

            binding.imageYoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.addMovieToWatchRecently(movie);
                    iTrailerItemClickListener.onTrailerItemClick(trailer);
                }
            });

            binding.buttonComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetComment = new BottomSheetComment(movie, trailer, heightScreen);
                    bottomSheetComment.show(fragmentManager, "AAA");
                    bottomSheetComment.setStyle(DialogFragment.STYLE_NORMAL, R.style.changeBackgroundOfBottomSheetComment);
                }
            });

            binding.buttonShares.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentShare = new Intent();
                    intentShare.setAction(Intent.ACTION_SEND);
                    intentShare.setType("text/plain");
                    intentShare.putExtra(Intent.EXTRA_TEXT,Utils.httpYoutube.concat(trailer.getKey()));
                    intentShare = Intent.createChooser(intentShare,"Share "+trailer.getName());
                    iOnActionShare.onActionShare(intentShare);
                }
            });

        }

    }
}
