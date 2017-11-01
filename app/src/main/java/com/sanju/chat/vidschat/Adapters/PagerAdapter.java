package com.sanju.chat.vidschat.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.sanju.chat.vidschat.Fragments.ChatTabFragment;
import com.sanju.chat.vidschat.Fragments.ContactTabFragment;

/**
 * Created by angad on 7/11/16.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private String tabtitles[] = new String[] { "Chat", "Contact"};
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatTabFragment();
            case 1:
                return new ContactTabFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }


}
