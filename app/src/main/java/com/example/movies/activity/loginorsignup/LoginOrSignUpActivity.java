package com.example.movies.activity.loginorsignup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.movies.R;
import com.example.movies.activity.main.MainActivity;
import com.example.movies.databinding.LayoutOptionsLoginSignupBinding;
import com.example.movies.firebase.FirebaseObjectUserManager;
import com.example.movies.locale.LocaleHelper;
import com.example.movies.model.users.UserProfile;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

public class LoginOrSignUpActivity extends AppCompatActivity {

    public static LayoutOptionsLoginSignupBinding binding;
    private static boolean isMainView = true;

    //GOOGLE AUTH
    private GoogleSignInOptions googleSignInOptions;
    public GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    //FACEBOOK AUTH
    CallbackManager callBackManager;
    //FIREBASE DATABASE
    FirebaseObjectUserManager firebaseObjectUserManager;
    //USER PROFILE
    public static UserProfile userProfileLogIn = new UserProfile("", "", "", "", "", "");
    public static UserProfile userProfileSignUp = new UserProfile("", "", "", "", "", "");
    //BOOLEAN VARIABLES TO CHECK USER's ACCOUNT LOGIN IS VALID
    public static boolean isEmailValidLogIn = false, isPasswordValidLogIn = false;
    //BOOLEAN VARIABLES TO CHECK USER's ACCOUNT SIGN UP IS VALID
    public static boolean isEmailValidSignUp = false, isPasswordValidSignUp = false, isFirstNameValidSignUp = false, isLastnameValidSignUp = false, isPasswordConfirmValidSignUp = false, isAgreeTernAndPolicy = false;

