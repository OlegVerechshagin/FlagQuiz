// SettingsActivity.java
// Activity to display SettingsActivityFragment on a phone
package com.olegverechshagin.flagquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
   // inflates the GUI, displays Toolbar and adds "up" button (заплняте GUI, отображает Toolbar и добавляет кнопку "up")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   }
}