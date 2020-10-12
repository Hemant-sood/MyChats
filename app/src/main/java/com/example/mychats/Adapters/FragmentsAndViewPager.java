package com.example.mychats.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mychats.Fragments.Find;
import com.example.mychats.Fragments.MyChats;
import com.example.mychats.Fragments.MyNetwork;

public class FragmentsAndViewPager extends FragmentPagerAdapter {


    public FragmentsAndViewPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0 :
                MyChats myChatsFragment = new MyChats();
                return myChatsFragment;
            case 1 :
                MyNetwork myNetwork = new MyNetwork();
                return myNetwork;
            case 2 :
                Find findFragment = new Find();
                return findFragment;

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
                case 0 :
                    return "My Chats";
                case 1 :
                    return "My Network";
            case 2 :
                return "Find";
        }
        return "";
    }
}
