package com.example.movies.data.model.reaction;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.movies.BR;
import com.example.movies.R;

import java.io.Serializable;

public class Reaction extends BaseObservable implements Serializable {

    private int idReact;
    private int drawable;

    public Reaction(){}

    @Bindable
    public int getIdReact() {
        return idReact;
    }

    public void setIdReact(int idReact) {
        this.idReact = idReact;
        this.notifyPropertyChanged(BR.idReact);
    }


    @Bindable
    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
        this.notifyPropertyChanged(BR.drawable);
    }

    public static Reaction getReaction(int type){
        Reaction a = new Reaction();
        a.setIdReact(type);
        switch (type){
            case 1:
                a.setDrawable(R.drawable.ic_like);
                break;
            case 2:
                a.setDrawable(R.drawable.ic_love);
                break;
            case 3:
                a.setDrawable(R.drawable.ic_lovelove);
                break;
            case 4:
                a.setDrawable(R.drawable.ic_haha);
                break;
            case 5:
                a.setDrawable(R.drawable.ic_wow);
                break;
            case 6:
                a.setDrawable(R.drawable.ic_sad);
                break;
            case 7:
                a.setDrawable(R.drawable.ic_angry);
                break;
        }
        return a;
    }

}
