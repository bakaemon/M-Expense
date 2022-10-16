package com.tqb.m_expense;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.tqb.m_expense.Database.AppDatabase;
import com.tqb.m_expense.Database.DAO.ExpenseDao;
import com.tqb.m_expense.Database.DAO.TripDao;
import com.tqb.m_expense.Database.DatabaseHelper;
import com.tqb.m_expense.Database.Entity.Expense;
import com.tqb.m_expense.Database.Entity.Trip;
import com.tqb.m_expense.View.Adapter.SectionsPagerAdapter;
import com.tqb.m_expense.databinding.ActivityExpenseBinding;

import java.util.HashMap;
import java.util.Map;

public class ExpenseActivity extends AppCompatActivity {
    public ActivityExpenseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarExpense);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}