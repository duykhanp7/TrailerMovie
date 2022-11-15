package com.example.movies.ui.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movies.utils.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import java.util.Date;

public class AdsManager {

    public static class AppOpen{
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        public boolean isShowingAd = false;
        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private long loadTime = 0;

        public void loadAd(Context context){
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            if (context != null){
                AppOpenAd.load(
                        context, Utils.APP_OPEN, request,
                        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                        new AppOpenAd.AppOpenAdLoadCallback() {
                            @Override
                            public void onAdLoaded(AppOpenAd ad) {
                                // Called when an app open ad has loaded.
                                Log.d("AAA", "Ad was loaded.");
                                appOpenAd = ad;
                                isLoadingAd = false;
                                loadTime = (new Date()).getTime();
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                // Called when an app open ad has failed to load.
                                Log.d("AAA", loadAdError.getMessage());
                                isLoadingAd = false;
                            }
                        });
            }
        }

        /** Utility method to check if ad was loaded more than n hours ago. */
        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - this.loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        public boolean isAdAvailable(){
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        private void showAdIfAvailable(Activity activity) {

        }

        public void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener){
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d("AAA", "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d("AAA", "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }

            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    // Called when fullscreen content is dismissed.
                    // Set the reference to null so isAdAvailable() returns false.
                    Log.d("AAA", "Ad dismissed fullscreen content.");
                    appOpenAd = null;
                    isShowingAd = false;

                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    // Called when fullscreen content is dismissed.
                    // Set the reference to null so isAdAvailable() returns false.
                    Log.d("AAA", "Ad dismissed fullscreen content.");
                    appOpenAd = null;
                    isShowingAd = false;

                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    // Called when fullscreen content is shown.
                    Log.d("AAA", "Ad showed fullscreen content.");
                }
            });
            isShowingAd = true;
            appOpenAd.show(activity);
        }

        public interface OnShowAdCompleteListener {
            void onShowAdComplete();
        }

    }

    public static class InterstitialAds{

        private InterstitialAd mInterstitialAd;

        public void loadAd(Context context){
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, Utils.INTERSTITIAL, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.i("AAA", "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d("AAA", loadAdError.toString());
                            mInterstitialAd = null;
                        }
                    });
        }

        public boolean isAdAvailable(){
            return mInterstitialAd != null;
        }

        public void showAdIfAvailable(Activity activity){
            if (mInterstitialAd != null) {
                mInterstitialAd.show(activity);
                loadAd(activity);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        }

    }

    public static class Rewarded{

        private RewardedAd mRewardedAd;

        public void loadAd(Context context){
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(context, Utils.REWARDED,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("AAA", loadAdError.toString());
                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            Log.d("AAA", "Ad was loaded.");
                        }
                    });
        }

        public boolean isAdAvailable(){
            return mRewardedAd != null;
        };

        public void showAdIfAvailable(Activity activity){
            if (mRewardedAd != null) {
                mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("AAA", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
                loadAd(activity);
            } else {
                Log.d("AAA", "The rewarded ad wasn't ready yet.");
            }
        }

    }

    public static class RewardedInterstitial{

        private RewardedInterstitialAd rewardedInterstitialAd;

        public void loadAd(Context context) {
            // Use the test ad unit ID to load an ad.
            RewardedInterstitialAd.load(context, Utils.REWARDED_INTERSTITIAL,
                    new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(RewardedInterstitialAd ad) {
                            Log.d("AAA", "Ad was loaded.");
                            rewardedInterstitialAd = ad;
                        }
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            Log.d("AAA", loadAdError.toString());
                            rewardedInterstitialAd = null;
                        }
                    });
        }

        public boolean isAdAvailable(){
            return rewardedInterstitialAd != null;
        }

        public void showAdIfAvailable(Activity activity){
            if(rewardedInterstitialAd != null){
                rewardedInterstitialAd.show(activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    }
                });
            }
            else{

            }
        }

    }

}
