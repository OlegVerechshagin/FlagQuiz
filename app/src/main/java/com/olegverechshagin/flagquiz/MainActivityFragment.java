// MainActivityFragment.java
// Класс содержит логику приложения Flag Quiz
package com.olegverechshagin.flagquiz;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivityFragment extends Fragment {
//   Строка, используемая при регистрации сообщений об ошибках
   private static final String TAG = "FlagQuiz Activity";

   private static final int FLAGS_IN_QUIZ = 10;

   private List<String> fileNameList; // имена файлов с флагами
   private List<String> quizCountriesList; // страны текущей викторины
   private Set<String> regionsSet; // регионы текущей викторины
   private String correctAnswer; // правильная страна для текущего флага
   private int totalGuesses; // количество попыток
   private int correctAnswers; // количество правильных ответов
   private int guessRows; // количество строк с кнопками вариантов
   private SecureRandom random; // генератор случайных чисел
   private Handler handler; // для задержки загрузки следующего флага
   private Animation shakeAnimation; // анимация неправильного ответа

   private LinearLayout quizLinearLayout; // макет с викториной
   private TextView questionNumberTextView; // номер текущего вопроса
   private ImageView flagImageView; // для вывода флага
   private LinearLayout[] guessLinearLayouts; // строки с кнопками
   private TextView answerTextView; // для привильного ответа

   public MainActivityFragment() {
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_settings, container, false);
   }
}