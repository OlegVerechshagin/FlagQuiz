// MainActivityFragment.java
// Класс содержит логику приложения Flag Quiz
package com.olegverechshagin.flagquiz;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
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

    //   Настройка MainActivityFragment при создании представления
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =
                inflater.inflate(R.layout.fragment_main, container, false);

        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

//      Загрузка анимации для неправильных ответов
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); // анимация повторяется 3 раза

//      Получение ссылок на компоненты графического интерфейса
        quizLinearLayout =
                (LinearLayout) view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView =
                (TextView) view.findViewById(R.id.questionNumberTextView);
        flagImageView = (ImageView) view.findViewById(R.id.flagImageView);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] =
                (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] =
                (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] =
                (LinearLayout) view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] =
                (LinearLayout) view.findViewById(R.id.row4LinearLayout);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);

//      Настройка слушателей для кнопок ответов
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

//      Назначение текста questionNumberTextView
        questionNumberTextView.setText(
                getString(R.string.question, 1, FLAGS_IN_QUIZ));
        return view; // возвращает представление фрагмента для вывода
    }

    //   Обновление guessRows на основании значения SharedPreferences
    public void updateGuessRows(SharedPreferences sharedPreferences) {
//        Получение количества отображаемых вариантов ответа
        String choices =
                sharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

//        Все компоненты LinearLayout скрываются
        for (LinearLayout layout : guessLinearLayouts)
            layout.setVisibility(View.GONE);

//        Отображение нужных компонентов LinearLayout
        for (int row = 0; row < guessRows; row++)
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }

    //    Обновление выбранных регионов по данным из SharedPreferences
    public void updateRegions(SharedPreferences sharedPreferences) {
        regionsSet =
                sharedPreferences.getStringSet(MainActivity.REGION, null);
    }

    //    Настройка и запуск следующей серии вопросов
    public void resetQuiz() {
//        Использование AssetManager для получения имен файлов изображений
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear(); // пустой список имен файлов изображений

        try {
//            Перебрать все регионы
            for (String region : regionsSet) {
//                get a list of all flag image files in this region
                String[] paths = assets.list(region);

                for (String path : paths) {
                    fileNameList.add(path.replace(".png", ""));
                }
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }

        correctAnswers = 0; // сброс количества правильных ответов
        totalGuesses = 0; // сброс общего количества попыток
        quizCountriesList.clear(); // очистка предыдущего списка стран

        int  flagCounter = 1;
        int numberOfFlags = fileNameList.size();

//        Добавление FLAGS_IN_QUIZ сдучайных фалов в quizCountriesList
        while (flagCounter <= FLAGS_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfFlags);

//            Получение случайного имени файла
            String filename = fileNameList.get(randomIndex);

//            Если регион включен, но еще не был выбран
            if (!quizCountriesList.contains(filename)) {
                quizCountriesList.add(filename); // добавить файл в список
                ++flagCounter;
            }
        }

        loadNextFlag(); // запустить викторину загрузкой первого флага
    }

//    Загрузка следующего флага после правильного ответа
    private void loadNextFlag() {
//        Получение имени файла следующего флага и удаление его из списка
        String nextImage = quizCountriesList.remove(0);
        correctAnswer = nextImage; // обновление правильного ответа
        answerTextView.setText(""); // очистка answerTextView

//        Отображение номера текущего вопроса
        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), FLAGS_IN_QUIZ));

//        Извлечение региона из имени следующего изображения
        String region = nextImage.substring(0, nextImage.indexOf('-'));

//        Использование AssetManager для загрузки следующего изображения
        AssetManager assets = getActivity().getAssets();

//        Получение объета InputStream для ресурса следующего флага
//        и попытка использования InputStream
        try (InputStream stream =
        assets.open(region + "/" + nextImage + ".png")) {
//            Загрузка графики в виде объекта Drawable и вывод на flagImageView
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);

            animate(false); // анимация пояления флага на экране
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        Collections.shuffle(fileNameList); // перестановка имен файлов

//        Перемещение правильного ответа в конец fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

//        Добавление 2, 4, 6 или 8 кнопок в зависимости от значения guessRows
        for (int row = 0; row < guessRows; row++) {
//            Размещение кнопок в currentTableRow
            for (int column = 0;
                    column < guessLinearLayouts[row].getChildCount();
                    column++) {
//                Получение ссылки на Button
                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

//                Назначение названия страны текстом newGuessButton
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getCountryName(filename));
            }
        }

//        Случайная замена одной кнопки правильным ответом
        int row = random.nextInt(guessRows); // выбор случайной строки
        int column = random.nextInt(2); // выбор случайного столбца
        LinearLayout randomRow = guessLinearLayouts[row]; // получение строки
        String countryName = getCountryName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(countryName);
    }
}