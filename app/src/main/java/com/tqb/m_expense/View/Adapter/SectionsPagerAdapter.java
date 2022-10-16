package com.tqb.m_expense.View.Adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.tqb.m_expense.Database.Entity.EntityInterface;
import com.tqb.m_expense.ExpenseFragment;
import com.tqb.m_expense.ExpensesFragment;
import com.tqb.m_expense.R;
import com.tqb.m_expense.StatisticFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public interface OnFragmentInteractionListener {
        default Fragment onFragmentInteraction(Fragment fragment) {
            return fragment;
        }
    }
    private OnFragmentInteractionListener listener;
    private final static CharSequence[] TAB_TITLES = {"Expenses", "Statistics"};
    private final static Fragment[] FRAGMENTS = {new ExpensesFragment(), new StatisticFragment()};

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public OnFragmentInteractionListener getListener() {
        return listener;
    }

    public void setListener(OnFragmentInteractionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = FRAGMENTS[position];
        return listener.onFragmentInteraction(fragment);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}