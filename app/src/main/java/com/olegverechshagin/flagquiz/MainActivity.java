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
         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   }
}


/*************************************************************************
 * (C) Copyright 1992-2016 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
