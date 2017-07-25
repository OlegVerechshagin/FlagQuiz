// MainActivity.java
// Управляет фрагментом MainActivityFragment на телефоне и фрагментами
// MainActivityFragment и SettingsActivityFragment на планшете
package com.olegverechshagin.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
//   Ключи для чтения данных из SharedPreferences
   public static final String CHOICES = "pref_numberOfChoices";
   public static final String REGION = "pref_regionsToInclude";

   private boolean phoneDevice = true; // включение портретного режима
   private boolean preferencesChanged = true; // настройки изменились?

//   Настройка MainActivity
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

//      Задание значений по умодчанию в файле SharedPreferences
      PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

//      Регистрация слушателя для изменений SharedPreferences
      PreferenceManager.getDefaultSharedPreferences(this).
              registerOnSharedPreferenceChangeListener(
                      preferencesChangedListener);

//      Определение размера экрана
      int screenSize = getResources().getConfiguration().screenLayout &
              Configuration.SCREENLAYOUT_SIZE_MASK;

//      Для планщетного устройства phoneDevice присваивается false
      if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
              screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
         phoneDevice = false; // не соответствует размерам телефона

//      На телефоне разрешена только портретная ориентация
      if (phoneDevice)
         setRequestedOrientation(
                 ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   }

//   Вызывается после завершения выполнения onCreate
   @Override
   protected void onStart() {
      super.onStart();

      if (preferencesChanged) {
//         После задания настроек по умолчанию инициализировать
//         MainActivityFragment и запустить викторину
         MainActivityFragment quizFragment = (MainActivityFragment)
                 getSupportFragmentManager().findFragmentById(
                         R.id.quizFragment);
         quizFragment.updateGuessRows(
                 PreferenceManager.getDefaultSharedPreferences(this));
         quizFragment.updateRegions(
                 PreferenceManager.getDefaultSharedPreferences(this));
         quizFragment.resetQuiz();
         preferencesChanged = false;
      }
   }

//   Меню отображается на телефоне или планшете в портретной ориентации
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
//      Получение текущей ориентации устройства
      int orientation = getResources().getConfiguration().orientation;

//      Отображение меню приложения только в потретной ориентации
      if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//         Заполнение меню
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
      }
      else
         return false;
   }

//   Отображает SettingsActivity при запуске на телефоне
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      Intent preferencesIntent = new Intent(this, SettingsActivity.class);
      startActivity(preferencesIntent);
      return super.onOptionsItemSelected(item);
   }
}