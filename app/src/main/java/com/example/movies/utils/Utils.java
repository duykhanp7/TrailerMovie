package com.example.movies.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    //EMAIL AND PASSWORD FOR AUTHENTICATION EMAIL
    public static final String EMAIL = "duykhangp7@gmail.com";
    public static final String PASSWORD = "0397439979adAD";

    //API KEY OF T M D B MOVIE
    public static String API_MOVIE_KEY = "904b3059ddd54e71c45dc72502d59375";
    //API KEY OF YOUTUBE
    public static String KEY_API_YOUTUBE = "AIzaSyAJ-V3QPQqEIny48doJbtxkXqjHWBLQak8";
    //DOMAIN
    public static String DOMAIN_API_MOVIE_GET_IMAGE = "https://image.tmdb.org/t/p/w500";
    //GENRE MOVIES
    public static final String latest = "latest";
    public static final String now_playing = "now_playing";
    public static final String popular = "popular";
    public static final String top_rated = "top_rated";
    public static final String upcoming = "upcoming";
    public static final String recommendations = "recommendations";
    public static final String similar = "similar";
    public static final String airing_today = "airing_today";
    public static final String on_the_air = "on_the_air";

    public static final String NowPlaying = "Now Playing";
    public static final String Popular = "Popular";
    public static final String TopRated = "Top Rated";
    public static final String UpComing = "Up Coming";
    public static final String AiringToday = "Airing Today";
    public static final String OnTheAir = "On The Air";

    public static final String Action = "Action";
    public static final String Adventure = "Adventure";
    public static final String Animation = "Animation";
    public static final String Comedy = "Comedy";
    public static final String Crime = "Crime";
    public static final String Documentary = "Documentary";
    public static final String Drama = "Drama";
    public static final String Family = "Family";
    public static final String Fantasy = "Fantasy";
    public static final String History = "History";
    public static final String Horror = "Horror";
    public static final String Music = "Music";
    public static final String Mystery = "Mystery";
    public static final String Romance = "Romance";
    public static final String ScienceFiction = "Science Fiction";
    public static final String TVMovie = "TV Movie";
    public static final String Thriller = "Thriller";
    public static final String War = "War";
    public static final String Western = "Western";
    //GENRES TV SHOW
    public static final String ActionAdventure = "Action & Adventure";
    public static final String Kids = "Kids";
    public static final String News = "News";
    public static final String Reality = "Reality";
    public static final String SciFiFantasy = "Sci-Fi & Fantasy";
    public static final String Soap = "Soap";
    public static final String Talk = "Talk";
    public static final String WarPolitics = "War & Politics";


    //LIST TITLE GENRE
    public static List<String> titleGenresMovie = new ArrayList<String>();
    public static List<String> titleGenresTVShow = new ArrayList<String>();
    public static List<String> typeArrayMovie = new ArrayList<String>
                        (Arrays.asList(Action, Adventure, Animation, Comedy,
                                Crime, Documentary, Drama, Family,
                                Fantasy, History, Horror, Music,
                                Mystery, Romance, ScienceFiction,TVMovie,
                                Thriller, War, Western));
    public static List<String> typeArrayTVShow = new ArrayList<>
                                (Arrays.asList(ActionAdventure, Animation, Comedy,
                                        Crime, Documentary, Drama, Family, Kids
                                        , Mystery, News, Reality, SciFiFantasy, Soap
                                        , Talk,WarPolitics, Western));

    //STATE LOAD INIT OR LOAD TO ADD NEW MOVIES
    public static final int INIT_MOVIES = 0;
    public static final int ADD_MOVIES = 1;
    //HTTPS FACEBOOK, INSTAGRAM, TWITTER
    public static final String httpFacebook = "https://www.facebook.com/";
    public static final String httpInstagram = "https://www.instagram.com/";
    public static final String httpTwitter = "https://twitter.com/";
    public static final String httpYoutube = "https://www.youtube.com/watch?v=";
    //TYPE : CAST OR CREW
    public static final String TYPE_CAST = "0";
    public static final String TYPE_CREW = "1";
    //GENDER
    //    0 - Not specified
    //    1 - Female
    //    2 - Male
    public static final String GENDER_MALE = "2";
    public static final String GENDER_FEMALE = "1";
    public static final String GENDER_OTHERS = "0";
    //SOCIAL
    public static final String FACEBOOK = "FACEBOOK";
    public static final String INSTAGRAM = "INSTAGRAM";
    public static final String TWITTER = "TWITTER";
    //MODE VISIBILITY FOR GENRES MOVIE
    public static boolean MOVIE_BY_CHIP_GENRES = false;
    //SPECIFIC CHARACTERS
    public static final String SPECIFIC_CHARACTERS = "[^a-zA-Z0-9]";
    public static final String REGEX_DOUBLE_SPACE = "\\s{2,}";
    //SPINNER ITEM ID
    public static final String HOME_ID = "HOME";
    public static final String TV_SHOWS_ID = "TV_SHOWS";
    public static final String FAVORITE_ID = "FAVORITE";
    public static final String SETTING_ID = "SETTING";
    public static final String OTHERS_ID = "OTHERS";
    public static final String KEYWORDS_SEARCH = "KEYS_SEARCH";
    public static final String MOVIES_WATCH_RECENTLY = "MOVIES_WATCH_RECENTLY";
    //TYPES MOVIE OR TV SHOW
    public static final String TYPE_MOVIE = "movie";
    public static final String TYPE_TV_SHOW = "tv";
    //FIREBASE : FAVORITES FOLDER NAME
    public static final String FIREBASE_FAVORITE_MOVIES_FOLDER = "FavoriteMovie";
    public static final String FIREBASE_WATCH_RECENTLY_MOVIES_FOLDER = "WatchRecentlyMovie";
    public static final String FIREBASE_FAVORITE_CAST_AND_CREW_FOLDER = "FavoriteCastAndCrew";
    public static final String FIREBASE_USERS_ACCOUNT_FOLDER = "UsersAccount";
    public static final String FIREBASE_CAST_FOLDER = "CastFolder";
    public static final String FIREBASE_CREW_FOLDER = "CrewFolder";
    //TYPE LOGIN
    public static final String LOGIN_BY_GOOGLE = "GG";
    public static final String LOGIN_BY_FACEBOOK = "FB";
    public static final String LOGIN_BY_INSTAGRAM = "INS";
    public static final String LOGIN_BY_TWITTER = "TWI";
    //TYPE FAVORITES MOVIE, CAST OR CREW
    public static final String FAVORITE_MOVIES = "MOVIES";
    public static final String FAVORITE_CASTS = "CASTS";
    public static final String FAVORITE_CREWS = "CREWS";
    //PROVIDER ID
    public static final String GOOGLE_PROVIDER = "google.com";
    public static final String FACEBOOK_PROVIDER = "facebook.com";
    public static final String PASSWORD_PROVIDER = "password";
    //User
    public static final String USERNAME = "username";
    public static final String USER_PASSWORD = "password";
    public static final String rememberMe = "rememberMe";
    //Comment
    public static final String COMMENT_FOLDER = "CommentFolder";
    //Keyword
    public static final String KEYWORD_FOLDER = "KeywordFolder";
    //Language key
    public static final String LANGUAGE_KEY = "language";
    //Setting database name
    public static final String SETTING_DATABASE_NAME = "setting.db";
    public static final String SETTING_TABLE_NAME = "setting_table";
    public static final String KEYWORD_TABLE_NAME = "keyword_table";
}
