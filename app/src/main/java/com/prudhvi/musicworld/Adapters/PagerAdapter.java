package com.prudhvi.musicworld.Adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.prudhvi.musicworld.Fragments.AlbumsFragment;
import com.prudhvi.musicworld.Fragments.PlaylistFragment;
import com.prudhvi.musicworld.Fragments.SongsFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new SongsFragment();
            case 1: return new AlbumsFragment();
            case 2: return new PlaylistFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