    public ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                GoogleSignInAccount account;
                try {
                    account = googleSignInAccountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    handleLoginByAuthCredential(authCredential);
                } catch (ApiException e) {
                    e.printStackTrace();
                }

            } else {
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_options_login_signup);
        binding.setMain(this);
        //LocaleHelper.setLocale(getApplicationContext(),"en");
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        //INITIALIZED FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance();
        //INITIALIZED FACEBOOK AUTH
        //FACEBOOK AUTH
        callBackManager = CallbackManager.Factory.create();
        //INITIALIZED FIREBASE
        firebaseObjectUserManager = new FirebaseObjectUserManager();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startMainActivity();
            Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.please_login_to_enjoy, Toast.LENGTH_LONG).show();
            userProfileSignUp.setEmail("");
            userProfileSignUp.setPassword("");
            disableErrorLayoutLogIn();
        }

        binding.layoutIncludeScreenLogIn.googleAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInWithGoogleAccount();
            }
        });

        //FACEBOOK AUTH
        binding.layoutIncludeScreenLogIn.facebookAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInWithFacebookAccount();
            }
        });

        //BUTTON LOG IN CLICK
        binding.layoutIncludeScreenLogIn.buttonLogInToAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserLogInInformationValid(userProfileLogIn);
                if (isUserLoginInformationValid()) {
                    loginWithEmailAndPassword(userProfileLogIn);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
        //BUTTON SIGN UP CLICK
        binding.layoutIncludeScreenSignUp.buttonRegisterAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserSignUpInformationValid(userProfileSignUp);
                if (isUserSignUpInformationValid()) {
                    if (!isAgreeTernAndPolicy) {
                        Toast.makeText(getApplicationContext(), R.string.please_see_read_tern_and_policy, Toast.LENGTH_LONG).show();
                    } else {
                        createUserWithEmailAndPassword(userProfileSignUp);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.create_acc_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
        //ON CHECK BOX CHANGED
        binding.layoutIncludeScreenSignUp.checkBoxAgreeTernAndPolicy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAgreeTernAndPolicy = b;
            }
        });


        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonLogInSplashScreenClick();
            }
        });

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonSignUpSplashScreenClick();
            }
        });

        binding.layoutIncludeScreenSignUp.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonBottomLogInIncludeLayoutClick();
            }
        });


        binding.layoutIncludeScreenLogIn.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonBottomSignUpIncludeLayoutClick();
            }
        });

        binding.layoutIncludeScreenLogIn.textInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextEmailLogInChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.layoutIncludeScreenLogIn.textInputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextPasswordLogInChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.layoutIncludeScreenSignUp.textInputFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextFirstNameSignUpChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.layoutIncludeScreenSignUp.textInputLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextLastNameSignUpChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.layoutIncludeScreenSignUp.textInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextEmailSignUpChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.layoutIncludeScreenSignUp.textInputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextPasswordSignUpChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.layoutIncludeScreenSignUp.textInputConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextPasswordConfirmSignUpChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.layoutIncludeScreenSignUp.iconBackLayoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonBackToSplashScreenClick();
            }
        });

        binding.layoutIncludeScreenLogIn.iconBackLayoutLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonBackToSplashScreenClick();
            }
        });

    }

    public static void showLayoutSplashScreen(int visibility) {
        binding.layoutSplashScreen.setVisibility(visibility);
    }

    public static void showLayoutScreenLogIn(int visibility) {
        binding.layoutScreenLogIn.setVisibility(visibility);
    }

    public static void showLayoutScreenSignUp(int visibility) {
        binding.layoutScreenSignUp.setVisibility(visibility);
    }


    public void logInWithGoogleAccount() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
        requestAuthWithGoogleAccount();
        googleSignInClient.signOut();
        //googleSignInClient.revokeAccess();
    }

    public void logInWithFacebookAccount() {
        LoginManager.getInstance().logInWithReadPermissions(LoginOrSignUpActivity.this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential authCredential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                handleLoginByAuthCredential(authCredential);
                LoginManager.getInstance().reauthorizeDataAccess(LoginOrSignUpActivity.this);
                LoginManager.getInstance().unregisterCallback(callBackManager);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }


    public void loginWithEmailAndPassword(UserProfile userProfile) {
        firebaseAuth.signInWithEmailAndPassword(userProfile.getEmail(), getPasswordEncrypted(userProfile.getPassword())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
                startMainActivity();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }


    public void createUserWithEmailAndPassword(UserProfile userProfile) {
        firebaseAuth.createUserWithEmailAndPassword(userProfile.getEmail(), getPasswordEncrypted(userProfile.getPassword())).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(getApplicationContext(), R.string.please_log_in_to_enjoy, Toast.LENGTH_LONG).show();
                userProfile.setPassword(getPasswordEncrypted(userProfile.getPassword()));
                firebaseObjectUserManager.addUser(userProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.create_acc_fail, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(), R.string.create_acc_fail, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void requestAuthWithGoogleAccount() {
        Intent requestAuth = googleSignInClient.getSignInIntent();
        launcher.launch(requestAuth);
    }

    public void handleLoginByAuthCredential(AuthCredential authCredential) {
        firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
                startMainActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void onButtonForgotPassword() {

    }

    public static void resetUserProfileToNull() {
        disableErrorLayoutLogIn();
        disableErrorLayoutSignUp();
    }

    public static void disableErrorLayoutLogIn() {
        userProfileLogIn.setFirstName("");
        userProfileLogIn.setLastName("");
        userProfileLogIn.setEmail("");
        userProfileLogIn.setPassword("");
        userProfileLogIn.setPhoneNumber("");
        userProfileLogIn.setPathImage("");

        binding.layoutIncludeScreenLogIn.textInputEmailLayout.setErrorEnabled(false);
        binding.layoutIncludeScreenLogIn.textInputPasswordLayout.setErrorEnabled(false);
    }

    public static void disableErrorLayoutSignUp() {

        userProfileSignUp.setFirstName("");
        userProfileSignUp.setLastName("");
        userProfileSignUp.setEmail("");
        userProfileSignUp.setPassword("");
        userProfileSignUp.setPhoneNumber("");
        userProfileSignUp.setPathImage("");

        binding.layoutIncludeScreenSignUp.textInputConfirmPassword.setText("");

        binding.layoutIncludeScreenSignUp.textInputFirstnameLayout.setErrorEnabled(false);
        binding.layoutIncludeScreenSignUp.textInputLastnameLayout.setErrorEnabled(false);
        binding.layoutIncludeScreenSignUp.textInputEmailLayout.setErrorEnabled(false);
        binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setErrorEnabled(false);
        binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setErrorEnabled(false);
    }


    public boolean isUserSignUpInformationValid() {
        return isFirstNameValidSignUp && isLastnameValidSignUp && isEmailValidSignUp && isPasswordValidSignUp && isPasswordConfirmValidSignUp;
    }


    public boolean isUserLoginInformationValid() {
        return isEmailValidLogIn && isPasswordValidLogIn;
    }

    public static boolean isEmailValid(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public void showUserSignUpInformationValid(UserProfile userProfile) {

        if (userProfile.getFirstName().isEmpty()) {
            binding.layoutIncludeScreenSignUp.textInputFirstnameLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputFirstnameLayout.setError(getString(R.string.invalid_first_name));
        } else {
            binding.layoutIncludeScreenSignUp.textInputFirstnameLayout.setErrorEnabled(false);
        }

        if (userProfile.getLastName().isEmpty()) {
            binding.layoutIncludeScreenSignUp.textInputLastnameLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputLastnameLayout.setError(getString(R.string.invalid_last_name));
        } else {
            binding.layoutIncludeScreenSignUp.textInputLastnameLayout.setErrorEnabled(false);
        }

        if (!isEmailValid(userProfile.getEmail())) {
            binding.layoutIncludeScreenSignUp.textInputEmailLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputEmailLayout.setError(getString(R.string.invalid_email));
        } else {
            binding.layoutIncludeScreenSignUp.textInputEmailLayout.setErrorEnabled(false);
        }

        if (userProfile.getPassword().isEmpty()) {
            binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setError(getString(R.string.invalid_password));
        } else {
            binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setErrorEnabled(false);
        }

        String passwordConfirm = Objects.requireNonNull(binding.layoutIncludeScreenSignUp.textInputConfirmPassword.getText()).toString();
        if (passwordConfirm.isEmpty()) {
            binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setError(getString(R.string.invalid_password_confirm));
        } else {
            if (!userProfile.getPassword().equals(passwordConfirm)) {
                binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setErrorEnabled(true);
                binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setError(getString(R.string.invalid_password_not_the_same));
            } else {
                binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setErrorEnabled(false);
            }
        }

    }


    public void showUserLogInInformationValid(UserProfile userProfile) {
        if (!isEmailValid(userProfile.getEmail())) {
            binding.layoutIncludeScreenLogIn.textInputEmailLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenLogIn.textInputEmailLayout.setError(getString(R.string.invalid_email));
        } else {
            binding.layoutIncludeScreenLogIn.textInputEmailLayout.setErrorEnabled(false);
        }

        if (userProfile.getPassword().isEmpty()) {
            binding.layoutIncludeScreenLogIn.textInputPasswordLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenLogIn.textInputPasswordLayout.setError(getString(R.string.invalid_password));
        } else {
            binding.layoutIncludeScreenLogIn.textInputPasswordLayout.setErrorEnabled(false);
        }
    }


    public void onButtonSignUpSplashScreenClick() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background_top_login));
        isMainView = false;
        resetUserProfileToNull();
        showLayoutScreenSignUp(View.VISIBLE);
        showLayoutSplashScreen(View.GONE);
        showLayoutScreenLogIn(View.GONE);
    }

    public void onButtonLogInSplashScreenClick() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background_top_login));
        isMainView = false;
        resetUserProfileToNull();
        showLayoutScreenLogIn(View.VISIBLE);
        showLayoutScreenSignUp(View.GONE);
        showLayoutSplashScreen(View.GONE);
    }


    public void onButtonBottomSignUpIncludeLayoutClick() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background_top_login));
        showLayoutScreenLogIn(View.GONE);
        showLayoutScreenSignUp(View.VISIBLE);
        resetUserProfileToNull();
    }

    public  void onButtonBottomLogInIncludeLayoutClick() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background_top_login));
        showLayoutScreenSignUp(View.GONE);
        showLayoutScreenLogIn(View.VISIBLE);
        resetUserProfileToNull();
    }


    public void onButtonBackToSplashScreenClick() {
        showLayoutSplashScreen(View.VISIBLE);
        showLayoutScreenLogIn(View.GONE);
        showLayoutScreenSignUp(View.GONE);
        isMainView = true;
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
    }

    public void startMainActivity() {
        Intent intentMainActivity = new Intent(LoginOrSignUpActivity.this, MainActivity.class);
        startActivity(intentMainActivity);
        finish();
    }


    public void onTextEmailLogInChanged(CharSequence charSequence) {
        if (!isEmailValid(charSequence.toString())) {
            isEmailValidLogIn = false;
            binding.layoutIncludeScreenLogIn.textInputEmailLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenLogIn.textInputEmailLayout.setError(getString(R.string.invalid_email));
        } else {
            isEmailValidLogIn = true;
            binding.layoutIncludeScreenLogIn.textInputEmailLayout.setErrorEnabled(false);
        }
    }

    public void onTextPasswordLogInChanged(CharSequence charSequence) {
        if (charSequence.toString().isEmpty()) {
            isPasswordValidLogIn = false;
            binding.layoutIncludeScreenLogIn.textInputPasswordLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenLogIn.textInputPasswordLayout.setError(getString(R.string.invalid_password));
        } else {
            isPasswordValidLogIn = true;
            binding.layoutIncludeScreenLogIn.textInputPasswordLayout.setErrorEnabled(false);
        }
    }


    public void onTextFirstNameSignUpChanged(CharSequence charSequence) {
        if (charSequence.toString().isEmpty()) {
            isFirstNameValidSignUp = false;
            binding.layoutIncludeScreenSignUp.textInputFirstnameLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputFirstnameLayout.setError(getString(R.string.invalid_first_name));
        } else {
            isFirstNameValidSignUp = true;
            binding.layoutIncludeScreenSignUp.textInputFirstnameLayout.setErrorEnabled(false);
        }
    }

    public void onTextLastNameSignUpChanged(CharSequence charSequence) {
        if (charSequence.toString().isEmpty()) {
            isLastnameValidSignUp = false;
            binding.layoutIncludeScreenSignUp.textInputLastnameLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputLastnameLayout.setError(getString(R.string.invalid_last_name));
        } else {
            isLastnameValidSignUp = true;
            binding.layoutIncludeScreenSignUp.textInputLastnameLayout.setErrorEnabled(false);
        }
    }

    public void onTextEmailSignUpChanged(CharSequence charSequence) {
        if (!isEmailValid(charSequence.toString())) {
            isEmailValidSignUp = false;
            binding.layoutIncludeScreenSignUp.textInputEmailLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputEmailLayout.setError(getString(R.string.invalid_email));
        } else {
            isEmailValidSignUp = true;
            binding.layoutIncludeScreenSignUp.textInputEmailLayout.setErrorEnabled(false);
        }
    }

    public void onTextPasswordSignUpChanged(CharSequence charSequence) {
        if (charSequence.toString().isEmpty()) {
            isPasswordValidSignUp = false;
            binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setError(getString(R.string.invalid_password));
        } else {
            if (charSequence.length() <= 6) {
                isPasswordValidSignUp = false;
                binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setErrorEnabled(true);
                binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setError(getString(R.string.pass_length_require));
            } else {
                isPasswordValidSignUp = true;
                binding.layoutIncludeScreenSignUp.textInputPasswordLayout.setErrorEnabled(false);
            }
        }
    }

    public void onTextPasswordConfirmSignUpChanged(CharSequence charSequence) {
        String passwordConfirm = Objects.requireNonNull(binding.layoutIncludeScreenSignUp.textInputConfirmPassword.getText()).toString();
        if (!userProfileSignUp.getPassword().equals(passwordConfirm) || passwordConfirm.isEmpty()) {
            isPasswordConfirmValidSignUp = false;
            binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setErrorEnabled(true);
            binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setError(getString(R.string.pass_not_the_same));
        } else {
            isPasswordConfirmValidSignUp = true;
            binding.layoutIncludeScreenSignUp.textInputConfirmPasswordLayout.setErrorEnabled(false);
        }
    }


    @Override
    public void onBackPressed() {
        if (!isMainView) {
            showLayoutScreenLogIn(View.GONE);
            showLayoutScreenSignUp(View.GONE);
            showLayoutSplashScreen(View.VISIBLE);
            isMainView = true;
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callBackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPasswordEncrypted(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public String getPasswordDecrypted(String passwordEncrypted) {
        return new String(Base64.getDecoder().decode(passwordEncrypted));
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(updateBaseContextLocale(newBase));
//    }


    @SuppressLint("ObsoleteSdkInt")
    private Context updateBaseContextLocale(Context context) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("lang", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("lang","en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

}
